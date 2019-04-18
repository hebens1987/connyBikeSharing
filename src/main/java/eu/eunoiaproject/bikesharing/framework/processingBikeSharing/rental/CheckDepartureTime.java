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



import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;

import org.matsim.core.mobsim.qsim.agents.BSRunner;

public class CheckDepartureTime
{
	//private final static Logger log = Logger.getLogger(CheckDepartureTime.class);
	
	/***************************************************************************/
	/** checks if the taken departureTime does match with the FacilityEndTime,
	 * if not it sets the departureTime to the FacilityEndTime
	 **/
	public static double checkDep(BasicPlanAgentImpl person, Activity fac, double departureTime) 

	/***************************************************************************/
	{	
		BSRunner runner = new BSRunner();
		runner.planComparison(person);
		for (int i = 0; i < person.getCurrentPlan().getPlanElements().size(); i++)
		{
			if (person.getCurrentPlan().getPlanElements().get(i) instanceof Activity)
			{
				Activity act = (Activity) person.getCurrentPlan().getPlanElements().get(i);

				
				if (fac.getCoord() == act.getCoord())
				{
					
					if (!( act.getEndTime() == departureTime))
					{
						//System.out.println(" Time ActEnd: " + act.getEndTime() + " Time Dep: " + departureTime);
						if (i < person.getCurrentPlan().getPlanElements().size()-1)
						{
							Leg leg = (Leg) person.getCurrentPlan().getPlanElements().get(i+1);
							leg.setDepartureTime(act.getEndTime());
							person.getCurrentPlan().getPlanElements().set(i+1, leg);
						}
						if (!(act.getEndTime() == Double.NEGATIVE_INFINITY))
							departureTime = act.getEndTime();
						break;
					}
				}	
				
			}
		}
	runner.planComparison(person);
	return departureTime;
	}
}
