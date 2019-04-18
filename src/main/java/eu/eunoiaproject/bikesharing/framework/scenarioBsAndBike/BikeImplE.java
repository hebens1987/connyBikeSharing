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
import org.matsim.vehicles.VehicleType;

//import java.util.LinkedHashMap;
//import java.util.Map;

public class BikeImplE implements BikesE{
	
	final Id<Vehicle> id;
	double time;
	double relocTime;
	Id<BikeSharingFacility> idReloc;
	double stateOfCharge;
	boolean bikeInStation;
	final double ohmicResistance;
	final double voltage;
	final double batteryChargeCapacity;
	Id<BikeSharingFacility> station2;
	double distance;
	//private final Map<String, Object> customAttributes = new LinkedHashMap<String, Object>();
	private boolean type;
	boolean operable;
	Id<BikeSharingFacility> origStation;
	double origStateOfCharge;
	
	
	/***************************************************************************/
	public BikeImplE (
			BikesE ebike) 
	/***************************************************************************/
	{
		this.id = ebike.getBikeId();
		this.time = ebike.getTime();
		this.stateOfCharge = ebike.getStateOfCharge();
		this.bikeInStation = ebike.getInfoIfBikeInStation();
		this.ohmicResistance = ebike.getOhmicResistance();
		this.voltage = ebike.getVoltage();
		this.batteryChargeCapacity = ebike.getBatteryChargeCapacity();
		this.station2 = ebike.getInStation();
		this.distance = ebike.getKmFullToEmpty();
		this.type = ebike.getIsEBike();
	}
	
	/***************************************************************************/
	public BikeImplE (Id<Vehicle> id, double time, double stateOfCharge, boolean bikeInStation, double ohmicResistance,
			double voltage, double batteryChargeCapacity, Id station, double distance, boolean type, Id origStation,
			double origStateOfCharge)
	/***************************************************************************/
	{
		this.id = id;
		this.time = time;
		this.stateOfCharge = stateOfCharge;
		this.bikeInStation = bikeInStation;
		this.ohmicResistance = ohmicResistance;
		this.voltage = voltage;
		this.batteryChargeCapacity = batteryChargeCapacity;
		this.station2 = station;
		this.distance = distance;
		this.type = type;
		this.origStation = origStation;
		this.origStateOfCharge = origStateOfCharge;
	}
	

	public void addBikes( final BikeImplE bikes ) {
		bikes.getBikeId();
	}

	
	// /////////////////////////////////////////////////////////////////////////
	// facility interface
	// /////////////////////////////////////////////////////////////////////////
	
	/***************************************************************************/
	@Override
	public Id<Vehicle> getBikeId() 
	/***************************************************************************/
	{
		return id ;
	}

	/***************************************************************************/
	@Override
	public double getTime() 
	/***************************************************************************/
	{
		return time;
	}

	/***************************************************************************/
	@Override
	public double getStateOfCharge() 
	/***************************************************************************/
	{
		return stateOfCharge;
	}

	/***************************************************************************/
	@Override
	public boolean getInfoIfBikeInStation() 
	/***************************************************************************/
	{
		return bikeInStation;
	}

	/***************************************************************************/
	@Override
	public double getOhmicResistance() 
	/***************************************************************************/
	{
		return ohmicResistance;
	}

	/***************************************************************************/
	@Override
	public double getVoltage() 
	/***************************************************************************/
	{
		return voltage;
	}

	/***************************************************************************/
	@Override
	public double getBatteryChargeCapacity() 
	/***************************************************************************/
	{
		return batteryChargeCapacity;
	}

	/***************************************************************************/
	@Override
	public Id<BikeSharingFacility> getInStation() 
	/***************************************************************************/
	{
		return station2;
	}

	/***************************************************************************/
	@Override
	public void setTime(double time) 
	/***************************************************************************/
	{
		this.time = time;
	}

	/***************************************************************************/
	@Override
	public void setStateOfCharge(double stateOfCharge)
	/***************************************************************************/
	{
	    this.stateOfCharge = stateOfCharge;	
	}

	/***************************************************************************/
	@Override
	public void setInfoIfBikeInStation(boolean bikeInStation) 
	/***************************************************************************/
	{
		this.bikeInStation = bikeInStation;
	}

	/***************************************************************************/
	@Override
	public void setInStation(Id<BikeSharingFacility> station) 
	/***************************************************************************/
	{
		this.station2 = station;	
	}

	/***************************************************************************/
	@Override
	public boolean getIsEBike() 
	/***************************************************************************/
	{
		return type;	
	}
	
	/***************************************************************************/
	@Override
	public double getKmFullToEmpty() 
	/***************************************************************************/
	{
		return distance;
	}

	/***************************************************************************/
	@Override
	public boolean getOperable() 
	/***************************************************************************/
	{
		return operable;
	}

	@Override
	public VehicleType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Id<Vehicle> getId() {
		Id<Vehicle> vehicleId = Id.create(id.toString(), Vehicle.class);
		return vehicleId;
	}

	@Override
	public Id getOrigStation() {
		return origStation;
		
	}
	
	@Override
	public double getOrigStateOfCharge() {
		return origStateOfCharge;
		
	}
}

	


	