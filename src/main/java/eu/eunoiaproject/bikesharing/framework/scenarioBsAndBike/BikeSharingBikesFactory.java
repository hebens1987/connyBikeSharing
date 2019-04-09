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

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.core.api.internal.MatsimFactory;

import eu.eunoiaproject.freeFloatingBS.BikesFF;

/**
 * Associated to a {@link BikeSharingFacilities} container,
 * allows to create facilities in a standard way.
 * @author thibautd
 */
public interface BikeSharingBikesFactory extends MatsimFactory {

	public BikesE createBikesE(BikesE ebike);
	
	public BikesE createBikesE(
			final Id id,
			int time,
			double stateOfCharge,
			boolean infoIfBikeInStation,
			final double ohmicResistance,
			final double voltage,
			final double batteryChargeCapacity,
			Id station,
			final double kmFullToEmpty,
			boolean isEType,
			Id origStation,
			double origStateOfCharge);
	
	public Bikes createBikesC(Bikes bike);
	
	public Bikes createBikesC(
			final Id bikesId,
			int timeOfLastAct,
			boolean infoIfBikeInStation,
			boolean isEType,
			Id station,
			Id origStation);
	
	public BikesFF createBikesFF(BikesFF bike);
	
	public BikesFF createBikesFF(
			final Id id,
			Coord coord,
			Id linkId);
	
	public BikesFF createBikesFF(
			final Id id,
			double x,
			double y,
			Id linkId);
	
	
}

