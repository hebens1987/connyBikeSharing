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
import org.matsim.core.router.RoutingModule;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.Facility;
import org.matsim.pt.router.TransitRouterImpl;
import org.matsim.core.mobsim.qsim.agents.BSRunner;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;
import org.matsim.core.mobsim.qsim.agents.TransitAgentImpl;

import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules.TUG_BSBikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules.TUG_BSEBikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules.TUG_WalkRoutingModule;
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
						startAndEndStation = calcPtTripStations(sStat, eStat, sStatE, eStatE, fromFac, toFac,
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
						startAndEndStation = calcPtTripStations(sStat, eStat, sStatE, eStatE, fromFac, toFac,
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
					startAndEndStation[1] = endStation;
				}
				else 
				{
					startAndEndStation = calcPtTripStations(sStat, eStat, sStatE, eStatE, fromFac, toFac,
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
					startAndEndStation = calcPtTripStations(sStat, eStat, sStatE, eStatE, fromFac, toFac,
							distanceCStart, distanceCEnd, distanceEStart, distanceEEnd,
							startStation, endStation,
							startEStation, endEStation, 
							maxSearchRadiusPtStation, idFromFac, idToFac);
				}
				
			}
			
			else //public transport needs to be used --> combined
			{
				startAndEndStation = calcPtTripStations(sStat, eStat, sStatE, eStatE, fromFac, toFac,
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
			BasicPlanAgentImpl basicAgentDelegate)
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
				startEStation, endEStation, departureTime, person, basicAgentDelegate);
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
				scenario, fromFacF.getLinkId(), toFacF.getLinkId(), new TransitAgentImpl( basicAgentDelegate));
		
		double travelTime = 0;
		
		if (ptLegs != null)
		{
			for (int i = 0; i < ptLegs.size(); i++)
			{
				if (ptLegs.get(i) instanceof Leg)
				{
					travelTime += ((Leg)ptLegs.get(i)).getTravelTime();
					if (travelTime < 1)
					{
						travelTime += 0.1;
					}
				}
				else if(ptLegs.get(i) instanceof Activity)
				{
					travelTime += (((Activity)ptLegs.get(i)).getStartTime() - ((Activity)ptLegs.get(i)).getEndTime());
				}
			}
		}
		
		if (travelTime < 0.1)
		{
			return Double.POSITIVE_INFINITY;
		}
		return travelTime;
	}

	
	/***************************************************************************/
	public StationAndType getEgressTrip(
			Facility fromFacF,
			Facility toFacF,
			BikeSharingFacility endStation,
			double departureTime,
			Person person)
	/***************************************************************************/
	{
		if (endStation == null)
		{
			StationAndType stat = new StationAndType();
			stat.station = null;
			stat.tripDur = Double.POSITIVE_INFINITY;
			return stat;
		}
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
		
		List<BikeSharingFacility> bsList = new ArrayList<BikeSharingFacility>(bs);
		for (int i = 0; i < bsList.size(); i++) //ensures that no stations is twice or more in list
		{
			for (int j = 0; j < bsList.size(); j++)
			{
				if (i != j)
				{
					if ((bsList.get(i).equals(bsList.get(j))|| bsList.get(j).getNumberOfAvailableBikes() < 1))
					{
						bsList.remove(j);
						j--;
					}
				}
				if (bsList.get(i).getNumberOfAvailableBikes() < 1)
				{
					bsList.remove(i);
					i--;
				}
			}
		}
		
		if (bsList.size() < 1)
		{
			StationAndType stat = new StationAndType();
			stat.station = null;
			stat.tripDur = Double.POSITIVE_INFINITY;
			return stat;
		}
		TransitRouterImpl pt = bSharingVehicles.trImpl;
		RoutingModule walk = new TUG_WalkRoutingModule(scenario);
		List<? extends PlanElement> egressWalk = walk.calcRoute(endStation, toFacF, departureTime, person);
		Leg egressWalkLeg = (Leg)egressWalk.get(0);
		double travelTimeEgressWalk = egressWalkLeg.getTravelTime();
		
		double duration = Double.POSITIVE_INFINITY;
		double travelTimeBike = Double.POSITIVE_INFINITY;
		double travelTimePt = 0;

		BikeSharingFacility startStation = null;
		for (int i = 0; i < bsList.size()-1; i++)
		{
			double durationTemp = Double.POSITIVE_INFINITY;
			List<Leg> egressPt = pt.calcRoute(fromFacF.getCoord(), bsList.get(i).getCoord(), departureTime, person);

			if (egressPt != null)
			{
				for (int j = 0; j < egressPt.size(); j++)
				{
					travelTimePt += egressPt.get(j).getTravelTime();
					if (travelTimePt < 0.1)
					{travelTimePt = 0.1;}
				}
			}
			
			if (travelTimePt < 0.1)
			{
				travelTimePt = Double.POSITIVE_INFINITY;
			}
			
			RoutingModule bikeE = new TUG_BSEBikeRoutingModule(scenario);
			RoutingModule bikeC = new TUG_BSBikeRoutingModule(scenario);
			List<? extends PlanElement> bikeElem;
			if (isEBikeStation)
			{
				bikeElem = bikeE.calcRoute(bsList.get(i), endStation, departureTime, person);
			}
			else
			{
				bikeElem = bikeC.calcRoute(bsList.get(i),endStation, departureTime, person);
			}
			
			Leg bikeLeg = (Leg)bikeElem.get(0);
			travelTimeBike = bikeLeg.getRoute().getDistance()/(16/3.6);
			
			durationTemp = travelTimePt + travelTimeBike + travelTimeEgressWalk;
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
			Person person)
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
		RoutingModule walk = new TUG_WalkRoutingModule(scenario);
		List<?extends PlanElement> accWalk = walk.calcRoute(fromFacF, startStation, departureTime, person);
		Leg accWalkLeg = (Leg)accWalk.get(0);
		double travelTimeAccWalk = accWalkLeg.getTravelTime();
		RoutingModule bike;
		if (isEBikeStation)
		{
			bike = new TUG_BSEBikeRoutingModule(scenario);
		}
		else
		{
			bike = new TUG_BSBikeRoutingModule(scenario);
		}
		List<? extends PlanElement> bikeElem = bike.calcRoute(startStation, endStation, departureTime+travelTimeAccWalk, person);
		Leg bikeLeg = (Leg)bikeElem.get(0);
		double len = bikeLeg.getRoute().getDistance();
		boolean isPt = ptUsedBecauseOfTripLength (len);
		if (isPt)
		{
			return Double.POSITIVE_INFINITY;
		}
		double travelTimeBike = bikeLeg.getRoute().getDistance()/(16/3.6);

		List<?extends PlanElement> egrWalk = walk.calcRoute(endStation, toFacF, departureTime, person);
		Leg egrWalkLeg = (Leg)egrWalk.get(0);
		double travelTimeEgrWalk = egrWalkLeg.getTravelTime();
		
		double travelTime = travelTimeAccWalk + travelTimeBike + travelTimeEgrWalk;
		return travelTime;
	}
	
	/***************************************************************************/
	public StationAndType getAccessTrip(
			Facility fromFacF,
			Facility toFacF,
			BikeSharingFacility startStation,
			double departureTime,
			Person person)
	/***************************************************************************/
	{
		if (startStation == null)
		{
			StationAndType stat = new StationAndType();
			stat.station = null;
			stat.tripDur = Double.POSITIVE_INFINITY;
			return stat;
		}
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
		for (int i = 0; i < bsList.size()-1; i++) //ensures that no stations is twice or more in list
		{
			for (int j = 0; j < bsList.size()-1; j++)
			{
				if (i != j)
				{
					if ((bsList.get(i).equals(bsList.get(j))|| bsList.get(j).getFreeParkingSlots() < 1))
					{
						bsList.remove(j);
						j--;
					}
				}
				if (bsList.get(i).getFreeParkingSlots() < 1)
				{
					bsList.remove(i);
					i--;
				}
			}
		}
		
		if (bsList.size() < 1)
		{
			StationAndType stat = new StationAndType();
			stat.station = null;
			stat.tripDur = Double.POSITIVE_INFINITY;
			return stat;
		}
		
		TransitRouterImpl pt = bSharingVehicles.trImpl;
		RoutingModule walk = new TUG_WalkRoutingModule(scenario);
		List<? extends PlanElement> accessWalk = walk.calcRoute(fromFacF, startStation, departureTime, person);
		Leg accessWalkLeg = (Leg)accessWalk.get(0);
		double travelTimeAccessWalk = accessWalkLeg.getTravelTime();
		
		double duration = Double.POSITIVE_INFINITY;
		
		BikeSharingFacility endStation = null;
		for (int i = 0; i < bsList.size()-1; i++)
		{
			double durationTemp = 0;
			RoutingModule bikeC = new TUG_BSBikeRoutingModule(scenario);
			RoutingModule bikeE = new TUG_BSEBikeRoutingModule(scenario);
			List<? extends PlanElement> bikeElem;
			if (isEBikeStation)
			{
				bikeElem = bikeE.calcRoute(startStation, bsList.get(i), departureTime+travelTimeAccessWalk, person);
			}
			else
			{
				bikeElem = bikeC.calcRoute(startStation, bsList.get(i), departureTime+travelTimeAccessWalk, person);
			}
			
			Leg bikeLeg = (Leg)bikeElem.get(0);
			double travelTimeBike = bikeLeg.getRoute().getDistance()/(16/3.6);
			double travelTimePt = 0;
			List<Leg> egressPt = pt.calcRoute(bsList.get(i).getCoord(), toFacF.getCoord(), departureTime+travelTimeAccessWalk+travelTimeBike, person);
			if (egressPt != null)
			{
				for (int j = 0; j < egressPt.size(); j++)
				{
					travelTimePt += egressPt.get(j).getTravelTime();
					if (travelTimePt < 0.1)
					{travelTimePt = 0.1;}
				}
			}
			
			if (travelTimePt < 0.1)
			{
				travelTimePt = Double.POSITIVE_INFINITY;
			}
			durationTemp = travelTimeAccessWalk + travelTimeBike + travelTimePt;
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
			double departureTime, Person person, BasicPlanAgentImpl basicAgentDelegate)
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

		double fullPtTravelTime = getFullPtTrip(fromFac, toFac, departureTime, person, basicAgentDelegate);
		
		//############################ full bs trip ###################################
		if (startStation != null && endStation != null)
		{
			bsTravelTime = getFullBSTrip(fromFac, toFac, startStation.station, endStation.station, departureTime, person);
		}
		if (startEStation != null && endEStation != null)
		{
			bsETravelTime = getFullBSTrip (fromFac, toFac, startEStation.station, endEStation.station, departureTime, person);
		}
		double bsTime = Double.POSITIVE_INFINITY;
		if (bsTravelTime < bsETravelTime)
		{
			bsTime = bsTravelTime;
			full[0] = startStation;
			full[1] = endStation;
			full [2] = new StationAndType();
			full[2].tripDur = bsTime;
		}
		else
		{
			bsTime = bsETravelTime;
			full[0] = startEStation;
			full[1] = endEStation;
			full [2] = new StationAndType();
			full[2].tripDur = bsTime;
		}
		if (bsTime != Double.POSITIVE_INFINITY)
		{
			if (bsTime <= 60*60)
			{
				if (bsTime <= (fullPtTravelTime*1.75))
				{
					duration = full[2].tripDur;
					toReturn[0] = full[0];
					toReturn[1] = full[1];
					toReturn[2] = new StationAndType();
					toReturn[2].tripDur = duration;
					return toReturn;
				}
			}
			else if (bsTime <= fullPtTravelTime*1.25)
			{
				duration = full[2].tripDur;
				toReturn[0] = full[0];
				toReturn[1] = full[1];
				toReturn[2] = new StationAndType();
				toReturn[2].tripDur = duration;
				return toReturn;
			}
		}
		//############################ bs as access trip ###################################

		if (startStation != null)
		{
			accC = getAccessTrip(fromFac, toFac, startStation.station, departureTime, person);
		}
		if (startEStation != null)
		{
			accE = getAccessTrip(fromFac, toFac, startEStation.station, departureTime,person);
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
		else if (accE.tripDur != Double.POSITIVE_INFINITY)
		{
			accessTime = accE.tripDur;
			access[0] = startEStation;
			access[1] = accE;
			access[2] = new StationAndType();
			access[2].tripDur= accC.tripDur;
		}
		if (accessTime != Double.POSITIVE_INFINITY)
		{
			if (accessTime <= (fullPtTravelTime*1.5))
			{
				duration = access[2].tripDur;
				toReturn[0] = access[0];
				toReturn[1] = access[1];
				toReturn[2] = new StationAndType();
				toReturn[2].tripDur = duration;
			}
		}	
		
		//############################ bs as egress trip ###################################
		if (endStation != null)
		{
			egrC = getEgressTrip(fromFac, toFac, endStation.station, departureTime, person);
		}
		if (endEStation != null)
		{
			egrE = getEgressTrip(fromFac,toFac, endEStation.station, departureTime, person);
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
		else if (egrE.tripDur != Double.POSITIVE_INFINITY)
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
		
		if (egressTime != Double.POSITIVE_INFINITY)
		{
			if (egressTime <= (fullPtTravelTime))
			{
				if (egress[2].tripDur < toReturn[2].tripDur)
				{
					duration = egress[2].tripDur;
					toReturn[0] = egress[0];
					toReturn[1] = egress[1];
					toReturn[2] = new StationAndType();
					toReturn[2].tripDur = duration;
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
	public StationAndType[] calcPtTripStations (int sStat, int eStat, int sStatE, int eStatE, 
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
		
		startStationPt = chooseClosePTStartStation(toFac , maxSearchRadiusPtStation, idFromFac );
		endStationPt =  chooseClosePTEndStation(fromFac, maxSearchRadiusPtStation, idToFac );
		startEStationPt = chooseClosePTEStartStation(toFac ,maxSearchRadiusPtStation, idFromFac  );
		endEStationPt = chooseClosePTEEndStation( fromFac,maxSearchRadiusPtStation, idToFac  );
		
		if (sStat + sStatE + eStat + eStatE == 4) //pt trip because of trip length --> use E-Bike
		{
			if (lenStartE < lenEndE)
			{
				startAndEndStation[0] = startEStation;
				endEStationPt.usedAsPtChange = true;
				startAndEndStation[1] = endEStationPt;	
			}
			
			else
			{
				startEStationPt.usedAsPtChange = true;
				startAndEndStation[0] = startEStationPt;
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
				endEStationPt.usedAsPtChange = true;
				startAndEndStation[1] = endEStationPt;	
			}
			
			else if (distancePTEnd > -1)
			{
				startAndEndStation[0] = startStation;
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
				startAndEndStation[1] = endStation;
				return startAndEndStation;
			}
			
			else if (distancePTEStart > -1)
			{
				System.out.println("Test");
				startEStationPt.usedAsPtChange = true;
				startAndEndStation[0] = startEStationPt;
				startAndEndStation[1] = endEStation;
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
					startAndEndStation[1] = endStationPt;
					return startAndEndStation;

				}
				
				else if (startEStationPt != null && startEStationPt.station != null)
				{
					startEStationPt.usedAsPtChange = true;
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
					startAndEndStation[0] = startEStationPt;
					startAndEndStation[1] = endEStation;
					return startAndEndStation;
				}
				
				if (endStationPt != null && endStationPt.station != null)
				{
					startAndEndStation[0] = startStation;
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
					startAndEndStation[1] = endEStationPt;
					return startAndEndStation;
				}
				
				else if (startEStationPt != null && startEStationPt.station != null)
				{
					startEStationPt.usedAsPtChange = true;
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
					startAndEndStation[0] = startEStationPt;
					startAndEndStation[1] = endEStation;
					return startAndEndStation;
				}
				
				if (endStationPt != null && endStationPt.station != null)
				{
					startAndEndStation[0] = startStation;
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

	
	/***************************************************************************/
	/** returns the most sensible bike-sharing-option, 
	 * (0)bs, (1)bs-pt, (2)pt-bs, (3)changeMode
	    because of long trip length                                           **/
	public static int bikeSharingOptions (StationAndType [] stations)
	/***************************************************************************/
	{
		//stations are the two stations, according to a typ
		// if usedAsPTChange is set to true --> type 1 or 2 are used,
		// according to which station has the true
		// if both stations are set to true --> mode change shall be performed
		
		if (stations == null)
		{
			return 3;
		}
		
		//if only one station was found --> change Mode
		else if ((stations[0] == null) || (stations[1]== null))
		{
			return 3;
		}
		
		//if the same station was found
		else if (stations[0].station == stations[1].station)
		{
			return 3;
		}
		
		
		else if (stations[0].usedAsPtChange == false)
		{
			// if start and end station are a bs station --> bs
			if (stations[1].usedAsPtChange == false)
			{
				return 0; //full bs trip
			}
			
			//if start station is a bs-station but end station is a pt station
			else
			{
				return 1; //first bs, then pt
			}
		}
		
		else if (stations[0].usedAsPtChange == true)
		{
			//if start station is a pt-station but end station is a bs station
			if (stations[1].usedAsPtChange == false)
			{
				return 2; //first pt, then bs
			}
		}
		
		else if (stations[0].station == stations[1].station)
		{
			//if start station equals end station
			return 3; //change mode
		}
		
		else
		{
			return 3;
		}
		return 3;
	}
}





