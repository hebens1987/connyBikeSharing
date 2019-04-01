/* *********************************************************************** *
 * project: org.matsim.*

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
import org.matsim.api.core.v01.population.Person;
import org.matsim.facilities.ActivityFacility;

/**
 * @author thibautd
 */
public class GotBikeEvent extends AbstractPersonEvent {
	public static final String EVENT_TYPE = "agentGotBikeAfterWaiting";

	public GotBikeEvent(
			final Event event) {
		super( event );
		if ( !event.getEventType().equals( EVENT_TYPE ) ) {
			throw new IllegalArgumentException( event.toString() );
		}
	}

	public GotBikeEvent(
			final double time,
			final Id<Person> personId,
			final Id<ActivityFacility> facilityId) {
		super( time , personId, facilityId );
	}

	@Override
	public String getEventType() {
		return EVENT_TYPE;
	}
}

