package eu.eunoiaproject.bikesharing.framework.routing.pedestrians;

import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;


public class TUG_WalkTravelDisutilityFactory implements TravelDisutilityFactory {

		
	@Inject
	BicycleConfigGroup bikeConfigGroup;
	@Inject
	PlanCalcScoreConfigGroup cnScoringGroup;
	
	/***************************************************************************/
	@Override
	public TravelDisutility createTravelDisutility(TravelTime timeCalculator) 
	/***************************************************************************/
	{
			return new TUG_WalkTravelDisutility(bikeConfigGroup, cnScoringGroup);
	
	}
	


}
