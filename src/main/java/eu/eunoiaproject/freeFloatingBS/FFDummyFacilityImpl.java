package eu.eunoiaproject.freeFloatingBS;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.WaitingData;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacility;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of a bike sharing facility.
 * @author thibautd
 */
public class FFDummyFacilityImpl implements FFDummyFacility {
	private final Coord coord;
	private final Id<BikesFF> id;
	public Id<Link> linkId;

	private final Map<String, Object> customAttributes = new LinkedHashMap<String, Object>();

	public FFDummyFacilityImpl(FFDummyFacility bsf) {
		this.id = bsf.getStationId();
		this.coord = bsf.getCoord();
		this.linkId = bsf.getLinkId();

		
	}
	
	public FFDummyFacilityImpl(
			final Id id,
			final Coord coord,
			Id<Link> linkId) {
		this.id = id;
		this.coord = coord;
		this.linkId = linkId;
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
	public Map<String, Object> getCustomAttributes() {
		return customAttributes;
	}

	@Override
	public Id<Link> getLinkId() {
		return linkId;
	}

	@Override
	public Id<BikesFF> getStationId() {
		return id;
	}
}

