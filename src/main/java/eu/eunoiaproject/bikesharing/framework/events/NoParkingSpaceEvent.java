/* *********************************************************************** *
 * project: org.matsim.*
 * AgentStartsWaitingForBikeEvent.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package eu.eunoiaproject.bikesharing.framework.events;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

public class NoParkingSpaceEvent extends Event{

	public static final String EVENT_TYPE = "noParkingSpot";
	
	private final Id<Link> linkId;
	
	private final String carsharingType;
	private final Person person;
	
	public NoParkingSpaceEvent(double time, Id<Link> linkId, String carsharingType, Person person) {
		super(time);
		this.linkId = linkId;
		this.carsharingType = carsharingType;
		this.person = person;
	}

	@Override
	public String getEventType() {
		String toReturn = EVENT_TYPE + " for Person " + person.getId().toString();
		return toReturn;
	}
	
	public Id<Link> getLinkId(){
		return this.linkId;
	}
	
	public String getBikesharingType() {
		return this.carsharingType;
	}
	
	public Person getPerson() {
		return this.person;
	}
	
	
}
