/* *********************************************************************** *
 * project: org.matsim.*
 * BikeSharingFacility.java
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
import org.matsim.facilities.Facility;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.WaitingData;

/**
 * Defines a bike sharing station: something with an Id,
 * a location (coord + link), a capacity and an initial
 * number of bikes.
 * @author thibautd
 */
public interface BikeSharingFacility extends Facility<BikeSharingFacility> {
	public int getNumberOfAvailableBikes();
	public int getTotalBikeNumber();
	public void setNumberOfAvailableBikes(int number);
	public int getFreeParkingSlots();
	public void setFreeParkingSlots(int number);
	public Coord getCoord();
	public Id<BikeSharingFacility> getStationId();
	public String getStationType();
	public String [] getCycles_in_station();
	public void setCycles_in_station(String [] cycles_in_station);
	public Id<Link> getLinkId();
	public int getOrigAvailBikes();
	public String [] getOrigCyclesInStation();
	public List<WaitingData> getWaitingToTakeBike();
	public void setWaitingToTakeBike(List<WaitingData> waitingAgents);
	public List<WaitingData>  getWaitingToReturnBike();
	public void setWaitingToReturnBike(List<WaitingData>  waitingAgents);
	public boolean getOngoingRelocation();
	public void setOngoingRelocation(boolean bol);
	public String[] getBikesInRelocation();
	public void setBikesInRelocation(String[] bikes);
	public double getTimeOfRelocation();
	public void setTimeOfRelocation(double time);
	
}

