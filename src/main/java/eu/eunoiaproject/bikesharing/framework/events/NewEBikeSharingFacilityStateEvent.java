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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.config.ReflectiveConfigGroup.StringGetter;
import org.matsim.core.config.ReflectiveConfigGroup.StringSetter;
import org.matsim.facilities.Facility;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;

import java.util.Map;

/**
 * @author thibautd
 */
public class NewEBikeSharingFacilityStateEvent extends Event {
	public static final String EVENT_TYPE = "newEBikeSharingFacilityState";

	int numberOfAvailableBikes;
	int totalBikeNumber;
	int freeParkingSlots;
	BikeSharingFacility station;
	Id<BikeSharingFacility> facilityId;
	String stationType;
	String [] cycles_in_station;
	int newAmountOfBikes;

	public NewEBikeSharingFacilityStateEvent(
			final Event event) {
		super( event.getTime() );
		if ( !event.getEventType().equals( EVENT_TYPE ) ) {
			throw new IllegalArgumentException( ""+event );
		}
		this.facilityId = Id.create( event.getAttributes().get( "stationId" ) , BikeSharingFacility.class );
		this.totalBikeNumber= Integer.parseInt( event.getAttributes().get( "totalNumberOfBikeSlots" ) ); 
		this.numberOfAvailableBikes= Integer.parseInt( event.getAttributes().get( "initialNumberOfBikes" ) ); 
		this.freeParkingSlots = Integer.parseInt( event.getAttributes().get( "numberOfEmptyBikeParkings" ) ); 
		this.stationType = ( event.getAttributes().get( "type" ) );
		this.cycles_in_station = event.getAttributes().get( "availableBikes" ).split(",");
		if (this.cycles_in_station == null)
		{
			this.cycles_in_station[0] = event.getAttributes().get( "availableBikes" ).toString();
		}

	}

	public NewEBikeSharingFacilityStateEvent(
			final double time,
			final Id<BikeSharingFacility> id,
			final int newAmountOfBikes) {
		super( time );
		this.facilityId = id;
		this.newAmountOfBikes = newAmountOfBikes;
	}


	public Id<BikeSharingFacility> getFacilityId() {
		return facilityId;
	}

	public int getNewAmountOfBikes() {
		return newAmountOfBikes;
	}
	

	@Override
	public Map<String, String> getAttributes() {
		final Map<String, String> atts = super.getAttributes();
		atts.put( "facilityId" , facilityId.toString() );
		atts.put( "availableBikes" , newAmountOfBikes+"" );
		return atts;
	}

	@Override
	public String getEventType() {
		return EVENT_TYPE;
	}
}

