package eu.eunoiaproject.freeFloatingBS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.vehicles.Vehicle;

import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacility;


public class ChooseBikeToTake {
	
	BikeSharingBikes bikesAll;
	
	public ChooseBikeToTake(Scenario scenario)
	{
		
		this.bikesAll = (BikeSharingBikes) 
				scenario.getScenarioElement( BikeSharingBikes.ELEMENT_NAME);
	}
	
	
	public BikeFFImpl chooseNearestBikeFF (Scenario scenario,Coord coord, double distance)
	{
		double distTemp = distance;
		if (distTemp == 0) {distTemp = 1000;}
		BikeFFImpl bike = null;
	
		List<BikesFF> list = new ArrayList<BikesFF>(bikesAll.bikesFF.values());
		
		for( int i = 0; i < list.size(); i++) 
		{
			if (list.get(i)!= null)
			{
				if (distTemp > CoordUtils.calcEuclideanDistance(coord, list.get(i).getCoordinate()))
				{
					distTemp = CoordUtils.calcEuclideanDistance(coord, list.get(i).getCoordinate());
					bike = (BikeFFImpl)list.get(i);
							
				}
			}
		}
		return bike;
	}
	
	public void removeBikeFFFromList (BikeFFImpl bikeToRemove)
	{
		bikesAll.bikesFF.remove(bikeToRemove.getBikeId());
	}
	
	public void addBikeFFToListAndActCoord (Id id, Coord coord, Id linkId)
	{
		
		Id <Vehicle> id2 = Id.create(id.toString(),Vehicle.class);
		BikesFF bike = new BikeFFImpl(id2, coord, linkId);
		bikesAll.bikesFF.put(id2, bike);

	}
}

	


	