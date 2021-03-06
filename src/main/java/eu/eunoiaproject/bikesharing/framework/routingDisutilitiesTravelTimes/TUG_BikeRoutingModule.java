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
package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.EmptyStageActivityTypes;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.facilities.Facility;
import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_BSTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_BikeTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_EBSTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BikeTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_EBSTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link RoutingModule} that allows agents to choose between bike sharing
 * and walk to go to their departure stops.
 *
 * @author thibautd
 */
public class TUG_BikeRoutingModule implements RoutingModule {

	/**
	 * @param initialNodeProportion the proportion of "initial nodes" to pass to the routing algorithm.
	 * This allows some randomness in the choice of the initial nodes.
	 */
	@Inject Scenario scenario;
	
	/***************************************************************************/
	public TUG_BikeRoutingModule() 
	/***************************************************************************/
	{
	}
	
	/***************************************************************************/
	public TUG_BikeRoutingModule(Scenario scenario) 
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
		final List<PlanElement> trip = new ArrayList<PlanElement>();
		TravelTime btt;
		TravelDisutility btd;
		String mode;

		BicycleConfigGroup config = (BicycleConfigGroup) scenario.getConfig().getModule("bicycleAttributes");
		if (person.getSelectedPlan().getType().contains("_bs"))
		{
			btt = new TUG_BSTravelTime(config);
			btd = new TUG_BSTravelDisutility(config);
			mode = EBConstants.BS_BIKE;
		}
		else if (person.getSelectedPlan().getType().contains("_ebs"))
		{
			btt = new TUG_EBSTravelTime(config);
			btd = new TUG_EBSTravelDisutility(config);
			mode = EBConstants.BS_E_BIKE;
		}
		else
		{
			btt = new TUG_BikeTravelTime(config);
			btd = new TUG_BikeTravelDisutility(config, scenario.getConfig());
			mode = TransportMode.bike;
		}
		
		double travelTime = 0;
		
		LeastCostPathCalculator routeAlgo = new Dijkstra(scenario.getNetwork(), btd, btt);
		
		//public Dijkstra(final Network network, final TravelDisutility costFunction, final TravelTime timeFunction) {
		//	this(network, costFunction, timeFunction, null);
		
		Path path = routeAlgo.calcLeastCostPath(scenario.getNetwork().getLinks().get(fromFacility.getLinkId()).getToNode(),
				scenario.getNetwork().getLinks().get(toFacility.getLinkId()).getFromNode(), departureTime, person, null);

		//double travelTime = 0.0;
		double distance = 0.0;
		Id <Link> startLinkId = fromFacility.getLinkId();
		Id <Link> endLinkId = toFacility.getLinkId();
		if (path.links.size()>0)
		{
			if (path.links.get(0).getId() != startLinkId)
			{
				path.links.add(0, scenario.getNetwork().getLinks().get(startLinkId));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(0), departureTime, person, null);
				travelTime = path.travelTime + travelTimeAddOn;
			}
		
			if (path.links.get(path.links.size()-1) != endLinkId)
			{
				path.links.add(scenario.getNetwork().getLinks().get(toFacility.getLinkId()));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(path.links.size()-1), departureTime, person, null);
				travelTime = path.travelTime + travelTimeAddOn;
			}
		}
		
		else
		{
			Node fromFac_fromNode = scenario.getNetwork().getLinks().get(fromFacility.getLinkId()).getFromNode();
			Node fromFac_toNode = scenario.getNetwork().getLinks().get(fromFacility.getLinkId()).getToNode();
			Node toFac_fromNode = scenario.getNetwork().getLinks().get(toFacility.getLinkId()).getFromNode();
			Node toFac_toNode = scenario.getNetwork().getLinks().get(toFacility.getLinkId()).getToNode();
			
			if (fromFac_fromNode.getId().equals(toFac_toNode.getId())
					&& fromFac_toNode.getId().equals(toFac_fromNode.getId()))
			{
				distance = CoordUtils.calcEuclideanDistance(fromFacility.getCoord(), toFacility.getCoord());
				path.links.add(scenario.getNetwork().getLinks().get(fromFacility.getLinkId()));
				path.links.add(scenario.getNetwork().getLinks().get(toFacility.getLinkId()));
				if (distance < 0.1)
				{
					distance = 5;
				}
				double travelTimeAddOn = distance / 4.5;
				travelTime = path.travelTime + travelTimeAddOn;
			}
			
			else if (startLinkId != endLinkId)
			{
				path.links.add(scenario.getNetwork().getLinks().get(fromFacility.getLinkId()));
				path.links.add(scenario.getNetwork().getLinks().get(toFacility.getLinkId()));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(0), departureTime, person, null)
						+ btt.getLinkTravelTime(path.links.get(path.links.size()-1), departureTime, person, null);
				travelTime = path.travelTime + travelTimeAddOn;
			}

			else
			{
				path.links.add(scenario.getNetwork().getLinks().get(fromFacility.getLinkId()));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(0), departureTime, person, null);
				travelTime = path.travelTime + travelTimeAddOn;
				
			}
		}
		//path.links.add(0, scenario.getNetwork().getLinks().get(fromFacility.getLinkId()));
		//path.links.add(scenario.getNetwork().getLinks().get(toFacility.getLinkId()));
		
		NetworkRoute route = new LinkNetworkRouteImpl(startLinkId, endLinkId);
		String routeDescr = "";
		String startLinkIdPath = path.links.get(0).getId().toString();
		String endLinkIdPath = path.links.get(path.links.size()-1).getId().toString();
		
		if (distance >= 5)
		{
			for (int i = 0; i < path.links.size(); i++) 
			{
				routeDescr += path.links.get(i).getId().toString();
			
				if (i != path.links.size())
				{
					routeDescr += " ";
				}
			}
		}
		
		else if (distance < 5)
		{
			for (int i = 0; i < path.links.size(); i++) 
			{
				routeDescr += path.links.get(i).getId().toString();
			
				if (i != path.links.size())
				{
					routeDescr += " ";
				}
				distance = distance + path.links.get(i).getLength();
			}
		}
		else
		{
			routeDescr = "sameLink";
		}
		
		if (travelTime == 0)
		{
			travelTime = path.travelTime;
		}

		route.setStartLinkId(startLinkId);
		route.setEndLinkId(endLinkId);
		
		final Leg leg = new LegImpl(mode);
		//System.out.println("path.travelTime = " + path.travelTime + ";  leg.getTravelTime() = " + leg.getTravelTime());
		route.setTravelTime(travelTime);
		route.setRouteDescription(routeDescr);
		route.setDistance(distance);
		leg.setRoute(route);
		leg.setTravelTime(travelTime);
		leg.setDepartureTime(departureTime);
		trip.add(leg);

		return trip;
		
	}

	/***************************************************************************/
	@Override
	public StageActivityTypes getStageActivityTypes() 
	/***************************************************************************/
	{
		return EmptyStageActivityTypes.INSTANCE;
	}
	
}
	