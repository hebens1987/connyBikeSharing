package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;

import org.matsim.api.core.v01.Id;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;

//import java.util.LinkedHashMap;
//import java.util.Map;

public class BikeImpl implements Bikes{
	
	final Id<Vehicle> id;
	double time;
	double relocTime;
	Id<BikeSharingFacility> idReloc;
	boolean bikeInStation;
	Id<BikeSharingFacility> station2;
	boolean isEType;
	//private final Map<String, Object> customAttributes = new LinkedHashMap<String, Object>();
	MobsimAgent agent;
	Id<BikeSharingFacility> origStation;
	
	/***************************************************************************/
	public BikeImpl (
			Bikes bike
			) 
	/***************************************************************************/
	{
		this.id = bike.getBikeId();
		this.time = bike.getTime();
		this.bikeInStation = bike.getInfoIfBikeInStation();
		this.isEType = bike.getIsEBike();
	}
	
	public void addBikes( final BikeImpl bikes ) {
		bikes.getBikeId();
	}
	
	/***************************************************************************/
	public BikeImpl (
			final Id<Vehicle> id,int time, boolean bikeInStation, boolean isEType, Id station2, Id origStation
			) 
	/***************************************************************************/
	{
		this.id = id;
		this.time = time;
		this.bikeInStation = bikeInStation;
		this.isEType = isEType;
		this.station2 = station2;
		this.origStation = origStation;
		this.relocTime = Double.POSITIVE_INFINITY;
		
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
	public boolean getInfoIfBikeInStation() 
	/***************************************************************************/
	{
		return bikeInStation;
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
		return isEType;	
	}

	@Override
	public Id<BikeSharingFacility> getInStation() {
		return station2;
	}

	@Override
	public Id<Vehicle> getId() {
		Id <Vehicle> vehicleId = Id.create(id.toString(), Vehicle.class);
		return vehicleId ;
	}
	
	@Override
	public VehicleType getType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Id getOrigStation() {
		return origStation;
	}

}

	


	