package org.matsim.core.mobsim.qsim.agents;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.HasPerson;
import org.matsim.core.mobsim.framework.PlanAgent;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.interfaces.MobsimVehicle;
import org.matsim.core.mobsim.qsim.pt.MobsimDriverPassengerAgent;
import org.matsim.core.mobsim.qsim.pt.TransitVehicle;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.eBikeHandling.ReturnBike;
import eu.eunoiaproject.bikesharing.framework.events.AgentStopsWaitingForBikeEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentStopsWaitingForFreeBikeSlotEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentWalksWithVerySlowSpeed;
import eu.eunoiaproject.bikesharing.framework.events.AgentChangesLegAfterAbortWaiting;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.BikeSharingContext;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.relocation.RelocationHandler;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.TakingReturningMethodology;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.WaitingData;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.WaitingListHandling;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.CreateSubtrips;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeAgent;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.Bikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikesE;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.EBikeSharingConfigGroup;
import eu.eunoiaproject.freeFloatingBS.BikeFFImpl;
import eu.eunoiaproject.freeFloatingBS.ChooseBikeToTake;
import eu.eunoiaproject.freeFloatingBS.FFDummyFacility;
import eu.eunoiaproject.freeFloatingBS.FFDummyFacilityImpl;
import org.matsim.vehicles.Vehicle;


/**
 */
public class BikesharingPersonDriverAgentImpl
implements MobsimDriverPassengerAgent,PlanAgent, HasPerson{

	private final static Logger log = Logger.getLogger(BikesharingPersonDriverAgentImpl.class);

	private final BasicPlanAgentImpl basicAgentDelegate;
	private final PlanBasedDriverAgentImpl driverAgentDelegate;
	private final TransitAgentImpl transitAgentDelegate ;

	private final Map<Id<Person>, BikeAgent> agentsC = new HashMap<Id<Person>, BikeAgent>();
	private final Map<Id<Person>, BikeAgent> agentsE = new HashMap<Id<Person>, BikeAgent>();

	private final Scenario scenario ;

	private final BikeSharingFacilities bsFac ;

	private final BikeSharingBikes bSharingVehicles ;

	private final BikeSharingContext bikeSharingContext;


	/***************************************************************************/

	public BikesharingPersonDriverAgentImpl(
		  final Plan plan,
		  MobsimVehicle veh, BikeSharingContext bikeSharingContext)
	/***************************************************************************/
	{
		this.bikeSharingContext = bikeSharingContext;

	    final QSim simulation = bikeSharingContext.getqSim();
		this.scenario = simulation.getScenario() ;

		this.basicAgentDelegate = new BasicPlanAgentImpl( plan, scenario, simulation.getEventsManager(), simulation.getSimTimer() ) ;
		this.driverAgentDelegate = new PlanBasedDriverAgentImpl( this.basicAgentDelegate ) ;
		this.transitAgentDelegate = new TransitAgentImpl( basicAgentDelegate );

		if ( scenario.getConfig().qsim().getNumberOfThreads() != 1 ) {
			throw new RuntimeException("does not work with multiple qsim threads (will use same instance of router)") ; 
		}
		this.basicAgentDelegate.setVehicle(veh);
		this.basicAgentDelegate.getModifiablePlan() ; // this lets the agent make a full copy of the plan, which can then be modified

		this.bsFac = (BikeSharingFacilities) scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME );
		this.bSharingVehicles =(BikeSharingBikes) scenario.getScenarioElement( BikeSharingBikes.ELEMENT_NAME );

	}
	
	@Override
	public void endLegAndComputeNextState(final double now) 
	/***************************************************************************/
	{
		BSRunner runner = new BSRunner();
		runner.planComparison(basicAgentDelegate);
		
		PlanElement nextElem = this.basicAgentDelegate.getNextPlanElement();
		if (nextElem instanceof Activity)
		{
			Activity act = (Activity) nextElem;
			if (!(act.getType().equals("home")))
			{
				act.setStartTime(now);
				if ((!(act.getEndTime()==Double.NEGATIVE_INFINITY)) && act.getEndTime() <= now)
				{
					act.setEndTime(now); //activity duration to 1 min;
				}
			}
		}
	
		runner.planComparison(basicAgentDelegate);

		this.basicAgentDelegate.endLegAndComputeNextState(now);

	}


	/***************************************************************************/
	/**Hebenstreit: We look at the PlanElement Activity and decide what to do 
	 * when the activity ends, this means if we are at a real Activity (home, work, shopping...)
	 * a bike sharing trip can be started with the mode "bs_walk",
	 * when we are at the surreal Activity "eb_interaction" we check if the bike gets
	 * taken or returned and check if the facility has an available (+loaded) bike
	 * or available bike parking respectively, if this is not the case different
	 * choice options (waiting, walking to a different station etc.) 
	 * are made possible**/
	@Override
	public void endActivityAndComputeNextState(final double now) 
	/***************************************************************************/
	{
		BSRunner runner = new BSRunner();
		runner.planComparison(basicAgentDelegate);
		
		//PlanElement last = this.basicAgentDelegate.getCurrentPlan().getPlanElements().get(
		//		this.basicAgentDelegate.getCurrentPlan().getPlanElements().size()-1);//Hebenstreit
		String actPlanMode = this.basicAgentDelegate.getCurrentPlan().getType();

		final PlanElement currentPlanElement = this.basicAgentDelegate.getCurrentPlanElement();
		if ( ! ( currentPlanElement instanceof Activity ) ) {
			log.warn("current plan element of agent=" + this.getId() + " is not instance of activity; following cast will fail.  Full plan =") ;
			for( PlanElement planElement : this.basicAgentDelegate.getCurrentPlan().getPlanElements() ){
				System.err.println( planElement.toString() ) ;
			}
			System.err.println("currentPlanElement=" + currentPlanElement.toString() ) ;
		}


		Activity thisElemX = (Activity) currentPlanElement;
		final Integer currentPlanElementIndex = this.basicAgentDelegate.getCurrentPlanElementIndex() ;
		if ( currentPlanElementIndex != this.basicAgentDelegate.getCurrentPlan().getPlanElements().size())
		{
			thisElemX.setEndTime(now);
		}
		
		handleRelocation (now, bsFac, bSharingVehicles,scenario);
		
		if (actPlanMode.equals(EBConstants.MODE_FF))
		{
			PlanElement thisElem = this.basicAgentDelegate.getCurrentPlanElement();
			PlanElement nextElem = this.basicAgentDelegate.getNextPlanElement();
			if (thisElem instanceof Activity)
			{
				ChooseBikeToTake cbtt = new ChooseBikeToTake(scenario);
				Activity thisAct = (Activity) thisElem;
				if (thisAct.getType().contains("_interaction")) //actual activity is bs-activity
				{
					if ((nextElem instanceof Activity) && (!(((Activity)nextElem).getType().contains("interaction"))))
						//2te interaction Activity - bike is returned, and needs to be added to the list again
					{
						Link link = scenario.getNetwork().getLinks().get(this.basicAgentDelegate.getCurrentLinkId());
						Coord coord = link.getFromNode().getCoord();
						cbtt.addBikeFFToListAndActCoord(thisAct.getFacilityId(), coord, link.getId());
						//log.warn("FF-Bike added: " + thisAct.getFacilityId() + "  at Coord:" + coord);
					}
				}
				else if (thisAct.getType().contains(" interaction")) // actual activity is access/egress activity
				{
					//do nothing
				}
				else //actual activity is plan activity
				{
					List<PlanElement> peList = this.basicAgentDelegate.getCurrentPlan().getPlanElements();
					int actIndex = currentPlanElementIndex;
					
					if (actIndex < this.basicAgentDelegate.getCurrentPlan().getPlanElements().size()-1) //Hebenstreit changed from size()-2
					for (int i = 1; i < this.basicAgentDelegate.getCurrentPlan().getPlanElements().size(); i++) //remove the 4 elements walk-interaction-bsff-interaction
					{
						if (peList.get(actIndex+i)instanceof Activity)
						{
							if (((Activity)peList.get(actIndex+i)).getType().contains("interaction"))
							{
								peList.remove(actIndex+i);
								i--;
							}
							else
							{
								break;
							}
						}
						if (peList.get(actIndex+i) instanceof Leg)
						{
								peList.remove(actIndex+i);
								i--;
						}
					}
					
					Activity nextAct = (Activity)peList.get(actIndex+1);
					nextAct = (Activity)peList.get(actIndex+1);
					double dist = CoordUtils.calcEuclideanDistance(thisAct.getCoord(), nextAct.getCoord());
					BikeFFImpl thisBikeFF = cbtt.chooseNearestBikeFF(scenario, thisAct.getCoord(), dist );
					List<PlanElement> trip = new ArrayList<PlanElement>();
					
					Link one = scenario.getNetwork().getLinks().get(thisAct.getLinkId());
					Link three = scenario.getNetwork().getLinks().get(nextAct.getLinkId());
					
					
					if (thisBikeFF == null)
					{
						//log.warn("Not FF-Bike was found");
						List<PlanElement> pe1 = BSRunner.createPTLegs(thisAct.getCoord(), nextAct.getCoord(), now, this.basicAgentDelegate.getPerson(), 
								scenario, thisAct.getLinkId(), nextAct.getLinkId(), new TransitAgentImpl( basicAgentDelegate));
						if (pe1 == null)
						{
							PlanElement peWalk = BSRunner.createLeg(one, three, EBConstants.BS_WALK_FF, now, this.basicAgentDelegate,
								  bikeSharingContext );
							trip.add(peWalk);
							peList.addAll(actIndex+1, trip);
						}
						else
						{
							trip.addAll(pe1);
							peList.addAll(actIndex+1, trip);
							for (int i = 0; i < peList.size(); i++)
							{
								if (peList.get(i) instanceof Leg)
								{
									Leg leg = ((Leg)peList.get(i));
						
									if ((leg.getMode().equals(TransportMode.transit_walk) && leg.getRoute() == null)
										|| ((leg.getMode().equals(TransportMode.transit_walk)) && (leg.getTravelTime()< 0.1)))
									{
											PlanElement pe = runner.genericRouteWithStartAndEndLink(leg, peList, scenario, i, thisAct.getLinkId(), nextAct.getLinkId());
											trip.remove(i);
											trip.add(i, pe);
									}
								}
							}
						}
					}
					
					else
					{
						Link two = scenario.getNetwork().getLinks().get(thisBikeFF.getLinkId());
						
						PlanElement pe1 = BSRunner.createLeg(one, two, EBConstants.BS_WALK_FF, now, this.basicAgentDelegate,
							  bikeSharingContext );
						List<PlanElement>pe1List = runner.peToPeList(pe1);
						Leg leg1 = (Leg)pe1;
						
						FFDummyFacility dummy1 = new FFDummyFacilityImpl(thisBikeFF.getBikeId(), thisBikeFF.getCoordinate(), Id.createLinkId(thisBikeFF.getLinkId().toString()));
						PlanElement interact1 = CreateSubtrips.createInteractionFF(dummy1, pe1List, leg1.getDepartureTime()+leg1.getTravelTime());
						
						PlanElement pe2 = BSRunner.createLeg(two, three, EBConstants.BS_BIKE_FF, now+leg1.getTravelTime(), this.basicAgentDelegate,
							  bikeSharingContext );
						List<PlanElement>pe2List = runner.peToPeList(pe2);
						Leg leg2 = (Leg)pe2;
						FFDummyFacility dummy2 = new FFDummyFacilityImpl(thisBikeFF.getBikeId(), nextAct.getCoord(), nextAct.getLinkId());
						PlanElement interact2 = CreateSubtrips.createInteractionFF(dummy2, pe2List, leg2.getDepartureTime()+leg2.getTravelTime());
						cbtt.removeBikeFFFromList(thisBikeFF);
						//log.warn("FF-Bike removed: " + thisBikeFF.getBikeId() + "  at Coord: " + thisBikeFF.getCoordinate());
						
						trip.add(pe1);
						trip.add(interact1);
						trip.add(pe2);
						trip.add(interact2);
						peList.addAll(actIndex+1, trip);
					}
					
				}
			}
			runner.planComparison(basicAgentDelegate);
		}
		
		else if (actPlanMode.contains(EBConstants.MODE))
		{		
			//thisElement is an Activity
			PlanElement thisElem = this.basicAgentDelegate.getCurrentPlanElement();
	
			//nextElement is a Leg
			PlanElement nextElem = this.basicAgentDelegate.getNextPlanElement();
			
			if (thisElem instanceof Activity) 
			{
				if (nextElem instanceof Leg)
				{
					if (nextElem != null)
					{
						Leg next = (Leg) nextElem;
						next.setDepartureTime(now);
						
						runner.bsRunner(thisElem, nextElem, now, this.basicAgentDelegate,
							  agentsC, agentsE, bsFac, bSharingVehicles, bikeSharingContext );
						
						if (this.basicAgentDelegate.getNextPlanElement() instanceof Leg)
						{
							next = (Leg) this.basicAgentDelegate.getNextPlanElement();
							next.setDepartureTime(now);
						}
						else
						{
							Activity wait = (Activity) this.basicAgentDelegate.getNextPlanElement();
							wait.setStartTime(now);
							wait.setEndTime(now+3*60);
						}
					}
				}
			}
		}
		if (now >= 900)
			{
				TakingReturningMethodology tr = new TakingReturningMethodology();
				
				if (bsFac.totalWaitingListTake.size() > 0)
				{
					List<WaitingData> waitingListTake = new ArrayList<WaitingData>(bsFac.totalWaitingListTake.values());
					tr.bubbleSort(waitingListTake, waitingListTake.size());
	
					
					if (waitingListTake.get(0) != null)
					{
						while (waitingListTake.get(0).time == now-900)
						{
							handleWaitingTake(now, waitingListTake);
							runner.planComparison(waitingListTake.get(0).bpAgent);
							waitingListTake.remove(0);
							if (waitingListTake.size() == 0) {break;}
						}
					}
				}
				
				if (bsFac.totalWaitingListReturn.size() > 0)
				{
				List<WaitingData> waitingListReturn = new ArrayList<WaitingData>(bsFac.totalWaitingListReturn.values());
				tr.bubbleSort(waitingListReturn, waitingListReturn.size());
				
					if (waitingListReturn.get(0) != null)
					{
						while (waitingListReturn.get(0).time == now-900)
						{
							handleWaitingReturn(now, waitingListReturn);
							runner.planComparison(waitingListReturn.get(0).bpAgent);
							waitingListReturn.remove(0);
							if (waitingListReturn.size() == 0) {break;}
						}
					}
				}
			}
			runner.planComparison(basicAgentDelegate);
		
		if (!this.getState().equals(State.ABORT))
		{
			//System.out.println("Curr_Index: " + basicAgentDelegate.getCurrentPlanElementIndex() + " planSize: " + basicAgentDelegate.getCurrentPlan().getPlanElements().size());
			
			if (this.basicAgentDelegate.getCurrentPlanElement() instanceof Activity)
			{
				this.basicAgentDelegate.endActivityAndComputeNextState(now);
			}
		}
	}
	
	

	private void handleWaitingTake( double now, List<WaitingData> waitingList )
	{
		if (waitingList != null)
			if (waitingList.size()>0)
			{
				if (now - 900 == waitingList.get(0).time)
				{
					//System.out.println(" Time TAKE = " + waitingList.get(0).time + " Person: " + waitingList.get(0).bpAgent.getId());
					Link startLink = null;
					Link endLink = null;
					BasicPlanAgentImpl agentInterim = waitingList.get(0).bpAgent;
					BSRunner runner =  new BSRunner();
					runner.planComparison(agentInterim);
					Activity nextAct = null;
					
//					BikesharingPersonDriverAgentImpl agent2 = new BikesharingPersonDriverAgentImpl( agentInterim );
//					final int index= agent2.getCurrentPlanElementIndex( agentInterim ) ;
					//int index = WithinDayAgentUtils.getCurrentPlanElementIndex( agentInterim );

					int index = agentInterim.getCurrentPlan().getPlanElements().indexOf( agentInterim.getCurrentPlanElement() ) ;


					BikeSharingFacility thisFac = waitingList.get(0).bsFac;
					WaitingListHandling.abortWaitingAtStation(scenario, thisFac, agentInterim, true);
					List<PlanElement> list = agentInterim.getCurrentPlan().getPlanElements();
					if (agentInterim.getCurrentPlanElement() instanceof Leg) //take
					{
						System.out.println("warum! + Person:" + agentInterim.getId()  + " Zeit: " + now); //agent was not removed before
					}
					Activity interaction = (Activity)agentInterim.getCurrentPlanElement();
					interaction.setEndTime(now);//Hebenstreit
					
					//remove all activities and legs between the current plan start and plan end activity
					for (int k = index+1; k < list.size(); k++)
					{
						if (list.get(k) instanceof Activity)
						{
							if (((((Activity)list.get(k)).getType().equals(EBConstants.WAIT)))||(((Activity)list.get(k)).getType().contains(EBConstants.INTERACTION_TYPE_BS)))
							{
								list.remove(k);
								k--;
							}
							
							else
							{
								break;
							}
						}
						
						else
						{
							list.remove(k);
							k--;
						}
					}
					
					startLink = scenario.getNetwork().getLinks().get(interaction.getLinkId());
					nextAct = (Activity)list.get(index+1);
					// --- if waiting is abortet walk is used instead
					endLink = scenario.getNetwork().getLinks().get(nextAct.getLinkId());
					
					
					PlanElement pe = BSRunner.createLeg(startLink, endLink, TransportMode.walk+"ing", now, agentInterim,
						  bikeSharingContext );
					
					
					List<PlanElement> pe2 = BSRunner.createPTLegs(startLink.getCoord(), endLink.getCoord(), now, agentInterim.getPerson(), 
							scenario, startLink.getId(), endLink.getId(), new TransitAgentImpl( basicAgentDelegate)); 
					Leg walkLeg = (Leg)pe;
					
					if (pe2 == null || pe2.size() == 1)
					{
						list.add(index+1, walkLeg);
					}
					
					else
					{
						Leg ptLeg1 = (Leg) pe2.get(0);
						Leg ptLegX = (Leg) pe2.get(pe2.size()-1);
						double startTime = ptLeg1.getDepartureTime();
						double endTime = ptLegX.getDepartureTime() + ptLegX.getTravelTime();
						double ptTravelTime = endTime - startTime;


						List <PlanElement> newLegs = new ArrayList<PlanElement>();
						
						//Choose the Mode, depending to the travel Time!
						if (ptTravelTime < walkLeg.getTravelTime())
						{
							newLegs = pe2;
							((Leg)newLegs.get(0)).setDepartureTime(now);
							list.addAll(index+1, newLegs);
						}
						else
						{
							Leg newLeg = null;
							newLeg = walkLeg;
							list.add(index+1, newLeg);
						}

					}
					agentInterim.getEvents().processEvent(new AgentChangesLegAfterAbortWaiting(
							now, agentInterim.getId(), thisFac.getId()));
					agentInterim.getEvents().processEvent(new AgentStopsWaitingForBikeEvent(
							now, agentInterim.getId(), Id.create(startLink.getId().toString(), ActivityFacility.class)));
					runner.planComparison(agentInterim);
				}
			
			}
	}
	
	private void handleWaitingReturn( double now, List<WaitingData> waitingList2 )
	{
		if (waitingList2!= null)
			if(waitingList2.size()>0)
			{
				if (now - 900 == waitingList2.get(0).time) //weil randomisiert festgelegt wird wann losgestartet wird (zwischen 0 und 60 sec untersch.)
				{
					//System.out.println(" Time Return = " + waitingList2.get(0).time + " Person: " + waitingList2.get(0).bpAgent.getId());
					BasicPlanAgentImpl agentInterim = waitingList2.get(0).bpAgent;
					BSRunner runner = new BSRunner();
					runner.planComparison(agentInterim);
					List<PlanElement> list = agentInterim.getCurrentPlan().getPlanElements(); //return
					if (agentInterim != null)
					{
						BikeSharingFacility thisFac = waitingList2.get(0).bsFac;
						if (agentInterim.getCurrentPlanElement() instanceof Leg)
						{
							System.out.println("    Wrong Type for agent: " + agentInterim.getId());
						}
						/*else
						{
							System.out.println("     Right: " + agentInterim.getId());
						}*/
						Activity interaction = (Activity)agentInterim.getCurrentPlanElement();
						Link startLink = scenario.getNetwork().getLinks().get(interaction.getLinkId());
						WaitingListHandling.abortWaitingAtStation(scenario, thisFac, agentInterim, false);
						agentInterim.getEvents().processEvent(new AgentStopsWaitingForFreeBikeSlotEvent(
								now, agentInterim.getId(), Id.create(startLink.getId().toString(), ActivityFacility.class)));
						agentInterim.getEvents().processEvent(new AgentWalksWithVerySlowSpeed(
							now, agentInterim.getId(), Id.create(thisFac.getLinkId().toString(), ActivityFacility.class)));
						
						Id<Link> walksFrom = startLink.getId();

//						BikesharingPersonDriverAgentImpl agent2 = new BikesharingPersonDriverAgentImpl(agentInterim);
//						final int actIndex= agent2.getCurrentPlanElementIndex(agentInterim) ;
						//int actIndex = WithinDayAgentUtils.getCurrentPlanElementIndex( agentInterim );

						int actIndex = agentInterim.getCurrentPlan().getPlanElements().indexOf( agentInterim.getCurrentPlanElement() ) ;

						List<PlanElement> agentInterimsPlan = agentInterim.getCurrentPlan().getPlanElements();
						
						Id<Link> walksTo = null;

						
						for (int k = actIndex+1; k < list.size(); k++)
						{
							if (list.get(k) instanceof Activity)
							{
								if (((((Activity)list.get(k)).getType().equals(EBConstants.WAIT)))||(((Activity)list.get(k)).getType().contains(EBConstants.INTERACTION_TYPE_BS)))
								{
									list.remove(k);
									k--;
								}
								
								else
								{
									Activity activityPlan = (Activity)agentInterimsPlan.get(k);
									walksTo = activityPlan.getLinkId();
									break;
								}
							}
							
							else
							{
								list.remove(k);
								k--;
							}
						}
						Link startLink2 = scenario.getNetwork().getLinks().get(walksTo);
						Link endLink2 = scenario.getNetwork().getLinks().get(walksFrom);
						PlanElement pe2 = BSRunner.createLeg(startLink2, endLink2, TransportMode.walk+"ing", now+1, agentInterim,
							  bikeSharingContext );
						Leg leg = (Leg)pe2;
						leg.setTravelTime(leg.getTravelTime()*1000);
						if (leg.getTravelTime() < 2500) {leg.setTravelTime(2500);}
						leg.getRoute().setTravelTime(leg.getTravelTime());
						agentInterimsPlan.add(actIndex+1, pe2);
						log.warn("Agent with ID:;" + agentInterim.getId() + ";...did not find a parking; and walks home with low utility");
						agentInterim.setStateToAbort(now);
					}
					runner.planComparison(agentInterim);
				}
			}
	}
	/***************************************************************************/
	private void handleRelocation( double now,
						 BikeSharingFacilities facilities, BikeSharingBikes b, Scenario scenario )
	/***************************************************************************/
	{
		int needRelocation = 0;
		int inRelocation = 0;
		if (bsFac != null)
		{
			if (bsFac.station_in_need_of_relocation != null)
			{
				needRelocation = bsFac.station_in_need_of_relocation.size();
			}
			
			if (bsFac.station_in_relocation != null)
			{
				inRelocation = bsFac.station_in_relocation.size();
			}
		}
		
		for (int i = 0; i < needRelocation; i++) //RELOCATION STARTET WENN
		{
			BikeSharingFacility f = bsFac.station_in_need_of_relocation.get(i);
			double timer = f.getTimeOfRelocation();
			
			final EBikeSharingConfigGroup ebConfig = (EBikeSharingConfigGroup)scenario.getConfig().getModule(EBikeSharingConfigGroup.GROUP_NAME);
			String tInterval = ebConfig.getValue("relocationInterval");
			int timeInterval = Integer.parseInt(tInterval);
			
			boolean stationIsEmpty = false;
			boolean stationIsFull = false;
			
			if (f.getFreeParkingSlots() <= 2)
			{
				stationIsEmpty = true;
			}
			else if (f.getNumberOfAvailableBikes() <= 2)
			{
				stationIsFull = true;
			}
			int n_parameter = 0; //TODO: Make this value a input parameter --> n_parameter
			if (n_parameter > 0)
			{
				if (now - timer >= timeInterval) 
				{
					if (stationIsEmpty)
					{
						RelocationHandler.actStation(f, f.getStationType(), true, n_parameter, now, facilities, b); //TODO:
					}
					if (stationIsFull)
					{
						RelocationHandler.actStation(f, f.getStationType(), false, n_parameter, now, facilities, b); //TODO:
					}
				}
				
				if (now - timer >= timeInterval/2)
				{
					if ((f.getNumberOfAvailableBikes() == 0) && (stationIsFull))
					{
						if (!f.getOngoingRelocation())
						RelocationHandler.actStation(f, f.getStationType(), false, n_parameter, now, facilities, b); //TODO:
					}
					else if ((f.getFreeParkingSlots() == 0) && (stationIsEmpty))
					{
						if (!f.getOngoingRelocation())
						RelocationHandler.actStation(f, f.getStationType(), true, n_parameter, now, facilities, b); //TODO:
					}
				}
			}
		}
		
		for (int i = 0; i < inRelocation; i++) //RELOCATION-ENDE - BIKES MÜSSEN RETURNED WERDEN
		{
			BikeSharingFacility f = bsFac.station_in_relocation.get(i);
			double timer = f.getTimeOfRelocation();
			String returnBike = now + " Relocation: returnBikes at Station: " + f.getId().toString() + ":";
			
			if (now >= timer)
			{
				if ((!(f == null)) && (!(f.getBikesInRelocation() == null)))
				{
					for (int j = 0; j < f.getBikesInRelocation().length; j++)
					{
						if (f.getStationType().contains("c"))
						{
							returnBike = returnBike + f.getBikesInRelocation()[j].toString() + ";";
							ReturnBike.returnBikeC(f, bSharingVehicles.bikes.get(
									Id.create(f.getBikesInRelocation()[j], Bikes.class)), now, facilities);
						}
						else
						{
							BikesE thisBike = bSharingVehicles.ebikes.get(
									Id.create(f.getBikesInRelocation()[j], BikesE.class));
							thisBike.setTime(now-1); 
							//set time to now, cause relocation does not cause a battery discharge
							returnBike = returnBike + f.getBikesInRelocation()[j].toString() + ";";
							ReturnBike.returnBikeE(f, thisBike, now, null, facilities);
							
						}
					}
					returnBike = returnBike + "\n";
					RelocationHandler.writeStringOnFile(returnBike);
					f.setBikesInRelocation(null);
					bsFac.station_in_relocation.remove(i);
					i--;
					inRelocation = inRelocation - 1;
				}
			}
			
		}
	}

	@Override
	public final boolean getEnterTransitRoute(TransitLine line, TransitRoute transitRoute, List<TransitRouteStop> stopsToCome,
			TransitVehicle transitVehicle) {
		return transitAgentDelegate.getEnterTransitRoute(line, transitRoute, stopsToCome, transitVehicle);
	}

	@Override
	public final boolean getExitAtStop(TransitStopFacility stop) {
		return transitAgentDelegate.getExitAtStop(stop);
	}

	@Override
	public final Id<TransitStopFacility> getDesiredAccessStopId() {
		return transitAgentDelegate.getDesiredAccessStopId();
	}

	@Override
	public final Id<TransitStopFacility> getDesiredDestinationStopId() {
		return transitAgentDelegate.getDesiredDestinationStopId();
	}

	@Override
	public final double getWeight() {
		return transitAgentDelegate.getWeight();
	}

	public Scenario getScenario(){
		return basicAgentDelegate.getScenario();
	}

	public EventsManager getEvents(){
		return basicAgentDelegate.getEvents();
	}

	@Override
	public Id<Link> getCurrentLinkId(){
		return driverAgentDelegate.getCurrentLinkId();
	}

	@Override
	public Facility<? extends Facility<?>> getCurrentFacility(){
		return basicAgentDelegate.getCurrentFacility();
	}

	@Override
	public Facility<? extends Facility<?>> getDestinationFacility(){
		return basicAgentDelegate.getDestinationFacility();
	}

	@Override
	public State getState(){
		return basicAgentDelegate.getState();
	}

	@Override
	public double getActivityEndTime(){
		return basicAgentDelegate.getActivityEndTime();
	}

	@Override
	public void setStateToAbort( double now ){
		basicAgentDelegate.setStateToAbort( now );
	}

	@Override
	public Double getExpectedTravelTime(){
		return basicAgentDelegate.getExpectedTravelTime();
	}

	@Override
	public Double getExpectedTravelDistance(){
		return basicAgentDelegate.getExpectedTravelDistance();
	}

	@Override
	public void notifyArrivalOnLinkByNonNetworkMode( Id<Link> linkId ){
		basicAgentDelegate.notifyArrivalOnLinkByNonNetworkMode( linkId );
	}

	@Override
	public Id<Link> getDestinationLinkId(){
		return basicAgentDelegate.getDestinationLinkId();
	}

	@Override
	public String getMode(){
		return basicAgentDelegate.getMode();
	}

	@Override
	public Id<Link> chooseNextLinkId(){
		return driverAgentDelegate.chooseNextLinkId();
	}

	@Override
	public void notifyMoveOverNode( Id<Link> newLinkId ){
		driverAgentDelegate.notifyMoveOverNode( newLinkId );
	}

	@Override
	public boolean isWantingToArriveOnCurrentLink(){
		return driverAgentDelegate.isWantingToArriveOnCurrentLink();
	}

	@Override
	public Id<Person> getId(){
		return basicAgentDelegate.getId();
	}

	@Override
	public PlanElement getCurrentPlanElement(){
		return basicAgentDelegate.getCurrentPlanElement();
	}

	@Override
	public PlanElement getNextPlanElement(){
		return basicAgentDelegate.getNextPlanElement();
	}

	@Override
	public PlanElement getPreviousPlanElement(){
		return basicAgentDelegate.getPreviousPlanElement();
	}

	@Override
	public Plan getCurrentPlan(){
		return basicAgentDelegate.getCurrentPlan();
	}

//	public int getCurrentPlanElementIndex(BasicPlanAgentImpl basicAgentDelegate2) {
//		return basicAgentDelegate2.getCurrentPlanElementIndex();
//	}

	@Override
	public Person getPerson(){
		return basicAgentDelegate.getPerson();
	}

	@Override
	public void setVehicle( MobsimVehicle veh ){
		basicAgentDelegate.setVehicle( veh );
	}

	@Override
	public MobsimVehicle getVehicle(){
		return basicAgentDelegate.getVehicle();
	}

	@Override
	public Id<Vehicle> getPlannedVehicleId(){
		return basicAgentDelegate.getPlannedVehicleId();
	}

}
