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
package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;

import org.matsim.api.core.v01.Id;
import org.matsim.vehicles.Vehicle;

public interface Bikes extends Vehicle {
	public boolean getIsEBike(); //0 --> conventional, 1 --> e-Bike
	public Id<Vehicle> getBikeId ();
	public double getTime ();
	public boolean getInfoIfBikeInStation();
	public Id<BikeSharingFacility> getInStation ();
	public void setTime (double time);
	public void setInfoIfBikeInStation(boolean bikeInStation);
	public void setInStation (Id<BikeSharingFacility> station);
	public Id<BikeSharingFacility> getOrigStation();
}
