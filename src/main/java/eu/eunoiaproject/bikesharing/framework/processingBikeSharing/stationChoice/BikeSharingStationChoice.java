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
package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice;

import java.util.*;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.Facility;
import org.matsim.pt.router.TransitRouterImpl;

import org.matsim.core.mobsim.qsim.agents.BSRunner;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;
import org.matsim.core.mobsim.qsim.agents.TransitAgentImpl;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.BikeSharingContext;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.StationAndType;

/**
 * 
 *hier noch miteinbeziehen 
 *ob Station A zum Abreisezeitpunkt verfuegbare Raeder
 *ob Station B zum Abreisezeitpunkt verfuebare Stellplaetze
 *
 * 
 *
 * @author Hebenstreit
 */
public class BikeSharingStationChoice
{
	Random random;
	public BikeSharingFacilities ebikeSharingFacilities2;
	public BikeSharingFacilities  bikeSharingFacilities2;
	public BikeSharingFacilities  ebikeSharingFacilitiesPt2;
	public BikeSharingFacilities  bikeSharingFacilitiesPt2;
	QuadTree<BikeSharingFacility> e_qt ;
	QuadTree<BikeSharingFacility> e_pt_qt ;
	QuadTree<BikeSharingFacility> b_qt ;
	QuadTree<BikeSharingFacility> b_pt_qt;
	
	Scenario scenario;

	static final Logger log = Logger.getLogger(BikeSharingStationChoice.class);
	
	/***************************************************************************/
	public BikeSharingStationChoice(
			Scenario scenario) 
	/***************************************************************************/
	{	
		this.scenario = scenario;
	}
	
	/***************************************************************************/
	public StationAndType[] getInitialStations(
			final Coord fromFac, 
			final Coord toFac, 
			double searchRadius,
			double maxSearchRadius, 
			double maxDistanceForBikeTrip,
			Id idFromFac,
			Id idToFac)
	/***************************************************************************/
	{
		double maxSearchRadiusPtStation = maxDistanceForBikeTrip;
		{
			StationAndType[] startAndEndStation = new StationAndType[2];
			//this array stores the start [0] and end [1] station
			
//			this.ebikeSharingFacilitiesPt2 = new BikeSharingFacilities();
//			this.bikeSharingFacilitiesPt2 = new BikeSharingFacilities();
//			this.ebikeSharingFacilities2 = new BikeSharingFacilities();
//			this.bikeSharingFacilities2 = new BikeSharingFacilities();
			
			BikeSharingFacilities bikeSharingFacilitiesAll = (BikeSharingFacilities) 
					scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME);
			
			this.ebikeSharingFacilitiesPt2 = bikeSharingFacilitiesAll.getSpecialFacilities("e_pt", scenario);
			this.ebikeSharingFacilities2 = bikeSharingFacilitiesAll.getSpecialFacilities("e", scenario);
			this.bikeSharingFacilitiesPt2 = bikeSharingFacilitiesAll.getSpecialFacilities("c_pt", scenario);
			this.bikeSharingFacilities2 = bikeSharingFacilitiesAll.getSpecialFacilities("c", scenario);
			this.e_qt = ebikeSharingFacilities2.getCurrentQuadTree();
			this.e_pt_qt = ebikeSharingFacilitiesPt2.getCurrentQuadTree();
			this.b_qt = bikeSharingFacilities2.getCurrentQuadTree();

//			log.warn("printing ...");
//			for( Map.Entry<Id<BikeSharingFacility>, BikeSharingFacility> entry : bikeSharingFacilitiesPt2.facilities.entrySet() ){
//				log.warn( "entry=" + entry.getValue() ) ;
//			}

//			if ( true ){
//				throw new RuntimeException( "stop" );
//			}

			this.b_pt_qt = bikeSharingFacilitiesPt2.getCurrentQuadTree();

//			log.warn("quadTree=" + this.b_pt_qt ) ;
			
			//final double directDistance = CoordUtils.calcEuclideanDistance( fromFacility , toFacility );

			StationAndType startStation = new StationAndType();
			StationAndType endStation  = new StationAndType();
			StationAndType startEStation  = new StationAndType();
			StationAndType endEStation  = new StationAndType();
			
			startStation = chooseCloseCStationInitial( fromFac , searchRadius, maxSearchRadius, idFromFac  );
			endStation = chooseCloseCStationInitial( toFac, searchRadius,  maxSearchRadius, idToFac );
			startEStation = chooseCloseEStationInitial( fromFac, searchRadius,  maxSearchRadius, idFromFac);
			endEStation = chooseCloseEStationInitial( toFac ,searchRadius,   maxSearchRadius, idToFac  );
			
			int sStat = 0;
			int eStat = 0;
			int sStatE = 0;
			int eStatE = 0;
			
			if (startStation != null && startStation.station != null) 		{sStat = 1;}
			if (endStation != null && endStation.station != null) 			{eStat = 1;}
			if (startEStation != null && startEStation.station != null) 	{sStatE = 1;}
			if (endEStation != null && endEStation.station != null) 		{eStatE = 1;}

			
			int conv = sStat + eStat;
			int electr = sStatE + eStatE;
			
			double distanceCStart = -1;
			double distanceCEnd = -1;
			double distanceEStart = -1;
			double distanceEEnd = -1;
			
			if (conv + electr == 4) //start and end Station for E-Bike and Conv-Bike found
			{
				distanceCStart = CoordUtils.calcEuclideanDistance(startStation.station.getCoord(), fromFac);
				distanceCEnd = CoordUtils.calcEuclideanDistance(endStation.station.getCoord(), fromFac);
				distanceEStart = CoordUtils.calcEuclideanDistance(startEStation.station.getCoord(), fromFac);
				distanceEEnd = CoordUtils.calcEuclideanDistance(endEStation.station.getCoord(), fromFac);
				startAndEndStation[0].bikeSharingType = 0;
				
				if (isConvBikeUsed(distanceCStart, distanceCEnd, distanceEStart, distanceEEnd))
				{
					double distance = CoordUtils.calcEuclideanDistance(startStation.station.getCoord(), endStation.station.getCoord());
					if (distance <= maxDistanceForBikeTrip)
					{
						startAndEndStation[0] = startStation;
						startAndEndStation[1] = endStation;
					}
					else 
					{
						startAndEndStation = calcPtTripStationsInit(sStat, eStat, sStatE, eStatE, fromFac, toFac,
								distanceCStart, distanceCEnd, distanceEStart, distanceEEnd,
								startStation, endStation,
								startEStation, endEStation, 
								maxSearchRadiusPtStation, idFromFac, idToFac);
					}
				}
				else
				{
					double distance = CoordUtils.calcEuclideanDistance(startEStation.station.getCoord(), endEStation.station.getCoord());
					if (distance <= maxDistanceForBikeTrip)
					{
						startAndEndStation[0] = startEStation;
						startAndEndStation[1] = endEStation;
					}
					else 
					{
						startAndEndStation = calcPtTripStationsInit(sStat, eStat, sStatE, eStatE, fromFac, toFac,
								distanceCStart, distanceCEnd, distanceEStart, distanceEEnd,
								startStation, endStation,
								startEStation, endEStation, 
								maxSearchRadiusPtStation, idFromFac, idToFac);
					}
				}	
			}
			
			else if (conv == 2) //start and end Station only for Conv-Bike found
			{
				double distance = CoordUtils.calcEuclideanDistance(startStation.station.getCoord(), endStation.station.getCoord());
				if (distance <= maxDistanceForBikeTrip)
				{
					startAndEndStation[0] = startStation;
					startAndEndStation[0].bikeSharingType = 0;
					startAndEndStation[1] = endStation;
				}
				else 
				{
					startAndEndStation = calcPtTripStationsInit(sStat, eStat, sStatE, eStatE, fromFac, toFac,
							distanceCStart, distanceCEnd, distanceEStart, distanceEEnd,
							startStation, endStation,
							startEStation, endEStation, 
							maxSearchRadiusPtStation, idFromFac, idToFac);
				}
			}
			else if ((electr + conv) == 0)
			{
				return null;
			}
			
			
			else if (electr == 2) //start and end Station only for E-Bike found
			{
				double distance = CoordUtils.calcEuclideanDistance(startEStation.station.getCoord(), endEStation.station.getCoord());
				if (distance <= maxDistanceForBikeTrip)
				{
					startAndEndStation[0] = startEStation;
					startAndEndStation[1] = endEStation;
				}
				else 
				{
					//start And End found, but trip length too long
					startAndEndStation = calcPtTripStationsInit(sStat, eStat, sStatE, eStatE, fromFac, toFac,
							distanceCStart, distanceCEnd, distanceEStart, distanceEEnd,
							startStation, endStation,
							startEStation, endEStation, 
							maxSearchRadiusPtStation, idFromFac, idToFac);
				}
				
			}
			
			else //public transport needs to be used --> combined
			{
				startAndEndStation = calcPtTripStationsInit(sStat, eStat, sStatE, eStatE, fromFac, toFac,
						distanceCStart, distanceCEnd, distanceEStart, distanceEEnd,
						startStation, endStation,
						startEStation, endEStation, 
						maxSearchRadiusPtStation, idFromFac, idToFac);
			}
			if (startAndEndStation == null) 
			{
				return null;
			}
			if (startAndEndStation[0] == null || startAndEndStation[1] == null || startAndEndStation[0].station == null || startAndEndStation[1].station == null )
			{
				return null;
			}
			if (startAndEndStation[0].station.getId().toString().equals(startAndEndStation[1].station.getId().toString()))
			{
				return null;
			}
			return startAndEndStation;
		}
	}
	
	/***************************************************************************/
	/** returns the chosen station, if pt is not considered                   **/
	/** can be a bike-, e-bike or pt-Station                                  **/
	public StationAndType[] getStationsDuringSim (
			Facility fromFac,
			Facility toFac,
			double searchRadius,
			double maxSearchRadius, 
			Person person,
			double departureTime,
			BasicPlanAgentImpl basicAgentDelegate,
			BikeSharingContext bsc,
			double maxBsDistance)
	/***************************************************************************/
	{
		BikeSharingFacilities bikeSharingFacilitiesAll = (BikeSharingFacilities) 
				scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME);
		this.ebikeSharingFacilitiesPt2 = bikeSharingFacilitiesAll.getSpecialFacilities("e_pt", scenario);
		this.ebikeSharingFacilities2 = bikeSharingFacilitiesAll.getSpecialFacilities("e", scenario);
		this.bikeSharingFacilitiesPt2 = bikeSharingFacilitiesAll.getSpecialFacilities("c_pt", scenario);
		this.bikeSharingFacilities2 = bikeSharingFacilitiesAll.getSpecialFacilities("c", scenario);
		this.e_qt = ebikeSharingFacilities2.getCurrentQuadTree();
		this.e_pt_qt = ebikeSharingFacilitiesPt2.getCurrentQuadTree();
		this.b_qt = bikeSharingFacilities2.getCurrentQuadTree();
		this.b_pt_qt = bikeSharingFacilitiesPt2.getCurrentQuadTree();
		
		
		//double maxSearchRadiusPtStation = maxDistanceForBikeTrip/1.42;
		StationAndType[] startAndEndStation = new StationAndType[2];
		//this array stores the start [0] and end [1] station
		//final double directDistance = CoordUtils.calcEuclideanDistance( fromFacility , toFacility );

		StationAndType startStation = new StationAndType();
		StationAndType endStation  = new StationAndType();
		StationAndType startEStation  = new StationAndType();
		StationAndType endEStation  = new StationAndType();

		/*if (fromFac == null || toFac == null) 
		{
			System.out.println(fromFac);
			System.out.println(toFac);
		}*/
		startStation = chooseCloseCStartStation( fromFac.getCoord() , searchRadius, maxSearchRadius, fromFac.getId() );
		endStation = chooseCloseCEndStation( toFac.getCoord(), searchRadius,  maxSearchRadius, toFac.getId()  );
		startEStation = chooseCloseEStartStation( fromFac.getCoord(), searchRadius,  maxSearchRadius, fromFac.getId() );
		endEStation = chooseCloseEEndStation( toFac.getCoord() ,searchRadius,   maxSearchRadius, toFac.getId()   );
		
		startAndEndStation = calcBSStations(fromFac, toFac, startStation, endStation, 
				startEStation, endEStation, departureTime, person, basicAgentDelegate, bsc, maxBsDistance);
		if (startAndEndStation == null || startAndEndStation[0]== null || startAndEndStation[0].station == null 
				|| startAndEndStation[1] == null || startAndEndStation[1].station == null)
		{
			return null;
		}
		
		if (startAndEndStation[0].station == startAndEndStation[1].station)
		{
			return null;
		}
		return startAndEndStation;
	}
	/***************************************************************************/
	public double getFullPtTrip(
			Facility fromFacF,
			Facility toFacF,
			double departureTime,
			Person person,
			BasicPlanAgentImpl basicAgentDelegate)
	/***************************************************************************/
	{
		BSRunner bsR = new BSRunner();
		List<PlanElement> ptLegs = bsR.createPTLegs(fromFacF.getCoord(), toFacF.getCoord(), departureTime, person, 
				scenario, fromFacF.getLinkId(), toFacF.getLinkId(), new TransitAgentImpl(basicAgentDelegate));
		
		if (ptLegs == null || ptLegs.size() < 1 )
		{
			return Double.POSITIVE_INFINITY;
		}
		
		else if (ptLegs.size() < 2)
		{
			return ((Leg)ptLegs.get(ptLegs.size()-1)).getTravelTime();
		}
		else
		{
			return ((Leg)ptLegs.get(ptLegs.size()-1)).getDepartureTime() + ((Leg)ptLegs.get(ptLegs.size()-1)).getTravelTime() - ((Leg)ptLegs.get(0)).getDepartureTime();
		}
	}

	
	/***************************************************************************/
	public StationAndType getEgressTrip(
			Facility fromFacF,
			Facility toFacF,
			BikeSharingFacility endStation,
			double departureTime,
			Person person, BikeSharingContext bsc)
	/***************************************************************************/
	{
		StationAndType statNull = new StationAndType();
		statNull.station = null;
		statNull.tripDur = Double.POSITIVE_INFINITY;


		boolean isEBikeStation = false;

		BikeSharingFacilities bikeSharingFacilitiesAll = (BikeSharingFacilities) 
				scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME);
		BikeSharingBikes bSharingVehicles = (BikeSharingBikes) 
				scenario.getScenarioElement( BikeSharingBikes.ELEMENT_NAME);
		
		CoordUtils.calcEuclideanDistance(fromFacF.getCoord(), toFacF.getCoord());
		double x = fromFacF.getCoord().getX() + ((toFacF.getCoord().getX()-fromFacF.getCoord().getX())/2);
		double y = fromFacF.getCoord().getY() + ((toFacF.getCoord().getY()-fromFacF.getCoord().getY())/2);
		Coord middle = new Coord(x,y);
		double radiusM = CoordUtils.calcEuclideanDistance(fromFacF.getCoord(), middle);
		double radiusStartEnd = 1000;
		
		Collection <BikeSharingFacility> bs = new HashSet<BikeSharingFacility>();
		
		if (endStation.getStationType().equals("e"))
		{
			isEBikeStation = true;
			this.ebikeSharingFacilitiesPt2 = bikeSharingFacilitiesAll.getSpecialFacilities("e_pt", scenario);
			this.e_pt_qt = ebikeSharingFacilitiesPt2.getCurrentQuadTree();
			
			bs.addAll(e_pt_qt.getDisk(x, y, radiusM));
			bs.addAll(e_pt_qt.getDisk(fromFacF.getCoord().getX(), fromFacF.getCoord().getY(), radiusStartEnd));
			bs.addAll(e_pt_qt.getDisk(toFacF.getCoord().getX(), toFacF.getCoord().getY(), radiusStartEnd));
		}
		else
		{
			this.bikeSharingFacilitiesPt2 = bikeSharingFacilitiesAll.getSpecialFacilities("c_pt", scenario);
			this.b_pt_qt = bikeSharingFacilitiesPt2.getCurrentQuadTree();
			bs.addAll(b_pt_qt.getDisk(x, y, radiusM));
			bs.addAll(b_pt_qt.getDisk(fromFacF.getCoord().getX(), fromFacF.getCoord().getY(), radiusStartEnd));
			bs.addAll(b_pt_qt.getDisk(toFacF.getCoord().getX(), toFacF.getCoord().getY(), radiusStartEnd));
		}
		Map<Id, BikeSharingFacility> bsMap = new HashMap<Id,BikeSharingFacility>();
		List<BikeSharingFacility> bsList = new ArrayList<BikeSharingFacility>(bs);
		for (int i = 0; i < bsList.size(); i++) //ensures that no stations is twice or more in list
		{
			if (bsList.get(i).getNumberOfAvailableBikes()< 1)
			{
				if (bsMap.get(bsList.get(i).getId()) == null)
				{
					bsMap.put(bsList.get(i).getId(), bsList.get(i));
				}
				else
				{
					bsList.remove(i);
				}	
			}
			else
			{
				bsList.remove(i);
			}
		}
		
		if (bsList.size() < 1)
		{
			return statNull;
		}
		TransitRouterImpl pt = bSharingVehicles.trImpl;
		
		double duration = Double.POSITIVE_INFINITY;
		double travelTimeBike = Double.POSITIVE_INFINITY;
		double travelTimePt = 0;

		BikeSharingFacility startStation = null;
		for (int i = 0; i < bsList.size()-1; i++)
		{
			double durationTemp = Double.POSITIVE_INFINITY;
			List<Leg> tripPt = pt.calcRoute(fromFacF.getCoord(), bsList.get(i).getCoord(), departureTime, person);

			if (tripPt != null)
			{
				for (int j = 0; j < tripPt.size(); j++)
				{
					travelTimePt += tripPt.get(j).getTravelTime();
					if (travelTimePt < 0.1)
					{travelTimePt = 0.1;}
				}
			}
			
			if (travelTimePt < 0.1)
			{
				travelTimePt = Double.POSITIVE_INFINITY;
			}
			if (travelTimePt == Double.POSITIVE_INFINITY)
			{
				return statNull;
			}
			
			Path p_bs = null;
		
			if (bsList.get(i).getStationType().equals("c"))
			{
				p_bs = bsc.getSharedBikePathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(bsList.get(i).getLinkId()).getFromNode(),
					scenario.getNetwork().getLinks().get(endStation.getLinkId()).getFromNode(), departureTime, person, null);
			}
			else
			{
				p_bs = bsc.getSharedEBikePathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(bsList.get(i).getLinkId()).getFromNode(),
						scenario.getNetwork().getLinks().get(endStation.getLinkId()).getFromNode(), departureTime, person, null);
				
			}
			
			travelTimeBike = p_bs.travelTime;
			if (travelTimeBike == Double.POSITIVE_INFINITY)
			{
				return statNull;
			}
			
			durationTemp = travelTimePt + travelTimeBike;
			if (durationTemp <= duration)
			{
				duration = durationTemp;
				startStation = bsList.get(i);
			}
		}
	StationAndType startStation1 = new StationAndType();
	startStation1.station = startStation;
	startStation1.tripDur = duration;
	startStation1.usedAsPtChange = true;
	return startStation1; //wird null returned, so ist ein "full pt trip" als Egress-Trip die beste Lösung!
	}
	
	/***************************************************************************/
	public double getFullBSTrip(
			Facility fromFacF,
			Facility toFacF,
			BikeSharingFacility startStation,
			BikeSharingFacility endStation,
			double departureTime,
			Person person,
			BikeSharingContext bsc)
	/***************************************************************************/
	{
		if ((startStation == null) || (endStation == null))
		{
			return Double.POSITIVE_INFINITY;
		}
		boolean isEBikeStation = false;
		if (startStation.getStationType().equals("e"))
		{
			isEBikeStation = true;
		}
	
		Path p_wa = bsc.getWalkPathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(fromFacF.getLinkId()).getFromNode(),
				scenario.getNetwork().getLinks().get(startStation.getLinkId()).getFromNode(), departureTime, person, null);
		Path p_bs = null;
		if (isEBikeStation)
		{
			p_bs = bsc.getSharedEBikePathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(startStation.getLinkId()).getFromNode(),
				scenario.getNetwork().getLinks().get(endStation.getLinkId()).getFromNode(), departureTime, person, null);
		}
		else
		{
			p_bs = bsc.getSharedBikePathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(startStation.getLinkId()).getFromNode(),
				scenario.getNetwork().getLinks().get(endStation.getLinkId()).getFromNode(), departureTime, person, null);
		}
		
		Path p_we = bsc.getWalkPathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(endStation.getLinkId()).getFromNode(),
				scenario.getNetwork().getLinks().get(toFacF.getLinkId()).getFromNode(), departureTime, person, null);
		Path direct = bsc.getSharedBikePathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(startStation.getLinkId()).getFromNode(),
				scenario.getNetwork().getLinks().get(endStation.getLinkId()).getFromNode(), departureTime, person, null);

		if (p_bs.travelTime > direct.travelTime*1.4)
		{	
			p_bs = direct;
		}
		
		if (p_bs == null || p_bs.links.size() < 1)
		{
			return Double.POSITIVE_INFINITY;
		}
		double len = p_bs.links.get(0).getLength();
		for (int i = 1; i < p_bs.links.size(); i++)
		{
			len += p_bs.links.get(i).getLength();
		}
		boolean isPt = ptUsedBecauseOfTripLength (len);
		if (isPt)
		{
			return Double.POSITIVE_INFINITY;
		}
		
		double travelTime = p_wa.travelTime + p_bs.travelTime + p_we.travelTime;
		return travelTime;
	}
	
	/***************************************************************************/
	public StationAndType getAccessTrip(
			Facility fromFacF,
			Facility toFacF,
			BikeSharingFacility startStation,
			double departureTime,
			Person person, BikeSharingContext bsc)
	/***************************************************************************/
	{
		StationAndType statNull = new StationAndType();
		statNull.station = null;
		statNull.tripDur = Double.POSITIVE_INFINITY;

		boolean isEBikeStation = false;
		BikeSharingFacilities bikeSharingFacilitiesAll = (BikeSharingFacilities) 
				scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME);
		BikeSharingBikes bSharingVehicles = (BikeSharingBikes) 
				scenario.getScenarioElement( BikeSharingBikes.ELEMENT_NAME);
		
		CoordUtils.calcEuclideanDistance(fromFacF.getCoord(), toFacF.getCoord());
		double x = fromFacF.getCoord().getX() + ((toFacF.getCoord().getX()-fromFacF.getCoord().getX())/2);
		double y = fromFacF.getCoord().getY() + ((toFacF.getCoord().getY()-fromFacF.getCoord().getY())/2);
		Coord middle = new Coord(x,y);
		double radiusM = CoordUtils.calcEuclideanDistance(fromFacF.getCoord(), middle);
		double radiusStartEnd = 1000;
		
		Collection <BikeSharingFacility> bs = new HashSet<BikeSharingFacility>();
		
		if (startStation.getStationType().equals("e"))
		{
			isEBikeStation = true;
			this.ebikeSharingFacilitiesPt2 = bikeSharingFacilitiesAll.getSpecialFacilities("e_pt", scenario);
			this.e_pt_qt = ebikeSharingFacilitiesPt2.getCurrentQuadTree();
			
			bs.addAll(e_pt_qt.getDisk(x, y, radiusM));
			bs.addAll(e_pt_qt.getDisk(fromFacF.getCoord().getX(), fromFacF.getCoord().getY(), radiusStartEnd));
			bs.addAll(e_pt_qt.getDisk(toFacF.getCoord().getX(), toFacF.getCoord().getY(), radiusStartEnd));
		}
		else
		{
			this.bikeSharingFacilitiesPt2 = bikeSharingFacilitiesAll.getSpecialFacilities("c_pt", scenario);
			this.b_pt_qt = bikeSharingFacilitiesPt2.getCurrentQuadTree();
			bs.addAll(b_pt_qt.getDisk(x, y, radiusM));
			bs.addAll(b_pt_qt.getDisk(fromFacF.getCoord().getX(), fromFacF.getCoord().getY(), radiusStartEnd));
			bs.addAll(b_pt_qt.getDisk(toFacF.getCoord().getX(), toFacF.getCoord().getY(), radiusStartEnd));
		}
		
		List<BikeSharingFacility> bsList = new ArrayList<BikeSharingFacility>(bs);
		Map <Id, BikeSharingFacility> bsMap = new HashMap<Id, BikeSharingFacility>();
		for (int i = 0; i < bsList.size(); i++) //ensures that no stations is twice or more in list
		{
			if (bsList.get(i).getFreeParkingSlots()< 1)
			{
				if (bsMap.get(bsList.get(i).getId()) == null)
				{
					bsMap.put(bsList.get(i).getId(), bsList.get(i));
				}
				else
				{
					bsList.remove(i);
				}	
			}
			else
			{
				bsList.remove(i);
			}
		}
		if (bsList.size() < 1)
		{
			StationAndType stat = new StationAndType();
			stat.station = null;
			stat.tripDur = Double.POSITIVE_INFINITY;
			return stat;
		}
		
		Path p_wa = bsc.getWalkPathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(fromFacF.getLinkId()).getFromNode(),
				scenario.getNetwork().getLinks().get(startStation.getLinkId()).getFromNode(), departureTime, person, null);
		
		TransitRouterImpl pt = bSharingVehicles.trImpl;
		
		double travelTimeAccessWalk = p_wa.travelTime;
		double duration = Double.POSITIVE_INFINITY;
		
		BikeSharingFacility endStation = null;
		for (int i = 0; i < bsList.size()-1; i++)
		{
			Path p_bs = null;
			if (bsList.get(i).getStationType().equals("c"))
			{
				p_bs = bsc.getSharedBikePathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(startStation.getLinkId()).getFromNode(),
						scenario.getNetwork().getLinks().get(bsList.get(i).getLinkId()).getFromNode(), departureTime, person, null);
			}
			else 
			{
				p_bs = bsc.getSharedEBikePathCalculator().calcLeastCostPath(scenario.getNetwork().getLinks().get(startStation.getLinkId()).getFromNode(),
						scenario.getNetwork().getLinks().get(bsList.get(i).getLinkId()).getFromNode(), departureTime, person, null);
			}

			double travelTimeBike = p_bs.travelTime;
			if (travelTimeBike == Double.POSITIVE_INFINITY)
			{
				return statNull;
			}
			double travelTimePt = 0;
			List<Leg> tripPt = pt.calcRoute(bsList.get(i).getCoord(), toFacF.getCoord(), departureTime+travelTimeAccessWalk+travelTimeBike, person);
			if (tripPt != null)
			{
				for (int j = 0; j < tripPt.size(); j++)
				{
					travelTimePt += tripPt.get(j).getTravelTime();
					if (travelTimePt < 0.1)
					{travelTimePt = 0.1;}
				}
			}
			
			if (travelTimePt < 0.1)
			{
				travelTimePt = Double.POSITIVE_INFINITY;
			}
			double durationTemp = travelTimeAccessWalk + travelTimeBike + travelTimePt;
			if (durationTemp < duration)
			{
				duration = durationTemp;
				endStation = bsList.get(i);
			}
		}
		
	StationAndType endStation1 = new StationAndType();
	endStation1.station =endStation;
	endStation1.tripDur = duration;
	endStation1.usedAsPtChange = true;
	return endStation1; //wird null returned, so ist ein "full pt trip" als Egress-Trip die beste Lösung!
	}
	
	/***************************************************************************/
	public StationAndType[] calcBSStations (Facility fromFac, Facility toFac,
			StationAndType startStation, StationAndType endStation, 
			StationAndType startEStation, StationAndType endEStation,
			double departureTime, Person person, BasicPlanAgentImpl basicAgentDelegate,
			BikeSharingContext bsc, double maxBsDistance)
	/***************************************************************************/
	{
		StationAndType [] access = new StationAndType[3];
		StationAndType [] egress= new StationAndType[3];
		StationAndType [] full= new StationAndType[3];
		StationAndType[] toReturn = new StationAndType[3];
		toReturn[2] = new StationAndType();
		toReturn[2].tripDur = Double.POSITIVE_INFINITY;
		
		StationAndType accC  = new StationAndType();
		accC.tripDur = Double.POSITIVE_INFINITY;
		StationAndType accE  = new StationAndType();
		accE.tripDur = Double.POSITIVE_INFINITY;
		StationAndType egrC = new StationAndType();
		egrC.tripDur = Double.POSITIVE_INFINITY;
		StationAndType egrE  = new StationAndType();
		egrE.tripDur = Double.POSITIVE_INFINITY;
		
		double bsTravelTime = Double.POSITIVE_INFINITY;
		double bsETravelTime = Double.POSITIVE_INFINITY;
		
		double duration = Double.POSITIVE_INFINITY;
		
		if ((startStation == null) && (endStation == null) && (startEStation == null) && (endEStation == null))
		{
			return null;
		}
		
		//List<StationAndType[]> list = new ArrayList<StationAndType[]>();

		double fullPtTravelTimePlus = getFullPtTrip(fromFac, toFac, departureTime, person, basicAgentDelegate)*1.25;//TODO Hebenstreit
		double bsTime = Double.POSITIVE_INFINITY;
		//############################ full bs trip ###################################
		if (startStation != null && endStation != null)
		{
			double distance = CoordUtils.calcEuclideanDistance(startStation.station.getCoord(), endStation.station.getCoord());
			if (distance <= maxBsDistance)
			{
				bsTravelTime = getFullBSTrip(fromFac, toFac, startStation.station, endStation.station, departureTime, person, bsc);
			}
		}
		if (startEStation != null && endEStation != null)
		{
			double distance = CoordUtils.calcEuclideanDistance(startEStation.station.getCoord(), endEStation.station.getCoord());
			if (distance <= maxBsDistance)
			{
				bsETravelTime = getFullBSTrip(fromFac, toFac, startEStation.station, endEStation.station, departureTime, person, bsc);
			}
		}
		if (Math.min(bsTravelTime,bsETravelTime) <= fullPtTravelTimePlus)
		{
			if (bsTravelTime < bsETravelTime)
			{
				bsTime = bsTravelTime;
				full[0] = startStation;
				full[1] = endStation;
				full [2] = new StationAndType();
				full[2].tripDur = bsTime;
				full[0].bikeSharingType = 0; //fullBS
				return full;
			}
			else if (bsETravelTime < Double.POSITIVE_INFINITY)
			{
				bsTime = bsETravelTime;
				full[0] = startEStation;
				full[1] = endEStation;
				full [2] = new StationAndType();
				full[0].bikeSharingType = 0; //fullBS
				full[2].tripDur = bsTime;
				return full;
			}
		}

		if (startStation != null)
		{
			accC = getAccessTrip(fromFac, toFac, startStation.station, departureTime, person, bsc);
		}
		if (startEStation != null)
		{
			accE = getAccessTrip(fromFac, toFac, startEStation.station, departureTime,person, bsc);
		}
		double accessTime = Double.POSITIVE_INFINITY;
		if (accC.tripDur < accE.tripDur)
		{
			accessTime = accC.tripDur;
			access[0] = startStation;
			access[1] = accC;
			access[2] = new StationAndType();
			access[2].tripDur= accC.tripDur;

		}
		else if (accE.tripDur < Double.POSITIVE_INFINITY)
		{
			accessTime = accE.tripDur;
			access[0] = startEStation;
			access[1] = accE;
			access[2] = new StationAndType();
			access[2].tripDur= accC.tripDur;
		}
		if (accessTime < Double.POSITIVE_INFINITY)
		{
			if (accessTime <= (fullPtTravelTimePlus*1.25))
			{
				duration = access[2].tripDur;
				toReturn[0] = access[0];
				toReturn[1] = access[1];
				toReturn[2] = new StationAndType();
				toReturn[2].tripDur = duration;
				toReturn[0].bikeSharingType = 1; //access trip (bs-pt)
			}
		}	
		//############################ bs as egress trip ###################################
		if (endStation != null)
		{
			egrC = getEgressTrip(fromFac, toFac, endStation.station, departureTime, person, bsc);
		}
		if (endEStation != null)
		{
			egrE = getEgressTrip(fromFac,toFac, endEStation.station, departureTime, person, bsc);
		}
		double egressTime = Double.POSITIVE_INFINITY;
		
		//egress Trip station
		if (egrC.tripDur < egrE.tripDur)
		{
			if (egrC.tripDur < toReturn[2].tripDur)
			{
				egressTime = egrC.tripDur;
				egress[0] = egrC;
				egress[1] = endStation;
				egress [2] = new StationAndType();
				egress[2].tripDur = egrC.tripDur;
			}
		}
		else if (egrE.tripDur < Double.POSITIVE_INFINITY)
		{
			if (egrE.tripDur < toReturn[2].tripDur)
			{
				egressTime = egrE.tripDur;
				egress[0] = egrE;
				egress[1] = endEStation;
				egress [2] = new StationAndType();
				egress[2].tripDur = egrE.tripDur;
			}
		}
		
		if (egressTime < Double.POSITIVE_INFINITY)
		{
			if (egressTime <= (fullPtTravelTimePlus))
			{
				if (egress[2].tripDur < toReturn[2].tripDur)
				{
					duration = egress[2].tripDur;
					toReturn[0] = egress[0];
					toReturn[1] = egress[1];
					toReturn[2] = new StationAndType();
					toReturn[2].tripDur = duration;
					toReturn[0].bikeSharingType = 2; //egress trip (pt-bs)
					return toReturn;
				}
				else
				{
					return toReturn;
				}
			}
			else
			{
				return toReturn;
			}
		}	
		return null;
	}

	
	/***************************************************************************/
	public StationAndType[] calcPtTripStationsInit (int sStat, int eStat, int sStatE, int eStatE, 
			Coord fromFac, Coord toFac,
			double lenStart, double lenEnd, double lenStartE, double lenEndE,
			StationAndType startStation, StationAndType endStation, 
			StationAndType startEStation, StationAndType endEStation,
			double maxSearchRadiusPtStation,
			Id idFromFac,Id idToFac)
	/***************************************************************************/
	{
		StationAndType[] startAndEndStation = new StationAndType[2];
		//this array stores the start [0] and end [1] station
		
		double distancePTStart = -1;
		double distancePTEnd = -1;
		double distancePTEStart = -1;
		double distancePTEEnd = -1;
		
		StationAndType startStationPt = new StationAndType();
		StationAndType endStationPt  = new StationAndType();
		StationAndType startEStationPt  = new StationAndType();
		StationAndType endEStationPt  = new StationAndType();
		
		startStationPt = chooseClosePTStation(fromFac , maxSearchRadiusPtStation, idFromFac );
		endStationPt =  chooseClosePTStation(toFac, maxSearchRadiusPtStation, idToFac );
		startEStationPt = chooseClosePTEStation(fromFac ,maxSearchRadiusPtStation, idFromFac  );
		endEStationPt = chooseClosePTEStation( toFac,maxSearchRadiusPtStation, idToFac  );
		
		if (sStat + sStatE + eStat + eStatE == 4) //pt trip because of trip length --> use E-Bike
		{
			if (lenStartE < lenEndE)
			{
				
				startAndEndStation[0] = startEStation;
				startAndEndStation[0].bikeSharingType= 1; //bs-pt (access)
				endEStationPt.usedAsPtChange = true;
				startAndEndStation[1] = endEStationPt;	
			}
			
			else
			{
				startEStationPt.usedAsPtChange = true;
				startAndEndStation[0] = startEStationPt;
				startAndEndStation[0].bikeSharingType= 2; //pt-bs (egress)
				startAndEndStation[1] = endEStation;	
			}
		}
		
		else if (sStat + sStatE == 2) //start Station of Conventional Bike and E-Bike found
		{
			if (endStationPt != null && endStationPt.station != null)
			{
				distancePTEnd = CoordUtils.calcEuclideanDistance(endStationPt.station.getCoord(), fromFac);
				//from Fac, as we want to use the bike as tributary
			}
			if (endEStationPt != null && endEStationPt.station!= null)
			{
				distancePTEEnd = CoordUtils.calcEuclideanDistance(endEStationPt.station.getCoord(), fromFac);
			}
			
			if (distancePTEEnd > -1)
			{
				startAndEndStation[0] = startEStation;
				startAndEndStation[0].bikeSharingType = 1; //bs-pt (access)
				endEStationPt.usedAsPtChange = true;
				startAndEndStation[1] = endEStationPt;	
			}
			
			else if (distancePTEnd > -1)
			{
				startAndEndStation[0] = startStation;
				startAndEndStation[0].bikeSharingType = 1; //bs-pt (access)
				endStationPt.usedAsPtChange = true;
				startAndEndStation[1] = endStationPt;
				return startAndEndStation;
			}
			
			else
			{
				return null;
			}
		}
		
		else if (eStat + eStatE == 2) //end Station of Conventional Bike and E-Bike found
		{
			if (startStationPt != null && startStationPt.station != null)
			{
				distancePTStart = CoordUtils.calcEuclideanDistance(startStationPt.station.getCoord(), toFac);
			}
			if (startEStationPt != null && startEStationPt.station!= null)
			{
				distancePTEStart = CoordUtils.calcEuclideanDistance(startEStationPt.station.getCoord(), toFac);
			}
			
			if (distancePTStart > -1)
			{
				startEStationPt.usedAsPtChange = true;
				startAndEndStation[0] = startStationPt;
				startAndEndStation[0].bikeSharingType = 2;//(pt-bs (egress))
				startAndEndStation[1] = endStation;
				return startAndEndStation;
			}
			
			else if (distancePTEStart > -1)
			{
				startEStationPt.usedAsPtChange = true;
				startAndEndStation[0] = startEStationPt;
				startAndEndStation[0].bikeSharingType = 2;//(pt-bs (egress))[1] = endEStation;
				return startAndEndStation;
			}
			
			else
			{
				return null;
			}
		}
		
		else if (sStat + eStatE == 2) //conv start Station und E-endStation found
		{
			if (lenEndE+100 > lenStart)
			{
				if (endStationPt != null && endStationPt.station != null)
				{
					startAndEndStation[0] = startStation;
					endStationPt.usedAsPtChange=true;
					startAndEndStation[0].bikeSharingType = 1;//(bs-pt (access))
					startAndEndStation[1] = endStationPt;
					return startAndEndStation;

				}
				
				else if (startEStationPt != null && startEStationPt.station != null)
				{
					startEStationPt.usedAsPtChange = true;
					startAndEndStation[0].bikeSharingType = 1;//(bs-pt (access))
					startAndEndStation[0] = startEStationPt;
					startAndEndStation[1] = endEStation;
					return startAndEndStation;
				}
				
				else
				{
					return null;
				}
			}
			
			else
			{
				if (startEStationPt != null && startEStationPt.station != null)
				{
					startEStationPt.usedAsPtChange = true;
					startAndEndStation[0].bikeSharingType = 2;//(pt-bs (egress))
					startAndEndStation[0] = startEStationPt;
					startAndEndStation[1] = endEStation;
					return startAndEndStation;
				}
				
				if (endStationPt != null && endStationPt.station != null)
				{
					startAndEndStation[0] = startStation;
					startAndEndStation[0].bikeSharingType = 1;//(bs-pt (access))
					endStationPt.usedAsPtChange = true;
					startAndEndStation[1] = endStationPt;
					return startAndEndStation;
				}
				
				else
				{
					return null; //pt only
				}
			}
		}
		
		else if (eStat + sStatE == 2) //conv end Station und E-startStation found
		{
			if (lenStartE-100 < lenEnd)
			{
				if (endEStationPt != null && endEStationPt.station != null)
				{
					startAndEndStation[0] = startEStation;
					endEStationPt.usedAsPtChange = true;
					startAndEndStation[0].bikeSharingType = 1;//(bs-pt (access))
					startAndEndStation[1] = endEStationPt;
					return startAndEndStation;
				}
				
				else if (startEStationPt != null && startEStationPt.station != null)
				{
					startEStationPt.usedAsPtChange = true;
					startAndEndStation[0].bikeSharingType = 1;//(bs-pt (access))
					startAndEndStation[0] = startEStationPt;
					startAndEndStation[1] = endEStation;
					return startAndEndStation;
				}
				
				else
				{
					return null;
				}
			}
			
			else
			{
				if (startEStationPt != null && startEStationPt.station != null)
				{
					startEStationPt.usedAsPtChange = true;
					startAndEndStation[0].bikeSharingType = 2;//(pt-bs(egress))
					startAndEndStation[0] = startEStationPt;
					startAndEndStation[1] = endEStation;
					return startAndEndStation;
				}
				
				if (endStationPt != null && endStationPt.station != null)
				{
					startAndEndStation[0] = startStation;
					startAndEndStation[0].bikeSharingType = 1;//(bs-pt (access))
					endStationPt.usedAsPtChange=true;
					startAndEndStation[1] = endStationPt;
					return startAndEndStation;
				}
				
				else
				{
					return null;
				}
			}
		}
		else
		{
			if (eStat == 1) // end Station of Conventional Bike found
			{
				if (startStationPt != null && startStationPt.station!= null)
				{
					startStationPt.usedAsPtChange = true;
					startAndEndStation[0] = startStationPt;
					startAndEndStation[1] = endStation;
					if (!(startStationPt == endStation))
					{
					return startAndEndStation;
					}
				}
				else return null;
			}
			
			if (sStat == 1) // start Station of Conventional Bike found
			{
				if (endStationPt != null && endStationPt.station!= null)
				{
					startAndEndStation[0] = startStation;
					endStationPt.usedAsPtChange = true;
					startAndEndStation[0].bikeSharingType = 1;//(bs-pt (access))
					startAndEndStation[1] = endStationPt;
					if (!(endStationPt == startStation))
					{
						return startAndEndStation;
					}
					return startAndEndStation;
				}
				else return null;
			}
			
			else if (sStatE == 1) // startStation of E-Bike found
			{
				if (endEStationPt != null && endEStationPt.station != null)
				{
					startAndEndStation[0] = startEStation;
					endEStationPt.usedAsPtChange = true;
					startAndEndStation[0].bikeSharingType = 1;//(bs-pt (access))
					startAndEndStation[1] = endEStationPt;
					return startAndEndStation;
				}
				else return null;
			}
			
			else if (eStatE == 1) // end Station of E-Bike found
			{
				if (startEStationPt != null && startEStationPt.station != null)
				{
					startEStationPt.usedAsPtChange = true;
					startAndEndStation[0].bikeSharingType = 2;//(pt-bs (egress))
					startAndEndStation[0] = startEStationPt;
					startAndEndStation[1] = endEStation;
					return startAndEndStation;
				}
				else return null;
			}
		}
		return startAndEndStation;
	}

	/***************************************************************************/
	/** returns true, if the conventional bike is used, else it returns false **/
	public boolean isConvBikeUsed (
			double distanceCStart,      
			double distanceCEnd,          
			double distanceEStart,       
			double distanceEEnd)             
	/***************************************************************************/
	{
		
		if (distanceCStart < 0 | distanceCEnd < 0 )
		{
			return false;
		}
		
		if (distanceEStart < 0 | distanceEEnd < 0 )
		{
			return true;
		}
		
		if ((distanceCStart < 0 | distanceCEnd < 0) & (distanceEStart < 0 | distanceEEnd < 0))
		{
			System.out.println("EBike and Bike Stations where not correctly set!");
			System.exit(0);
		}

			
		//chooses e-bikes or bikes for simulation
		if ((distanceCStart + distanceCEnd) < (distanceEStart + distanceEEnd + 200))
		{
			//conventional bikes were chosen
			return true;
		}
		
		else
		{
			//e-bikes were chosen
			return false;
		}
	}
	
	/***************************************************************************/
	public  StationAndType  chooseCloseEStartStation(
			final Coord facility,
			final double searchRadius,
			final double maxSearchRadius,
			final Id facilityId) 
	/***************************************************************************/
	{
		Collection<BikeSharingFacility> stationsInRadius = 
		e_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadius);
			
		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() < 2)
				{
					stationsInRadius = 
					e_qt.getDisk(
							facility.getX(),
							facility.getY(),
							maxSearchRadius*2);
				}

		if (stationsInRadius.size() > 0)
		{
			toReturn.station = e_qt.getClosest(
					facility.getX(),
					facility.getY());
		
			if (toReturn.station.getNumberOfAvailableBikes() == 0)
			{
				if (stationsInRadius.size() > 1)
				{
					List<BikeSharingFacility> list = new ArrayList<BikeSharingFacility>(stationsInRadius);
						
					for (int i = 0; i < stationsInRadius.size(); i++)
					{
						if(list.get(i).getNumberOfAvailableBikes()!= 0)
						{
							toReturn.station = list.get(i);
							toReturn.type = false;
							return toReturn;
						}	
					}
					return null;
				}
			}
			
			toReturn.type = false;
			return toReturn;
		}
		return null;
	}
	
	/***************************************************************************/
	public StationAndType  chooseCloseCStartStation(
			final Coord facility,
			final double searchRadius,
			final double maxSearchRadius,
			final Id facilityId) 
	/***************************************************************************/
	{	
		if (facility== null || b_qt == null)
		{
			return null;
		}
		Collection<BikeSharingFacility> stationsInRadius = 
				b_qt.getDisk(
							facility.getX(),
							facility.getY(),
							searchRadius);
					
				StationAndType toReturn = new StationAndType();
				
				if (stationsInRadius.size() < 2)
						{
							stationsInRadius = 
							b_qt.getDisk(
									facility.getX(),
									facility.getY(),
									maxSearchRadius);
						}
				
				
				if (stationsInRadius.size() > 0)
				{
					toReturn.station = b_qt.getClosest(
							facility.getX(),
							facility.getY());
				
					if (toReturn.station.getNumberOfAvailableBikes() == 0)
					{
						if (stationsInRadius.size() > 1)
						{
							List<BikeSharingFacility> list = new ArrayList<BikeSharingFacility>(stationsInRadius);
								
							for (int i = 0; i < stationsInRadius.size(); i++)
							{
								if(list.get(i).getNumberOfAvailableBikes()!= 0)
								{
									toReturn.station = list.get(i);
									toReturn.type = false;
									return toReturn;
								}	
							}
							
							return null;
						}
					}
					
					toReturn.type = false;
					return toReturn;
				}
				return null;
	}


	/***************************************************************************/
	public StationAndType  chooseCloseEEndStation(
			final Coord facility,
			final double searchRadius,
			final double maxSearchRadius,
			final Id facilityId) 
	/***************************************************************************/
	{	
		Collection<BikeSharingFacility> stationsInRadius = 
		e_qt.getDisk(
					facility.getX(),
					facility.getY(),
					maxSearchRadius);	
		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() < 2)
		{
			stationsInRadius = 
				e_qt.getDisk(
						facility.getX(),
						facility.getY(),
						maxSearchRadius);
		}
		
		if (stationsInRadius.size() == 0) {return null;}
		
		if (stationsInRadius.size() > 0)
		{
				toReturn.station = e_qt.getClosest(
						facility.getX(),
						facility.getY());

				if (toReturn.station.getFreeParkingSlots() == 0)
				{
					if (stationsInRadius.size() > 1)
					{
						List<BikeSharingFacility> list = new ArrayList<BikeSharingFacility>(stationsInRadius);
							
						for (int i = 0; i < stationsInRadius.size(); i++)
						{
							if(list.get(i).getFreeParkingSlots()!= 0)
							{
								toReturn.station = list.get(i);
								toReturn.type = true;
								return toReturn;
							}	
						}
						
						return null;
					}
				}
				
				toReturn.type = true;
				return toReturn;
			}
			return null;
	}

	/***************************************************************************/
	public StationAndType  chooseCloseCEndStation(
			final Coord facility,
			final double searchRadius,
			final double maxSearchRadius,
			final Id facilityId) 
	/***************************************************************************/
	{	
		Collection<BikeSharingFacility> stationsInRadius = 
		b_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadius);
			
		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() < 2)
		{
			stationsInRadius = 
				b_qt.getDisk(
						facility.getX(),
						facility.getY(),
						maxSearchRadius);
		}
		
		if (stationsInRadius.size() > 0)
		{
			toReturn.station = b_qt.getClosest(
					facility.getX(),
					facility.getY());
		
			if (toReturn.station.getFreeParkingSlots() == 0)
			{
				if (stationsInRadius.size() > 1)
				{
					List<BikeSharingFacility> list = new ArrayList<BikeSharingFacility>(stationsInRadius);
						
					for (int i = 0; i < stationsInRadius.size(); i++)
					{
						if(list.get(i).getFreeParkingSlots()!= 0)
						{
							toReturn.station = list.get(i);
							toReturn.type = false;
							return toReturn;
						}	
					}
					
					return null;
				}
			}
			
			toReturn.type = false;
			return toReturn;
		}
		return null;
	}
	/***************************************************************************/
	public StationAndType  chooseClosePTEStartStation(
			final Coord facility,
			final double searchRadiusPtStation,
			final Id facilityId) 
	/***************************************************************************/
	{	
		Collection<BikeSharingFacility> stationsInRadius = 
				e_pt_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadiusPtStation);
			
		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() == 0)
				{
					stationsInRadius = 
							e_pt_qt.getDisk(
							facility.getX(),
							facility.getY(),
							searchRadiusPtStation+200);
				}
		
		if (stationsInRadius.size() > 0)
		{
			toReturn.station = e_pt_qt.getClosest(
					facility.getX(),
					facility.getY());
		
			if (toReturn.station.getNumberOfAvailableBikes() == 0)
			{
				if (stationsInRadius.size() > 1)
				{
					List<BikeSharingFacility> list = new ArrayList<BikeSharingFacility>(stationsInRadius);
						
					for (int i = 0; i < stationsInRadius.size(); i++)
					{
						if(list.get(i).getNumberOfAvailableBikes()!= 0)
						{
							toReturn.station = list.get(i);
							toReturn.type = false;
							return toReturn;
						}	
					}
					
					return null;
				}
			}
			
			toReturn.type = false;
			return toReturn;
		}
		return null;
}


	/***************************************************************************/
	public StationAndType  chooseClosePTEEndStation(
			final Coord facility,
			final double searchRadiusPtStation,
			final Id facilityId) 
	/***************************************************************************/
	{	
		Collection<BikeSharingFacility> stationsInRadius = 
				e_pt_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadiusPtStation);
			
		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() == 0)
		{
			stationsInRadius = 
					e_pt_qt.getDisk(
						facility.getX(),
						facility.getY(),
						searchRadiusPtStation+200);
		}
		
		if (stationsInRadius.size() > 0)
		{
			toReturn.station = e_pt_qt.getClosest(
				facility.getX(),
				facility.getY());
		
			if (toReturn.station.getFreeParkingSlots() == 0)
			{
				if (stationsInRadius.size() > 1)
				{
					List<BikeSharingFacility> list = new ArrayList<BikeSharingFacility>(stationsInRadius);
						
					for (int i = 0; i < stationsInRadius.size(); i++)
					{
						if(list.get(i).getFreeParkingSlots()!= 0)
						{
							toReturn.station = list.get(i);
							toReturn.type = false;
							return toReturn;
						}	
					}
					
					return null;
				}
			}
		}
		
		if (stationsInRadius.size() == 0)
		{
			return null;
		}
		

		if (toReturn.station == null)
		{
			return null;
		}
		
		toReturn.type = true;
		return toReturn;
	}
	
	/***************************************************************************/
	public StationAndType  chooseClosePTStartStation(
			final Coord facility,
			final double searchRadiusPtStation,
			final Id facilityId) 
	/***************************************************************************/
	{	
		if (bikeSharingFacilitiesPt2 == null) return null;
		Collection<BikeSharingFacility> stationsInRadius = 
				b_pt_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadiusPtStation);
			
		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() < 2)
				{
					stationsInRadius = 
							b_pt_qt.getDisk(
							facility.getX(),
							facility.getY(),
							searchRadiusPtStation+200);
				}

		if (stationsInRadius.size() > 0)
		{
			toReturn.station = b_pt_qt.getClosest(
					facility.getX(),
					facility.getY());
		
			if (toReturn.station == null)
			if (toReturn.station.getNumberOfAvailableBikes() == 0)
			{
				if (stationsInRadius.size() > 1)
				{
					List<BikeSharingFacility> list = new ArrayList<BikeSharingFacility>(stationsInRadius);
						
					for (int i = 0; i < stationsInRadius.size(); i++)
					{
						if(list.get(i).getNumberOfAvailableBikes()!= 0)
						{
							toReturn.station = list.get(i);
							toReturn.type = false;
							return toReturn;
						}	
					}
					
					return null;
				}
			}
			
			toReturn.type = false;
			return toReturn;
		}
		return null;
}
	
	/***************************************************************************/
	public StationAndType  chooseClosePTStation(
			final Coord facility,
			final double searchRadiusPtStation,
			final Id facilityId) 
	/***************************************************************************/
	{	
		if (bikeSharingFacilitiesPt2 == null) return null;
		Collection<BikeSharingFacility> stationsInRadius = 
				b_pt_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadiusPtStation);
		
		stationsInRadius = 
							b_pt_qt.getDisk(
							facility.getX(),
							facility.getY(),
							searchRadiusPtStation+200);

		StationAndType toReturn = new StationAndType();

		if (stationsInRadius.size() > 0)
		{
			toReturn.station = b_pt_qt.getClosest(
					facility.getX(),
					facility.getY());

			toReturn.type = false;
			return toReturn;
		}
		return null;
}
	/***************************************************************************/
	public StationAndType  chooseClosePTEStation(
			final Coord facility,
			final double searchRadiusPtStation,
			final Id facilityId) 
	/***************************************************************************/
	{	
		if (bikeSharingFacilitiesPt2 == null) return null;
		Collection<BikeSharingFacility> stationsInRadius = 
				e_pt_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadiusPtStation);
		
		stationsInRadius = 
							e_pt_qt.getDisk(
							facility.getX(),
							facility.getY(),
							searchRadiusPtStation+200);

		StationAndType toReturn = new StationAndType();

		if (stationsInRadius.size() > 0)
		{
			toReturn.station = e_pt_qt.getClosest(
					facility.getX(),
					facility.getY());

			toReturn.type = false;
			return toReturn;
		}
		return null;
}


	/***************************************************************************/
	public StationAndType  chooseClosePTEndStation(
			final Coord facility,
			final double searchRadiusPtStation,
			final Id facilityId) 
	/***************************************************************************/
	{	
		Collection<BikeSharingFacility> stationsInRadius = 
				b_pt_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadiusPtStation);

		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() == 0)
		{
			stationsInRadius = 
					b_pt_qt.getDisk(
						facility.getX(),
						facility.getY(),
						searchRadiusPtStation+200);
		}
		
		if (stationsInRadius.size() > 0)
		{
			toReturn.station = bikeSharingFacilitiesPt2.getCurrentQuadTree().getClosest(
				facility.getX(),
				facility.getY());
			
			if (toReturn.station != null)
			if (toReturn.station.getFreeParkingSlots() == 0)
			{
				if (stationsInRadius.size() > 1)
				{
					List<BikeSharingFacility> list = new ArrayList<BikeSharingFacility>(stationsInRadius);
						
					for (int i = 0; i < stationsInRadius.size(); i++)
					{
						if(list.get(i).getNumberOfAvailableBikes()!= 0)
						{
							toReturn.station = list.get(i);
							toReturn.type = false;
							return toReturn;
						}	
					}
					
					return null;
				}
			}
		}
		
		if (stationsInRadius.size() == 0)
		{
			return null;
		}
		

		if (toReturn.station == null)
		{
			return null;
		}
		
		toReturn.type = true;
		return toReturn;
	} 

	
	/***************************************************************************/
	/** returns true, if publicTransport gets used, within a BS-Trip
	    because of a too long trip length, 
	    needed for intermodal-simulation                                     **/
	public static boolean ptUsedBecauseOfTripLength (
			double distance)
	/***************************************************************************/
	{
		if (distance > 6500)
		{
			return true;
		}
		else
		{	
			return false;
		}
	}
	
	
	/***************************************************************************/
	public  StationAndType  chooseCloseEStationInitial(
			final Coord facility,
			final double searchRadius,
			final double maxSearchRadius,
			final Id facilityId) 
	/***************************************************************************/
	{
		if (e_qt == null)
		{
			return null;
		}
		Collection<BikeSharingFacility> stationsInRadius = 
		e_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadius);
			
		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() > 0)
		{
			toReturn.station = e_qt.getClosest(
					facility.getX(),
					facility.getY());
		
			toReturn.type = false;
			return toReturn;
		}
		return null;
	}
	
	/***************************************************************************/
	public StationAndType  chooseCloseCStationInitial(
			final Coord facility,
			final double searchRadius,
			final double maxSearchRadius,
			final Id facilityId) 
	/***************************************************************************/
	{	if ((bikeSharingFacilities2 == null) || (b_qt == null))
		{
			return null;
		}
	
		if (facility == null)
		{
			return null;
		}
		
		Collection<BikeSharingFacility> stationsInRadius = 
				b_qt.getDisk(
							facility.getX(),
							facility.getY(),
							searchRadius);
					
				StationAndType toReturn = new StationAndType();
				
				if (stationsInRadius.size() > 0)
				{
					toReturn.station = b_qt.getClosest(
							facility.getX(),
							facility.getY());

					toReturn.type = false;
					return toReturn;
				}
				return null;
	}


	/***************************************************************************/
	public StationAndType  chooseClosePTEStationInitial(
			final Coord facility,
			final double searchRadiusPtStation,
			final Id facilityId) 
	/***************************************************************************/
	{	
		Collection<BikeSharingFacility> stationsInRadius = 
				e_pt_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadiusPtStation);
			
		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() == 0)
				{
					stationsInRadius = 
							e_pt_qt.getDisk(
							facility.getX(),
							facility.getY(),
							searchRadiusPtStation+200);
				}
		
		if (stationsInRadius.size() > 0)
		{
			toReturn.station = e_pt_qt.getClosest(
					facility.getX(),
					facility.getY());
			toReturn.type = false;
			return toReturn;
		}
		return null;
	}
	
	/***************************************************************************/
	public StationAndType  chooseClosePTStationInitial(
			final Coord facility,
			final double searchRadiusPtStation,
			final Id facilityId) 
	/***************************************************************************/
	{	
		Collection<BikeSharingFacility> stationsInRadius = 
				b_pt_qt.getDisk(
					facility.getX(),
					facility.getY(),
					searchRadiusPtStation);
		
			
		StationAndType toReturn = new StationAndType();
		
		if (stationsInRadius.size() < 1)
				{
					stationsInRadius = 
							b_pt_qt.getDisk(
							facility.getX(),
							facility.getY(),
							searchRadiusPtStation+200);
				}
		
		if (stationsInRadius.size() > 0)
		{
			toReturn.station = b_pt_qt.getClosest(
					facility.getX(),
					facility.getY());

			toReturn.type = false;
			return toReturn;
		}
		return null;
	}
}





