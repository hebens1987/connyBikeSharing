package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.arrivalEvents;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.internal.HasPersonId;

import java.util.Map;

/**
 * This is similar to the VehicleArrival and PersonArrival events.
 * It is used for scoring teleported legs.
 */
public final class BikeArrivalEvent extends Event implements HasPersonId {

	public static final String ATTRIBUTE_PERSON = "person";
	public static final String ATTRIBUT_DISTANCE = "distance";
	public static final String EVENT_TYPE = "travelled_bike";


    private Id<Person> agentId;
    private double distance;

    public BikeArrivalEvent(double time, Id<Person> agentId, double distance) {
        super(time);
        this.agentId = agentId;
        this.distance = distance;
    }

    public Id<Person> getPersonId() {
    	return agentId;
    }
    
    public double getDistance() {
    	return distance;
    }
    
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> attributes = super.getAttributes();
        attributes.put(ATTRIBUTE_PERSON, agentId.toString());
        attributes.put(ATTRIBUT_DISTANCE, Double.toString(distance));
        return attributes;
    }
}