package eu.parking2019;

import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;


public class ParkingSpotImpl implements ParkingSpot{
	
	Id id;
	Id idPerson;
	double duration;
	double price;
	Coord coordinate = new Coord(0,0);
	Id<Link> linkId;
	String type;
	
	/***************************************************************************/
	public ParkingSpotImpl(ParkingSpot parking) 
	/***************************************************************************/
	{
		this.id = parking.getParkingId();
		this.coordinate = parking.getCoordinate();
		this.price = parking.getPrice();
		this.duration = parking.getFreeDuration();
		this.idPerson = parking.getPersonId();
		this.linkId = parking.getLinkId();
		this.type = parking.getType();
		
	}
	
	/***************************************************************************/
	public ParkingSpotImpl (Id id, double x, double y, double price, double duration, Id person, Id<Link> linkId, String type) 
	/***************************************************************************/
	{
		this.id = id;
		this.coordinate.setX(x);
		this.coordinate.setY(y);
		this.price = price;
		this.duration = duration;
		this.idPerson = person;
		this.linkId = linkId;
		this.type = type;
	}
	
	/***************************************************************************/
	public void getParking (ParkingSpot parking) 
	/***************************************************************************/
	{
		this.id = parking.getParkingId();
		this.coordinate = parking.getCoordinate();
		this.price = parking.getPrice();
		this.duration = parking.getFreeDuration();
		this.idPerson = parking.getPersonId();
	}
	
	

	/***************************************************************************/
	@Override
	public Coord getCoordinate() 
	/***************************************************************************/
	{
		return coordinate;
	}

	@Override
	public Id getParkingId() {
		return id;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public double getFreeDuration() {
		return duration;
	}

	@Override
	public Id getPersonId() {
		return idPerson;
	}

	@Override
	public Id<Link> getLinkId() {
		return linkId;
	}

	@Override
	public Coord getCoord() {
		return getCoordinate();
	}

	@Override
	public Id<ParkingSpot> getId() {
		return getParkingId();
	}

	@Override
	public Map<String, Object> getCustomAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPersonId(Id<Person> person) {
		this.idPerson = person;
		
	}

	@Override
	public String getType() {
		return type;
	}
}

	


	