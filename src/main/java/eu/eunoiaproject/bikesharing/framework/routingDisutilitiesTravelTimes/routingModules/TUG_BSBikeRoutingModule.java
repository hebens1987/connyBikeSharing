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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.EmptyStageActivityTypes;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;
import org.matsim.facilities.Facility;
import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_BSTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_BikeTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link RoutingModule} that allows agents to choose between bike sharing
 * and walk to go to their departure stops.
 *
 * @author thibautd
 */
public class TUG_BSBikeRoutingModule implements RoutingModule {

	/**
	 * @param initialNodeProportion the proportion of "initial nodes" to pass to the routing algorithm.
	 * This allows some randomness in the choice of the initial nodes.
	 */
	@Inject Scenario scenario;
	
	
	/***************************************************************************/
	public TUG_BSBikeRoutingModule() 
	/***************************************************************************/
	{
	}
	/***************************************************************************/
	public TUG_BSBikeRoutingModule(Scenario scenario) 
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
		if (departureTime == 0)
		{
			departureTime = 1;
		}
		// TODO Create list of legs with moving pathways and walking
		final List<PlanElement> trip = new ArrayList<PlanElement>();

		BicycleConfigGroup config = (BicycleConfigGroup) scenario.getConfig().getModule("bicycleAttributes");
		TUG_BSTravelTime btt = new TUG_BSTravelTime(config);
		TUG_BSTravelDisutility btd = new TUG_BSTravelDisutility(config);
		
		LeastCostPathCalculator routeAlgo = new Dijkstra(scenario.getNetwork(), btd, btt);
		
		//public Dijkstra(final Network network, final TravelDisutility costFunction, final TravelTime timeFunction) {
		//	this(network, costFunction, timeFunction, null);
		
		RoutingModuleHelper rmh = new RoutingModuleHelper(fromFacility, toFacility, 
				departureTime, person, routeAlgo,
				scenario, btt, EBConstants.BS_BIKE);
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
	