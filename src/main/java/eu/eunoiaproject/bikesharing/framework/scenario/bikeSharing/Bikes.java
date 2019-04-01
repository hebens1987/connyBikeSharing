package eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing;

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
