package eu.eunoiaproject.freeFloatingBS;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.framework.PassengerAgent;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BikeFFImpl implements BikesFF{
	
	final Id id;
	Id linkId;
	Coord coordinate = new Coord(0,0);
	
	/***************************************************************************/
	public BikeFFImpl (BikesFF bike) 
	/***************************************************************************/
	{
		this.id = bike.getBikeId();
		this.coordinate = bike.getCoordinate();
		this.linkId = bike.getLinkId();
	}
	
	/***************************************************************************/
	public BikeFFImpl (Id id, double x, double y, Id linkId) 
	/***************************************************************************/
	{
		this.id = id;
		this.coordinate.setX(x);
		this.coordinate.setY(y);
		this.linkId = linkId;
	}
	
	
	/***************************************************************************/
	public void addBikes( final BikeFFImpl bikes ) 
	/***************************************************************************/
	{
		bikes.getBikeId();
	}
	
	/***************************************************************************/
	public BikeFFImpl (	
			final Id id, Coord coord, Id linkId) 
	/***************************************************************************/
	{
		this.id = id;
		this.coordinate = coord;
		this.linkId = linkId;
		
	}
	
	/***************************************************************************/
	@Override
	public Id<BikesFF> getBikeId() 
	/***************************************************************************/
	{
		return id ;
	}

	/***************************************************************************/
	@Override
	public Coord getCoordinate() 
	/***************************************************************************/
	{
		return coordinate;
	}

	

	/***************************************************************************/
	@Override
	public void setCoordinate(Coord coord) 
	/***************************************************************************/
	{
		this.coordinate = coord;
	}

	/***************************************************************************/
	@Override
	public Id<Vehicle> getId()
	/***************************************************************************/
	{
		Id <Vehicle> vehicleId = Id.create(id.toString(), Vehicle.class);
		return vehicleId ;
	}
	
	/***************************************************************************/
	@Override
	public VehicleType getType() 
	/***************************************************************************/
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Id getLinkId() {
		return linkId;
	}
	
	@Override
	public void setLinkId(Id linkId) 
	{
		this.linkId = linkId;
	}
}

	


	