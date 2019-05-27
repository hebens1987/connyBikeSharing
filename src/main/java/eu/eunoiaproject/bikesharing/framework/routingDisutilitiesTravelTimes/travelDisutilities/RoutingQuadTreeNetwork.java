package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkImpl;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.QuadTreeRebuilder;

public class RoutingQuadTreeNetwork {
	
	private QuadTreeRebuilder<Link> quadTreeBuilderL = null;
	
	public RoutingQuadTreeNetwork (Network network)
	{
		if (quadTreeBuilderL == null)
		{
			this.quadTreeBuilderL = new QuadTreeRebuilder<>();
			List<Link> links = new ArrayList<Link>(network.getLinks().values());
			for (int i = 0; i < links.size(); i++)
			{
				quadTreeBuilderL.put(links.get(i).getCoord(), links.get(i));
			}
		}
	}
	public Network getCatchmentAreaNetwork(
			Network network, 
			Coord origin,
			Coord destination)
	{
		NetworkImpl newNet = (NetworkImpl)NetworkUtils.createNetwork();
	
		double newX = origin.getX() - (origin.getX() - destination.getX())/2;
		double newY = origin.getY() - (origin.getY() - destination.getY())/2;
		
		double distanceABhalf = CoordUtils.calcEuclideanDistance(origin, destination)/2;
		
		List<Link> links = new ArrayList<Link>(quadTreeBuilderL.getQuadTree().getDisk(newX, newY, distanceABhalf+1000));
		
		//List<Link> links = new ArrayList<Link>(network.getLinks().values());

		for (int i = 0; i <links.size(); i++) //adds all links within the definded disk + adds missing nodes
		{
			if ((links.get(i).getAllowedModes().contains(TransportMode.bike)) ||
				(links.get(i).getAllowedModes().contains(TransportMode.walk)))
				{
					if (newNet.getNodes().get(links.get(i).getFromNode().getId()) == null)
					{
						newNet.createAndAddNode(links.get(i).getFromNode().getId(),links.get(i).getFromNode().getCoord());
					}
					if (newNet.getNodes().get(links.get(i).getToNode().getId()) == null)
					{
						newNet.createAndAddNode(links.get(i).getToNode().getId(),links.get(i).getFromNode().getCoord());
					}
					//double len = CoordUtils.calcEuclideanDistance(links.get(i).getFromNode().getCoord(), links.get(i).getToNode().getCoord());
					//if ( len < links.get(i).getLength())
					//{
						double len = links.get(i).getLength();
					//}
					newNet.createAndAddLink(links.get(i).getId(), links.get(i).getFromNode(), links.get(i).getToNode(), 
							len, links.get(i).getFreespeed(), links.get(i).getCapacity(), links.get(i).getNumberOfLanes());
				//}
				}
		}
		return newNet;
	}
}
