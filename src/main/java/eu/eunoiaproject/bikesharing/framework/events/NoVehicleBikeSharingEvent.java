package eu.eunoiaproject.bikesharing.framework.events;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

public class NoVehicleBikeSharingEvent extends Event{

	public static final String EVENT_TYPE = "no BS Bike ";
	
	private final Id<Link> linkId;
	private final Person person;
	
	private final String bikesharingType;
	
	public NoVehicleBikeSharingEvent(double time, Id<Link> linkId, String bikesharingType, Person person) {
		super(time);
		this.linkId = linkId;
		this.bikesharingType = bikesharingType;
		this.person = person;
	}

	@Override
	public String getEventType() {
		String toReturn = EVENT_TYPE + " for person = " + person.getId().toString();
		return toReturn;
	}
	
	public Id<Link> getLinkId(){
		return this.linkId;
	}
	
	public String getBikesharingType() {
		return this.bikesharingType;
	}
	
	public Person getPerson() {
		return this.person;
	}

}
