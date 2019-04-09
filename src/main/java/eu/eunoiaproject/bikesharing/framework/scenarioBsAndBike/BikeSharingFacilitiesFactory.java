/* *********************************************************************** *
 * project: org.matsim.*
 * BikeSharingFacilitiesFactory.java
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
package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;

import java.util.List;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.api.internal.MatsimFactory;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.WaitingData;

/**
 * Associated to a {@link BikeSharingFacilities} container,
 * allows to create facilities in a standard way.
 * @author thibautd 
 */
public interface BikeSharingFacilitiesFactory extends MatsimFactory {

	public BikeSharingFacility createBikeSharingFacility(
			Id<BikeSharingFacility> id, 
			Coord coord, 
			int numberOfAvailableBikes,
			int numberOfAvailableBikes2,
			int totalNumberOfBikes, 
			int freeParkingSlots, 
			String type, 
			String[] cyclesInStation, 
			String[] cyclesInStation2, 
			Id<Link> linkId,
			List<WaitingData>  waitingAgentsTake,
			List<WaitingData>  waitingAgentsReturn);
}
