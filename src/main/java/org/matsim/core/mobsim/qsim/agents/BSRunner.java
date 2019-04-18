package org.matsim.core.mobsim.qsim.agents;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.BSTypeAndPlanElements;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.BikeSharingContext;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.EBikeSharingQsimFactory;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.TakingReturningMethodology;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.BikeSharingStationChoice;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.CalcProbability;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.CalcProbability.Probability;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.CreateSubtrips;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_BikeTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_WalkTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_WalkTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BSAtt;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BSAttribsAgent;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeAgent;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.EBikeSharingConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.StationAndType;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.mobsim.qsim.pt.PTPassengerAgent;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.PopulationFactoryImpl;
import org.matsim.core.population.routes.GenericRouteImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.population.routes.RouteFactoryImpl;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.Facility;
import org.matsim.pt.router.TransitRouter;
import org.matsim.pt.router.TransitRouterImpl;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**

 */
public class BSRunner {

	private final static Logger log = Logger.getLogger(BSRunner.class);
	private BikeSharingContext bikeSharingContext;
	private Scenario scenario;
	/***************************************************************************/
	/**
	 * Hebenstreit: This class implements a BikesharingPersonDriverAgent
	 * It is one of the most important classes for bike-sharing
	 * it recognizes a bs-interaction one step before it will be performed
	 * and creates re-planning possibility during the ongoing mobsim
	 * this means planElements get exchanged or enhanced 
	 * **/
	public BSRunner()
	{
	}

	// -----------------------------------------------------------------------------------------------------------------------------

	
	/**
	 * *************************************************************************/
	void bsRunner(
		  PlanElement thisElem,
		  PlanElement nextElem,
		  double now,
		  BasicPlanAgentImpl basicAgentDelegate,
		  Map<Id<Person>, BikeAgent> agentsC,
		  Map<Id<Person>, BikeAgent> agentsE,
		  BikeSharingFacilities bsFac,
		  BikeSharingBikes bSharingVehicles, 
		  BikeSharingContext bikeSharingContext )
	/***************************************************************************/
	{
		this.bikeSharingContext = bikeSharingContext;
		this.scenario = bikeSharingContext.getqSim().getScenario();
		//TransitRouterImpl trImpl = bSharingVehicles.generatePTRouterForBS(scenario);

		//------------------------------------------
		TakingReturningMethodology trMet = new TakingReturningMethodology();
		planComparison(basicAgentDelegate);
		
		if (!(((Activity)thisElem).getType().contains("interaction") || 
				(((Activity)thisElem).getType().equals(EBConstants.WAIT))))
			//if (!(pAct0.getType().contains(interaction))&&!wait) 
			//the currentActivity has no "interaction" and not "wait" so check Athe initial plan
		{
			Activity nextAct = null;

			final int currentPlanElementIndex = basicAgentDelegate.getCurrentPlanElementIndex();
//			final int currentPlanElementIndex = WithinDayAgentUtils.getCurrentPlanElementIndex( basicAgentDelegate ) ;
			if (basicAgentDelegate.getCurrentPlan().getPlanElements().size()-1 - currentPlanElementIndex != 0)//Hebenstreit: changed from != 1
			{
				final int s = currentPlanElementIndex;
				for (int i = s+1; i <= basicAgentDelegate.getCurrentPlan().getPlanElements().size(); i++)
				{
					if (basicAgentDelegate.getCurrentPlan().getPlanElements().get(i) instanceof Leg)
					{
						basicAgentDelegate.getCurrentPlan().getPlanElements().remove(i);
						i--;
					}
					
					else if (basicAgentDelegate.getCurrentPlan().getPlanElements().get(i) instanceof Activity)
					{
						nextAct = (Activity) basicAgentDelegate.getCurrentPlan().getPlanElements().get(i);
						if (nextAct.getType().contains("interaction")||nextAct.getType().contains("wait"))
						{
							basicAgentDelegate.getCurrentPlan().getPlanElements().remove(nextAct);
							i--;
						}
						else
						{
							nextAct = (Activity) basicAgentDelegate.getCurrentPlan().getPlanElements().get(i);
							break;
						}
					}
				}
				planComparison(basicAgentDelegate);
			}
			//this step deletes the legs and not "plan activities" in between two plan activities
			
			
			Activity fromFac = (Activity)thisElem;
			//System.out.println(fromFac.getType());
			Activity toFac = nextAct;

			//System.out.println(toFac.getType());
			StationAndType[] sat = new StationAndType[2];
			BikeSharingStationChoice bsChoice = new BikeSharingStationChoice(scenario);
			ActivityFacilitiesFactory ff =  scenario.getActivityFacilities().getFactory();
			if (toFac.getCoord() != null)
			{
				BSAtt att = BSAttribsAgent.getPersonAttributes(basicAgentDelegate.getPerson(), scenario);
//				Facility fromFacF = new ActivityFacilityImpl(fromFac.getFacilityId(), fromFac.getCoord(), fromFac.getLinkId());
				Facility fromFacF = ff.createActivityFacility( fromFac.getFacilityId(), fromFac.getCoord(), fromFac.getLinkId());
				Facility toFacF = ff.createActivityFacility( toFac.getFacilityId(), toFac.getCoord(), toFac.getLinkId());
				sat = bsChoice.getStationsDuringSim(fromFacF,toFacF,
						att.searchRadius, att.maxSearchRadius, basicAgentDelegate.getPerson(), now, basicAgentDelegate);
				BSTypeAndPlanElements planElementAndType = calcBSRoute(fromFac, toFac, now, scenario, basicAgentDelegate , sat, bikeSharingContext );
				List<PlanElement> actualPlanElem = planElementAndType.peList;
				int bikeSharingType = planElementAndType.type;
				basicAgentDelegate.getCurrentPlan().getPlanElements().addAll( currentPlanElementIndex +1,actualPlanElem );
				//Hier soll der bs-plan von Act (ohne interaction) zu Act(ohne interaction) auf gültigkeit geprüft werden!
	
				int actIndex = currentPlanElementIndex;
				planComparison(basicAgentDelegate);
	
						
				//TODO: Hebenstreit revise this - as we do it more than once
				//-----get - C -- and - E - BS-Stations--------------------------
				final BikeSharingFacilities bsFacilities = (BikeSharingFacilities)
						scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME );
				BikeSharingFacilities c = new BikeSharingFacilities();
				BikeSharingFacilities e = new BikeSharingFacilities();
				for(Entry<Id<BikeSharingFacility>, BikeSharingFacility> tmp : bsFacilities.getFacilities().entrySet())
				{
					if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().equals("c"))
					{
						c.addFacility(tmp.getValue(),scenario);
					}
					
					if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().equals("e"))
					{
						e.addFacility(tmp.getValue(),scenario);
					}
				}
				//------------------------------------------------------------
	
				planComparison(basicAgentDelegate);		
				CalcProbability cp = new CalcProbability(scenario);
				//FULL BS-Trip
				double searchLength1 = 0;
				double searchLength2 = 0;
				
				if (bikeSharingType == 0)
				{
					searchLength1 = att.maxSearchRadius;
					searchLength2 = att.maxSearchRadius;
				}
				if (bikeSharingType == 1)
				{
					searchLength1 = att.maxSearchRadius;
					searchLength2 = att.ptSearchRadius;
				}
				
				if (bikeSharingType == 2)
				{
					searchLength1 = att.ptSearchRadius;
					searchLength2 = att.maxSearchRadius;
					
				}
				
				boolean itIsAnEStation = false;
				final EBikeSharingConfigGroup ebConfig = (EBikeSharingConfigGroup)scenario.getConfig().getModule(EBikeSharingConfigGroup.GROUP_NAME);

				Probability toUse = cp.getProbability(fromFac.getCoord(), toFac.getCoord(), searchLength1, searchLength2, c, e, scenario);
				if (!(Boolean.parseBoolean(ebConfig.getValue("useProbability")))) //if false
				{
					if (toUse.startStationC != null && toUse.endStationC != null)
						if ((toUse.startStationC.getNumberOfAvailableBikes() > 0)
							&& (toUse.endStationC.getNumberOfAvailableBikes() > 0))//if at least one bicycle is in the station
						{
							toUse.probabilityC = 1;
							itIsAnEStation = false;
						}
					if (toUse.startStationE != null && toUse.endStationE != null)
						if ((toUse.startStationE.getNumberOfAvailableBikes() > 0) 
								&& (toUse.startStationE.getFreeParkingSlots() > 0))
						{
							toUse.probabilityE = 1;
						}
				}
				if(toUse.probabilityE >= toUse.probabilityC)
				{
					itIsAnEStation = true;
				}
						
				planComparison(basicAgentDelegate);
				if (itIsAnEStation)
				{
					handleProbabilityMeasures(toUse.probabilityE, actIndex, basicAgentDelegate.getCurrentPlan().getPlanElements(), fromFac, toFac, toUse.startStationE, toUse.endStationE, scenario, now,
								basicAgentDelegate, bikeSharingType );
				}
					
				else
				{
					handleProbabilityMeasures(toUse.probabilityC, actIndex, basicAgentDelegate.getCurrentPlan().getPlanElements(), fromFac, toFac, toUse.startStationC, toUse.endStationC, scenario, now,
								basicAgentDelegate, bikeSharingType );
				}
				//TODO: Hebenstreit - hier wird nur nach typischen BS-Stationen gesucht - also Start- und End-Station --> nicht nach ptStations
				planComparison(basicAgentDelegate);
				List<PlanElement> trip = basicAgentDelegate.getCurrentPlan().getPlanElements();
				int index = currentPlanElementIndex;
				
				for (int i = index; i < trip.size(); i++)
				{
					if (trip.get(i) instanceof Leg)
					{
						Leg leg = ((Leg)trip.get(i));
			
						if ((leg.getMode().equals(TransportMode.transit_walk) && leg.getRoute() == null)
							|| (leg.getMode().equals(TransportMode.transit_walk) && leg.getTravelTime() < 0.1))
						{
								PlanElement pe = genericRouteWithStartAndEndLink(leg, trip, scenario, i, fromFac.getLinkId(), toFac.getLinkId());
								trip.remove(i);
								trip.add(i, pe);
								//String mode = leg.getMode();
								Route route = leg.getRoute();
								route.setTravelTime(leg.getTravelTime());
								route.setDistance(leg.getTravelTime()/1.5);
								//System.out.println("Hebenstreit: Mode = " + mode);
						}
					}
				}
				
			}
		}
		planComparison(basicAgentDelegate);
		if (nextElem instanceof Leg) 
		{
				Leg leg = (Leg)nextElem;
				Activity nextAct = null;
				Activity thisAct = (Activity) thisElem;  
				
				nextAct =basicAgentDelegate.getNextActivity();
				
				if (thisAct.getType().contains(EBConstants.INTERACTION_TYPE_BS)) 
					//2nd BS-Walk, as the current activity is eb_interaction and the next Leg is bs_walk
					//this means the bike should be returned
					{
		
						if (leg.getMode().equals(EBConstants.BS_WALK))
						{
							boolean successful = trMet.returningABike( basicAgentDelegate, bsFac, thisAct, nextAct, now,
								  agentsC, agentsE, bikeSharingContext );
							if (successful)
							{
								
								BikeSharingFacility station = bsFac.getFacilities().get(thisAct.getFacilityId());
								if (station.getWaitingToTakeBike() != null)
									if (station.getWaitingToTakeBike().size()>0)
									{
										trMet.checkingForWaingToTake(
											thisElem,	basicAgentDelegate,
											bSharingVehicles,	bsFac,
											thisAct, nextAct,
											now,
											  station,	agentsC,
											agentsE,leg, bikeSharingContext);
									}
								((Leg) nextElem).setDepartureTime(now);
								basicAgentDelegate.getNextActivity().setStartTime(now + ((Leg) nextElem).getTravelTime() );
							}
						}
					}
				
				if (leg.getMode().equals(TransportMode.bike))
				{
					System.out.println("Hebenstreit: Warum habe ich hier ein bike im Plan?");
				}
	
				if ((leg.getMode().equals(EBConstants.BS_BIKE))|| (leg.getMode().equals(EBConstants.BS_E_BIKE)))
				{
					boolean successful = trMet.takingABike(thisElem, basicAgentDelegate, bSharingVehicles, bsFac, nextAct,thisAct, now,
						  agentsC, agentsE, leg, bikeSharingContext );
					if (successful)
					{
						BikeSharingFacility station = bsFac.getFacilities().get(thisAct.getFacilityId());
	
						if (station.getWaitingToReturnBike() != null)
							if(station.getWaitingToReturnBike().size() > 0)
						{
						trMet.checkingForWaingToReturn(
							  bSharingVehicles,
								bsFac,
								thisAct,
								nextAct,
								now,
								scenario,
								agentsC,
								agentsE,
							  station, bikeSharingContext );
						}
					}
				}
		}
		planComparison(basicAgentDelegate);
	}
	/***************************************************************************/	
	public PlanElement genericRouteWithStartAndEndLink(Leg leg, 
			List<PlanElement> peList,Scenario scenario,
			int index, Id<Link> fromFac, Id<Link> toFac)
	/***************************************************************************/
	{
		Activity previousAct = null;
		Activity subseqAct = null;
		Id<Link> linkP = null;
		Id<Link> linkN = null;
		if (index == 0)
		{
			linkP = fromFac;
		}
		else
		{
			previousAct = (Activity) peList.get(index-1);
			
		}
		if (previousAct != null)
		{
			if (previousAct.getLinkId() != null)
			{
				linkP = previousAct.getLinkId();
			}
			else if (previousAct.getType().equals("pt interaction"))
			{
				Leg previousLeg = (Leg)peList.get(index-2);
				//scenario.getTransitSchedule().getFacilities();
				if (previousLeg.getRoute() != null)
				{
					linkP = previousLeg.getRoute().getEndLinkId();
				}
				else
				{
					System.out.println("hier etwas einfallen lassen !! Hebenstreit");
				}
			}
		}

		if (index == peList.size()-1)
		{
			linkN = toFac;
		}
		else
		{
			while (peList.get(index+1) instanceof Leg)
			{
				peList.remove(index+1);
			}
			subseqAct = (Activity)peList.get(index+1);
			//System.out.println("SubsequentAct: " + subseqAct);
		}
		if (subseqAct != null)
		{
			if (subseqAct.getLinkId() != null)
			{
				linkN = subseqAct.getLinkId();
			}
			else if (subseqAct.getType().equals("pt interaction"))
			{
				Leg nextLeg = (Leg)peList.get(index+2);
				
				if (nextLeg.getRoute() != null)
				linkN = nextLeg.getRoute().getStartLinkId();
				//scenario.getTransitSchedule().getFacilities();
			}
		}
		
		Link startLink = scenario.getNetwork().getLinks().get(linkP);
		Link endLink = scenario.getNetwork().getLinks().get(linkN);
			
		//System.out.println("LinkP: " + linkP + "  LinkN: " + linkN);
		PlanElement pe = peList.get(index);
			
		Route route = new GenericRouteImpl(linkP, linkN);
		leg = (Leg)pe;
		route.setEndLinkId(endLink.getId());
		route.setStartLinkId(startLink.getId());
		route.setTravelTime(leg.getTravelTime());
		route.setRouteDescription(startLink.getId() + " " + endLink.getId());
		route.setDistance(route.getTravelTime()/(0.9/1.41)); //4 km/h
		leg.setRoute(route);
		
		if (leg.getRoute().getStartLinkId() == null)
				{
			System.out.println("stopp hier");
				}
		return leg;
	}
	
	
	/***************************************************************************/
	public void planComparison(BasicPlanAgentImpl basicAgentDelegate)
	/***************************************************************************/
	{
		List<PlanElement>pe1 = basicAgentDelegate.getCurrentPlan().getPlanElements(); 
		//List<PlanElement>pe2 = basicAgentDelegate.getPerson().getSelectedPlan().getPlanElements();
		//this call will afterwards match pe1;
		setAllPlanElement(basicAgentDelegate.getCurrentPlan(),pe1); //Hebenstreit
	} 
	
	/***************************************************************************/
	/** makes the initial and the executed plan equal - necessary for bike sharing */
	 public final void setAllPlanElement( Plan plan, List<PlanElement> pe) 
	 /***************************************************************************/
	 {
	  plan.getPerson().getSelectedPlan().getPlanElements().clear();
	  plan.getPerson().getSelectedPlan().getPlanElements().addAll(pe);
	 }
	
	
	/***************************************************************************/
	public static List<PlanElement> createPTLegs(
			Coord start, 
			Coord destination,
			double now, 
			Person person,
			Scenario scenario,
			Id<Link> startLinkId,
			Id<Link> endLinkId,
			TransitAgentImpl agent)
	/***************************************************************************/
	{	
		BikeSharingBikes bSharingVehicles = (BikeSharingBikes) 
				scenario.getScenarioElement( BikeSharingBikes.ELEMENT_NAME);
		TransitRouterImpl pt = bSharingVehicles.trImpl;
		
		List<Leg> trip = null;
		/*Id<TransitStopFacility> accessStopId = ((PTPassengerAgent) agent).getDesiredAccessStopId();
		Id<TransitStopFacility> egressStopId = ((PTPassengerAgent) agent).getDesiredDestinationStopId();
		if (accessStopId != null && egressStopId != null)
		{*/
		trip = pt.calcRoute(start, destination, now, person);
		if (trip == null ||trip.size() == 1)
		{
			if (trip == null) { return null;}
			if (((Leg)trip.get(0)).getMode().equals(TransportMode.transit_walk))
			return null;
		}
	
		trip.get(0).setDepartureTime(now);
		trip.get(0).getRoute().setStartLinkId(startLinkId);
		double departure = now;
		
		int i = 1;
		while (i <trip.size())
		{
			departure = departure + trip.get(i-1).getTravelTime();
			trip.get(i).setDepartureTime(departure);
			i++;
		}
		List<PlanElement> peTrip = new ArrayList<PlanElement>();
		
		int j = 0;
		for (j = 0; j<trip.size()-1; j++)
		{
			peTrip.add(trip.get(j));
			peTrip.add(CreateSubtrips.createInteractionPT(null,null, now));
		}
		peTrip.add(trip.get(j));
		
		int k = 0;
		Id<Link> startLinkIdLastPart = null;
		double newStartTime = 0;
		for (k = 0; k < peTrip.size()-1; k++)
		{
			if (k != 0)
			{
				Leg leg = (Leg) peTrip.get(k);
				Activity act = (Activity) peTrip.get(k+1);
				leg.setDepartureTime(((Activity)peTrip.get(k-1)).getEndTime());
				act.setStartTime(leg.getDepartureTime()+leg.getTravelTime());
				act.setEndTime(act.getStartTime()+EBConstants.TIME_RETURN);
				act.setLinkId(leg.getRoute().getEndLinkId());
				leg.getRoute().setDistance(leg.getTravelTime()/1.5);
				leg.getRoute().setTravelTime(leg.getTravelTime());
				startLinkIdLastPart = leg.getRoute().getEndLinkId();
				newStartTime = leg.getDepartureTime() + leg.getTravelTime();
				
			}
			else
			{
				Leg leg = (Leg) peTrip.get(k);
				Activity act = (Activity) peTrip.get(k+1);
				leg.setDepartureTime(now);
				act.setStartTime(leg.getDepartureTime()+leg.getTravelTime());
				act.setEndTime(act.getStartTime()+EBConstants.TIME_RETURN);
				act.setLinkId(leg.getRoute().getEndLinkId());
				leg.getRoute().setDistance(leg.getTravelTime()/1.5);
				leg.getRoute().setTravelTime(leg.getTravelTime());
				newStartTime = leg.getDepartureTime() + leg.getTravelTime();
			}
			k++;
		}
		Leg leg = (Leg) peTrip.get(k);
		Route route = null;
		if (startLinkIdLastPart == null)
		{
			route = new GenericRouteImpl (startLinkId, endLinkId);
			route.setStartLinkId(startLinkId);
		}
		else
		{
			route = new GenericRouteImpl (startLinkIdLastPart, endLinkId);
			route.setStartLinkId(startLinkIdLastPart);
		}
		
		if (route.getStartLinkId() == null)
		{
			System.out.println("warum?");
		}
		
		route.setEndLinkId(endLinkId);
		((Leg)peTrip.get(k)).setRoute(route);
		if (newStartTime < 0.01)
		{
			leg.setDepartureTime(departure);
		}
		else
		{
			leg.setDepartureTime(newStartTime);
		}
		leg.getRoute().setDistance(leg.getTravelTime()/1.5);
		leg.getRoute().setTravelTime(leg.getTravelTime());
		
		return peTrip;
	
	}
	
	public static PlanElement createLeg(
		  Link startLink, Link destinationLink,
		  String mode, double departureTime,
		  BasicPlanAgentImpl basicAgentDelegate, BikeSharingContext bikeSharingContext )
	{
		Scenario scenario = bikeSharingContext.getqSim().getScenario();

		TravelTime btt;
		double travelTime = 0;
		BSRunner runner = new BSRunner();
		runner.planComparison(basicAgentDelegate);
		BicycleConfigGroup confBC = (BicycleConfigGroup) scenario.getConfig().getModule("bicycleAttributes" );

		LeastCostPathCalculator routeAlgo;
		if ((mode.equals(EBConstants.BS_BIKE ))||(mode.equals(EBConstants.BS_E_BIKE ))||(mode.equals(TransportMode.bike )))
		{
			routeAlgo = bikeSharingContext.getSharedBikePathCalculator() ;
			btt = new TUG_BSTravelTime(confBC);
		}
		
		else //walking or bs_walk etc.
		{
			routeAlgo = bikeSharingContext.getWalkPathCalculator() ;
			btt = new TUG_WalkTravelTime( confBC );
		}

		Path path = routeAlgo.calcLeastCostPath(scenario.getNetwork().getLinks().get(startLink.getId() ).getToNode(),
				scenario.getNetwork().getLinks().get(destinationLink.getId()).getFromNode(), departureTime, basicAgentDelegate.getPerson(), null );
		
		double distance = 0.0;
		Id <Link> startLinkId = startLink.getId();
		Id <Link> endLinkId = destinationLink.getId();
		if (path.links.size()>0)
		{
			if (path.links.get(0).getId() != startLinkId)
			{
				path.links.add(0, scenario.getNetwork().getLinks().get(startLinkId));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(0), departureTime, basicAgentDelegate.getPerson(), null);
				travelTime = path.travelTime + travelTimeAddOn;
			}
		
			if (path.links.get(path.links.size()-1) != endLinkId)
			{
				path.links.add(scenario.getNetwork().getLinks().get(destinationLink.getId()));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(path.links.size()-1), departureTime, basicAgentDelegate.getPerson(), null);
				travelTime = path.travelTime + travelTimeAddOn;
			}
		}
		
		else
		{
			Node fromFac_fromNode = scenario.getNetwork().getLinks().get(startLink.getId()).getFromNode();
			Node fromFac_toNode = scenario.getNetwork().getLinks().get(startLink.getId()).getToNode();
			Node toFac_fromNode = scenario.getNetwork().getLinks().get(destinationLink.getId()).getFromNode();
			Node toFac_toNode = scenario.getNetwork().getLinks().get(destinationLink.getId()).getToNode();
			
			if (fromFac_fromNode.getId().equals(toFac_toNode.getId())
					&& fromFac_toNode.getId().equals(toFac_fromNode.getId()))
			{
				distance = CoordUtils.calcEuclideanDistance(startLink.getCoord(), destinationLink.getCoord());
				path.links.add(scenario.getNetwork().getLinks().get(startLink.getId()));
				path.links.add(scenario.getNetwork().getLinks().get(destinationLink.getId()));
				if (distance < 0.1)
				{
					distance = 5;
				}
				double travelTimeAddOn = distance / 4.5;
				travelTime = path.travelTime + travelTimeAddOn;
			}
			
			else if (startLinkId != endLinkId)
			{
				path.links.add(scenario.getNetwork().getLinks().get(startLink.getId()));
				path.links.add(scenario.getNetwork().getLinks().get(destinationLink.getId()));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(0), departureTime, basicAgentDelegate.getPerson(), null)
						+ btt.getLinkTravelTime(path.links.get(path.links.size()-1), departureTime, basicAgentDelegate.getPerson(), null);
				travelTime = path.travelTime + travelTimeAddOn;
			}

			else
			{
				path.links.add(scenario.getNetwork().getLinks().get(startLink.getId()));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(0), departureTime, basicAgentDelegate.getPerson(), null);
				travelTime = path.travelTime + travelTimeAddOn;
				
			}
		}
		//path.links.add(0, scenario.getNetwork().getLinks().get(fromFacility.getLinkId()));
		//path.links.add(scenario.getNetwork().getLinks().get(toFacility.getLinkId()));
		
		NetworkRoute route = new LinkNetworkRouteImpl(startLinkId, endLinkId);
		String routeDescr = "";
		String startLinkIdPath = path.links.get(0).getId().toString();
		String endLinkIdPath = path.links.get(path.links.size()-1).getId().toString();
		
		if (distance >= 5)
		{
			for (int i = 0; i < path.links.size(); i++) 
			{
				routeDescr += path.links.get(i).getId().toString();
			
				if (i != path.links.size())
				{
					routeDescr += " ";
				}
			}
		}
		
		else if (distance < 5)
		{
			for (int i = 0; i < path.links.size(); i++) 
			{
				routeDescr += path.links.get(i).getId().toString();
			
				if (i != path.links.size())
				{
					routeDescr += " ";
				}
				distance = distance + path.links.get(i).getLength();
			}
		}
		else
		{
			routeDescr = "sameLink";
		}
		
		if (travelTime == 0)
		{
			travelTime = path.travelTime;
		}

		route.setStartLinkId(startLinkId);
		route.setEndLinkId(endLinkId);
		
		final Leg leg = new LegImpl(mode);
		//System.out.println("path.travelTime = " + path.travelTime + ";  leg.getTravelTime() = " + leg.getTravelTime());
		route.setTravelTime(travelTime);
		route.setRouteDescription(routeDescr);
		route.setDistance(distance);
		leg.setRoute(route);
		leg.setTravelTime(travelTime);
		leg.setDepartureTime(departureTime);
		return leg;
		
	}
	
	/***************************************************************************/
	public static Route createRouteOnly(
			Id<Link> startLink, Id<Link> endLink, 
			String mode, 
			Scenario scenario,
			Person p)  
	/***************************************************************************/
	{	
		PopulationFactory pf = scenario.getPopulation().getFactory() ;
		RouteFactoryImpl routeFactory = ((PopulationFactoryImpl)pf).getRouteFactory() ;
		Route route = routeFactory.createRoute(routeFactory.getRouteClassForType(mode), startLink, endLink);
		return route;		
	}
	
	
	/***************************************************************************/
	/**Hebenstreit: This method removes all BSPlanElements after the first bs_walk was found,
	 * it does not remove the first found bs_walk, it removes the next elements 
	 * until a plan activity (like home or shopping) was found **/
	public static Activity removeBSPlanElements(
		  List<PlanElement> planElements,
		  BasicPlanAgentImpl basicAgentDelegate2 )
	/***************************************************************************/
	{
//		int actIndex = basicAgentDelegate2.getCurrentPlanElementIndex();
		
		//int actIndex = WithinDayAgentUtils.getCurrentPlanElementIndex( basicAgentDelegate2 ) ;
//		BikesharingPersonDriverAgentImpl agent2 = new BikesharingPersonDriverAgentImpl(basicAgentDelegate2);
//		final int actIndex= agent2.getCurrentPlanElementIndex(basicAgentDelegate2) ;
		final int actIndex = basicAgentDelegate2.getCurrentPlan().getPlanElements().indexOf( basicAgentDelegate2.getCurrentPlanElement() ) ;

		for (int i = actIndex + 1; i < basicAgentDelegate2.getCurrentPlan().getPlanElements().size(); i++)
		{
			PlanElement pe = basicAgentDelegate2.getCurrentPlan().getPlanElements().get(i);
			if (pe instanceof Activity)
			{
				Activity act = (Activity)pe;
				if (act.getType().contains("interaction")||act.getType().contains(EBConstants.WAIT))
				{
					planElements.remove(i);
					i--;
				}
				
				else
				{
					planElements.remove(i);
					return (Activity)pe;
				}
			}
			else if (pe instanceof Leg)
			{
				planElements.remove(i);
				i--;
			}
		}
	return null;
	}
	
	/***************************************************************************/
	/**creates an EBikeSharingConstants.Interaction_Type interaction**/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static PlanElement createInteraction( 
		final Facility facility, double now, double duration, String takeReturn) 
	/***************************************************************************/
	{
		final Activity actE = new ActivityImpl( 
				EBConstants.INTERACTION_TYPE_BS + takeReturn , facility.getCoord() );
		
		actE.setMaximumDuration( duration );
		actE.setLinkId( facility.getLinkId() );
		// XXX This may cause problems if IDs of ActivityFacilities 
		//and BikeSharingFacilities overlap... needs to be checked before
		actE.setFacilityId( facility.getId() );
		actE.setEndTime(now + duration );
		actE.setStartTime(now );
		//String newType= actE.getType().toString()+"_take"; 
		//Hebenstreit: TODO: erst bei Letzter iteration!
		//actE.setType(newType);
		return actE;	
	}
	
	/***************************************************************************/
	private List<PlanElement> getFullTrip( List<PlanElement> first,
							   PlanElement second,
							   List<PlanElement> third,
							   PlanElement fourth,
							   List<PlanElement> fifth,
							   Scenario scenario )
	/***************************************************************************/
	{
		List<PlanElement> trip = new ArrayList<PlanElement>();
		
		trip.addAll(first);
		trip.add(second);
		trip.addAll(third);
		trip.add(fourth);
		trip.addAll(fifth);
		
		return trip;
	}
	
	
	/***************************************************************************/
	/**This method defines 4 main options for (e-)bike-sharing: 
	 * #A [ bike-sharing only (bike or e-bike sharing) (0) ],
	 * #B [ public transport usage because BS-Station too far from Facility
	 * BS-PT (1), BS-PT (2), changeMode(3) ]
	 * #C [ if intermodal is used: 
	 * public transport usage because total-trip-length too long for cycling,   
	 * trips may deviate from those 4 main options]      */
	private BSTypeAndPlanElements calcBSRoute(
		  Activity fromFacility,
		  Activity toFacility,
		  double departureTime,
		  Scenario scenario,
		  BasicPlanAgentImpl basicAgentDelegate,
		  StationAndType[] startAndEnd, BikeSharingContext bikeSharingContext )  //bikeSharingOptionSelection -1 --> full pt Trip
	/***************************************************************************/
	{
		BSTypeAndPlanElements bsReturn = new BSTypeAndPlanElements();
		
		boolean useEBSStart = false;
		boolean useEBSEnd = false;
		boolean useEBS = false;
		Person person = basicAgentDelegate.getPerson();
		StationAndType start = new StationAndType();
		StationAndType end = new StationAndType();
		//TODO: define ptUsage - due to the different possible types:
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

		int bikeSharingOptionSelection;
		// Chain of trip: walk - bike - walk (==0)
		List<PlanElement> firstLeg = null;
		BikeSharingFacility startBSFac = null;
		BikeSharingFacility endBSFac = null;
		if (startAndEnd != null)
		{
			start = startAndEnd[0];	
			end = startAndEnd[1];
			if (start != null )	{
				startBSFac = start.station;
				if (startBSFac != null)
				if (start.station.getStationType().equals("e"))
				{
					useEBSStart = true;
				}
			}
			if (end != null ) {
				endBSFac = end.station;
				if (startBSFac != null)
				if (end.station.getStationType().equals("e"))
				{
					useEBSEnd = true;
				}
			}
			
			if (useEBSEnd == useEBSStart)
			{
				useEBS = useEBSEnd;
			}
			else
			{
				log.warn("Start- and End-Station do not belong to same BS-Type!!!");
			}
			
			bikeSharingOptionSelection = BikeSharingStationChoice.bikeSharingOptions(startAndEnd);
		}
		
		else 
			bikeSharingOptionSelection = 3;

		
		Link fromLink = scenario.getNetwork().getLinks().get(fromFacility.getLinkId());
		Link toLink = scenario.getNetwork().getLinks().get(toFacility.getLinkId());
		// Choice 3: Chain of trip: pt
		List<PlanElement> trip;
		List<PlanElement> thirdLeg;
		List<PlanElement> fifthLeg;
		if (bikeSharingOptionSelection == 3)
		{
			BikeSharingBikes bSharingVehicles = (BikeSharingBikes) 
					scenario.getScenarioElement( BikeSharingBikes.ELEMENT_NAME);
			TransitRouterImpl pt = bSharingVehicles.trImpl;
			trip = createPTLegs(fromFacility.getCoord(),toFacility.getCoord(),departureTime,person,
					scenario, fromFacility.getLinkId(), toFacility.getLinkId(), new TransitAgentImpl( basicAgentDelegate));

			if (trip == null)
			{
				trip = peToPeList(createLeg(fromLink, toLink, EBConstants.BS_WALK, departureTime, basicAgentDelegate, bikeSharingContext ) );
			}

		

			//log.warn("Person#" + basicAgentDelegate.getPerson().getId() + "#cannot use BikeSharing");
		}
		
		// Choice 0: bike sharing only
		else if(bikeSharingOptionSelection == 0)
		{
			if ((startBSFac == null) || (endBSFac== null)) //no start or end station found
			{
//				trip = createPTLegs(fromFacility.getCoord(), toFacility.getCoord(), departureTime, person, scenario, fromFacility.getLinkId(), toFacility.getLinkId());
//				trip = firstLeg;
				trip = peToPeList(createLeg(fromLink, toLink, EBConstants.BS_WALK,departureTime, basicAgentDelegate, bikeSharingContext ) );
			}
			else
			{	Link startStationLink = scenario.getNetwork().getLinks().get(start.station.getLinkId());
				Link endStationLink = scenario.getNetwork().getLinks().get(end.station.getLinkId());	
				firstLeg = peToPeList(createLeg(fromLink, startStationLink, EBConstants.BS_WALK, departureTime, basicAgentDelegate, bikeSharingContext ) );
				double departureTimeTempA = departureTime + ((Leg) firstLeg.get(0)).getTravelTime();
				PlanElement second = CreateSubtrips.createInteractionTakeBS(start.station, firstLeg, departureTimeTempA);
				if (start.station.getStationType().equals("e"))
				{
					thirdLeg = peToPeList(createLeg(startStationLink,endStationLink,EBConstants.BS_E_BIKE, departureTimeTempA+EBConstants.TIME_TAKE, basicAgentDelegate,
						  bikeSharingContext ) );
				}
				else
				{
					thirdLeg = peToPeList(createLeg(startStationLink,endStationLink,EBConstants.BS_BIKE, departureTimeTempA+EBConstants.TIME_TAKE, basicAgentDelegate,
						  bikeSharingContext ) );
				}
				double departureTimeTempC = ((Leg) thirdLeg.get(0)).getDepartureTime() + ((Leg) thirdLeg.get(0)).getTravelTime()+EBConstants.TIME_RETURN;
				PlanElement fourth = CreateSubtrips.createInteractionReturnBS(end.station, thirdLeg, departureTimeTempC);
				fifthLeg = peToPeList(createLeg(endStationLink,toLink,EBConstants.BS_WALK, departureTimeTempC, basicAgentDelegate, bikeSharingContext ) );
				trip = getFullTrip (firstLeg, second, thirdLeg, fourth, fifthLeg,scenario);	
			}
		}
		// Choice 1: Chain of trip: walk - bike - pt	
		else if(bikeSharingOptionSelection == 1)
		{
			if (start == null || end == null)
			{
				trip = createPTLegs(
						fromFacility.getCoord(), toFacility.getCoord(), departureTime, person, 
						scenario, fromFacility.getLinkId(), toFacility.getLinkId() , new TransitAgentImpl( basicAgentDelegate));
				
				if (trip== null)
				{
					trip = peToPeList(createLeg(fromLink, toLink, EBConstants.BS_WALK, departureTime, basicAgentDelegate, bikeSharingContext ) );
				}
			}
			else if (startBSFac == null || endBSFac == null)
			{
				trip = createPTLegs(fromFacility.getCoord(), toFacility.getCoord(), departureTime, person, 
						scenario, fromFacility.getLinkId(), toFacility.getLinkId(), new TransitAgentImpl( basicAgentDelegate));
				if (trip == null)
				{
					trip = peToPeList(createLeg(fromLink, toLink, EBConstants.BS_WALK, departureTime, basicAgentDelegate, bikeSharingContext ) );
				}
			}
			else
			{
				Link startStationLink = scenario.getNetwork().getLinks().get(start.station.getLinkId());
				Link endStationLink = scenario.getNetwork().getLinks().get(end.station.getLinkId());	
				firstLeg = peToPeList(createLeg(fromLink, startStationLink, EBConstants.BS_WALK, departureTime, basicAgentDelegate, bikeSharingContext ) );
				double departureTimeTempA = departureTime + ((Leg) firstLeg.get(0)).getTravelTime() + EBConstants.TIME_TAKE;
				PlanElement second = CreateSubtrips.createInteractionTakeBS(start.station, firstLeg, departureTimeTempA-EBConstants.TIME_TAKE);
				if (start.station.getStationType().equals("e"))
				{
					thirdLeg = peToPeList(createLeg(startStationLink,endStationLink,EBConstants.BS_E_BIKE, departureTimeTempA, basicAgentDelegate, bikeSharingContext ) );
				}
				else
				{
					thirdLeg = peToPeList(createLeg(startStationLink,endStationLink,EBConstants.BS_BIKE, departureTimeTempA, basicAgentDelegate, bikeSharingContext ) );
				}
				double departureTimeTempB = ((Leg) thirdLeg.get(0)).getDepartureTime() + ((Leg) thirdLeg.get(0)).getTravelTime()+EBConstants.TIME_RETURN;
				PlanElement fourth = CreateSubtrips.createInteractionReturnBS( end.station, thirdLeg, departureTimeTempB-EBConstants.TIME_RETURN);
				fifthLeg = createPTLegs(end.station.getCoord(), toFacility.getCoord(), departureTimeTempB, person, 
						scenario, end.station.getLinkId(), toFacility.getLinkId(), new TransitAgentImpl( basicAgentDelegate));
				if (fifthLeg == null)
				{
					fifthLeg = peToPeList(createLeg(fromLink, startStationLink, EBConstants.BS_WALK, departureTimeTempB, basicAgentDelegate, bikeSharingContext ) );
				}
					
				trip = getFullTrip (firstLeg, second, thirdLeg, fourth, fifthLeg, scenario);
			}
		}
		// Choice 2: Chain of trip: pt - bike - walk 			
		else if(bikeSharingOptionSelection == 2)
		{
			Link startStationLink = scenario.getNetwork().getLinks().get(start.station.getLinkId());
			Link endStationLink = scenario.getNetwork().getLinks().get(end.station.getLinkId());	
			firstLeg = createPTLegs(fromFacility.getCoord(),start.station.getCoord(),departureTime,person, 
					scenario, fromFacility.getLinkId(), start.station.getLinkId(), new TransitAgentImpl( basicAgentDelegate));
			//TODO: Hebenstreit
			if (firstLeg == null)
			{
				firstLeg = peToPeList(createLeg(fromLink, startStationLink, EBConstants.BS_WALK, departureTime, basicAgentDelegate, bikeSharingContext ) );
			}
			double departureTimeTempA = departureTime + ((Leg) firstLeg.get(0)).getTravelTime() + EBConstants.TIME_TAKE;
			PlanElement second = CreateSubtrips.createInteractionTakeBS(start.station, firstLeg, departureTimeTempA-EBConstants.TIME_TAKE);

			if (start.station.getStationType().equals("e"))
			{
				thirdLeg = peToPeList(createLeg(startStationLink,endStationLink,EBConstants.BS_E_BIKE,departureTimeTempA, basicAgentDelegate, bikeSharingContext ) );
			}
			else
			{
				thirdLeg = peToPeList(createLeg(startStationLink,endStationLink,EBConstants.BS_BIKE,departureTimeTempA, basicAgentDelegate, bikeSharingContext ) );
			}
			double departureTimeTempB = ((Leg) thirdLeg.get(0)).getDepartureTime() + ((Leg) thirdLeg.get(0)).getTravelTime() + EBConstants.TIME_RETURN ;
			PlanElement fourth = CreateSubtrips.createInteractionReturnBS(end.station, thirdLeg, departureTimeTempB-EBConstants.TIME_RETURN);
			fifthLeg = peToPeList(createLeg(endStationLink,toLink, EBConstants.BS_WALK,departureTimeTempB, basicAgentDelegate, bikeSharingContext ) );
			trip = getFullTrip (firstLeg, second, thirdLeg, fourth, fifthLeg, scenario);	
			
		}

		else
		{
			bikeSharingOptionSelection = 3;
			//log.warn("Es konnte kein Bike-Sharing Typ ausgewählt werden! Agent nutzt nun ÖV oder walk(2)");
			trip = createPTLegs(fromFacility.getCoord(),toFacility.getCoord(),departureTime,person, 
					scenario, fromFacility.getLinkId(), toFacility.getLinkId(), new TransitAgentImpl( basicAgentDelegate));
			if (trip == null)
			{
				trip = peToPeList(createLeg(fromLink,toLink,EBConstants.BS_WALK, departureTime, basicAgentDelegate, bikeSharingContext ) );
			}
		}
		
		for (int i = 0; i < trip.size(); i++)
		{
			if (trip.get(i) instanceof Leg)
			{
				Leg leg = ((Leg)trip.get(i));
	
				if (leg.getMode().equals(TransportMode.transit_walk)&& leg.getRoute() == null)
				{
						PlanElement pe = genericRouteWithStartAndEndLink(leg, trip, scenario, i, fromFacility.getLinkId(), toFacility.getLinkId());
						trip.remove(i);
						trip.add(i, pe);
				
						//String mode = leg.getMode();
						Route route = leg.getRoute();
						route.setTravelTime(leg.getTravelTime());
						route.setDistance(leg.getTravelTime()/1.5);
						//System.out.println("Hebenstreit: Mode = " + mode);
				}
			}
		}
		bsReturn.peList = trip;
		bsReturn.type = bikeSharingOptionSelection;
		planComparison(basicAgentDelegate);
		//log.warn("Person: " + basicAgentDelegate.getId() + ": choose bikeSharingOption: " + bikeSharingOptionSelection );
		return bsReturn;
	}
	/***************************************************************************/
	/**Converts a PlanElement to a List<PlanElement>  */
	List<PlanElement> peToPeList( PlanElement pe )
	/***************************************************************************/
	{
		List<PlanElement> list = new ArrayList<PlanElement>();
		list.add(pe);
		return list;
	}
	
	/***************************************************************************/
	private void handleProbabilityMeasures(
		  double probabilityValue,
		  int actIndex,
		  List<PlanElement> pEList,
		  Activity thisAct,
		  Activity nextAct,
		  BikeSharingFacility startStation,
		  BikeSharingFacility endStation,
		  Scenario scenario,
		  double now,
		  BasicPlanAgentImpl basicAgentDelegate,
		  int routingType )
	/***************************************************************************/
	{
		double random = Math.random();

		Activity act2 = null;
		while (actIndex+1 < basicAgentDelegate.getCurrentPlan().getPlanElements().size())
		{
			PlanElement pe = pEList.get(actIndex+1);
			if (pe instanceof Activity)
			{
				Activity peAct = (Activity) pe;
				if (!(peAct.getType().contains("interaction")))
				{
					act2 = peAct;
					break;
				}
			}
			pEList.remove(actIndex+1);
		}
		
		if ((random > probabilityValue)|| (routingType == 3)) //E und C probability!!!
		{
			//TODO: NO BS --> ChangeMode to Full-PT-Trip
						
			List<PlanElement> ptInsertion = createPTLegs(thisAct.getCoord(), nextAct.getCoord(), 
					now, basicAgentDelegate.getPerson(), scenario, thisAct.getLinkId(), nextAct.getLinkId(), new TransitAgentImpl( basicAgentDelegate));
			
			Leg legInsteadOfPt = null;
			Activity some = null;
			if (ptInsertion == null)
			{
				Link first = scenario.getNetwork().getLinks().get(thisAct.getLinkId());
				Link second = scenario.getNetwork().getLinks().get(nextAct.getLinkId());
				legInsteadOfPt = (Leg)createLeg(first, second, EBConstants.BS_WALK, now, basicAgentDelegate, bikeSharingContext );
				pEList.add(actIndex+1, legInsteadOfPt);
				nextAct.setStartTime(now + legInsteadOfPt.getTravelTime());
				//if (nextAct.getEndTime() < nextAct.getStartTime() && (!nextAct.getType().equals("home")))
				//{
				//	nextAct.setEndTime(nextAct.getStartTime() + 60*5);
				//}//Hebenstreit - Achtung
			}
			
			else
			{
				pEList.addAll(actIndex+1, ptInsertion);
				some = (Activity)pEList.get(actIndex+1+ptInsertion.size());
				Leg last = (Leg) pEList.get(actIndex+ptInsertion.size());
				some.setStartTime(last.getDepartureTime()+last.getTravelTime());
				//if (some.getEndTime() < some.getStartTime()&& (!nextAct.getType().equals("home"))) //Hebenstreit - Achtung
				//{
				//	some.setEndTime(nextAct.getStartTime() + 60*5);
				//}
			}
		}
		
		else
		{
			//Activity act1 = (Activity)pEList.get(actIndex);
			List<PlanElement> act1_int1 = null;
			Activity interact1 = (Activity) CreateSubtrips.createInteractionBS(startStation,"_t", EBConstants.TIME_TAKE);
			List<PlanElement> int1_int2 = null;
			Activity interact2 = (Activity) CreateSubtrips.createInteractionBS(endStation,"_r", EBConstants.TIME_RETURN);
			List<PlanElement>  int2_act2 = null;
			
			Link int1 = scenario.getNetwork().getLinks().get(interact1.getLinkId());
			if (int1 == null)
			{
				System.out.println("Seems that the (Start)Link with id: " + interact1.getLinkId() + "  is not part of the network!");
			}
			Link int2 = scenario.getNetwork().getLinks().get(interact2.getLinkId());
			if (int2 == null)
			{
				System.out.println("Seems that the (End)Link with id: " + interact2.getLinkId() + "  is not part of the network!");
			}
			
			if (act2 == null)
			{
				System.out.println("Hebenstreit: Warum!?");
			}
			Link endLink = scenario.getNetwork().getLinks().get(act2.getLinkId());
			Link startLink = scenario.getNetwork().getLinks().get(thisAct.getLinkId());

			//-------- type 0 = Full BS-Trip
			//walk from Act1 to Interact1
			//bs from Interact1 to Interact2
			//walk from Interact2 to Act2
			if (routingType == 0)
			{
				act1_int1 = peToPeList(createLeg(startLink, int1, EBConstants.BS_WALK, now, basicAgentDelegate, bikeSharingContext ) );
				((Leg)act1_int1.get(0)).setTravelTime(((Leg)act1_int1.get(0)).getTravelTime());
				((Leg)act1_int1.get(0)).getRoute().setTravelTime(((Leg)act1_int1.get(0)).getTravelTime());
				((Leg)act1_int1.get(0)).setDepartureTime(now);
				interact1.setStartTime(now+((Leg)act1_int1.get(0)).getTravelTime());
				interact1.setEndTime(interact1.getStartTime()+EBConstants.TIME_TAKE);
				if (startStation.getStationType().equals("e"))
				{
					int1_int2 = peToPeList(createLeg(int1, int2, EBConstants.BS_E_BIKE, interact1.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
				}
				else
				{
					int1_int2 = peToPeList(createLeg(int1, int2, EBConstants.BS_BIKE, interact1.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
				}
				
				((Leg)int1_int2.get(0)).setDepartureTime(interact1.getEndTime());
				((Leg)int1_int2.get(0)).setTravelTime(((Leg)int1_int2.get(0)).getTravelTime());
				((Leg)int1_int2.get(0)).getRoute().setTravelTime(((Leg)int1_int2.get(0)).getTravelTime());
				interact2.setStartTime(((Leg)int1_int2.get(0)).getDepartureTime()+((Leg)int1_int2.get(0)).getTravelTime());
				interact2.setEndTime(interact2.getStartTime()+EBConstants.TIME_RETURN);
				int2_act2 = peToPeList(createLeg(int2, endLink, EBConstants.BS_WALK, interact2.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
				((Leg)int2_act2.get(0)).setDepartureTime(interact2.getEndTime());
				((Leg)int2_act2.get(0)).setTravelTime(((Leg)int2_act2.get(0)).getTravelTime());
				((Leg)int2_act2.get(0)).getRoute().setTravelTime(((Leg)int2_act2.get(0)).getTravelTime());
			}
			
			//-------- type 1 = First BS - then PT
			// walk from Act1 to Interact 1
			// bs from Interact1 to Interact2
			// pt from Interact2 to Act2
			else if (routingType == 1)
			{
				act1_int1 = peToPeList(createLeg(startLink, int1, EBConstants.BS_WALK, now, basicAgentDelegate, bikeSharingContext ) );
				((Leg)act1_int1.get(0)).setDepartureTime(now);
				interact1.setStartTime(now+((Leg)act1_int1.get(0)).getTravelTime());
				interact1.setEndTime(interact1.getStartTime()+EBConstants.TIME_TAKE);
				if (startStation.getStationType().equals("e"))
				{
					int1_int2 = peToPeList(createLeg(int1, int2, EBConstants.BS_E_BIKE, interact1.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
				}
				else
				{
					int1_int2 = peToPeList(createLeg(int1, int2, EBConstants.BS_BIKE, interact1.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
				}
				((Leg)int1_int2.get(0)).setDepartureTime(interact1.getEndTime());
				((Leg)int1_int2.get(0)).setTravelTime(((Leg)int1_int2.get(0)).getTravelTime());
				((Leg)int1_int2.get(0)).getRoute().setTravelTime(((Leg)int1_int2.get(0)).getTravelTime());
				interact2.setStartTime(((Leg)int1_int2.get(0)).getDepartureTime()+((Leg)int1_int2.get(0)).getTravelTime());
				interact2.setEndTime(interact2.getStartTime()+EBConstants.TIME_RETURN);
				int2_act2 = createPTLegs (int2.getCoord(), act2.getCoord(), now, basicAgentDelegate.getPerson(), 
						scenario, int2.getId(), act2.getLinkId(), new TransitAgentImpl( basicAgentDelegate));
				if (int2_act2 == null) 
				{
					int2_act2 = peToPeList(createLeg(int2, endLink, EBConstants.BS_WALK, interact2.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
				}
				((Leg)int2_act2.get(0)).setDepartureTime(interact2.getEndTime());
			}
			
			
			//-------- type 2 = First PT - then BS
			// pt from Act1 to Interact 1
			// bs from Interact1 to Interact2
			// walk from Interact2 to Act2
			else if (routingType == 2)
			{
				act1_int1 = createPTLegs (startLink.getCoord(), int1.getCoord(), now, basicAgentDelegate.getPerson(), 
						scenario, startLink.getId(), int1.getId(), new TransitAgentImpl( basicAgentDelegate));
				if (act1_int1 == null) 
				{
					act1_int1 = peToPeList(createLeg(int2, endLink, EBConstants.BS_WALK, interact2.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
				}
				((Leg)act1_int1.get(0)).setDepartureTime(now);
				interact1.setStartTime(now+((Leg)act1_int1.get(act1_int1.size()-1)).getDepartureTime() + ((Leg)act1_int1.get(act1_int1.size()-1)).getTravelTime());
				interact1.setEndTime(interact1.getStartTime()+EBConstants.TIME_TAKE);
				if (startStation.getStationType().equals("e"))
				{
					int1_int2 = peToPeList(createLeg(int1, int2, EBConstants.BS_E_BIKE, interact1.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
				}
				else 
				{
					int1_int2 = peToPeList(createLeg(int1, int2, EBConstants.BS_BIKE, interact1.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
				}
				
				((Leg)int1_int2.get(0)).setDepartureTime(interact1.getEndTime());
				((Leg)int1_int2.get(0)).setTravelTime(((Leg)int1_int2.get(0)).getTravelTime());
				((Leg)int1_int2.get(0)).getRoute().setTravelTime(((Leg)int1_int2.get(0)).getTravelTime());
				interact2.setStartTime(((Leg)int1_int2.get(0)).getDepartureTime()+((Leg)int1_int2.get(0)).getTravelTime());
				interact2.setEndTime(interact2.getStartTime()+EBConstants.TIME_RETURN);
				int2_act2 = peToPeList(createLeg(int2, endLink, EBConstants.BS_WALK, interact2.getEndTime(), basicAgentDelegate, bikeSharingContext ) );
			}
			
			//add all Elements
			List <PlanElement> trip = new ArrayList<PlanElement>();
			trip.addAll(act1_int1);
			trip.add(interact1);
			trip.addAll(int1_int2);
			trip.add(interact2);
			trip.addAll(int2_act2);
			

			pEList.addAll(actIndex+1, trip);

			
			/*System.out.println("TravelTime: " + act1_int1.getTravelTime() + "   DepTime: " + act1_int1.getDepartureTime());
			System.out.println("EndTime:    " + interact1.getEndTime() + "   Dur:" + interact1.getMaximumDuration());
			System.out.println("TravelTime: " + int1_int2.getTravelTime() + "   DepTime: " + int1_int2.getDepartureTime());
			System.out.println("EndTime:   " + interact2.getEndTime() + "   Dur: " + interact2.getMaximumDuration());
			System.out.println("TravelTime: " + int2_act2.getTravelTime() + "   DepTime: " + int2_act2.getDepartureTime());*/
			planComparison(basicAgentDelegate);
			//System.out.println("Test");
			}
	}
}
