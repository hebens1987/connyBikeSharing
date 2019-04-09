package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.Facility;

import eu.eunoiaproject.bikesharing.framework.EBConstants;

public class RoutingModuleHelper {
	
	List<PlanElement> peList = null;
	
	public RoutingModuleHelper (Facility fromFacility, Facility toFacility, 
		double departureTime, Person person, LeastCostPathCalculator routeAlgo,
		Scenario scenario, TravelTime btt,String mode)
		{
		
			this.peList = adaptPath (fromFacility, toFacility, departureTime, person, routeAlgo,
				scenario, btt, mode);
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
		
		double travelTimePath = path.travelTime;
		if (travelTimePath == Double.NaN)
		travelTimePath = -0.1;
		
		Id <Link> startLinkId = fromFacility.getLinkId();
		Id <Link> endLinkId = toFacility.getLinkId();
		if (path.links.size()>0)
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
				travelTime = travelTimePath + travelTimeAddOn;
			}
			
			else if (startLinkId != endLinkId)
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
		}
		LinkNetworkRouteImpl route = new LinkNetworkRouteImpl(startLinkId, endLinkId);
		String routeDescr = "";
		
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
}
