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
package eu.eunoiaproject.bikesharing.framework.routing.pedestrians;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.routes.GenericRouteImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.EmptyStageActivityTypes;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.facilities.Facility;
import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@link RoutingModule} that allows agents to choose between bike sharing
 * and walk to go to their departure stops.
 *
 * @author thibautd
 */
public class TUG_WalkRoutingModule implements RoutingModule {

	/**
	 * @param initialNodeProportion the proportion of "initial nodes" to pass to the routing algorithm.
	 * This allows some randomness in the choice of the initial nodes.
	 */
	@Inject Scenario scenario;
	private static final Logger log = Logger.getLogger(TUG_WalkRoutingModule.class);
	
	/***************************************************************************/
	public TUG_WalkRoutingModule() 
	/***************************************************************************/
	{
	}
	/***************************************************************************/
	public TUG_WalkRoutingModule(Scenario scenario) 
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
			// TODO Create list of legs with moving pathways and walking
			final List<PlanElement> trip = new ArrayList<PlanElement>();
	
			BicycleConfigGroup config = (BicycleConfigGroup) scenario.getConfig().getModule("bicycleAttributes");
			PlanCalcScoreConfigGroup conf2 = (PlanCalcScoreConfigGroup) scenario.getConfig().getModule("planCalcScore");
			TUG_WalkTravelTime btt = new TUG_WalkTravelTime(config);
			TUG_WalkTravelDisutility btd = new TUG_WalkTravelDisutility();
			
			LeastCostPathCalculator routeAlgo = new Dijkstra(scenario.getNetwork(), btd, btt);
			
			//public Dijkstra(final Network network, final TravelDisutility costFunction, final TravelTime timeFunction) {
			//	this(network, costFunction, timeFunction, null);
			
			Path path = routeAlgo.calcLeastCostPath(scenario.getNetwork().getLinks().get(fromFacility.getLinkId()).getToNode(),
					scenario.getNetwork().getLinks().get(toFacility.getLinkId()).getFromNode(), departureTime, person, null);
	
			double travelTime = Double.NaN;
			double distance = 0.0;
			Id <Link> startLinkId = fromFacility.getLinkId();
			Id <Link> endLinkId = toFacility.getLinkId();
			
			if (fromFacility.getCoord() == toFacility.getCoord())
			{
				NetworkRoute route = new LinkNetworkRouteImpl(startLinkId, endLinkId);
				route.setDistance(5);
				route.setTravelTime(2);
				final Leg leg = new LegImpl(TransportMode.walk);
				leg.setRoute(route);
				leg.setTravelTime(2);
				leg.setDepartureTime(departureTime);
				trip.add(leg);
			}
			
			if (path == null || path.links == null || path.links.size() == 0)
			{
				Node fromFac_fromNode = scenario.getNetwork().getLinks().get(fromFacility.getLinkId()).getFromNode();
				Node fromFac_toNode = scenario.getNetwork().getLinks().get(fromFacility.getLinkId()).getToNode();
				Node toFac_fromNode = scenario.getNetwork().getLinks().get(toFacility.getLinkId()).getFromNode();
				Node toFac_toNode = scenario.getNetwork().getLinks().get(toFacility.getLinkId()).getToNode();
				
				if (path == null || path.links == null) 
					// if it runs into here - the network has had a problem as path returned nul
				{
					distance = CoordUtils.calcEuclideanDistance(fromFacility.getCoord(), toFacility.getCoord());
					travelTime = distance / 1.1 * 1.41;
					final Leg leg = new LegImpl(TransportMode.walk);
					Route route = new GenericRouteImpl (fromFacility.getLinkId(), toFacility.getLinkId());
					
					//System.out.println("path.travelTime = " + path.travelTime + ";  leg.getTravelTime() = " + leg.getTravelTime());
					route.setTravelTime(travelTime);
					route.setRouteDescription(fromFacility.getLinkId().toString()+" "+toFacility.getLinkId().toString());
					route.setDistance(distance);
					leg.setRoute(route);
					leg.setTravelTime(travelTime);
					leg.setDepartureTime(departureTime);
					trip.add(leg);
					log.warn("no Route found from link: "+ 	fromFacility.getLinkId().toString() +
												" to link: " + toFacility.getLinkId().toString());
					return trip;
				}
				
				else if (fromFac_fromNode.getId().equals(toFac_toNode.getId())
						&& fromFac_toNode.getId().equals(toFac_fromNode.getId()))
				{
					distance = CoordUtils.calcEuclideanDistance(fromFacility.getCoord(), toFacility.getCoord());
					path.links.add(scenario.getNetwork().getLinks().get(fromFacility.getLinkId()));
					path.links.add(scenario.getNetwork().getLinks().get(toFacility.getLinkId()));
					double travelTimeAddOn = distance / 1.1 * 1.41;
					distance = 2;
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
			
			else 
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

			
			NetworkRoute route = new LinkNetworkRouteImpl(startLinkId, endLinkId);
			String routeDescr = "";
			String startLinkIdPath = path.links.get(0).getId().toString();
			String endLinkIdPath = path.links.get(path.links.size()-1).getId().toString();
			
			if (distance >= 2)
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
			
			else if (distance < 2)
			{
				for (int i = 0; i < path.links.size(); i++) 
				{
					routeDescr += path.links.get(i).getId().toString();
				
					if (i != path.links.size())
					{
						routeDescr += " ";
					}
					distance += path.links.get(i).getLength();
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
			
			final Leg leg = new LegImpl(TransportMode.walk);
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
