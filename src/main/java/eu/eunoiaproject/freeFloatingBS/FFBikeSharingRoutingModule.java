package eu.eunoiaproject.freeFloatingBS;

import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import org.matsim.core.mobsim.qsim.agents.BSRunner;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.CreateSubtrips;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BSAtt;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BSAttribsAgent;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingBikes;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.EmptyStageActivityTypes;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.Facility;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * a {@link RoutingModule} for bike sharing trips including public transport trips.
 * Bike sharing trips are composed of an access walk
 * a bike part, and an egress walk and will be combined with pt - if the bs-stations 
 * are too far away from a Facility
 *
 * @author hebenstreit
 */
public class FFBikeSharingRoutingModule implements RoutingModule {
	private final StageActivityTypes stageTypes = EmptyStageActivityTypes.INSTANCE;
	private final BikeSharingBikes allBikes;
	Scenario scenario;
	CreateSubtrips c;
	int varifyer = 0;
	public final RoutingModule bsRouting;
	public final RoutingModule bsWalkRouting;
	public final RoutingModule ptRouting;
	public final RoutingModule ebs2Routing;
	private static final Logger log = Logger.getLogger(FFBikeSharingRoutingModule.class);
	
	/***************************************************************************/
	@Inject
	public FFBikeSharingRoutingModule(
			final Scenario scenario,
			@Named( TransportMode.pt )
			final RoutingModule ptRouting,
			@Named (EBConstants.BS_BIKE_FF)
			final RoutingModule bsRouting,
			@Named (EBConstants.BS_WALK_FF)
			final RoutingModule bsWalkRouting,
			@Named (EBConstants.MODE_FF)
			final RoutingModule ebsRouting)
	/***************************************************************************/
	{
		this(	scenario,
				(BikeSharingBikes)scenario.getScenarioElement(BikeSharingBikes.ELEMENT_NAME),
				ptRouting,
				bsRouting,
				bsWalkRouting,
				ebsRouting);
	
		this.scenario = scenario; //TODO: RADIEN RICHTIG STELLEN
	}


	/***************************************************************************/
	public FFBikeSharingRoutingModule(
			Scenario scenario,
			final BikeSharingBikes bikeSharingBikes,
			final RoutingModule ptRouting,
			final RoutingModule bsRouting,
			final RoutingModule bsWalkRouting,
			final RoutingModule ebs2Routing) 
	/***************************************************************************/
	{
		this.allBikes = bikeSharingBikes;
		this.ptRouting = ptRouting;
		this.bsRouting = bsRouting;
		this.bsWalkRouting = bsWalkRouting;
		this.ebs2Routing = ebs2Routing;
		this.c = new CreateSubtrips();
		this.scenario = scenario;
	}
	
	public List<PlanElement> getMyTrip( //TODO: Hebenstreit
			List<PlanElement> first,
			PlanElement second,
			List<PlanElement> third,
			PlanElement fourth,
			List<PlanElement> trip)	
	{
		trip.addAll(first);
		trip.add(second);
		trip.addAll(third);
		trip.add(fourth);
		
		return trip;

	}
	
	
	/***************************************************************************/
	/**This method defines 4 main options for (e-)bike-sharing: 
	 * #A [ bike-sharing only (bike or e-bike sharing) (1) ],
	 * #B [ public transport usage because BS-Station too far from Facility
	 * PT-BS (2), BS-PT (3), PT-BS-PT (4) ]
	 * #C [ if intermodal is used: 
	 * public transport usage because total-trip-length too long for cycling,   
	 * trips may deviate from those 4 main options]     */
	@Override                      
	public List<PlanElement> calcRoute(
			Facility fromFacility,
			Facility toFacility,
			double departureTime,
			Person person) 
	/***************************************************************************/
	{
		BSRunner runner = new BSRunner();
		List<PlanElement> trip = new ArrayList<PlanElement>();
		
		Coord from = fromFacility.getCoord();
		Coord to = toFacility.getCoord();
		double distanceAA = CoordUtils.calcEuclideanDistance(from, to);
		distanceAA = distanceAA * 2 / 3;


		Activity act = null; //Hebenstreit
		
		ChooseBikeToTake cbtt = new ChooseBikeToTake(scenario);
		BikesFF bike = cbtt.chooseNearestBikeFF(scenario, fromFacility.getCoord(), distanceAA);
		
		if (bike == null)
		{
			//change trip to public transport trip
			trip = c.createPtSubtrip(fromFacility, toFacility, departureTime, person, scenario, ptRouting);
			if (trip == null)
			{
				trip = c.createWalkBikeSubtrip(fromFacility, toFacility, departureTime, person, bsWalkRouting);
			}
			return trip;
		}
		Coord bikeCoord = bike.getCoordinate();
		Coord startCoord = fromFacility.getCoord();
		
		double distance = NetworkUtils.getEuclideanDistance(bikeCoord, startCoord);
		
		BSAtt att = BSAttribsAgent.getPersonAttributes( person, scenario);
		if  (distance > att.maxSearchRadius)
		{
			//change trip to public transport trip
			trip = c.createPtSubtrip(fromFacility, toFacility, departureTime, person, scenario, ptRouting);
			if (trip == null)
			{
				trip = c.createWalkBikeSubtrip(fromFacility, toFacility, departureTime, person, bsWalkRouting);
			}
			return trip;
		}
		
		else 
		{
			FFDummyFacility dummy = new FFDummyFacilityImpl(bike.getBikeId(), bike.getCoordinate(), bike.getLinkId());
			
				List<PlanElement> firstLeg = c.createWalkBikeSubtrip(fromFacility,dummy,departureTime,person,bsWalkRouting);
				double departureTimeTempA = departureTime + ((Leg) firstLeg.get(0)).getTravelTime() + 180; //TODO:Hebenstreit
				PlanElement second = CreateSubtrips.createInteractionFF(dummy, firstLeg, departureTimeTempA-180);
				List<PlanElement> thirdLeg = c.createWalkBikeSubtrip(dummy,toFacility,departureTimeTempA,person,bsRouting);
				Leg thirdL = (Leg)thirdLeg;
				PlanElement fourth = CreateSubtrips.createInteractionFF(dummy, thirdLeg, thirdL.getDepartureTime()+thirdL.getTravelTime());
				
				trip = getMyTrip(firstLeg, second, thirdLeg, fourth,trip);
		}
		
		return trip;
	}
	

	/***************************************************************************/
	@Override
	public StageActivityTypes getStageActivityTypes() 
	/***************************************************************************/
	{
		return stageTypes;
	}
	
}

