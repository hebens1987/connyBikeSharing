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
package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities;

import org.matsim.core.config.Config;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;

import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BikeTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_EBSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_WalkTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;


/**
* returns new TUG_BikeTravelDisutility
* Autors: Hebenstreit, Aspäck
*/

public class IKK_BikeTravelDisutilityFactory implements TravelDisutilityFactory {

		
	@Inject
	BicycleConfigGroup bikeConfigGroup;
	@Inject
	Config config;
	@Inject
	PlanCalcScoreConfigGroup cnScoringGroup;
	
	/***************************************************************************/
	@Override
	public TravelDisutility createTravelDisutility(TravelTime timeCalculator)
	/***************************************************************************/
	{
		if (timeCalculator instanceof TUG_BikeTravelTime)
		{
			return new TUG_BikeTravelDisutility(bikeConfigGroup, config);
		}
		else if (timeCalculator instanceof TUG_EBSTravelTime)
		{
			return new TUG_EBSTravelDisutility(bikeConfigGroup);
		}
		else if (timeCalculator instanceof TUG_BSTravelTime)
		{
			return new TUG_BSTravelDisutility(bikeConfigGroup);
		}
		else if (timeCalculator instanceof TUG_WalkTravelTime)
		{
			return new TUG_WalkTravelDisutility(bikeConfigGroup);
		}
		else
			return null;
		
	}

}
