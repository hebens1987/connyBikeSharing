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
package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.routes.GenericRouteImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.TravelTime;
import org.matsim.facilities.Facility;

public class RoutingModuleHelper {
	
	List<PlanElement> peList = null;
	
	public RoutingModuleHelper (Facility fromFacility, Facility toFacility, 
		double departureTime, Person person, LeastCostPathCalculator routeAlgo,
		Scenario scenario, TravelTime tt,String mode)
		{
		
			this.peList = adaptPath (fromFacility, toFacility, departureTime, person, routeAlgo,
				scenario, tt, mode);
		}
	
	private List<PlanElement> adaptPath (Facility fromFacility, Facility toFacility, 
			double departureTime, Person person, LeastCostPathCalculator routeAlgo,
			Scenario scenario, TravelTime btt,String mode)
	{
		final List<PlanElement> trip = new ArrayList<PlanElement>();
		Path path = routeAlgo.calcLeastCostPath(scenario.getNetwork().getLinks().get(fromFacility.getLinkId()).getToNode(),
				scenario.getNetwork().getLinks().get(toFacility.getLinkId()).getFromNode(), departureTime, person, null);
		double travelTime = 0.0;
		double distance = 0.0;
		Id <Link> startLinkId = fromFacility.getLinkId();
		Id <Link> endLinkId = toFacility.getLinkId();
		
		double travelTimePath = 0;
		if (path == null)
		{
			System.out.println("warum nur?");
			Route route = new GenericRouteImpl(startLinkId, endLinkId);
			final Leg leg = new LegImpl(mode);
			//System.out.println("path.travelTime = " + path.travelTime + ";  leg.getTravelTime() = " + leg.getTravelTime());
			route.setTravelTime(10);
			route.setRouteDescription(startLinkId + " " + endLinkId);
			route.setDistance(25);
			leg.setRoute(route);
			leg.setTravelTime(20);
			leg.setDepartureTime(departureTime);
			trip.add(leg);
			return trip;
		}
		travelTimePath = path.travelTime;
		
		/*if (path.links.size()>0)
		{
			if (path.links.get(0).getId() != startLinkId)
			{
				path.links.add(0, scenario.getNetwork().getLinks().get(startLinkId));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(0), departureTime, person, null);
				travelTime = travelTimePath + travelTimeAddOn;
				
			}
		
			if (path.links.get(path.links.size()-1) != endLinkId)
			{
				path.links.add(scenario.getNetwork().getLinks().get(toFacility.getLinkId()));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(path.links.size()-1), departureTime, person, null);
				travelTime = travelTimePath + travelTimeAddOn;
			}
		}
		
		else
		{
			Node fromFac_fromNode = scenario.getNetwork().getLinks().get(fromFacility.getLinkId()).getFromNode();
			Node fromFac_toNode = scenario.getNetwork().getLinks().get(fromFacility.getLinkId()).getToNode();
			Node toFac_fromNode = scenario.getNetwork().getLinks().get(toFacility.getLinkId()).getFromNode();
			Node toFac_toNode = scenario.getNetwork().getLinks().get(toFacility.getLinkId()).getToNode();
			
			
			if (startLinkId != endLinkId)
			{
				path.links.add(scenario.getNetwork().getLinks().get(fromFacility.getLinkId()));
				path.links.add(scenario.getNetwork().getLinks().get(toFacility.getLinkId()));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(0), departureTime, person, null)
						+ btt.getLinkTravelTime(path.links.get(path.links.size()-1), departureTime, person, null);
				travelTime = travelTimePath + travelTimeAddOn;
			}
	
			else
			{
				path.links.add(scenario.getNetwork().getLinks().get(fromFacility.getLinkId()));
				double travelTimeAddOn = btt.getLinkTravelTime(path.links.get(0), departureTime, person, null);
				travelTime = travelTimePath + travelTimeAddOn;
			}
		}*/
		LinkNetworkRouteImpl route = new LinkNetworkRouteImpl(startLinkId, endLinkId);
		String routeDescr = "";
		

			for (int i = 0; i < path.links.size(); i++) 
			{
				routeDescr += path.links.get(i).getId().toString();
				distance += path.links.get(i).getLength();
			
				if (i != path.links.size()-1)
				{
					routeDescr += " ";
				}
			}
			
		route.setStartLinkId(startLinkId);
		if (startLinkId == null)
		{
			System.out.println("stopp hier");
		}
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
}
