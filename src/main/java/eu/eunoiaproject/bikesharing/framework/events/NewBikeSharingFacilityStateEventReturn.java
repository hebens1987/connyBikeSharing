/* *********************************************************************** *
 * project: org.matsim.*
 * NewBikeSharingFacilityStateEvent.java
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

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;

import java.util.Map;

public class NewBikeSharingFacilityStateEventReturn extends Event {
	public static final String EVENT_TYPE = EBConstants.INTERACTION_TYPE_BS+"_r";

	private final Id<BikeSharingFacility> facilityId;
	private final int newAmountOfBikes;
	private final int newAmountOfParkings;

	public NewBikeSharingFacilityStateEventReturn(
			final Event event) {
		super( event.getTime() );
		if ( !event.getEventType().equals( EVENT_TYPE ) ) {
			throw new IllegalArgumentException( ""+event );
		}
		this.facilityId = Id.create( event.getAttributes().get( "facilityId" ) , BikeSharingFacility.class );
		this.newAmountOfBikes = Integer.parseInt( event.getAttributes().get( "newAmountOfBikes" ) ); 
		this.newAmountOfParkings = Integer.parseInt(event.getAttributes().get("newAmountOfParkings"));
	}

	public NewBikeSharingFacilityStateEventReturn(
			final double time,
			final Id<BikeSharingFacility> facilityId,
			final int newAmountOfBikes,
			final int newAmountOfParkingSpots) {
		super( time );
		this.facilityId = facilityId;
		this.newAmountOfBikes = newAmountOfBikes;
		this.newAmountOfParkings = newAmountOfParkingSpots;
	}
	

	public Id<BikeSharingFacility> getFacilityId() {
		return facilityId;
	}

	public int getNewAmountOfBikes() {
		return newAmountOfBikes;
	}
	
	public int getNewAmountOfParkings() {
		return newAmountOfBikes;
	}

	@Override
	public Map<String, String> getAttributes() {
		final Map<String, String> atts = super.getAttributes();
		atts.put( "facilityId" , facilityId.toString() );
		atts.put( "newAmountOfBikes" , newAmountOfBikes+"" );
		atts.put("newAmountOfParkings", newAmountOfParkings+"");
		return atts;
	}

	@Override
	public String getEventType() {
		return EVENT_TYPE;
	}
	
	
}

