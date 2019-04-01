package eu.parking2019;


import java.util.List;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.routes.GenericRouteImpl;


public class ParkingStartHandler{
	
	public ParkingStartHandler() 
	{
	}
	
	public void insertAccessLeg (
			double now, Coord coord, Scenario scenario, BasicParkingPlanAgentImpl agent, Activity act)
	{
		Id<Person> id = agent.getPerson().getId();
		DummyParkingSpot p = new DummyParkingSpot();
		ParkingSpot parking = new ParkingSpotImpl(null, act.getCoord().getX(), act.getCoord().getY(), 0, 0, agent.getId(), act.getLinkId(), "parkplatz");
		Coord pCoord = parking.getCoord();
		Id<Link> linkId = parking.getLinkId();
		
		boolean publicParking = true;
		
		if (!(act.getType().endsWith(TypesOfParkingSpots.PARKEN)))
		{
			publicParking = false;
		}
		
		if (publicParking)
		{
				linkId = parking.getLinkId();
				Route route = new GenericRouteImpl(linkId, linkId);
				route.setDistance(250);
				route.setTravelTime(4*60);
				route.setEndLinkId(act.getLinkId());
				route.setStartLinkId(act.getLinkId());
				Leg legParking = new LegImpl(TransportMode.access_walk);
				legParking.setDepartureTime(now);
				legParking.setRoute(route);
				legParking.setTravelTime(route.getTravelTime());
				List<PlanElement> pe = agent.getCurrentPlan().getPlanElements();
				pe.add(agent.getCurrentPlanElementIndex()+1, legParking);
				//System.out.println("Handle Parking Start - for Person " + id.toString());
		}
	}
}