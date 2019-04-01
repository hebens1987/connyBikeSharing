package eu.parking2019;

import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.routes.GenericRouteImpl;


/**
 * Handles the Parking End Event
 * @author hebenstreitC
 */
public class ParkingEndHandler{
	
	public void insertEgressLeg (double now, Activity act, Scenario scenario, BasicParkingPlanAgentImpl agent)
	{
		Person person = agent.getPerson();
		
		boolean  publicParking = true;

		if (!(act.getType().endsWith(TypesOfParkingSpots.PARKEN)))
		{
			publicParking = false;
		}

		if (publicParking)
		{
			Route route = new GenericRouteImpl(act.getLinkId(),act.getLinkId() );
			route.setStartLinkId(act.getLinkId());
			route.setEndLinkId(act.getLinkId());
			route.setDistance(250);
			route.setTravelTime(4*60);
			
			Leg carLeg = (Leg)agent.getCurrentPlan().getPlanElements().get(agent.getCurrentPlanElementIndex()+1);
			carLeg.setDepartureTime(act.getEndTime()+route.getTravelTime());
			Leg legParking = new LegImpl(TransportMode.egress_walk);
			legParking.setRoute(route);
			legParking.setDepartureTime(act.getEndTime());
			legParking.setTravelTime(route.getTravelTime());
			
			List<PlanElement> pe = agent.getCurrentPlan().getPlanElements();
			pe.add(agent.getCurrentPlanElementIndex()+1, legParking);
			//System.out.println("Insert Egress Leg - for Person " + person.getId().toString());
		}

	}
}

