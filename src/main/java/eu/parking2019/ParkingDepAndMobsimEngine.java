package eu.parking2019;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.events.PersonStuckEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.qsim.InternalInterface;
import org.matsim.core.mobsim.qsim.interfaces.DepartureHandler;
import org.matsim.core.mobsim.qsim.interfaces.MobsimEngine;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.misc.Time;
import org.matsim.facilities.Facility;
import org.matsim.vis.snapshotwriters.TeleportationVisData;

import javax.inject.Inject;
import java.util.*;

/**
 * Hebenstreit: Includes all modes, which in this case are
 * bs, bs_walk, walk, bike, car, pt, transit_walk
 * and is DepartureHandler and MobsimEngine for them, further onPrepareSim() the
 * bs-station and bs-bikes get reset
 */
public final class ParkingDepAndMobsimEngine implements DepartureHandler, MobsimEngine{
	
	private static final Logger log = Logger.getLogger( ParkingDepAndMobsimEngine.class ) ;
	@Inject EventsManager eventsManager;
	
	private final Queue<Tuple<Double, MobsimAgent>> parking_egr_List = new PriorityQueue<>(
			30, new Comparator<Tuple<Double, MobsimAgent>>() {

		@Override
		public int compare(Tuple<Double, MobsimAgent> o1, Tuple<Double, MobsimAgent> o2) {
			int ret = o1.getFirst().compareTo(o2.getFirst()); // first compare time information
			if (ret == 0) {
				ret = o2.getSecond().getId().compareTo(o1.getSecond().getId()); // if they're equal, compare the Ids: the one with the larger Id should be first
			}
			return ret;
		}
	});	
	
	private final Queue<Tuple<Double, MobsimAgent>> parking_acc_List = new PriorityQueue<>(
			30, new Comparator<Tuple<Double, MobsimAgent>>() {

		@Override
		public int compare(Tuple<Double, MobsimAgent> o1, Tuple<Double, MobsimAgent> o2) {
			int ret = o1.getFirst().compareTo(o2.getFirst()); // first compare time information
			if (ret == 0) {
				ret = o2.getSecond().getId().compareTo(o1.getSecond().getId()); // if they're equal, compare the Ids: the one with the larger Id should be first
			}
			return ret;
		}
	});	
	
	private final Queue<Tuple<Double, MobsimAgent>> car_p_List = new PriorityQueue<>(
			30, new Comparator<Tuple<Double, MobsimAgent>>() {

		@Override
		public int compare(Tuple<Double, MobsimAgent> o1, Tuple<Double, MobsimAgent> o2) {
			int ret = o1.getFirst().compareTo(o2.getFirst()); // first compare time information
			if (ret == 0) {
				ret = o2.getSecond().getId().compareTo(o1.getSecond().getId()); // if they're equal, compare the Ids: the one with the larger Id should be first
			}
			return ret;
		}
	});	
	
	
	private final Queue<Tuple<Double, MobsimAgent>> transitWalkList = new PriorityQueue<>(
			30, new Comparator<Tuple<Double, MobsimAgent>>() {

		@Override
		public int compare(Tuple<Double, MobsimAgent> o1, Tuple<Double, MobsimAgent> o2) {
			int ret = o1.getFirst().compareTo(o2.getFirst()); // first compare time information
			if (ret == 0) {
				ret = o2.getSecond().getId().compareTo(o1.getSecond().getId()); // if they're equal, compare the Ids: the one with the larger Id should be first
			}
			return ret;
		}
	});	

	
	private final Queue<Tuple<Double, MobsimAgent>> ptList = new PriorityQueue<>(
			30, new Comparator<Tuple<Double, MobsimAgent>>() {

		@Override
		public int compare(Tuple<Double, MobsimAgent> o1, Tuple<Double, MobsimAgent> o2) {
			int ret = o1.getFirst().compareTo(o2.getFirst()); // first compare time information
			if (ret == 0) {
				ret = o2.getSecond().getId().compareTo(o1.getSecond().getId()); // if they're equal, compare the Ids: the one with the larger Id should be first
			}
			return ret;
		}
	});

		private final Queue<Tuple<Double, MobsimAgent>> walkList = new PriorityQueue<>(
				30, new Comparator<Tuple<Double, MobsimAgent>>() {

			@Override
			public int compare(Tuple<Double, MobsimAgent> o1, Tuple<Double, MobsimAgent> o2) {
				int ret = o1.getFirst().compareTo(o2.getFirst()); // first compare time information
				if (ret == 0) {
					ret = o2.getSecond().getId().compareTo(o1.getSecond().getId()); // if they're equal, compare the Ids: the one with the larger Id should be first
				}
				return ret;
			}
		});

			private final Queue<Tuple<Double, MobsimAgent>> bikeList = new PriorityQueue<>(
				30, new Comparator<Tuple<Double, MobsimAgent>>() {

			@Override
			public int compare(Tuple<Double, MobsimAgent> o1, Tuple<Double, MobsimAgent> o2) {
				int ret = o1.getFirst().compareTo(o2.getFirst()); // first compare time information
				if (ret == 0) {
					ret = o2.getSecond().getId().compareTo(o1.getSecond().getId()); // if they're equal, compare the Ids: the one with the larger Id should be first
				}
				return ret;
			}
		});
			
			private final Queue<Tuple<Double, MobsimAgent>> carList = new PriorityQueue<>(
					30, new Comparator<Tuple<Double, MobsimAgent>>() {

				@Override
				public int compare(Tuple<Double, MobsimAgent> o1, Tuple<Double, MobsimAgent> o2) {
					int ret = o1.getFirst().compareTo(o2.getFirst()); // first compare time information
					if (ret == 0) {
						ret = o2.getSecond().getId().compareTo(o1.getSecond().getId()); // if they're equal, compare the Ids: the one with the larger Id should be first
					}
					return ret;
				}
			});
	
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_car_p = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_acc_walk = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_egr_walk = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_walk = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_car = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_bike = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_pt = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_ptWalk = new LinkedHashMap<>();

			private InternalInterface internalInterface;
			private Scenario scenario;
			private final boolean withTravelTimeCheck ;

			@Inject
			public ParkingDepAndMobsimEngine(Scenario scenario, EventsManager eventsManager) {
				this.scenario = scenario;
				this.eventsManager = eventsManager;
				
				withTravelTimeCheck = scenario.getConfig().qsim().isUsingTravelTimeCheckInTeleportation() ;
			}

	@Override
	public boolean handleDeparture(double now, MobsimAgent agent, Id<Link> linkId) 
	{

		String s = agent.getMode();
		if ( agent.getExpectedTravelTime()==null || agent.getExpectedTravelTime()==Time.UNDEFINED_TIME ) {
			Logger.getLogger( this.getClass() ).info( "mode: " + agent.getMode() );
			throw new RuntimeException("bikesharing does not work when travel time is undefined.  There is also really no magic fix for this,"
					+ " since we cannot guess travel times for arbitrary modes and arbitrary landscapes.  kai/mz, apr'15 & feb'16") ;
		}

		Double travelTime = agent.getExpectedTravelTime() ;
		if ( withTravelTimeCheck ) {
			//Id<Person> pId = agent.getId();
			Double speed = agent.getExpectedTravelTime()/agent.getExpectedTravelDistance() ;
			Facility<?> dpfac = agent.getCurrentFacility() ;
			Facility<?> arfac = agent.getDestinationFacility() ;
			travelTime = ParkingDepAndMobsimEngine.travelTimeCheck(travelTime, speed, dpfac, arfac);
		}
		double arrivalTime = now + travelTime ;
		
		if (agent.getMode().equals(TransportMode.access_walk))
		{
			this.parking_acc_List.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_acc_walk.put(agentId, agentInfo);
		}
		
		if (agent.getMode().equals(TransportMode.egress_walk))
		{
			this.parking_egr_List.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_egr_walk.put(agentId, agentInfo);
		}
		
		if (agent.getMode().equals("car_p"))
		{
			this.car_p_List.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_car_p.put(agentId, agentInfo);
		}
		
		if (agent.getMode().equals(TransportMode.pt))
		{
			this.ptList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_pt.put(agentId, agentInfo);
		}
		
		if (agent.getMode().equals(TransportMode.transit_walk))
		{
			this.transitWalkList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_ptWalk.put(agentId, agentInfo);
		}
		
		if (agent.getMode().equals(TransportMode.walk))
		{
			this.walkList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_walk.put(agentId, agentInfo);
		}
		
		if (agent.getMode().equals(TransportMode.bike))
		{
			this.bikeList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_bike.put(agentId, agentInfo);
		}
		
		if (agent.getMode().equals(TransportMode.car))
		{
			this.carList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_car.put(agentId, agentInfo);
		}
		return true;
	}
 
	//@Override
	//public Collection<AgentSnapshotInfo> addAgentSnapshotInfo(Collection<AgentSnapshotInfo> snapshotList) {
	//	double time = internalInterface.getMobsim().getSimTimer().getTimeOfDay();
	//	for (TeleportationVisData teleportationVisData : teleportationData.values()) {
	//		teleportationVisData.calculatePosition(time);
	//		snapshotList.add(teleportationVisData);
	//	}
	//	return snapshotList;
	//}

	@Override
	public void doSimStep(double time) {
		handleBSArrivals();
	}
	

	private void handleBSArrivals() {
		double now = internalInterface.getMobsim().getSimTimer().getTimeOfDay();
		
		while (car_p_List.peek() != null) {
			Tuple<Double, MobsimAgent> entry = car_p_List.peek();
			if (entry.getFirst() <= now) {
				car_p_List.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new ArrivalEvent(now, personAgent.getId(), distance, "car_p"));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_car_p.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
		
		while (parking_acc_List.peek() != null) {
			Tuple<Double, MobsimAgent> entry = parking_acc_List.peek();
			if (entry.getFirst() <= now) {
				parking_acc_List.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new ArrivalEvent(now, personAgent.getId(), distance, "access_walk"));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_acc_walk.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
		
		while (parking_egr_List.peek() != null) {
			Tuple<Double, MobsimAgent> entry = parking_egr_List.peek();
			if (entry.getFirst() <= now) {
				parking_egr_List.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new ArrivalEvent(now, personAgent.getId(), distance, "egress_walk"));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_egr_walk.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
		
		
		while (transitWalkList.peek() != null) {
			Tuple<Double, MobsimAgent> entry = transitWalkList.peek();
			if (entry.getFirst() <= now) {
				transitWalkList.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new ArrivalEvent(now, personAgent.getId(), distance, "transit_walk"));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_ptWalk.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
		
		while (ptList.peek() != null) {
			Tuple<Double, MobsimAgent> entry = ptList.peek();
			if (entry.getFirst() <= now) {
				ptList.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new ArrivalEvent(now, personAgent.getId(), distance, "pt"));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_pt.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
		
		while (walkList.peek() != null) {
			Tuple<Double, MobsimAgent> entry = walkList.peek();
			if (entry.getFirst() <= now) {
				walkList.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new ArrivalEvent(now, personAgent.getId(), distance, "walk"));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_walk.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
	
		
		while (bikeList.peek() != null) {
			Tuple<Double, MobsimAgent> entry = bikeList.peek();
			if (entry.getFirst() <= now) {
				bikeList.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new ArrivalEvent(now, personAgent.getId(), distance, "bike"));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_bike.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
		
		while (carList.peek() != null) {
			Tuple<Double, MobsimAgent> entry = carList.peek();
			if (entry.getFirst() <= now) {
				carList.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new ArrivalEvent(now, personAgent.getId(), distance , "car"));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_car.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
	}

	@Override
	public void onPrepareSim() {
		
		log.warn("###################--Next Iteration started--#####################");
		
	}

	@Override
	public void afterSim() {
		double now = internalInterface.getMobsim().getSimTimer().getTimeOfDay();
		
		for (Tuple<Double, MobsimAgent> entry : car_p_List) {
			MobsimAgent agent = entry.getSecond();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		car_p_List.clear();
		

		for (Tuple<Double, MobsimAgent> entry : parking_acc_List) {
			MobsimAgent agent = entry.getSecond();
			Id<Link> id = agent.getDestinationLinkId();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		parking_acc_List.clear();
		
		
		for (Tuple<Double, MobsimAgent> entry : parking_egr_List) {
			MobsimAgent agent = entry.getSecond();
			Id<Link> id = agent.getDestinationLinkId();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		parking_egr_List.clear();
		
		
		for (Tuple<Double, MobsimAgent> entry : transitWalkList) {
			MobsimAgent agent = entry.getSecond();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		transitWalkList.clear();

		for (Tuple<Double, MobsimAgent> entry : walkList) {
			MobsimAgent agent = entry.getSecond();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		walkList.clear();
		
		for (Tuple<Double, MobsimAgent> entry : bikeList) {
			MobsimAgent agent = entry.getSecond();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		bikeList.clear();
		
		for (Tuple<Double, MobsimAgent> entry : carList) {
			MobsimAgent agent = entry.getSecond();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		carList.clear();
		
		for (Tuple<Double, MobsimAgent> entry : ptList) {
			MobsimAgent agent = entry.getSecond();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		ptList.clear();	
	}
		
	@Override
	public void setInternalInterface(InternalInterface internalInterface) {
		this.internalInterface = internalInterface;
	}

	private static Double travelTimeCheck(Double travelTime, Double speed, Facility<?> dpfac, Facility<?> arfac) {
		if ( speed==null ) {
			// if we don't have a bushwhacking speed, the only thing we can do is trust the router
			return travelTime ;
		} 
		
		if ( dpfac == null || arfac == null ) {
			log.warn( "dpfac = " + dpfac ) ;
			log.warn( "arfac = " + arfac ) ;
			throw new RuntimeException("have bushwhacking mode but nothing that leads to coordinates; don't know what to do ...") ;
			// (means that the agent is not correctly implemented)
		}
		
		if ( dpfac.getCoord()==null || arfac.getCoord()==null ) {
			// yy this is for example the case if activities are initialized at links, without coordinates.  Could use the link coordinate
			// instead, but somehow this does not feel any better. kai, feb'16
			
			return travelTime ;
		}
			
		final Coord dpCoord = dpfac.getCoord();
		final Coord arCoord = arfac.getCoord();
				
		double dist = NetworkUtils.getEuclideanDistance( dpCoord, arCoord ) ;
		double travelTimeTmp = dist / speed ;
		
		if ( travelTimeTmp < travelTime ) {
			return travelTime ;
		}
			
		return travelTimeTmp ;
	}

}
