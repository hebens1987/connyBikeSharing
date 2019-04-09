package eu.eunoiaproject.bikesharing.framework.events;

import org.matsim.api.core.v01.population.Person;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikesE;

import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.Event;


public class EBikeBatteryGoneEmpty extends Event {
	
	public static final String EVENT_TYPE = "eBike came to empty during usage";
	private BikesE ebike;
	double departureTime;
	Person person;
	ActivityEndEvent endEvent;
	ActivityStartEvent startEvent;
	Event event;
	boolean battery;

	/***************************************************************************/
	public EBikeBatteryGoneEmpty(double now, BikesE ebike, Person person)
	/***************************************************************************/
	{
	super(now);
	this.ebike  = ebike;
	this.person  = person;

	}
	
	public Person getPerson() {
		return this.person;
	}

	
	/***************************************************************************/
	public String getEventType() 
	/***************************************************************************/
	{
		String toReturn = EVENT_TYPE + " for person:  " + person.getId().toString() + " bikeId= " + ebike.getBikeId().toString();
		return toReturn;
	}	
}
