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
