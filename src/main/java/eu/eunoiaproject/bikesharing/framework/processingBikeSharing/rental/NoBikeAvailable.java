/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;
import org.matsim.core.mobsim.qsim.agents.TransitAgentImpl;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.events.AgentStartsWaitingForBikeEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentChangesLegModeEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentChoseNewStationEvent;
import eu.eunoiaproject.bikesharing.framework.events.NoVehicleBikeSharingEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.BikeSharingContext;
import org.matsim.core.mobsim.qsim.agents.BSRunner;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.BikeSharingStationChoice;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.CalcProbability;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BSAtt;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BSAttribsAgent;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.EBikeSharingConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.StationAndType;

public class NoBikeAvailable 
{
	private final static Logger log = Logger.getLogger(NoBikeAvailable.class);
	
	/***************************************************************************/
	/**
	 * Hebenstreit: If no Bike was available the fist time, this method should 
	 * be called. It searches for an other bs-station, if not found, the agent
	 * is considered to wait 3 times 5 minutes - so at maximum 15 minutes.
	 * The Agent will be randomly set to walk or pt - if no bike-station is 
	 * available after 15 minutes of waiting
	 * - returns 0 if there is no new Bike Available, but the agent still waits
	 * - returns 1 if the route was reset to a walk or pt trip
	 * - returns 2 if there was a new Station with Available Bikes found**/
	@SuppressWarnings("unchecked")
	public static void noBikeAvailable(
		  BikeSharingFacility station,
		  Activity nextAct,
		  double now,
		  BasicPlanAgentImpl basicAgentDelegate, BikeSharingContext bikeSharingContext )
	//setzt den Plan anders, sodass der Agent rerouting macht oder wartet
	//wird aufgerufen wenn das Aktuelle Element Bs-Interaction ist
	/***************************************************************************/
	{
		
		Scenario scenario = bikeSharingContext.getqSim().getScenario() ;

		//if a station change was already conducted once
		// 1:1 chance for wait and change legMode
		BSRunner runner = new BSRunner();
		runner.planComparison(basicAgentDelegate);
//		int planIndex = basicAgentDelegate.getCurrentPlanElementIndex();
//		BikesharingPersonDriverAgentImpl agent = new BikesharingPersonDriverAgentImpl(basicAgentDelegate);
//		int planIndex = agent.getCurrentPlanElementIndex(basicAgentDelegate) ;
		int planIndex = basicAgentDelegate.getCurrentPlan().getPlanElements().indexOf( basicAgentDelegate.getCurrentPlanElement() ) ;
		List<PlanElement> planElements = basicAgentDelegate.getCurrentPlan().getPlanElements();
		//PlanElement peX = planElements.get(planIndex);
		String modeChangeOrWait = "wait";
		if (planIndex > 3)
		{
			//as there already was a station change - only a mode Change is possible anymore
			// waiting and mode change is shared 50/50
			if (planElements.get(planIndex-3) instanceof Leg)
			{
				Leg twoLegsBefore = (Leg)planElements.get(planIndex-3);
				
				if ((twoLegsBefore.getMode().equals(TransportMode.walk))||(twoLegsBefore.getMode().equals(EBConstants.BS_WALK)))
				{
					double desider = Math.random();
					modeChangeOrWait = EBConstants.WAIT;
					if (station.getWaitingToTakeBike() == null)
					{
						if (desider <= 0.5)
						{
							modeChangeOrWait = "changeMode";
						}
					}
					
					else if (station.getWaitingToTakeBike().size() > 1)
					{
							modeChangeOrWait = "changeMode";
					}
					
					else if (station.getWaitingToTakeBike().size() == 1)
					{
						if (desider <= 0.75)
						modeChangeOrWait = "changeMode";
					}
					
					else 
					{
						modeChangeOrWait = "wait";
					}
					runner.planComparison(basicAgentDelegate);
				}
			}
		}
		
		double waitingTime = 0;
		BikeSharingStationChoice bsChoice = new BikeSharingStationChoice(scenario);
		BSAtt att = BSAttribsAgent.getPersonAttributes( basicAgentDelegate.getPerson(), scenario);
		double searchRadius =  att.searchRadius;
		double maxSearchRadius =  att.maxSearchRadius;
		
		StationAndType[] sat = new StationAndType[2];
		if (nextAct.getCoord() == null)
		{
			System.out.println("NoBikeAvailable - hier toFac Coord NULL");
		}
//		ActivityFacility actFac = new ActivityFacilityImpl(nextAct.getFacilityId(), nextAct.getCoord(), nextAct.getLinkId());
		ActivityFacilitiesFactory ff = scenario.getActivityFacilities().getFactory();
		ActivityFacility actFac = ff.createActivityFacility( nextAct.getFacilityId(), nextAct.getCoord(), nextAct.getLinkId() ) ;
		sat = bsChoice.getStationsDuringSim((Facility)station, actFac, searchRadius, 
				maxSearchRadius, basicAgentDelegate.getPerson(), now, basicAgentDelegate);
		StationAndType newChoiceStart;
		StationAndType newChoiceEnd;
		
		if (sat == null)
		{
 			newChoiceStart = null;
			newChoiceEnd = null;
		}
		
		else
		{
			newChoiceStart = sat[0];
			newChoiceEnd = sat[1];
		}
		
		
		Network network = scenario.getNetwork();
		
		//es wurde während der Wartezeit geprüft ob ein Fahrrad verfügbar wurde...
		if (basicAgentDelegate.getNextPlanElement() instanceof Activity)
		{
			System.out.println("ERROR - 125 - NoBikeAvailable.java");
		}
		runner.planComparison(basicAgentDelegate);
		LinkNetworkRouteImpl route = (LinkNetworkRouteImpl) 
				((Leg)basicAgentDelegate.getNextPlanElement()).getRoute();
				
		final List<PlanElement> trip = new ArrayList<PlanElement>();
		
		double travelTimeTotal = 0;
		
		Activity noBSAct = null;
		
		int indexOfInsertion = 
				planElements.indexOf(basicAgentDelegate.getCurrentPlanElement())+1;
		
		//----- here we implement the drawn choice strategy for the agent -----
		String whatToChoose = null;

		if (whatToChoose == null)
		{
			if ((newChoiceStart == null) || (newChoiceEnd == null) || (newChoiceEnd == newChoiceStart)|| newChoiceStart.station == station)
				
			{
				whatToChoose = modeChangeOrWait;
				//log.warn("New Choice ( = Start Station) also null:  " + basicAgentDelegate.getPerson().getId());
				//log.info("The chosen strategy is: " + whatToChoose);
			}
			
			else
			{
				final EBikeSharingConfigGroup ebConfig = (EBikeSharingConfigGroup)scenario.getConfig().getModule(EBikeSharingConfigGroup.GROUP_NAME);
				boolean useProbability = Boolean.parseBoolean(ebConfig.getValue("useProbability"));
				if (useProbability)
				{
					CalcProbability prob = new CalcProbability(scenario);
					double take = Double.parseDouble(ebConfig.getProbabilityTake());
					double park = Double.parseDouble(ebConfig.getProbabilityReturn());
					double likely = prob.getProbabilityForStationChangeTake(newChoiceStart.station, newChoiceEnd.station, take, park);
					whatToChoose = NotAvailable.randomChoiceFirstly(true, newChoiceStart.station, likely, station);
				}
				else
				{
					whatToChoose = NotAvailable.randomChoiceFirstly(true, newChoiceStart.station, 1, station);
				}
				//log.info("The chosen strategy is: " + whatToChoose);
			}
		}
		//----------------------------------------------------------------------
		
		if (whatToChoose.equals("wait"))
		{
			if (newChoiceStart != null)
			if (newChoiceStart.station != null)
			{
				if (newChoiceStart.station.getWaitingToTakeBike() != null)
				{
					double desider = Math.random();
				
					if (newChoiceStart.station.getWaitingToTakeBike().size() > 1)
					{
						if (desider <= 0.5)
							whatToChoose = "changeMode";
					}
				}
			}
		}
		
		if (whatToChoose.equals("chooseNewStation"))
			//if there was a new station found insert the new bike-sharing-trip 
		{	
			PlanElement pe = basicAgentDelegate.getCurrentPlanElement();
			if (pe instanceof Activity)
			{
				Activity actPe = (Activity)pe;
				//System.out.println("now: "+ now);
				//System.out.println("act: "+ actPe.getEndTime());
				actPe.setEndTime(now);
			}
			basicAgentDelegate.getEvents().processEvent(new AgentChoseNewStationEvent(
					now, basicAgentDelegate.getId(), station.getStationId()));
			
			scenario = basicAgentDelegate.getScenario() ;
			Link stationLink1 = network.getLinks().get(newChoiceStart.station.getLinkId());
			Link stationLink2 = network.getLinks().get(newChoiceEnd.station.getLinkId());
			Link startLink = network.getLinks().get(route.getStartLinkId());
			
			noBSAct = BSRunner.removeBSPlanElements(planElements, basicAgentDelegate );
			
			Link endLink = network.getLinks().get(noBSAct.getLinkId()); 
			
			//WALK_LEG
			Leg p0 = (Leg)BSRunner.createLeg(startLink, stationLink1 , EBConstants.BS_WALK, now, basicAgentDelegate, bikeSharingContext );
			p0.setDepartureTime(now);
			trip.add(p0); 
			
			//BS_INTERACTION
			Activity p1 = (Activity) BSRunner.createInteraction(newChoiceStart.station, p0.getDepartureTime()+p0.getTravelTime(), EBConstants.TIME_TAKE, "_t");
			p1.setStartTime(p0.getTravelTime() + p0.getDepartureTime());
			p1.setEndTime(p1.getStartTime()+  EBConstants.TIME_TAKE);
			trip.add(p1); //new station, as old one had no bikes
			
			//BIKE_LEG
			Leg p2;
			if (newChoiceStart.station.getStationType().equals("e"))
			{
				p2 = (Leg)BSRunner.createLeg(stationLink1, stationLink2, EBConstants.BS_E_BIKE, p1.getEndTime(), basicAgentDelegate, bikeSharingContext );
			}
			else
			{
				p2 = (Leg)BSRunner.createLeg(stationLink1, stationLink2, EBConstants.BS_BIKE, p1.getEndTime(), basicAgentDelegate, bikeSharingContext );

			}
			p2.setDepartureTime(p1.getEndTime());
			trip.add(p2); 
			
			//BS_INTERACTION		
			Activity p3 = (Activity) BSRunner.createInteraction(newChoiceEnd.station, p2.getDepartureTime()+p2.getTravelTime(), EBConstants.TIME_RETURN, "_r");
			p3.setStartTime(p2.getTravelTime() + p2.getDepartureTime());
			p3.setEndTime(p3.getStartTime()+ EBConstants.TIME_RETURN);	
			trip.add(p3);
			
			//WALK_LEG	
			Leg p4 = (Leg)BSRunner.createLeg(stationLink2, endLink , EBConstants.BS_WALK, p3.getEndTime(), basicAgentDelegate, bikeSharingContext );
			p4.setDepartureTime(p3.getEndTime());
			trip.add(p4);
			
			travelTimeTotal = p0.getTravelTime() + p2.getTravelTime() + p4.getTravelTime() + EBConstants.TIME_TAKE + EBConstants.TIME_RETURN;
		}
	
		else if (whatToChoose.equals("wait"))
		{
			//System.out.println(basicAgentDelegate.getPerson().getId() + " = wait (1)"); //Hebenstreit
			Activity bsInteractPe = (Activity) basicAgentDelegate.getCurrentPlanElement();
			Coord waitCoord = bsInteractPe.getCoord();
			bsInteractPe.setEndTime(now);
			Activity bsInteractWait = new ActivityImpl(bsInteractPe.getType(),waitCoord, bsInteractPe.getLinkId());
			Leg leg = (Leg) basicAgentDelegate.getNextPlanElement();
			leg.setDepartureTime(15*60+1);
			basicAgentDelegate.getEvents().processEvent(new NoVehicleBikeSharingEvent(
					now, route.getStartLinkId(), EBConstants.MODE, 
					basicAgentDelegate.getPerson()));
			basicAgentDelegate.getEvents().processEvent(new AgentStartsWaitingForBikeEvent(
					now, basicAgentDelegate.getId(), Id.create(station.getId().toString(), ActivityFacility.class)));

			List <PlanElement> list = basicAgentDelegate.getCurrentPlan().getPlanElements();
			//add 5 times activities of waiting - for every activity add 3 minutes Time
			bsInteractWait.setType(EBConstants.WAIT);
			bsInteractWait.setStartTime(now);
			bsInteractWait.setEndTime(now+3*60);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
			Activity bsInteractWait2 = new ActivityImpl(bsInteractPe.getType(),waitCoord, bsInteractPe.getLinkId());
			bsInteractWait2.setStartTime(now + 3*60);
			bsInteractWait2.setEndTime(now + 6*60);
			bsInteractWait2.setType(EBConstants.WAIT);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
			Activity bsInteractWait3 = new ActivityImpl(bsInteractPe.getType(),waitCoord, bsInteractPe.getLinkId());
			bsInteractWait3.setStartTime(now + 6*60);
			bsInteractWait3.setEndTime(now + 9*60);
			bsInteractWait3.setType(EBConstants.WAIT);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
			Activity bsInteractWait4 = new ActivityImpl(bsInteractPe.getType(),waitCoord, bsInteractPe.getLinkId());
			bsInteractWait4.setStartTime(now + 9*60);
			bsInteractWait4.setEndTime(now + 12*60);
			bsInteractWait4.setType(EBConstants.WAIT);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
			Activity bsInteractWait5 = new ActivityImpl(bsInteractPe.getType(),waitCoord, bsInteractPe.getLinkId());
			bsInteractWait5.setStartTime(now + 12*60);
			bsInteractWait5.setEndTime(now+ 15*60);
			//bsInteractWait5.setEndTime(now + 15*60+1);
			bsInteractWait5.setType(EBConstants.WAIT);
			bsInteractWait.setFacilityId(bsInteractPe.getFacilityId());
//			final int currentPlanElementIndex = basicAgentDelegate.getCurrentPlanElementIndex();
//			BikesharingPersonDriverAgentImpl agent2 = new BikesharingPersonDriverAgentImpl(basicAgentDelegate);
//			final int currentPlanElementIndex= agent2.getCurrentPlanElementIndex(basicAgentDelegate) ;
			int currentPlanElementIndex = basicAgentDelegate.getCurrentPlan().getPlanElements().indexOf( basicAgentDelegate.getCurrentPlanElement() ) ;
			list.add( currentPlanElementIndex +1, bsInteractWait5 );
			list.add( currentPlanElementIndex +1, bsInteractWait4 );
			list.add( currentPlanElementIndex +1, bsInteractWait3 );
			list.add( currentPlanElementIndex +1, bsInteractWait2 );
			list.add( currentPlanElementIndex +1, bsInteractWait );
			WaitingListHandling.addAgentToWaitingListOfStation(scenario, station, basicAgentDelegate, true, now);
			waitingTime = 15*60;
		}
		
		else if (whatToChoose.equals("changeMode"))
		{
			noBSAct = BSRunner.removeBSPlanElements(planElements, basicAgentDelegate );
			noBSAct.getCoord();
			
			Link test = network.getLinks().get(route.getStartLinkId());
			double dist = CoordUtils.calcEuclideanDistance(noBSAct.getCoord(),test.getFromNode().getCoord());
			Link startLink = network.getLinks().get(route.getStartLinkId());
			Link endLink = network.getLinks().get(noBSAct.getLinkId()); 
			double random = Math.random();
			
			if (dist < 1500)
			{
				//log.warn("Walk_1:  " + basicAgentDelegate.getPerson().getId());
				//WALK_LEG
				//Hebenstreit vor Urlaub: hier muss über basicAgentDelegate auf den aktuellen Plan 
				//zugegriffen und dieser verändert werden - zu prüfen
				Leg p = (Leg)BSRunner.createLeg(startLink, endLink, TransportMode.walk, now, basicAgentDelegate, bikeSharingContext );
				p.setDepartureTime(now);
				trip.add(p); 
				travelTimeTotal = p.getTravelTime();
			}
			
			else if (dist > 3500)
			{
				//log.warn("PT_0:  " + basicAgentDelegate.getPerson().getId());
				List<PlanElement> p0 = BSRunner.createPTLegs(startLink.getCoord(), endLink.getCoord() , now, 
						basicAgentDelegate.getPerson(), scenario, startLink.getId(), endLink.getId(), new TransitAgentImpl(basicAgentDelegate));
				if (p0 == null)
				{
					Leg p0a = (Leg) BSRunner.createLeg(startLink, endLink, TransportMode.walk, now, basicAgentDelegate,
						  bikeSharingContext );
					p0a.setDepartureTime(now);
					trip.add(p0a); 
				}
				else
				{
					((Leg) p0.get(0)).setDepartureTime(now);
					trip.addAll(p0); 
				}
			}
			
			if (random <= 0.5)
			{
				//log.warn("Walk_1:  " + basicAgentDelegate.getPerson().getId());
				//WALK_LEG
				//Hebenstreit vor Urlaub: hier muss über basicAgentDelegate auf den aktuellen Plan 
				//zugegriffen und dieser verändert werden - zu prüfen
				
				Leg p1 = (Leg)BSRunner.createLeg(startLink, endLink , TransportMode.walk, now, basicAgentDelegate, bikeSharingContext );
				p1.setDepartureTime(now);
				trip.add(p1); 
				travelTimeTotal = p1.getTravelTime();
			}
			else
			{
				//PT_LEG
				//log.warn("PT_2:  " + basicAgentDelegate.getPerson().getId());
				List<PlanElement> pe2 = BSRunner.createPTLegs(startLink.getCoord(), endLink.getCoord() , now, basicAgentDelegate.getPerson(), 
						scenario, startLink.getId(), endLink.getId(), new TransitAgentImpl(basicAgentDelegate));
				if (pe2 == null)
				{
					Leg pe2a = (Leg) BSRunner.createLeg(startLink, endLink, TransportMode.walk, now, basicAgentDelegate,
						  bikeSharingContext );
					pe2a.setDepartureTime(now);
					trip.add(pe2a); 
				}
				else
				{
					((Leg) pe2.get(0)).setDepartureTime(now);
					trip.addAll(pe2); 
				}
			}
			runner.planComparison(basicAgentDelegate);
			basicAgentDelegate.getEvents().processEvent(new AgentChangesLegModeEvent(
					now,basicAgentDelegate.getId(), station.getId()));
			
		}
		runner.planComparison(basicAgentDelegate);
		//actualize the startTime of the subsequently following Activity
		if (noBSAct != null) //if there was an activity found, which is not eb_interaction
		{
			basicAgentDelegate.getEvents().processEvent(new NoVehicleBikeSharingEvent(
					now, route.getStartLinkId(), EBConstants.MODE, basicAgentDelegate.getPerson()));
			
			noBSAct.setStartTime(now+travelTimeTotal+waitingTime);
			trip.add(noBSAct);
			
			planElements.addAll(indexOfInsertion, trip); 
			
			Id<Person> p = basicAgentDelegate.getPerson().getId();
			//System.out.println("NoBike for Person: " + p);
			
			Activity act = (Activity)basicAgentDelegate.getCurrentPlanElement();
	
			new ActivityEndEvent(
					now, 
					p, 
					basicAgentDelegate.getCurrentLinkId(), 
					(Id<ActivityFacility>)basicAgentDelegate.getCurrentFacility().getId(),
					act.getType());
		}
		runner.planComparison(basicAgentDelegate);
		log.info("Agent with ID:;" + basicAgentDelegate.getPerson().getId() + ";did not get a Bike -->;" + whatToChoose);
	}
	
}
