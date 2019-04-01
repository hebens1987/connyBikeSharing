
package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice;


import eu.eunoiaproject.bikesharing.framework.EBConstants;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.router.RoutingModule;
import org.matsim.facilities.Facility;
import java.util.List;


/**
 * a {@link RoutingModule} for bike sharing trips including public transport trips.
 * Bike sharing trips are composed of an access walk
 * a bike part, and an egress walk and will be combined with pt - if the bs-stations 
 * are too far away from a Facility
 *
 * @author hebenstreit
 */
public class CreateSubtrips {
	
	
	private static final Logger log = Logger.getLogger(CreateSubtrips.class);
	
	
	/***************************************************************************/
	public List<PlanElement> createBikeSharingSubtrip(
			final Facility startStation,
			final Facility endStation,
			double departureTime,
			final Person person,
			RoutingModule ebRouting,
			RoutingModule bsWalkRouting) 
	/***************************************************************************/
	{
		final List<? extends PlanElement> trip =
			ebRouting.calcRoute(
					startStation,
					endStation,
					departureTime,
					person );
		
		if (trip == null)
		{
			bsWalkRouting.calcRoute(
					startStation,
					endStation,
					departureTime,
					person );
		}
		
		return (List<PlanElement>) trip;
	}
	
	/***************************************************************************/
	public List<PlanElement> createPtSubtrip(
			final Facility startStation,
			final Facility endStation,
			double departureTime,			
			final Person person, 
			Scenario scenario,
			RoutingModule ptRouting) 
	/***************************************************************************/
	{
		List<? extends PlanElement> trip = ptRouting.calcRoute(startStation, endStation,  departureTime, person);
		if (trip == null)
		{
			return null;
		}
		return (List<PlanElement>) trip;
	}


	/***************************************************************************/
	public static PlanElement createInteractionReturnBS( 
			final Facility facility, List<? extends PlanElement> firstLeg, double now) 
	/***************************************************************************/{
		String interaction = EBConstants.INTERACTION_TYPE_BS + "_r";
		PlanElement firstLeg2 = firstLeg.get(firstLeg.size()-1);
		if (firstLeg2 instanceof Leg)
		{
			Leg leg2 = (Leg)firstLeg2;
			
		}

		final Activity actE = new ActivityImpl( interaction , facility.getCoord() );
		actE.setType(interaction);
		actE.setMaximumDuration( EBConstants.TIME_RETURN ); //30 sec = 1/2 min
		((ActivityImpl) actE).setLinkId( facility.getLinkId() );
		((ActivityImpl) actE).setFacilityId( facility.getId() );;
		((ActivityImpl) actE).setStartTime(now);
		((ActivityImpl) actE).setEndTime(now + EBConstants.TIME_RETURN);
		return actE;	
		
	}
	
	/***************************************************************************/
	public static PlanElement createInteractionTakeBS( 
			final Facility facility, List<? extends PlanElement> firstLeg, double now ) 
	/***************************************************************************/{
		
		String interaction = EBConstants.INTERACTION_TYPE_BS + "_t";
		PlanElement firstLeg2 = firstLeg.get(firstLeg.size()-1);
		if (firstLeg2 instanceof Leg)
		{
			Leg leg2 = (Leg)firstLeg2;
			
		}
		
		final Activity actE = new ActivityImpl( interaction , facility.getCoord() );
		actE.setType(interaction);
		actE.setMaximumDuration( EBConstants.TIME_TAKE ); // 120 sec = 2 min
		((ActivityImpl) actE).setLinkId( facility.getLinkId() );
		((ActivityImpl) actE).setFacilityId( facility.getId() );
		((ActivityImpl) actE).setStartTime(now);
		((ActivityImpl) actE).setEndTime(now + EBConstants.TIME_TAKE);
		return actE;	
		
	}
	
	/***************************************************************************/
	public static PlanElement createInteractionFF( 
			final Facility facility, List<? extends PlanElement> firstLeg, double now ) 
	/***************************************************************************/{
		
		String interaction = EBConstants.INTERACTION_TYPE_FF;
		PlanElement firstLeg2 = firstLeg.get(firstLeg.size()-1);
		
		final Activity actE = new ActivityImpl( interaction , facility.getCoord() );
		actE.setType(interaction);
		actE.setMaximumDuration( 90 );
		((ActivityImpl) actE).setLinkId( facility.getLinkId() );
		((ActivityImpl) actE).setFacilityId( facility.getId() );
		((ActivityImpl) actE).setCoord(facility.getCoord() );
		((ActivityImpl) actE).setStartTime(now);
		((ActivityImpl) actE).setEndTime(now + 90);
		return actE;	

	}
	
	
	/***************************************************************************/
	public static PlanElement createInteractionBS( 
			final Facility facility, String str, double duration) 
	/***************************************************************************/{
		
		String interaction = EBConstants.INTERACTION_TYPE_BS + str;
		
		final Activity actE = new ActivityImpl( interaction , facility.getCoord() );
		actE.setType(interaction);
		actE.setMaximumDuration( duration); //120 seconds = 2 min
		((ActivityImpl) actE).setLinkId( facility.getLinkId() );
		((ActivityImpl) actE).setFacilityId( facility.getId() );
		return actE;	
	}
	
	/***************************************************************************/
	public static PlanElement createInteractionPT( 
			final Coord coord, Id<Link> linkId, double now) 
	/***************************************************************************/{
		
		String interaction = "pt interaction";
		
		final Activity actE = new ActivityImpl( interaction , coord, linkId);
		((ActivityImpl) actE).setEndTime(now);
		((ActivityImpl) actE).setStartTime(now);
		actE.setMaximumDuration(0);//x seconds

		return actE;
	}
	/***************************************************************************/
	public List<PlanElement> createWalkBikeSubtrip(
			final Facility fromFacility,
			final Facility toFacility,
			double departureTime,
			final Person person,
			RoutingModule routing) 
	/***************************************************************************/
	{
		final List<? extends PlanElement> trip =
			routing.calcRoute(
					fromFacility,
					toFacility,
					departureTime,
					person );

		return (List<PlanElement>) trip;
	}
	
	/***************************************************************************/
	public List<PlanElement> createFFSubtrip(
			final Facility fromFacility,
			final Facility toFacility,
			double departureTime,
			final Person person,
			RoutingModule routing)
	/***************************************************************************/
	{
		final List<? extends PlanElement> trip =
			routing.calcRoute(
					fromFacility,
					toFacility,
					departureTime,
					person );

		return (List<PlanElement>) trip;
	}
	
}

