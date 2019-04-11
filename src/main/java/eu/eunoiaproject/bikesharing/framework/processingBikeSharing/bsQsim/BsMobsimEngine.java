package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim;

import org.apache.log4j.Category;
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
import org.matsim.core.mobsim.qsim.agents.PlanBasedDriverAgentImpl;
import org.matsim.core.mobsim.qsim.agents.TransitAgent;
import org.matsim.core.mobsim.qsim.agents.TransitAgentImpl;
import org.matsim.core.mobsim.qsim.interfaces.DepartureHandler;
import org.matsim.core.mobsim.qsim.interfaces.MobsimEngine;
import org.matsim.core.mobsim.qsim.pt.AbstractTransitDriverAgent;
import org.matsim.core.mobsim.qsim.pt.DefaultTransitDriverAgentFactory;
import org.matsim.core.mobsim.qsim.pt.PTPassengerAgent;
import org.matsim.core.mobsim.qsim.pt.TransitDriverAgentImpl;
import org.matsim.core.mobsim.qsim.pt.TransitQSimEngine;
import org.matsim.core.mobsim.qsim.pt.TransitStopAgentTracker;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetsimEngine;
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
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.Bikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikesE;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.EBikeSharingConfigGroup;
import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.arrivalEvents.BikeArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.arrivalEvents.BsArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.arrivalEvents.BsWalkArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.arrivalEvents.CarArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.arrivalEvents.FfBsArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.arrivalEvents.PTArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.arrivalEvents.TransitWalkArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.arrivalEvents.WalkArrivalEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.relocation.RelocationHandler;
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
public final class BsMobsimEngine implements MobsimEngine {
	
	Map<String,TravelDisutilityFactory> travelDisutilityFactories; 
	Map<String,TravelTime> travelTimes;
	LeastCostPathCalculatorFactory pathCalculatorFactory;
	private Collection<MobsimAgent> ptDrivers;
	private static final Logger log = Logger.getLogger( BsMobsimEngine.class ) ;
	private Scenario scenario;
	private EventsManager eventsManager;
	private final boolean withTravelTimeCheck;
	TeleportationEngine teleport;
	TransitQSimEngine trans;
	
	@Inject
	public BsMobsimEngine(Scenario scenario, EventsManager eventsManager, 
			LeastCostPathCalculatorFactory pathCalculatorFactory,
			Map<String,TravelDisutilityFactory> travelDisutilityFactories, 
			Map<String,TravelTime> travelTimes, QSim qsim) {
		this.scenario = scenario;
		this.travelDisutilityFactories = travelDisutilityFactories;
		this.travelTimes = travelTimes;
		this.eventsManager = eventsManager;
		this.pathCalculatorFactory = pathCalculatorFactory;
		scenario.getConfig().qsim().setUsingTravelTimeCheckInTeleportation(false);
		this.withTravelTimeCheck = scenario.getConfig().qsim().isUsingTravelTimeCheckInTeleportation() ;
		this.trans = new TransitQSimEngine(qsim);
		this.teleport = new TeleportationEngine(scenario, eventsManager);


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
	public void doSimStep(double time) {
		
	}


	@Override
	public void afterSim() {
	}

	@Override
	public void setInternalInterface(InternalInterface internalInterface) {
		}

}
