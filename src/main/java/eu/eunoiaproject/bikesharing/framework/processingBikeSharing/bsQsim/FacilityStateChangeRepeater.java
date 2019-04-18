/* *********************************************************************** *
 * project: org.matsim.*
 * FacilityStateChangeRepeater.java
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
package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim;

import eu.eunoiaproject.bikesharing.framework.events.NewBikeSharingFacilityStateEventReturn;
import eu.eunoiaproject.bikesharing.framework.events.NewBikeSharingFacilityStateEventTake;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;

import org.matsim.core.mobsim.qsim.QSim;

/**
 * A simple {@link BikeSharingManagerListener}, which generates 
 * {@link NewBikeSharingFacilityStateEvent}s each time the state of a bike sharing
 * facility changes.
 * @author thibautd
 */
public class FacilityStateChangeRepeater implements EBikeSharingManagerListener {
	private final QSim qSim;
	

	public FacilityStateChangeRepeater(final QSim qSim) {
		this.qSim = qSim;
	}

	@Override
	public void handleChange(
			final BikeSharingFacility facilityInNewState, boolean isTaking) {
		
		if (isTaking)
		{
		qSim.getEventsManager().processEvent(
			new NewBikeSharingFacilityStateEventTake(
				qSim.getSimTimer().getTimeOfDay(),
				facilityInNewState.getStationId(),
			facilityInNewState.getNumberOfAvailableBikes(),
			facilityInNewState.getFreeParkingSlots()));
		}
		else
		{
			qSim.getEventsManager().processEvent(
					new NewBikeSharingFacilityStateEventReturn(
						qSim.getSimTimer().getTimeOfDay(),
						facilityInNewState.getStationId(),
					facilityInNewState.getNumberOfAvailableBikes(),
					facilityInNewState.getFreeParkingSlots()));
		}
	}
}

