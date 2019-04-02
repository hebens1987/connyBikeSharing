package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes;

import java.util.List;

import org.matsim.api.core.v01.population.PlanElement;

public class BSTypeAndPlanElements 
{

	public List<PlanElement> peList;
	public int type;
	// 0 = full bs trip
	// 1 = bs-pt
	// 2 = pt-bs
	// 3 = changeMode
}
