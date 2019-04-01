package eu.eunoiaproject.bikesharing.framework.events;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

public class AgentStartsWaiting extends Event{

	public static final String EVENT_TYPE = "wait: Agent starts waiting";
	
	private final Id stationId;
	private final Person person;
	
	public AgentStartsWaiting(double time, Person person, Id stationId) {
		super(time);
		this.stationId = stationId;
		this.person = person;
	}

	@Override
	public String getEventType() {
		String toReturn = EVENT_TYPE + " person = " + person.getId().toString() + " facility= " + stationId.toString();
		return toReturn;

	}
	
	public Person getPerson() {
		return this.person;
	}

}
