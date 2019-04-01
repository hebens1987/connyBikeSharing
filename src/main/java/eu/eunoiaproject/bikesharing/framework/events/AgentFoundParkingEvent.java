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

import eu.eunoiaproject.bikesharing.framework.events.AgentStartsWaitingForBikeEvent;
import eu.eunoiaproject.bikesharing.framework.events.AgentStopsWaitingForFreeBikeSlotEvent;
import eu.eunoiaproject.bikesharing.framework.events.ChangeLegEvent;

/**
 * @author thibautd
 */
public class AgentFoundParkingEvent extends AgentStopsWaitingForFreeBikeSlotEvent {
	public static final String EVENT_TYPE = "agent stops Waiting as he found a parking";

	/***************************************************************************/
	public AgentFoundParkingEvent(
			final Event event) 
	/***************************************************************************/
	{
		super( event );
		if ( !event.getEventType().equals( EVENT_TYPE ) ) {
			throw new IllegalArgumentException( event.toString() );
		}
	}

	/***************************************************************************/
	public AgentFoundParkingEvent(
			final double time,
			final Id personId,
			final Id facilityId) 
	/***************************************************************************/
	{
		super( time , personId, facilityId );
	}

	/***************************************************************************/
	@Override
	public String getEventType() 
	/***************************************************************************/
	{
		return EVENT_TYPE;
	}
}

