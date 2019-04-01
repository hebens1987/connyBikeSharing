package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.events.PersonStuckEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.AgentSource;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.qsim.InternalInterface;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.TeleportationEngine;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;
import org.matsim.core.mobsim.qsim.agents.TransitAgentImpl;
import org.matsim.core.mobsim.qsim.interfaces.DepartureHandler;
import org.matsim.core.mobsim.qsim.interfaces.MobsimEngine;
import org.matsim.core.mobsim.qsim.pt.AbstractTransitDriverAgent;
import org.matsim.core.mobsim.qsim.pt.PTPassengerAgent;
import org.matsim.core.mobsim.qsim.pt.TransitDriverAgentImpl;
import org.matsim.core.mobsim.qsim.pt.TransitQSimEngine;
import org.matsim.core.mobsim.qsim.pt.TransitStopAgentTracker;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.misc.Time;
import org.matsim.facilities.Facility;
import org.matsim.pt.config.TransitRouterConfigGroup;
import org.matsim.pt.router.PreparedTransitSchedule;
import org.matsim.pt.router.TransitRouterConfig;
import org.matsim.pt.router.TransitRouterImpl;
import org.matsim.pt.router.TransitRouterNetworkTravelTimeAndDisutility;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
import org.matsim.vehicles.Vehicle;
import org.matsim.vis.snapshotwriters.TeleportationVisData;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.WaitingData;
import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.TravelEvents.BikeArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.TravelEvents.BsArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.TravelEvents.BsWalkArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.TravelEvents.CarArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.TravelEvents.FfBsArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.TravelEvents.PTArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.TravelEvents.TransitWalkArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.TravelEvents.WalkArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.relocation.RelocationHandler;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.Bikes;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikesE;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.EBikeSharingConfigGroup;
import eu.eunoiaproject.freeFloatingBS.BikesFF;

import javax.inject.Inject;
import java.util.*;
import java.util.Map.Entry;

/**
 * Hebenstreit: Includes all modes, which in this case are
 * bs, bs_walk, walk, bike, car, pt, transit_walk
 * and is DepartureHandler and MobsimEngine for them, further onPrepareSim() the
 * bs-station and bs-bikes get reset
 */
public final class BsDepartureAndMobsimEngine implements DepartureHandler, MobsimEngine, AgentSource{
	
	Map<String,TravelDisutilityFactory> travelDisutilityFactories; 
	Map<String,TravelTime> travelTimes;
	LeastCostPathCalculatorFactory pathCalculatorFactory;
	private Collection<MobsimAgent> ptDrivers;
	QSim qsim; 
	TransitQSimEngine trans;
	TeleportationEngine teleport;
	
	public static class TransitAgentTriesToTeleportException extends RuntimeException {
		public TransitAgentTriesToTeleportException(String message) {
			super(message);
		}

		private static final long serialVersionUID = 1L;

	}


	private static final Logger log = Logger.getLogger( BsDepartureAndMobsimEngine.class ) ;
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
	
	private final Queue<Tuple<Double, MobsimAgent>> walkFF = new PriorityQueue<>(
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
	private final Queue<Tuple<Double, MobsimAgent>> bikeFF = new PriorityQueue<>(
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
	
	//todo - make a Tuple for every Agent who did a bike-sharing trip, so this agent can be reset to a "eBikeSharing" trip.
	//that means bs-walk / bs / bs-walk - shall be reset to eBikeSharing

		
	private final Queue<Tuple<Double, MobsimAgent>> bsList = new PriorityQueue<>(
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

		private final Queue<Tuple<Double, MobsimAgent>> bsWalkList = new PriorityQueue<>(
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
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_bs = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_bs_walk = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_walk = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_car = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_bike = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_pt = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_ptWalk = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_walkFF = new LinkedHashMap<>();
			private final LinkedHashMap<Id<Person>, TeleportationVisData> teleportationData_bikeFF = new LinkedHashMap<>();
			private InternalInterface internalInterface;
			private Scenario scenario;
			private final boolean withTravelTimeCheck;

			@Inject
			public BsDepartureAndMobsimEngine(Scenario scenario, EventsManager eventsManager, 
					LeastCostPathCalculatorFactory pathCalculatorFactory,
					Map<String,TravelDisutilityFactory> travelDisutilityFactories, Map<String,TravelTime> travelTimes, QSim qsim) {
				this.scenario = scenario;
				this.travelDisutilityFactories = travelDisutilityFactories;
				this.travelTimes = travelTimes;
				this.eventsManager = eventsManager;
				this.qsim = qsim;
				this.pathCalculatorFactory = pathCalculatorFactory;
				withTravelTimeCheck = scenario.getConfig().qsim().isUsingTravelTimeCheckInTeleportation() ;
				this.trans = new TransitQSimEngine(qsim);
				this.teleport = new TeleportationEngine(scenario, eventsManager);
			}

	@Override
	public boolean handleDeparture(double now, MobsimAgent agent, Id<Link> linkId) 
	{
		boolean returnType = true;
		String s = agent.getMode();
		Double travelTime = agent.getExpectedTravelTime() ;
		
		//if ( agent.getExpectedTravelTime()==null || agent.getExpectedTravelTime()==Time.UNDEFINED_TIME ) {
		//	Logger.getLogger( this.getClass() ).info( "mode: " + agent.getMode());
		//	return true;
		//}
		if ( withTravelTimeCheck ) {
			//Id<Person> pId = agent.getId();
			Double speed = agent.getExpectedTravelTime()/agent.getExpectedTravelDistance() ;
			Facility<?> dpfac = agent.getCurrentFacility() ;
			Facility<?> arfac = agent.getDestinationFacility() ;
			travelTime = BsDepartureAndMobsimEngine.travelTimeCheck(travelTime, speed, dpfac, arfac);
		}
		double arrivalTime = now + travelTime ;

		String requestedMode = agent.getMode();
		boolean isPt = false;
		/*if (qsim.getScenario().getConfig().transit().getTransitModes().contains(requestedMode)) 
		{
			BikesharingPersonDriverAgentImpl bsAgent = (BikesharingPersonDriverAgentImpl)agent;
			BasicPlanAgentImpl bpAgent = bsAgent.getPlanAgent(bsAgent);
			TransitAgentImpl trAgent = new TransitAgentImpl(bpAgent); //Hebenstreit
			
			// this puts the agent into the transit stop.
			Id id = agent.getCurrentFacility().getId();
			Id<TransitStopFacility> accessStopId = ((PTPassengerAgent) trAgent).getDesiredAccessStopId();
			if (accessStopId == null) 
			{
				// looks like this agent has a bad transit route, likely no
				// route could be calculated for it
				log.error("pt-agent doesn't know to what transit stop to go. Removing agent from simulation. Agent " + agent.getId().toString());
				qsim.getAgentCounter().decLiving();
				qsim.getAgentCounter().incLost();
				
			}
			this.ptList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_pt.put(agentId, agentInfo);
			returnType = true;
		}*/
		if (agent instanceof TransitDriverAgentImpl)
		{
			returnType = trans.handleDeparture(now, agent, linkId);
		}

		
		else if (agent.getMode().equals(TransportMode.pt))
		{
			this.ptList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_pt.put(agentId, agentInfo);
			returnType = true;
		}

		else if (agent.getMode().equals(TransportMode.access_walk))
		{
			//returnType = teleport.handleDeparture(now, agent, linkId);
			this.parking_acc_List.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_acc_walk.put(agentId, agentInfo);
		}
		
		else if (agent.getMode().equals(TransportMode.egress_walk))
		{
			//returnType = teleport.handleDeparture(now, agent, linkId);
			this.parking_egr_List.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_egr_walk.put(agentId, agentInfo);
		}
		
		else if (agent.getMode().equals("car_p"))
		{
			this.car_p_List.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_car_p.put(agentId, agentInfo);
			returnType = true;
		}
		
		else if (agent.getMode().equals(EBConstants.BS_BIKE)||(agent.getMode().equals(EBConstants.BS_E_BIKE)))
		{
			this.bsList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_bs.put(agentId, agentInfo);
			returnType = true;
		}

		else if (agent.getMode().equals(TransportMode.transit_walk))
		{
			//returnType = teleport.handleDeparture(now, agent, linkId);
			this.transitWalkList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_ptWalk.put(agentId, agentInfo);
		}
		
		else if (agent.getMode().equals(EBConstants.BS_WALK))
		{
			this.bsWalkList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_bs_walk.put(agentId, agentInfo);
			returnType = true;
		}
		
		else if (agent.getMode().equals(TransportMode.walk))
		{
			this.walkList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_walk.put(agentId, agentInfo);
			returnType = true;
		}
		
		else if (agent.getMode().equals(TransportMode.bike))
		{
			this.bikeList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_bike.put(agentId, agentInfo);
			returnType = true;
		}
		
		else if (agent.getMode().equals(TransportMode.car))
		{
			this.carList.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_car.put(agentId, agentInfo);
			returnType = true;
		}
		
		else if (agent.getMode().equals(EBConstants.BS_BIKE_FF))
		{
			this.bikeFF.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_bikeFF.put(agentId, agentInfo);
			returnType = true;
		}
		
		else if (agent.getMode().equals(EBConstants.BS_WALK_FF))
		{
			this.walkFF.add(new Tuple<>(arrivalTime, agent));
			Id<Person> agentId = agent.getId();
			Link currLink = this.scenario .getNetwork().getLinks().get(linkId);
			Link destLink = this.scenario .getNetwork().getLinks().get(agent.getDestinationLinkId());
			Coord fromCoord = currLink.getToNode().getCoord();
			Coord toCoord = destLink.getToNode().getCoord();
			TeleportationVisData agentInfo = new TeleportationVisData(now, agentId, fromCoord, toCoord, travelTime);
			this.teleportationData_walkFF.put(agentId, agentInfo);
			returnType = true;
		}
		return returnType;
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
		teleport.doSimStep(time);
		trans.doSimStep(time);
	}
	


	private void handleBSArrivals() // Hebenstreit TODO: Change that all events are passed over!
	{
		double now = internalInterface.getMobsim().getSimTimer().getTimeOfDay();
		
		while (bsList.peek() != null) {
			Tuple<Double, MobsimAgent> entry = bsList.peek();
			if (entry.getFirst() <= now) {
				bsList.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new BsArrivalEvent(now, personAgent.getId(), distance));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_bs.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
		
		while (walkFF.peek() != null) {
			
			Tuple<Double, MobsimAgent> entry = walkFF.peek();
			if (entry.getFirst() <= now) {
				walkFF.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new WalkArrivalEvent(now, personAgent.getId(), distance));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_walkFF.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
		while (bikeFF.peek() != null) {
			Tuple<Double, MobsimAgent> entry = bikeFF.peek();
			if (entry.getFirst() <= now) {
				bikeFF.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new FfBsArrivalEvent(now, personAgent.getId(), distance));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_bikeFF.remove(personAgent.getId());
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
				this.eventsManager.processEvent(new TransitWalkArrivalEvent(now, personAgent.getId(), distance));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_ptWalk.remove(personAgent.getId());
				internalInterface.arrangeNextAgentState(personAgent);
			} else {
				break;
			}
		}
	
		
		while (bsWalkList.peek() != null) {
			Tuple<Double, MobsimAgent> entry = bsWalkList.peek();
			if (entry.getFirst() <= now) {
				bsWalkList.poll();
				MobsimAgent personAgent = entry.getSecond();
				personAgent.notifyArrivalOnLinkByNonNetworkMode(personAgent.getDestinationLinkId());
				double distance = personAgent.getExpectedTravelDistance();
				this.eventsManager.processEvent(new BsWalkArrivalEvent(now, personAgent.getId(), distance));
				personAgent.endLegAndComputeNextState(now);
				this.teleportationData_bs_walk.remove(personAgent.getId());
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
				this.eventsManager.processEvent(new PTArrivalEvent(now, personAgent.getId(), distance));
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
				this.eventsManager.processEvent(new WalkArrivalEvent(now, personAgent.getId(), distance));
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
				this.eventsManager.processEvent(new BikeArrivalEvent(now, personAgent.getId(), distance));
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
				this.eventsManager.processEvent(new CarArrivalEvent(now, personAgent.getId(), distance));
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
		
		trans.onPrepareSim();
		teleport.onPrepareSim();
		BikeSharingBikes bSharingVehicles = (BikeSharingBikes) scenario.getScenarioElement( BikeSharingBikes.ELEMENT_NAME);
		BikeSharingFacilities bsFac = (BikeSharingFacilities) scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME);
		
		if (bSharingVehicles != null)
		{
			List<BikesE> bikes = new ArrayList<BikesE>(bSharingVehicles.ebikes.values());
			List<Bikes> cbikes = new ArrayList<Bikes>(bSharingVehicles.bikes.values());
			
			//reset the EBikes
			for (int i = 0; i < bSharingVehicles.ebikes.size(); i++)
			{	
				bikes.get(i).setTime(0);
				bikes.get(i).setInfoIfBikeInStation(true);
				bikes.get(i).setInStation(bikes.get(i).getOrigStation());
				bikes.get(i).setStateOfCharge(bikes.get(i).getOrigStateOfCharge());
			}
			
			//reset the FFBikes
			bSharingVehicles.bikesFF = new HashMap<Id<Vehicle>, BikesFF>();
			bSharingVehicles.bikesFF.putAll(bSharingVehicles.bikesFFOrig);
			
			//reset the Bikes
			for (int i = 0; i < bSharingVehicles.bikes.size(); i++)
			{	
				cbikes.get(i).setTime(0);
				cbikes.get(i).setInfoIfBikeInStation(true);
				cbikes.get(i).setInStation(cbikes.get(i).getOrigStation());
			}
		}
		
		if (bsFac != null)
		{
			List<BikeSharingFacility> fac = new ArrayList<BikeSharingFacility>(bsFac.facilities.values());
			//reset the Station
			for (int i = 0; i < bsFac.facilities.size(); i++)
			{	
				bsFac.facilities.get(fac.get(i).getId()).setCycles_in_station(fac.get(i).getOrigCyclesInStation());
				bsFac.facilities.get(fac.get(i).getId()).setFreeParkingSlots(fac.get(i).getTotalBikeNumber() - fac.get(i).getOrigAvailBikes());
				bsFac.facilities.get(fac.get(i).getId()).setNumberOfAvailableBikes(fac.get(i).getOrigAvailBikes());
				bsFac.facilities.get(fac.get(i).getId()).setWaitingToReturnBike(null);
				bsFac.facilities.get(fac.get(i).getId()).setWaitingToReturnBike(null);
				if ((bsFac.facilities.get(fac.get(i).getId()).getNumberOfAvailableBikes() <= 2) 
						|| (bsFac.facilities.get(fac.get(i).getId()).getFreeParkingSlots() <= 2))
				{
					if (bsFac.station_in_need_of_relocation == null)
					{
						bsFac.station_in_need_of_relocation = new ArrayList<BikeSharingFacility>();
						bsFac.facilities.get(fac.get(i).getId()).setTimeOfRelocation(3600);
						bsFac.station_in_need_of_relocation.add(bsFac.facilities.get(fac.get(i).getId()));
					}
				}
			}
			
			
		}
		if (bsFac != null)
		{
			bsFac.totalWaitingListReturn = new HashMap<Id<Person>, WaitingData>();
			bsFac.totalWaitingListTake = new HashMap<Id<Person>, WaitingData>();
			bsFac.station_in_need_of_relocation = null;
			bsFac.station_in_relocation = null;
		}
		
		/*AllParkingSpots allParkingSpots= (AllParkingSpots) scenario.getScenarioElement(AllParkingSpots.ELEMENT_NAME);
		allParkingSpots.filledParking = new LinkedHashMap <>(allParkingSpots.filledParkingOrig);
		allParkingSpots.filledParkingPrivate = new LinkedHashMap <>( allParkingSpots.filledParkingPrivateOrig);
		allParkingSpots.parkingPrivate =new LinkedHashMap <>( allParkingSpots.parkingPrivateOrig);
		allParkingSpots.parking =  new LinkedHashMap <>( allParkingSpots.parkingOrig);
		allParkingSpots.parkingStartedAt = new LinkedHashMap <>();*/

		log.warn("###################--Next Iteration started--#####################");
		 RelocationHandler.writeStringOnFile("-------------------iteration-started--------------");
		
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
				

		for (Tuple<Double, MobsimAgent> entry : bsList) {
			MobsimAgent agent = entry.getSecond();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		bsList.clear();
		
		for (Tuple<Double, MobsimAgent> entry : transitWalkList) {
			MobsimAgent agent = entry.getSecond();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		transitWalkList.clear();
		
		for (Tuple<Double, MobsimAgent> entry : bsWalkList) {
			MobsimAgent agent = entry.getSecond();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		bsWalkList.clear();

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
			QSim qsim = new QSim(scenario, eventsManager); //public transport implementation Hebenstreit
			TransitQSimEngine tqsim = new TransitQSimEngine(qsim);
			tqsim.afterSim();
			eventsManager.processEvent(new PersonStuckEvent(now, agent.getId(), agent.getDestinationLinkId(), agent.getMode()));
		}
		ptList.clear();	
		trans.afterSim();
		teleport.afterSim();
	}
		
	@Override
	public void setInternalInterface(InternalInterface internalInterface) {
		this.internalInterface = internalInterface;
		QSim qsim = new QSim(scenario, eventsManager); //public transport implementation Hebenstreit
		TransitQSimEngine tqsim = new TransitQSimEngine(qsim);
		tqsim.setInternalInterface(internalInterface);
		teleport.setInternalInterface(internalInterface);
		trans.setInternalInterface(internalInterface);
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

	@Override
	public void insertAgentsIntoMobsim() 
	{
	 System.out.println("Test");
	}

}
