package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;


public interface BikesE extends Bikes{
	
	public double getStateOfCharge (); 
	public double getOhmicResistance ();
	public double getVoltage ();
	public double getBatteryChargeCapacity ();
	public void setStateOfCharge (double stateOfCharge); 
	public double getOrigStateOfCharge (); 
	public double getKmFullToEmpty();
	public boolean getOperable();
	// in station = charge
	// out of station = discharge
}
