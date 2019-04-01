/* *********************************************************************** *
 * project: org.matsim.*
 * BikeSharingFacilityImpl.java
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
package eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.TakingReturningMethodology;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.WaitingData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of a bike sharing facility.
 * @author thibautd
 */
public class BikeSharingFacilityImpl implements BikeSharingFacility {
	private final Coord coord;
	private final Id id;
	public int origAvailBikes;
	public String[] origCyclesInStation;
	public List<WaitingData> waitingAgentsTake;
	public List<WaitingData> waitingAgentsReturn;
	TakingReturningMethodology tr = new TakingReturningMethodology ();
	int numberOfAvailableBikes;
	int totalNumberOfBikes;
	int freeParkingSlots;
	String type;
	boolean ongoingRelocation;
	String[] bikesInRelocation;
	double relocationAtTime;
	String[]cyclesInStation;
	public Id<Link> linkId;

	private final Map<String, Object> customAttributes = new LinkedHashMap<String, Object>();
	
	public BikeSharingFacilityImpl() 
	{
		this.id = null;
		this.coord = null;
		this.numberOfAvailableBikes = -1;
		this.origAvailBikes = -1;
		this.totalNumberOfBikes = -1;
		this.freeParkingSlots = -1;
		this.type = null;
		this.cyclesInStation = null;
		this.linkId = null;
		this.waitingAgentsTake = null;
		this.waitingAgentsReturn = null;
	}

	public BikeSharingFacilityImpl(
			BikeSharingFacility bsf) 
	{
		this.id = bsf.getId();
		this.coord = bsf.getCoord();
		this.numberOfAvailableBikes = bsf.getNumberOfAvailableBikes();
		this.origAvailBikes = bsf.getOrigAvailBikes();
		this.totalNumberOfBikes = bsf.getTotalBikeNumber();
		this.freeParkingSlots = bsf.getFreeParkingSlots();
		this.type = bsf.getStationType();
		this.cyclesInStation = bsf.getCycles_in_station();
		this.linkId = bsf.getLinkId();
		this.waitingAgentsTake = bsf.getWaitingToTakeBike();
		this.waitingAgentsReturn = bsf.getWaitingToReturnBike();
	}
	
	public BikeSharingFacilityImpl(
			final Id id,
			final Coord coord,
			int numberOfAvailableBikes,
			int numberOfAvailableBikesOrig,
			int totalNumberOfBikes,
			int freeParkingSlots,
			String type,
			String[]cyclesInStation,
			String[]cyclesInStationOrig,
			Id<Link> linkId,
			List<WaitingData> waitingAgentsTake,
			List<WaitingData> waitingAgentsReturn
			) {
		this.id = id;
		this.coord = coord;
		this.numberOfAvailableBikes = numberOfAvailableBikes;
		this.origAvailBikes = numberOfAvailableBikesOrig;
		this.origCyclesInStation = cyclesInStationOrig;
		this.totalNumberOfBikes = totalNumberOfBikes;
		this.freeParkingSlots = freeParkingSlots;
		this.type = type;
		this.cyclesInStation = cyclesInStation;
		this.linkId = linkId;
		this.waitingAgentsTake = waitingAgentsTake;
		this.waitingAgentsReturn = waitingAgentsReturn;
	}

	// /////////////////////////////////////////////////////////////////////////
	// facility interface
	// /////////////////////////////////////////////////////////////////////////
	@Override
	public String[] getOrigCyclesInStation() {
		return origCyclesInStation;
	}
	
	@Override
	public Coord getCoord() {
		return coord;
	}

	@Override
	public Id getId() {
		return id;
	}
	
	@Override
	public int getOrigAvailBikes() {
		return origAvailBikes ;
	}

	@Override
	public Map<String, Object> getCustomAttributes() {
		return customAttributes;
	}


	// /////////////////////////////////////////////////////////////////////////
	// specific methods
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public int getNumberOfAvailableBikes() {
		
		return numberOfAvailableBikes;
	}

	@Override
	public int getTotalBikeNumber() {
		return totalNumberOfBikes;
	}

	@Override
	public void setNumberOfAvailableBikes(int number) {
		this.numberOfAvailableBikes = number;
		
	}

	@Override
	public int getFreeParkingSlots() {
		return freeParkingSlots;
	}

	@Override
	public void setFreeParkingSlots(int number) {
		this.freeParkingSlots = number;
		
	}

	@Override
	public Id<BikeSharingFacility> getStationId() {
		return id;
	}

	@Override
	public String getStationType() {
		return type;
	}

	@Override
	public String[] getCycles_in_station() {
		return cyclesInStation;
	}

	@Override
	public void setCycles_in_station(String[] cycles_in_station) {
		this.cyclesInStation = cycles_in_station;
		
	}

	@Override
	public Id<Link> getLinkId() {
		return linkId;
	}

	@Override
	public List<WaitingData> getWaitingToTakeBike() {
		if (waitingAgentsTake != null && waitingAgentsTake.size() >1)
		{
		 tr.bubbleSort(waitingAgentsTake, waitingAgentsTake.size());
		}
		return waitingAgentsTake;
	}

	@Override
	public void setWaitingToTakeBike(List<WaitingData> waitingAgentsTake) {
		this.waitingAgentsTake = waitingAgentsTake;
		
	}
	
	@Override
	public List<WaitingData>  getWaitingToReturnBike() {
			
		if (waitingAgentsReturn != null && waitingAgentsReturn.size() >1)
		{
			 tr.bubbleSort(waitingAgentsReturn, waitingAgentsReturn.size());
			}
		return waitingAgentsReturn;
	}

	@Override
	public void setWaitingToReturnBike(List<WaitingData> waitingAgentsReturn) {
		this.waitingAgentsReturn = waitingAgentsReturn;
		
	}

	@Override
	public boolean getOngoingRelocation() {
		return ongoingRelocation;
	}

	@Override
	public void setOngoingRelocation(boolean bol) {
		this.ongoingRelocation = bol;
	}

	@Override
	public String[] getBikesInRelocation() {
		return bikesInRelocation;
	}

	@Override
	public void setBikesInRelocation(String[] bikes) {
		this.bikesInRelocation = bikes;
		
	}

	@Override
	public double getTimeOfRelocation() {
		return relocationAtTime;
	}

	@Override
	public void setTimeOfRelocation(double time) {
		this.relocationAtTime = time;
		
	}


}

