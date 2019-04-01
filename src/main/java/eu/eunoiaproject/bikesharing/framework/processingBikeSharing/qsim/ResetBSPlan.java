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
package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim;

import java.util.List;


import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
//import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.LegImpl;
import org.matsim.core.router.RoutingModule;
import org.matsim.population.algorithms.PlanAlgorithm;
import org.apache.log4j.Logger;
//import eu.eunoiaproject.bikesharing.framework.routing.bikeSharing.BikeSharingRouteFactory;

/**
 * a {@link RoutingModule} for bike sharing trips including public transport trips.
 * Bike sharing trips are composed of an access walk
 * a bike part, and an egress walk and will be combined with pt - if the bs-stations 
 * are too far away from a Facility
 *
 * @author hebenstreit
 */
public class ResetBSPlan implements PlanAlgorithm  
{
	
	private static final Logger log =
			Logger.getLogger(ResetBSPlan.class);
	
	@Override
	public void run(Plan plan) 
	{
		//Person person = plan.getPerson();
		//Plan initialPlan = person.getPlans().get(0);
		
		List<PlanElement> peList = plan.getPlanElements();
		
		Leg leg = (Leg)peList.get(peList.size()-1);
		if (leg.getMode().equals("eBikeSharing"))
		{
			planCheck(plan, "eBikeSharing");
			for (int i = 0; i < plan.getPlanElements().size()-2; i++)
			{
				if (plan.getPlanElements().get(i) instanceof Leg)
				{
					while (plan.getPlanElements().get(i+1) instanceof Leg)
					{
						plan.getPlanElements().remove(i+1);
					}
				}
				
			}
			log.warn("eBikeSharing for Person " + plan.getPerson().getId() + " ... Route was reseted...(Hebenstreit)");
		}
		else if (leg.getMode().equals("ffBikeSharing"))
		{
			planCheck(plan, "ffBikeSharing");
			for (int i = 0; i <plan.getPlanElements().size()-1; i++)
			{
				if (plan.getPlanElements().get(i) instanceof Leg)
				{
					while (plan.getPlanElements().get(i+1) instanceof Leg)
					{
						plan.getPlanElements().remove(i+1);
						i++;
					}
				}
			}
			log.warn("ffBikeSharing for Person " + plan.getPerson().getId() + " ... Route was reseted...(Hebenstreit)");
		}
	}
	
	public void planCheck(Plan plan, String string) 
	{
		List<PlanElement> peList = plan.getPlanElements();
		
		Leg leg = (Leg)peList.get(peList.size()-1);
		if (leg.getMode().equals(string))
		{
			int sizeOrig = peList.size();
			int i = 0;
			
			while (i < peList.size())
			{
				if (peList.get(i) instanceof Activity)
				{
					Activity act = (Activity) peList.get(i);
					if (act.getType().contains("interaction"))
					{
						peList.remove(i);
						i--;
					}
					if (act.getType().equals("wait"))
					{
						peList.remove(i);
						i--;
					}
				}
				if (peList.get(i) instanceof Leg)
				{
					if (i < peList.size())
					{
						if (peList.get(i+1) instanceof Leg)
						{
							peList.remove(i);
						}
						((Leg)peList.get(i)).setMode(string);
					}
			}
				i++;
			}
			if (sizeOrig == peList.size())
			{
				
			}
			
			else
			{
				for (int j = 0; j < peList.size(); j++)
				{
					if (peList.get(j) instanceof Activity)
					{
						if (peList.get(j) instanceof Leg)
						{
							//do nothing
						}
						
						else if (j < peList.size()-2)
						{
							//BikeSharingRouteFactory bsFac = new BikeSharingRouteFactory();
							Leg legBS = new LegImpl(string);
							peList.add(j+1, legBS);
							j=j+1;
						}
					}
				}
			}
		}
	}
}

