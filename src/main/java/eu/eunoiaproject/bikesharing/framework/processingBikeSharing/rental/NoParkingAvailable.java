package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.eunoiaproject.bikesharing.examples.example03configurablesimulation.BikeSharingConfigGroup;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.events.AgentStartsWaitingForFreeBikeSlotEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentChoseNewStationEvent;
import eu.eunoiaproject.bikesharing.framework.events.NoParkingSpaceEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.BikeSharingContext;

import org.matsim.core.mobsim.qsim.agents.BSRunner;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.BikeSharingStationChoice;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.CalcProbability;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BSAtt;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BSAttribsAgent;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeAgent;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.EBikeSharingConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.StationAndType;

public class NoParkingAvailable {
	
	private final static Logger log = Logger.getLogger(NoParkingAvailable.class);
	
	/***************************************************************************/
	/**
	 * Hebenstreit: If no BikeParking was available the fist time, this method should 
	 * be called. It searches for an other bs-station, if not found, the agent
	 * is considered to wait 3 times 5 minutes - so at maximum 15 minutes.
	 * The Agent will be aborted (PersonStuck) - if no bike-station with available 
	 * Parking is available after 15 minutes of waiting
	 * - returns 0 if there is no new Parking Available, but the agent still waits
	 * - returns 1 if the route was aborted
	 * - returns 2 if there was a new Station with Available Parking found**/
	public static void noParkingAvailable(
		  BikeSharingFacility station,
		  Activity nextAct,
		  double now,
		  BasicPlanAgentImpl basicAgentDelegate,
		  BikeSharingContext bikeSharingContext )
	/***************************************************************************/
	{
		Scenario scenario = bikeSharingContext.getqSim().getScenario() ;

		BSRunner runner = new BSRunner();
		runner.planComparison(basicAgentDelegate);
		
		LinkNetworkRouteImpl route = (LinkNetworkRouteImpl) 
				((Leg)basicAgentDelegate.getNextPlanElement()).getRoute();
		BikeSharingStationChoice bsChoice = new BikeSharingStationChoice(scenario);

		BSAtt att = BSAttribsAgent.getPersonAttributes( basicAgentDelegate.getPerson(), scenario);
		double searchRadius =  att.searchRadius;
		double maxSearchRadius =   att.maxSearchRadius;
		double maxSearchRadiusPt =   att.ptSearchRadius;
		double maxDistanceForBikeTrip = att.maxBSTripLength;

		StationAndType[] sat = new StationAndType[2];

		if (nextAct.getCoord() == null)
		{
			System.out.println("NoParkingAvailable - hier toFac Coord NULL");
		}
		ActivityFacilitiesFactory ff = scenario.getActivityFacilities().getFactory();
//		ActivityFacility actFac = new ActivityFacilityImpl (nextAct.getFacilityId(), nextAct.getCoord(), nextAct.getLinkId());
		ActivityFacility actFac = ff.createActivityFacility( nextAct.getFacilityId(), nextAct.getCoord(), nextAct.getLinkId());
		sat = bsChoice.getStationsDuringSim((Facility)station, actFac, searchRadius,
				maxSearchRadius, basicAgentDelegate.getPerson(), now, basicAgentDelegate);

		StationAndType newChoiceEnd = null;
		if (sat != null)
		{
			newChoiceEnd = sat[1];
		}
		double likely = 0;
		String whatToChoose = "";
		
		if (newChoiceEnd != null)
		{
			EBikeSharingConfigGroup ebConfig = (EBikeSharingConfigGroup)scenario.getConfig().getModule("bikeSharingFacilities");
			boolean useProbability = Boolean.parseBoolean(ebConfig.getUseProbability());
			if (useProbability)
			{
				double take = Double.parseDouble(ebConfig.getProbabilityTake());
				double park = Double.parseDouble(ebConfig.getProbabilityReturn());
				CalcProbability prob = new CalcProbability(scenario);
				likely = prob.getProbabilityForStationChangeReturn(newChoiceEnd.station, take, park);
				whatToChoose = NotAvailable.randomChoiceFirstly(false, newChoiceEnd.station, likely, station);
			}
			else
			{
				whatToChoose = NotAvailable.randomChoiceFirstly(false, newChoiceEnd.station, 1, station);
			}
		}

		if ((newChoiceEnd == null) || (whatToChoose.equals("wait"))|| newChoiceEnd.station == station) //whatToChoose is "waiting", there is no other choice option
		{
			
			whatToChoose = "wait";
			/*if (newChoiceEnd == null)
			{
				log.warn("New Choice Return also null:  " + basicAgentDelegate.getPerson().getId() + " _ " + whatToChoose);
			}
			else
			{
				log.info("The chosen strategy is: " + whatToChoose);
			}*/
			
			Activity bsInteractPe = (Activity) basicAgentDelegate.getCurrentPlanElement();
			bsInteractPe.setEndTime(now);
			Activity bsInteractWait = new ActivityImpl(bsInteractPe.getType(), bsInteractPe.getLinkId());
			Leg leg = (Leg) basicAgentDelegate.getNextPlanElement();
			leg.setDepartureTime(15*60);
			basicAgentDelegate.getEvents().processEvent(new NoParkingSpaceEvent(
					now, route.getStartLinkId(), "eBikeSharing", 
					basicAgentDelegate.getPerson()));
			basicAgentDelegate.getEvents().processEvent(new AgentStartsWaitingForFreeBikeSlotEvent(
					now, basicAgentDelegate.getId(), Id.create(station.getId().toString(), ActivityFacility.class)));
			
			List <PlanElement> list = basicAgentDelegate.getCurrentPlan().getPlanElements();
			//add 5 times activities of waiting - for every activity add 3 minutes Time
			Coord coordInteract = bsInteractPe.getCoord();
			bsInteractWait.setType("wait");
			bsInteractWait.setEndTime(now + 3*60);
			bsInteractWait.setStartTime(now);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
			Activity bsInteractWait2 = new ActivityImpl(bsInteractPe.getType(),coordInteract, bsInteractPe.getLinkId());
			bsInteractWait2.setEndTime(now + 6*60);
			bsInteractWait2.setType("wait");
			bsInteractWait2.setStartTime(now + 3*60);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
			Activity bsInteractWait3 = new ActivityImpl(bsInteractPe.getType(),coordInteract, bsInteractPe.getLinkId());
			bsInteractWait3.setEndTime(now + 9*60);
			bsInteractWait3.setType("wait");
			bsInteractWait3.setStartTime(now + 6*60);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
			Activity bsInteractWait4 = new ActivityImpl(bsInteractPe.getType(),coordInteract, bsInteractPe.getLinkId());
			bsInteractWait4.setEndTime(now + 12*60);
			bsInteractWait4.setType("wait");
			bsInteractWait4.setStartTime(now + 9*60);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
			Activity bsInteractWait5 = new ActivityImpl(bsInteractPe.getType(),coordInteract, bsInteractPe.getLinkId());
			bsInteractWait5.setEndTime(now + 15*60);
			bsInteractWait5.setType("wait");
			bsInteractWait5.setStartTime(now + 12*60);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
//			BikesharingPersonDriverAgentImpl agent2 = new BikesharingPersonDriverAgentImpl(basicAgentDelegate);
//			final int currentPlanElementIndex= agent2.getCurrentPlanElementIndex(basicAgentDelegate) ;
			final int currentPlanElementIndex = basicAgentDelegate.getCurrentPlan().getPlanElements().indexOf( basicAgentDelegate.getCurrentPlanElement() ) ;
			list.add( currentPlanElementIndex +1, bsInteractWait5 );
			list.add( currentPlanElementIndex +1, bsInteractWait4 );
			list.add( currentPlanElementIndex +1, bsInteractWait3 );
			list.add( currentPlanElementIndex +1, bsInteractWait2 );
			list.add( currentPlanElementIndex +1, bsInteractWait );
			WaitingListHandling.addAgentToWaitingListOfStation(scenario, station, basicAgentDelegate, false, now);
		}
		
		if (whatToChoose.equals("chooseNewStation"))
		{
			basicAgentDelegate.getEvents().processEvent(new AgentChoseNewStationEvent(
					now, basicAgentDelegate.getId(), station.getId()));
//			List<PlanElement> planElements = basicAgentDelegate.getCurrentPlan().getPlanElements();
			List<PlanElement> planElements = basicAgentDelegate.getCurrentPlan().getPlanElements();
			int indexOfInsertion =
					planElements.indexOf(basicAgentDelegate.getCurrentPlanElement()) + 1;
			
			//route = (LinkNetworkRouteImpl) 
			//		((Leg)basicAgentDelegate.getNextPlanElement()).getRoute();
			scenario = basicAgentDelegate.getScenario() ;
			Network network = scenario.getNetwork();
			
			final List<PlanElement> trip = new ArrayList<PlanElement>();

			double travelTimeTotal = 0;
		
			planElements.remove(basicAgentDelegate.getNextPlanElement());			  
			//entfernt bs_walk
			Activity actAfterBs = (Activity)basicAgentDelegate.getNextPlanElement(); 
			//Aktivität
			planElements.remove(basicAgentDelegate.getNextPlanElement()); 			  
			//entfernt nachfolgende Aktivität
	
			
			//füge BS-Leg und Activität BS-Interaction ein

			Link startLink = network.getLinks().get(station.getLinkId());
			Link stationLink = network.getLinks().get(newChoiceEnd.station.getLinkId());
			Link endLink = network.getLinks().get(actAfterBs.getLinkId());
			
			//System.out.println(endLink);

			//BIKE_LEG
			Leg p2 ;
			if (station.getStationType().equals("e"))
			{
				p2 = (Leg)BSRunner.createLeg(startLink, stationLink, EBConstants.BS_E_BIKE, now, basicAgentDelegate, bikeSharingContext );
			}
			
			else
			{
				p2 = (Leg)BSRunner.createLeg(startLink, stationLink, EBConstants.BS_BIKE, now, basicAgentDelegate, bikeSharingContext );
			}
			
			p2.setDepartureTime(now);
			trip.add(p2); 
			
			//BS_INTERACTION		
			Activity a2 = (Activity) BSRunner.createInteraction(newChoiceEnd.station, p2.getDepartureTime()+p2.getTravelTime(), EBConstants.TIME_RETURN, "_r");
			a2.setStartTime(p2.getTravelTime() + p2.getDepartureTime());
			a2.setEndTime(a2.getStartTime()+ EBConstants.TIME_RETURN);	
			trip.add(a2);
			
			//WALK_LEG	
			Leg p3 = (Leg)BSRunner.createLeg(stationLink, endLink , EBConstants.BS_WALK, now, basicAgentDelegate, bikeSharingContext );
			p3.setDepartureTime(a2.getEndTime());
			trip.add(p3);
			
			travelTimeTotal = p2.getTravelTime() + p3.getTravelTime() + EBConstants.TIME_RETURN;
			
			actAfterBs.setStartTime(now+travelTimeTotal);
			
			//shall insert a new bike-sharing-trip
			trip.add(actAfterBs);
			//shall insert a new bike-sharing-trip
			planElements.addAll(indexOfInsertion, trip);
			
			//create new Plan
			BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( scenario.getConfig(), BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class ) ;
			//switch( bikeSharingConfig.getRunType() ) {
			//	case standard:
					//			for (int i = 0; i < planElements.size(); i++ )
					//			{
					//				basicAgentDelegate.setPlanElement(i, planElements.get(i));
					//			}
			//		throw new RuntimeException("this is not possible.  But I also do not see why it should be needed, since the code already operates on the agent's " +
			//							     "planElements. kai, apr'19") ;
			//	case debug:
			//		break;
			//	default:
			//		throw new RuntimeException("not implemented") ;
			//}

			Id<Person> pers = basicAgentDelegate.getPerson().getId();
			log.info("Agent with ID:;" + basicAgentDelegate.getPerson().getId()+ ";did not get a Parking Spot and choose -->;" + whatToChoose);
			runner.planComparison(basicAgentDelegate);
		}
	}
}
