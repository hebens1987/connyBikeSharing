package eu.parking2019;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.facilities.Facility;

public interface ParkingSpot extends Facility<ParkingSpot>{

	public Id<ParkingSpot> getParkingId ();
	public Coord getCoordinate();
	public double getPrice();
	public double getFreeDuration();
	public Id<Person> getPersonId();
	public void setPersonId(Id<Person> person);
	public String getType();
}
