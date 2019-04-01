package eu.eunoiaproject.freeFloatingBS;

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
public interface FFDummyFacility extends Facility<FFDummyFacility> {
	public Coord getCoord();
	public Id<BikesFF> getStationId();
	public Id<Link> getLinkId();
}

