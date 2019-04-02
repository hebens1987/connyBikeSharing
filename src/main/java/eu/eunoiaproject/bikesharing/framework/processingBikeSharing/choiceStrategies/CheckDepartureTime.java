package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.choiceStrategies;



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
