package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;

import eu.eunoiaproject.bikesharing.framework.events.AgentChangesLegAfterAbortWaiting;
import eu.eunoiaproject.bikesharing.framework.events.AgentFoundBikeEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentFoundParkingEvent;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.BSRunner;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacility;


/***************************************************
 * This method implements how agents act, if no bike is
 * available at the bike-sharing station
 * a) search a new Station within near distance
 * b) wait a certain time - or until a bike is available
 * c) walk instead of bike-sharing
 * d) us pt instead of bike-sharing**/

public class WaitingListHandling {
	
	/***************************************************************************/
	/**
	 * This method adds the Agent to the waiting list of a specific station **/
	public static void addAgentToWaitingListOfStation (
			Scenario scenario,
			BikeSharingFacility bsStation,
			BasicPlanAgentImpl agent,
			boolean wantsToTakeBike,
			double now)
	/***************************************************************************/
	{
		BSRunner runner = new BSRunner();
		runner.planComparison(agent);
		WaitingData wd = new WaitingData();
		wd.time = now;
		wd.bpAgent = agent;
		wd.personId = agent.getId();
		wd.bsFac = bsStation;
		

		
		BikeSharingFacilities bsFac = (BikeSharingFacilities) 
				scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME);
		
		if (wantsToTakeBike == true)
		{
			//add the first person to the waiting list of the station
			if (bsStation.getWaitingToTakeBike() == null)
			{
				List<WaitingData> w = new ArrayList <WaitingData>();
				w.add(wd);
				bsStation.setWaitingToTakeBike(w);
			}
			
			//if there is already a person waiting, add the other waiting persons
			else
			{
				List<WaitingData> w = bsStation.getWaitingToTakeBike();
				w.add(wd);
				bsStation.setWaitingToTakeBike(w);
			}
			
			//List <WaitingData> listTemp = new ArrayList<WaitingData>(bsFac.totalWaitingListTake.values());
			
			bsFac.totalWaitingListTake.put(wd.personId,wd);
			//System.out.println("Time TAKE - put : " + wd.time +  " Person: " + agent.getId());
		}
		
		else if (wantsToTakeBike == false)
		{
			//add the first person to the waiting list of the station
			if (bsStation.getWaitingToReturnBike() == null)
			{			
				List<WaitingData> w = new ArrayList <WaitingData>();
				w.add(wd);
				bsStation.setWaitingToReturnBike(w);
			}
			
			//if there is already a person waiting, add the other waiting persons
			else
			{
				List<WaitingData> w = bsStation.getWaitingToReturnBike();
				w.add(wd);	
				bsStation.setWaitingToReturnBike(w);
			}

			runner.planComparison(agent);
			bsFac.totalWaitingListReturn.put(wd.personId,wd);
			//System.out.println("Time RETURN - put : " + wd.time + " Person: " + agent.getId());
		}
		
	}
	

	/***************************************************************************/
	/** this method removes a specific agent from the waiting list, this must
	 * be done if an agent aborts the waiting, 
	 * if an agent wants to take a bike bikeToTake = true,
	 * if an agent wants to return a bike bikeToTake = false, **/
	public static BasicPlanAgentImpl abortWaitingAtStation (
			Scenario scenario,
			BikeSharingFacility bsStation,
			BasicPlanAgentImpl agent,
			boolean wantsToTakeBike)
	/***************************************************************************/
	{
		BikeSharingFacilities bsFac = (BikeSharingFacilities) 
				scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME);
		
		
		
		if (wantsToTakeBike == true) //das rad sollte ausgeborgt werden
		{
			bsFac.totalWaitingListTake.remove(agent.getId());
			List<WaitingData> list = bsStation.getWaitingToTakeBike();
		
			if (list != null)
			for (int i = 0; i < list.size(); i++)
			{
				if ((list.get(i).personId.equals(agent.getId())))
				{
					BasicPlanAgentImpl pers = list.get(i).bpAgent;
					list.remove(list.get(i));
					bsStation.setWaitingToTakeBike(list);
					//System.out.println(agent.getPerson().getId() + " = abortWaitTake (2a)"); //Hebenstreit
					return pers;
				}
			}
		}
		
		else if (wantsToTakeBike == false) //das rad sollte zurueckgegeben werden
		{
			bsFac.totalWaitingListReturn.remove(agent.getId());
			List<WaitingData> list = bsStation.getWaitingToReturnBike();
			
			if (list != null)
			for (int i = 0; i < list.size(); i++)
			{
				if ((list.get(i).personId.equals(agent.getPerson().getId())))
				{
					BasicPlanAgentImpl pers = list.get(i).bpAgent;
					list.remove(list.get(i));
					bsStation.setWaitingToReturnBike(list);
					//System.out.println(agent.getPerson().getId() + " = abortWaitReturn (2b)"); //Hebenstreit
					return pers;
				}
			}
		}
		
		return null;
	}

}
