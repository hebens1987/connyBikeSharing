package eu.parking2019;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;

/**
 * chooses a parking spot for a person
 * "noe" declares that parking takes place in lower Austria
 * as parking spots in NÖ are no objects, for lower Austria, 
 * it works different than for Vienna
 * 
 * @author hebenstreitC
 */

public class DummyParkingSpot{

	
	public DummyParkingSpot()
	{
	}
	
	
	public ParkingSpotImpl parking (
			Scenario scenario,Coord coord, double distance, 
			Activity act, Id<Person> personId, double now,
			boolean activityInNOE)
	{

		ParkingSpotImpl p = new ParkingSpotImpl(null, 0, 0, 0, 24*3600, personId, act.getLinkId(), "noe" );
		//creates Dummy Parking Spot for NÖ
		return p;

	}

	
	
}

	


	