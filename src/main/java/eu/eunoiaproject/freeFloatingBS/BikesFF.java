package eu.eunoiaproject.freeFloatingBS;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.vehicles.Vehicle;

public interface BikesFF extends Vehicle {

	public Id getBikeId ();
	public void setCoordinate (Coord coord);
	public Coord getCoordinate();
	public Id getLinkId();
	public void setLinkId(Id linkId);
}
