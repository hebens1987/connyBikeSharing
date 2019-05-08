package eu.eunoiaproject.bikesharing.framework.planStrategy;

import java.util.ArrayList;
import java.util.List;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.utils.geometry.CoordUtils;

public class CheckReplannedPlans 
{ 
	public boolean planLegLongerThan (List<PlanElement> pe, double length)
	{
	            List<Activity> acts = new ArrayList<Activity>();
	            for (int i = 0; i < pe.size(); i++)
	            {
	                if (pe.get(i) instanceof Activity)
	                {
	                    Activity act = (Activity)pe.get(i);
	                    if (!(act.getType().contains("interaction")))
	                    {
	                          if (!(act.getType().contains("wait")))
	                          {
	                                acts.add(act);
	                          }
	                    }
	                }
	            }
	            for (int i = 0; i < acts.size()-1; i++ )
	            {
	                  Coord c1 = acts.get(i).getCoord();
	                  Coord c2 = acts.get(i+1).getCoord();
	                  if (CoordUtils.calcEuclideanDistance(c1, c2) > (length/1.4))
	                  {
	                        return true;
	                  }     
	            }
	            return false;
	}
	
	public String getPlanMode (List<PlanElement> planelems)
	{
		String mode = "other";
		for (int i = 0; i < planelems.size(); i++)
		{
			if (planelems.get(i) instanceof Leg)
			{
				Leg leg = (Leg)planelems.get(i);
				if ((leg.getMode().equals("eBikeSharing")) || leg.getMode().contains("bs"))
				{	mode = "eBikeSharing";
					return mode;
				}
				else if (leg.getMode().equals(TransportMode.walk+"ing"))
				{
					mode = TransportMode.walk+"ing";
				}
				else if (leg.getMode().equals(TransportMode.bike))
				{
					mode = TransportMode.bike;
					return mode;
				}
			}
		}
		return mode;
	}
}
