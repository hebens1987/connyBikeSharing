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

import java.util.List;
import java.util.Map;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeAgent;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.Bikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikesE;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.facilities.ActivityFacility;

import eu.eunoiaproject.bikesharing.framework.eBikeHandling.ReturnBike;
import eu.eunoiaproject.bikesharing.framework.eBikeHandling.TakeABike;
import eu.eunoiaproject.bikesharing.framework.events.AgentStopsWaitingForBikeEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentStopsWaitingForFreeBikeSlotEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentFoundBikeEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentFoundParkingEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.BikeSharingContext;
import org.matsim.core.mobsim.qsim.agents.BSRunner;


public class TakingReturningMethodology {
	
	private final static Logger log = Logger.getLogger(TakingReturningMethodology.class);
	
	public boolean takingABike(
		  PlanElement thisElem,
		  BasicPlanAgentImpl basicAgentDelegate,
		  BikeSharingBikes bSharingVehicles,
		  BikeSharingFacilities bsFac,
		  Activity nextAct,
		  Activity pAct0,
		  double now,
		  Map<Id<Person>, BikeAgent> agentsC,
		  Map<Id<Person>, BikeAgent> agentsE,
		  Leg leg, BikeSharingContext bikeSharingContext )
	{
		
		BSRunner runner = new BSRunner();
		runner.planComparison(basicAgentDelegate);
		//TransitRouterImpl trImpl = bSharingVehicles.generatePTRouterForBS(scenario);
		boolean value = false;
		Activity pAct = (Activity) thisElem; 
		//thisElem must be bs_interaction - this means a bike shall be taken
		BikeSharingFacility station = bsFac.getFacilities().get(pAct.getFacilityId());
		Id<Person> personId = basicAgentDelegate.getPerson().getId();
		//log.info(now + ": Agent with ID " + personId + 
		//		" tries to take Bike at Station: " + station.getId());
		
		if (station != null)
		if (station.getStationType().equals("c"))
		{
			Bikes bike = TakeABike.takeCBike(station, bSharingVehicles.getFacilities(), now, bsFac);
			if (bike == null)
			{
				if (basicAgentDelegate.getNextPlanElement() instanceof Activity)
				{
					log.info("Agent with ID:;" + personId + 
							";is still Waiting for a C-Bike at Station: " + station.getId());
				}
				else
				{
					log.info("Agent with ID:; " + personId + 
						";could not take a C-Bike at Station:;" + station.getId());
					//Insert new Choice Strategy (C)
					NoBikeAvailable.noBikeAvailable(
						station, nextAct, now, basicAgentDelegate, bikeSharingContext ); //false is not EBike
					value = false;
				}
			}
			else
			{
				Activity actThis = (Activity)thisElem;
				if (actThis.getType().equals("wait"))
				{
					log.info("Agent with ID:;" + personId + ";was waiting at Station;" + station.getId() + ";and got a bike");
				}
				log.info("Agent with ID:;" + personId + 
						";took the C-Bike at Station:;" + station.getId() + ";(Bike = " + bike.getBikeId() + ")" );
				BikeAgent newAgC = new BikeAgent();
				newAgC.setBike(bike);
				newAgC.setPersonId(personId);
				agentsC.put(personId,newAgC);
				value = true;
				
				List<WaitingData> statList = station.getWaitingToTakeBike();
				if (statList != null)
				for (int j = 0; j < statList.size(); j++)
				{
					if (statList.get(j).personId.equals(personId))
					{
						statList.remove(j);
						station.setWaitingToTakeBike(statList);
						break;
					}
				}
				//should be bs
			}
		}
		else
		{
			BikesE bikeE = TakeABike.takeEBike(station, bSharingVehicles.getEFacilities(), now, basicAgentDelegate, bsFac, leg);
			if (bikeE == null)
			{
				if (basicAgentDelegate.getNextPlanElement() instanceof Activity)
				{
					log.info("Agent with ID:;" + personId + 
							";still Waiting for a E-Bike at Station:;" + station.getId());
				}
				else
				{
					log.info("Agent with ID:;" + personId + 
							";could not take an C-Bike at Station:;" + station.getId());
					//Insert new Choice Strategy (E)
					NoBikeAvailable.noBikeAvailable(
							station, nextAct, now, basicAgentDelegate, bikeSharingContext ); //true - it is an EBike
					value = false;
				}
			}
			else
			{
				Activity actThis = (Activity)thisElem;
				log.info("Agent with ID:;" + personId + 
						";took an E-Bike at Station:;" + station.getId() + ";(BikeId= ;" + bikeE.getBikeId() + ")");
				BikeAgent newAgE = new BikeAgent();
				newAgE.setBikeE(bikeE);
				newAgE.setPersonId(personId);
				agentsE.put(personId,newAgE);
				
				PopulationFactory pf = bikeSharingContext.getqSim().getScenario().getPopulation().getFactory();;
				pf.createRoute(
						NetworkRoute.class,
						leg.getRoute().getStartLinkId(), 
						leg.getRoute().getEndLinkId());
				List<WaitingData> statList = station.getWaitingToTakeBike();
				if (statList != null)
				for (int j = 0; j < statList.size(); j++)
				{
					if (statList.get(j).personId.equals(personId))
					{
						statList.remove(j);
						station.setWaitingToTakeBike(statList);
						break;
					}
				}
				value = true;
				
			}
		}
		runner.planComparison(basicAgentDelegate);
		return value;
	}
	
	public void checkingForWaingToReturn(
		  BikeSharingBikes bSharingVehicles,
		  BikeSharingFacilities bsFac,
		  Activity pAct0,
		  Activity nextAct,
		  double now,
		  Scenario scenario,
		  Map<Id<Person>, BikeAgent> agentsC,
		  Map<Id<Person>, BikeAgent> agentsE,
		  BikeSharingFacility station, BikeSharingContext bikeSharingContext )
	{
		//if a bike was successfully taken, check the toReturn waiting list, if its length is greater zero, make agent arrive
		if (station.getWaitingToReturnBike()!=null)
		{
			if (station.getWaitingToReturnBike().size()>0)
			{
				BSRunner runner = new BSRunner();
				runner.planComparison(station.getWaitingToReturnBike().get(0).bpAgent);
				BasicPlanAgentImpl agentInterim1 = station.getWaitingToReturnBike().get(0).bpAgent;
				returningABike( station.getWaitingToReturnBike().get(0 ).bpAgent,
					  bsFac, nextAct, pAct0, now,
					  agentsC, agentsE, bikeSharingContext );
				
				log.warn("Agent with ID:;" + agentInterim1.getId() + ";was waiting to return a bike, while a bike was taken!;"  + now);
				agentInterim1.getEvents().processEvent(new AgentFoundParkingEvent(
						now, agentInterim1.getId(), station.getId()));
				//String personId = agentInterim1.getId().toString();
				agentInterim1.getEvents().processEvent(new AgentStopsWaitingForFreeBikeSlotEvent(
				now, agentInterim1.getId(), Id.create(station.getId().toString(), ActivityFacility.class)));
//				final int agentInterim1CurrentPlanElementIndex = agentInterim1.getCurrentPlanElementIndex();
//				BikesharingPersonDriverAgentImpl agent2 = new BikesharingPersonDriverAgentImpl(agentInterim1);
//				final int agentInterim1CurrentPlanElementIndex= agent2.getCurrentPlanElementIndex(agentInterim1) ;
				int agentInterim1CurrentPlanElementIndex = agentInterim1.getCurrentPlan().getPlanElements().indexOf( agentInterim1.getCurrentPlanElement() ) ;
				if (agentInterim1.getCurrentPlan().getPlanElements().get( agentInterim1CurrentPlanElementIndex ) instanceof Leg)
				{
					System.out.println("TODO: Hebenstreit  ....  TakingReturningMethodology 189");
				}
				Activity act = (Activity)agentInterim1.getCurrentPlan().getPlanElements().get( agentInterim1CurrentPlanElementIndex );
				act.setEndTime(now);

				List<PlanElement> list = agentInterim1.getCurrentPlan().getPlanElements();
				
				int index = agentInterim1CurrentPlanElementIndex;
				while (list.get(index+1) instanceof Activity)
				{					
					list.remove(list.get(index+1));
				}
				Leg leg2 = (Leg)agentInterim1.getNextPlanElement();
				leg2.setDepartureTime(now);
				
				act.setEndTime(now);
				list.remove(list.get(index+1));
				list.add(index+1, leg2);

				WaitingListHandling.abortWaitingAtStation (scenario,station,agentInterim1,false);
				runner.planComparison(agentInterim1);
			}
		}
		
	}
	
		// TODO Auto-generated method stub
	
	public void checkingForWaingToTake(
		  PlanElement thisElem,
		  BasicPlanAgentImpl basicAgentDelegate,
		  BikeSharingBikes bSharingVehicles,
		  BikeSharingFacilities bsFac,
		  Activity pAct0,
		  Activity nextAct,
		  double now,
		  BikeSharingFacility station,
		  Map<Id<Person>, BikeAgent> agentsC,
		  Map<Id<Person>, BikeAgent> agentsE,
		  Leg legInp, BikeSharingContext bikeSharingContext )
	{
		BSRunner runner = new BSRunner();
		runner.planComparison(basicAgentDelegate);
			//if a bike was successfully returned, check the toTake waiting list, if its length is greater zero, make agent arrive
			if (station.getWaitingToTakeBike()!=null)
			{
				if (station.getWaitingToTakeBike().size()>0)
				{
					BasicPlanAgentImpl agentInterim1 = station.getWaitingToTakeBike().get(0).bpAgent;
					takingABike(
							thisElem, station.getWaitingToTakeBike().get(0).bpAgent, 
							bSharingVehicles, bsFac, nextAct, pAct0, now,
						  agentsC, agentsE, legInp, bikeSharingContext );

					log.warn("Agent with ID:;" + agentInterim1.getId() + ";was waiting to take a bike, while a bike was returned!;"  + now);
					agentInterim1.getEvents().processEvent(new AgentFoundBikeEvent(
							now, agentInterim1.getId(), station.getId()));
					//String personId = agentInterim1.getId().toString();
					agentInterim1.getEvents().processEvent(new AgentStopsWaitingForBikeEvent(
					now, agentInterim1.getId(), Id.create(station.getId().toString(), ActivityFacility.class)));
//					final int agentInterim1CurrentPlanElementIndex = agentInterim1.getCurrentPlanElementIndex();
					//final int agentInterim1CurrentPlanElementIndex = WithinDayAgentUtils.getCurrentPlanElementIndex( agentInterim1 ) ;
//					BikesharingPersonDriverAgentImpl agent2 = new BikesharingPersonDriverAgentImpl(agentInterim1);
//					final int agentInterim1CurrentPlanElementIndex= agent2.getCurrentPlanElementIndex(agentInterim1) ;
					int agentInterim1CurrentPlanElementIndex = agentInterim1.getCurrentPlan().getPlanElements().indexOf( agentInterim1.getCurrentPlanElement() ) ;
					//if (agentInterim1.getCurrentPlan().getPlanElements().get( agentInterim1CurrentPlanElementIndex ) instanceof Leg)
					//		{
					//			System.out.println("ERROR: TODO: HEBENSTREIT");
					//		}
					Activity act = (Activity)agentInterim1.getCurrentPlan().getPlanElements().get( agentInterim1CurrentPlanElementIndex );
					act.setEndTime(now);

					List<PlanElement> list = agentInterim1.getCurrentPlan().getPlanElements();
					
					int index = agentInterim1CurrentPlanElementIndex;
					while (list.get(index+1) instanceof Activity)
					{					
						list.remove(list.get(index+1));
					}
					Leg leg2 = (Leg)agentInterim1.getNextPlanElement();
					leg2.setDepartureTime(now);
					act.setEndTime(now);
					list.remove(list.get(index+1));
					list.add(index+1, leg2);

					
					WaitingListHandling.abortWaitingAtStation (bikeSharingContext.getqSim().getScenario(),station,agentInterim1,true);
					
				}
				//a bike was returned - so a new bike can be taken at the same time
			}
			runner.planComparison(basicAgentDelegate);
	}
	

	public boolean returningABike(
		  BasicPlanAgentImpl basicAgentDelegate,
		  BikeSharingFacilities bsFac,
		  Activity thisAct,
		  Activity nextAct,
		  double now,
		  Map<Id<Person>, BikeAgent> agentsC,
		  Map<Id<Person>, BikeAgent> agentsE, BikeSharingContext bikeSharingContext )
	{
		BSRunner runner = new BSRunner();
		runner.planComparison(basicAgentDelegate);
		boolean returnCompletedE = false;
		boolean returnCompletedC = false;
		BikeSharingFacility station = bsFac.getFacilities().get(thisAct.getFacilityId());
		Id<Person> personId = basicAgentDelegate.getPerson().getId();
		boolean value = false;

		//conventional bike sharing
		if (station.getStationType().equals("c"))
		{
			//if there isn't the right agent in the storage, something went wrong
			if (agentsC.get(personId) != null)
			{
				Bikes bike = agentsC.get(personId).getBike();
				if (bike != null)
				{
					//log.info(now + ": Agent with ID" + personId +
					//		" tries to return C-Bike at Station: " + station.getId());
					returnCompletedC = ReturnBike.returnBikeC(station, bike, now, bsFac);
					
					if (returnCompletedC)
					{
						log.info("Agent with ID: ;" + personId + 
								";returned a C-Bike  at Station:;" + station.getId() + ";" + now);
						List<WaitingData> statList = station.getWaitingToReturnBike();
						if (statList != null)
						for (int j = 0; j < statList.size(); j++)
						{
							if (statList.get(j).personId.equals(personId))
							{
								statList.remove(j);
								station.setWaitingToReturnBike(statList);
								break;
							}
						}
						agentsC.remove(personId);
						value = true;
					}
					
					else
					{
						log.info("Agent with ID:;" + personId +
								";was not able to park bike at C-station;" + station.getStationId());
						
						if (agentsC.get(personId) == null)
						{
							log.warn("### Agent with Id:" + personId + " wasn't found in c-bike-list");
						}
						
						if(!(bsFac.totalWaitingListReturn.containsKey(basicAgentDelegate.getPerson().getId())))
						{
							NoParkingAvailable.noParkingAvailable(station, nextAct, now, basicAgentDelegate,
								  bikeSharingContext );
							value = false;
						}
					}
				}	
			}
		}
		
		//electrical BikeSharingStation
		else
		{
			//if there isn't the right agent in the storage, something went wrong
			if (agentsE.get(personId) != null)
			{
				BikesE bikeE = agentsE.get(personId).getBikeE();
				//log.info(now + ": Agent with ID " + personId + 
				//		" tries to return E-Bike at Station: " + station.getId());
				returnCompletedE = ReturnBike.returnBikeE(station, bikeE, now, basicAgentDelegate, bsFac);
				
				if (returnCompletedE)
				{
					agentsE.remove(personId);
					value = true;
					log.info("Agent with ID:;" + personId + 
							";returned an E-Bike at Station:;" + station.getId());

					List<WaitingData> statList = station.getWaitingToReturnBike();
					if (statList != null)
					for (int j = 0; j < statList.size(); j++)
					{
						if (statList.get(j).personId.equals(personId))
						{
							statList.remove(j);
							station.setWaitingToReturnBike(statList);
							break;
						}
					}
				}
				
				else
				{
					log.info("Agent with ID:;" + personId +
							";was not able to park bike at E-station;" + station.getStationId());
					
					
					if (agentsE.get(personId) == null)
					{
						log.warn("### Agent with Id " + personId + " wasn't found in e-bike-list");
					}
					
					if (!(bsFac.totalWaitingListReturn.containsKey(basicAgentDelegate.getPerson().getId())))
					{
						NoParkingAvailable.noParkingAvailable(station, nextAct, now,
							  basicAgentDelegate, bikeSharingContext );
						value = false;
					}
												
				}
			}
		}
		runner.planComparison(basicAgentDelegate);
		return value;
	}
	public void bubbleSort(List <WaitingData> arr, int n)
	{
	   int i, j;
	   WaitingData wdTemp = new WaitingData();
	   for (i = 0; i < n; i++)
	   {
	       // Last i elements are already in place   
	       for (j = 0; j < n-i-1; j++)
	       {
	           if (arr.get(j).time > arr.get(j+1).time)
	           {
	        	wdTemp = arr.get(j);
	        	arr.set(j, arr.get(j+1));
	        	arr.set(j+1, wdTemp);
	           }
	       }
	   }
	}

}
