/* *********************************************************************** *
 * project: org.matsim.*
 * BikeSharingRoutingModule.java
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
package eu.eunoiaproject.bikesharing.framework.routing.bikeSharing;

import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.StationAndType;
import org.matsim.core.mobsim.qsim.agents.BSRunner;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.BikeSharingStationChoice;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.CreateSubtrips;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BSAtt;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BSAttribsAgent;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacility;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.router.EmptyStageActivityTypes;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.StageActivityTypes;

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
public class EBikeSharingRoutingModule implements RoutingModule {
	private final StageActivityTypes stageTypes = EmptyStageActivityTypes.INSTANCE;
	
	private final BikeSharingFacilities bikeSharingFacilitiesAll;
	private final BikeSharingFacilities bikeSharingFacilities;
	private final BikeSharingFacilities ebikeSharingFacilities;
	private final BikeSharingBikes allBikes;
	private double searchRadius;
	CreateSubtrips c;
	int varifyer = 0;
	public final RoutingModule ptRouting;
	@Inject Scenario scenario;
	public final RoutingModule bsRouting;
	public final RoutingModule bseRouting;
	public final RoutingModule bsWalkRouting;
	private final BikeSharingFacilities ebikeSharingFacilitiesWithPTInteraction;
	private final BikeSharingFacilities bikeSharingFacilitiesWithPTInteraction;
	private static final Logger log = Logger.getLogger(EBikeSharingRoutingModule.class);
	
	/***************************************************************************/
	@Inject
	public EBikeSharingRoutingModule(
			final Scenario scenario,
			@Named( TransportMode.pt )
			final RoutingModule ptRouting,
			@Named (EBConstants.BS_BIKE)
			final RoutingModule bsRouting,
			@Named (EBConstants.BS_E_BIKE)
			final RoutingModule bseRouting,
			@Named (EBConstants.BS_WALK)
			final RoutingModule bsWalkRouting,
			@Named (EBConstants.MODE)
			final RoutingModule ebsRouting)
	/***************************************************************************/
	{
		this(	scenario,
				(BikeSharingBikes)scenario.getScenarioElement(BikeSharingBikes.ELEMENT_NAME),
				ptRouting,
				bsRouting,
				bseRouting,
				bsWalkRouting,
				ebsRouting);
	
		this.scenario = scenario; //TODO: RADIEN RICHTIG STELLEN
	}

	/***************************************************************************/
	public EBikeSharingRoutingModule(
			Scenario scenario,
			final BikeSharingBikes bikesAll,
			final RoutingModule ptRouting,
			final RoutingModule bsRouting,
			final RoutingModule bseRouting,
			final RoutingModule bsWalkRouting,
			final RoutingModule ebs2Routing) 
	/***************************************************************************/
	{
		this.bikeSharingFacilitiesAll = (BikeSharingFacilities) 
				scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME);
		this.allBikes = bikesAll;
		this.ptRouting = ptRouting;
		this.bsRouting = bsRouting;
		this.bsWalkRouting = bsWalkRouting;
		this.bseRouting = bseRouting;
		this.c = new CreateSubtrips();
		this.scenario = scenario;
		BikeSharingStationChoice bsChoice = new BikeSharingStationChoice(scenario);
		this.bikeSharingFacilities = bsChoice.bikeSharingFacilities2;
		this.ebikeSharingFacilities = bsChoice.ebikeSharingFacilities2;
		this.ebikeSharingFacilitiesWithPTInteraction = bsChoice.bikeSharingFacilitiesPt2;
		this.bikeSharingFacilitiesWithPTInteraction = bsChoice.ebikeSharingFacilitiesPt2;
	}
	
	public List<PlanElement> getMyTrip( //TODO: Hebenstreit
			List<PlanElement> first,
			PlanElement second,
			List<PlanElement> third,
			PlanElement fourth,
			List<PlanElement> fifth,
			List<PlanElement> trip)	
	{
		trip.addAll(first);
		trip.add(second);
		trip.addAll(third);
		trip.add(fourth);
		trip.addAll(fifth);
		
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
		List<PlanElement> trip = new ArrayList<PlanElement>();

		Activity act = null; //Hebenstreit
		
		StationAndType start = new StationAndType();
		StationAndType end = new StationAndType();
		
		
		
		boolean ptUsage = false; //TODO: define ptUsage - due to the different possible types:
		// because of a very long trip length (intermodal)
		// because no station near at departure or arrival facility
		// there are the following options:
		// * no public transport used = bike-sharing only
		// * public transport used, because BS-Station too far from Facility
		//	  - PT <-> BS         = no BS-Station near the Departure Facility
		//	  - BS <-> PT         = no BS-Station near the Arrival Facility
		//	  - PT <-> BS <-> PT  = no BS-Station near the Departure and Arrival Facility
		// * public transport used, because Total-Trip Length too long for cycling
		//    - Intermodal        = this should be used, if total trip length is too long
		
		//if no pt is used
		

		BSAtt att = BSAttribsAgent.getPersonAttributes( person, scenario);
		

		BikeSharingStationChoice bsChoice = new BikeSharingStationChoice(scenario);
		StationAndType[] startAndEnd = bsChoice.getInitialStations(
				fromFacility.getCoord(), toFacility.getCoord(), att.searchRadius,
				att.maxSearchRadius, att.maxBSTripLength, fromFacility.getId(), toFacility.getId());
		/*if (startAndEnd == null)
		{
			log.warn("Agent mit ID: " + person.getId() + " has no useful Bike-Sharing Stations within Reach");
		}
		else if (startAndEnd[0] == startAndEnd[1])
		{
			log.warn("Agent mit ID: " + person.getId() + "found same Start- and End-Station (will not use bike-sharing)");
		}*/
		int bikeSharingOptionSelection;
		// Chain of trip: walk - bike - walk (==0)
		List<PlanElement> firstLeg = null;
		List<PlanElement> thirdLeg = null;
		List<PlanElement> fifthLeg = null;
		BikeSharingFacility startBSFac = null;
		BikeSharingFacility endBSFac = null;
		if (startAndEnd != null)
		{
			start = startAndEnd[0];	
			end = startAndEnd[1];
			if (start != null )	startBSFac = start.station;
			if (end != null )endBSFac = end.station;
			bikeSharingOptionSelection = BikeSharingStationChoice.bikeSharingOptions (startAndEnd);
		}
		
		else 
			bikeSharingOptionSelection = 3;

		// Choice 3: Chain of trip: pt
		if (bikeSharingOptionSelection == 3)
		{
			trip = c.createPtSubtrip(fromFacility,
					toFacility,
					departureTime,person, scenario, ptRouting);
			
			if ((trip == null)|| (trip.size() <2))
			{
				trip = (List<PlanElement>) c.createWalkBikeSubtrip(fromFacility, toFacility, departureTime, person, bsWalkRouting);
			}
			if (trip == null)
			{
				System.out.println("Problemtest: Hebenstreit");
			}
		}
		// Choice 0: bike sharing only
		else if(bikeSharingOptionSelection == 0)
		{
			if ((startBSFac == null) || (endBSFac== null)) //no start or end station found
			{
				trip = c.createPtSubtrip(fromFacility, toFacility, departureTime, person, scenario,ptRouting);
				
				if ((trip == null) || (trip.size()<2))
				{
					c.createWalkBikeSubtrip(fromFacility, toFacility, departureTime, person, bsWalkRouting);
				}
			}
			else
			{	
				firstLeg = c.createWalkBikeSubtrip(fromFacility,start.station,departureTime,person,bsWalkRouting);
				double departureTimeTempA = departureTime + ((Leg) firstLeg.get(0)).getTravelTime() + EBConstants.TIME_TAKE; //TODO:Hebenstreit
				PlanElement second = CreateSubtrips.createInteractionTakeBS(start.station, firstLeg, departureTimeTempA-(EBConstants.TIME_TAKE));
				if (start.station.getStationType().equals("e"))
				{
					thirdLeg = c.createWalkBikeSubtrip(start.station,end.station,departureTimeTempA,person,bseRouting);
				}
				else
				{
					thirdLeg = c.createWalkBikeSubtrip(start.station,end.station,departureTimeTempA,person,bsRouting);
				}
				double departureTimeTempB = ((Leg) thirdLeg.get(0)).getDepartureTime() + ((Leg) thirdLeg.get(0)).getTravelTime() + EBConstants.TIME_RETURN;
				PlanElement fourth = CreateSubtrips.createInteractionReturnBS(end.station, thirdLeg, departureTimeTempB-EBConstants.TIME_RETURN);
				fifthLeg = c.createWalkBikeSubtrip(end.station,toFacility,departureTimeTempB,person,bsWalkRouting);
				trip = getMyTrip(firstLeg, second, thirdLeg, fourth, fifthLeg, trip);
			}
		}
		// Choice 1: Chain of trip: walk - bike - pt	
		else if(bikeSharingOptionSelection == 1)
		{
			if (start == null || end == null)
			{
				trip =  (List<PlanElement>)c.createPtSubtrip(
						fromFacility, toFacility, departureTime, person, scenario, ptRouting);
				if ((trip== null)|| (trip.size()<2))
				{
					trip = (List<PlanElement>) c.createWalkBikeSubtrip(fromFacility, toFacility, departureTime, person, bsWalkRouting);
				}
				if (trip == null)
				{
					System.out.println("Problemtest: Hebenstreit");
				}
			}
			else if (startBSFac == null || endBSFac == null)
			{
				trip = (List<PlanElement>) c.createPtSubtrip(fromFacility, toFacility, departureTime, person, scenario, ptRouting);
				if (trip == null)
				{
					trip = (List<PlanElement>) c.createWalkBikeSubtrip(fromFacility, toFacility, departureTime, person, bsWalkRouting);
				}
				
				if (trip == null)
				{
					System.out.println("Problemtest: Hebenstreit");
				}
			}
			else
			{
		
				firstLeg = c.createWalkBikeSubtrip(fromFacility,start.station,departureTime,person,bsWalkRouting);
				double departureTimeTempA = departureTime + ((Leg) firstLeg.get(0)).getTravelTime() + (EBConstants.TIME_TAKE);
				PlanElement second = CreateSubtrips.createInteractionTakeBS(start.station, firstLeg, departureTimeTempA-(EBConstants.TIME_TAKE));
				if (start.station.getStationType().equals("e"))
				{
					thirdLeg = c.createWalkBikeSubtrip(start.station,end.station,departureTimeTempA,person, bseRouting);
				}
				else
				{
					thirdLeg = c.createWalkBikeSubtrip(start.station,end.station,departureTimeTempA,person, bsRouting);
				}
				
				double departureTimeTempB = ((Leg) thirdLeg.get(0)).getDepartureTime() + ((Leg) thirdLeg.get(0)).getTravelTime()+EBConstants.TIME_RETURN;
				PlanElement fourth = CreateSubtrips.createInteractionReturnBS(end.station, thirdLeg, departureTimeTempB-EBConstants.TIME_RETURN);
				fifthLeg = c.createPtSubtrip(end.station,toFacility,departureTimeTempB,person, scenario, ptRouting);
				if ((fifthLeg == null) || (fifthLeg.size() < 2))
				{
					fifthLeg = (List<PlanElement>) c.createWalkBikeSubtrip(fromFacility, toFacility, departureTimeTempB, person, bsWalkRouting);
				}
				trip =  getMyTrip(firstLeg, second, thirdLeg, fourth, fifthLeg, trip);
			}
			
		}
		// Choice 2: Chain of trip: pt - bike - walk 			
		else if(bikeSharingOptionSelection == 2)
		{
			firstLeg = c.createPtSubtrip(fromFacility,start.station,departureTime,person, scenario, ptRouting);
			if ((firstLeg == null) ||(firstLeg.size()<2))
			{
				firstLeg = (List<PlanElement>) c.createWalkBikeSubtrip(fromFacility, start.station, departureTime, person, bsWalkRouting);
			}
			
			double departureTimeTempA = departureTime + ((Leg) firstLeg.get(0)).getTravelTime() + EBConstants.TIME_TAKE;
			if (firstLeg.size() > 1)
			{
				int i = 1;
				while (i < firstLeg.size())
				{
					if (firstLeg.get(i) instanceof Leg)
					{
					departureTimeTempA = departureTimeTempA + ((Leg)firstLeg.get(i)).getTravelTime();
					}
					else //instance of Activity
					{
						PlanElement element = CreateSubtrips.createInteractionPT(((Activity)firstLeg.get(i)).getCoord(), ((Activity)firstLeg.get(i)).getLinkId(), departureTimeTempA);
						firstLeg.set(i, element);
						departureTimeTempA = departureTimeTempA + 0;
					}
					i++;
				}
			}
			PlanElement second = CreateSubtrips.createInteractionTakeBS(start.station, firstLeg, departureTimeTempA);
			if (start.station.getStationType().equals("e"))
			{
				thirdLeg = c.createWalkBikeSubtrip(start.station,end.station,departureTimeTempA,person, bseRouting);
			}
			else
			{
				thirdLeg = c.createWalkBikeSubtrip(start.station,end.station,departureTimeTempA,person, bsRouting);
			}
			PlanElement fourth = CreateSubtrips.createInteractionReturnBS(end.station, thirdLeg, ((Leg) firstLeg.get(0)).getTravelTime()+((Leg) firstLeg.get(0)).getDepartureTime() );
			double departureTimeTempB = ((Leg) thirdLeg.get(0)).getDepartureTime() + ((Leg) thirdLeg.get(0)).getTravelTime() + EBConstants.TIME_RETURN ;
			fifthLeg = c.createWalkBikeSubtrip(end.station,toFacility,departureTimeTempB,person, bsWalkRouting);
			
			trip = getMyTrip(firstLeg, second, thirdLeg, fourth, fifthLeg, trip);
		}
		//######################################################################
	
		else if(bikeSharingOptionSelection == 3)
		{
			trip =  c.createPtSubtrip(fromFacility,
					toFacility,
					departureTime,person, scenario, ptRouting);
			if (trip == null)
			{
				trip = (List<PlanElement>) c.createWalkBikeSubtrip(fromFacility, toFacility, departureTime, person, bsWalkRouting);
			}
		}

		//######################################################################
		else 
		{
			System.out.println("Fehler bei der Zuordnung des Bike-Sharing-Typs");
			System.exit(0);
			return null;
		}
		

		for (int i = 0; i < trip.size()-1; i++)
		{
			if (trip.get(i) instanceof Leg)
			{
				Leg leg = ((Leg)trip.get(i));
	
				if (leg.getMode().equals(TransportMode.transit_walk))
				{
					BSRunner runner = new BSRunner();
					PlanElement pe = runner.genericRouteWithStartAndEndLink(leg, trip, scenario, i, fromFacility.getLinkId(), toFacility.getLinkId());
					trip.remove(i);
					trip.add(i, pe);
					String mode = leg.getMode();
					Route route = leg.getRoute();
					route.setDistance(leg.getTravelTime()/1.5);
					route.setTravelTime(leg.getTravelTime());
					//System.out.println("Hebenstreit: Mode = " + mode);
				}
			}
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
