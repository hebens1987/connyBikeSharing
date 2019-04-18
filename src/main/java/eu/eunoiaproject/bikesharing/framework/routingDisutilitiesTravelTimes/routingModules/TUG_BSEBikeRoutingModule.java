/* *********************************************************************** *
 * project: org.matsim.*
 * TransitWithMultiModalAccessRoutingModule.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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
package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules;


import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.EmptyStageActivityTypes;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.facilities.Facility;
import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_EBSTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_EBSTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;

import java.util.List;

/**
 * A {@link RoutingModule} that allows agents to choose between bike sharing
 * and walk to go to their departure stops.
 *
 * @author thibautd
 */
public class TUG_BSEBikeRoutingModule implements RoutingModule {

	/**
	 * @param initialNodeProportion the proportion of "initial nodes" to pass to the routing algorithm.
	 * This allows some randomness in the choice of the initial nodes.
	 */
	@Inject Scenario scenario;
	
	/***************************************************************************/
	public TUG_BSEBikeRoutingModule() 
	/***************************************************************************/
	{
	}
	/***************************************************************************/
	public TUG_BSEBikeRoutingModule(Scenario scenario) 
	/***************************************************************************/
	{
		this.scenario = scenario;
	}



	/***************************************************************************/
	@Override
	public List<? extends PlanElement> calcRoute(Facility fromFacility,
			Facility toFacility, double departureTime, Person person) 
	/***************************************************************************/
	{
		//departureTime = ActTime.actDepartureTime(person, fromFacility, departureTime);
		// TODO Create list of legs with moving pathways and walking

		BicycleConfigGroup config = (BicycleConfigGroup) scenario.getConfig().getModule("bicycleAttributes");
		TUG_EBSTravelTime btt = new TUG_EBSTravelTime(config);
		TUG_EBSTravelDisutility btd = new TUG_EBSTravelDisutility(config);
		
		LeastCostPathCalculator routeAlgo = new Dijkstra(scenario.getNetwork(), btd, btt);
		
		
		RoutingModuleHelper rmh = new RoutingModuleHelper(fromFacility, toFacility, 
				departureTime, person, routeAlgo,
				scenario, btt, EBConstants.BS_E_BIKE);
		return rmh.peList;

		
	}

	/***************************************************************************/
	@Override
	public StageActivityTypes getStageActivityTypes() 
	/***************************************************************************/
	{
		return EmptyStageActivityTypes.INSTANCE;
	}
	
}
