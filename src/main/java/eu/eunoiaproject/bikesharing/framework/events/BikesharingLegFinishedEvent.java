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
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vehicles.Vehicle;

public class BikesharingLegFinishedEvent extends Event{

	
	private final Id<Person> personId;
	
	private final Id<Vehicle> vehicleId;
	
	private final Leg leg;
	
	public static final String EVENT_TYPE = "Bikesharing Leg Finished";
	
	
	public BikesharingLegFinishedEvent(double time, Id<Person> personId, Id<Vehicle> vehicleId, Leg leg) {
		super(time);
		
		this.personId = personId;
		
		this.vehicleId = vehicleId;
		
		this.leg = leg;
	}

	@Override
	public String getEventType() {
		// TODO Auto-generated method stub
		return EVENT_TYPE;
	}
	
	
	public Id<Person> getPersonId(){
		return this.personId;
	}
	
	public Id<Vehicle> getvehicleId(){
		return this.vehicleId;
	}
	
	public Leg getLeg() {
		
		return this.leg;
	}

}
