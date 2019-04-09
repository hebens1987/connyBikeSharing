package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes;

import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;

import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;


/**
* returns new TUG_BikeTravelDisutility
* Autors: Hebenstreit, Aspäck
*/

public class IKK_BikeTravelDisutilityFactory implements TravelDisutilityFactory {

		
	@Inject
	BicycleConfigGroup bikeConfigGroup;
	@Inject
	PlanCalcScoreConfigGroup cnScoringGroup;
	
	/***************************************************************************/
	@Override
	public TravelDisutility createTravelDisutility(TravelTime timeCalculator)
	/***************************************************************************/
	{
		return new TUG_BikeTravelDisutility(bikeConfigGroup );
	}

}
