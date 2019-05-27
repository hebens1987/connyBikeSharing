package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes;

	import java.util.Arrays;
	import java.util.List;

	import org.apache.log4j.Logger;
	import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
	import org.matsim.api.core.v01.network.Network;
	import org.matsim.api.core.v01.network.Node;
	import org.matsim.api.core.v01.population.Leg;
	import org.matsim.api.core.v01.population.Person;
	import org.matsim.api.core.v01.population.PlanElement;
	import org.matsim.api.core.v01.population.PopulationFactory;
	import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
	import org.matsim.core.network.NetworkUtils;
	import org.matsim.core.population.LegImpl;
	import org.matsim.core.population.routes.RouteFactoryImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.population.routes.NetworkRoute;
	import org.matsim.core.population.routes.RouteUtils;
	import org.matsim.core.router.Dijkstra;
	import org.matsim.core.router.EmptyStageActivityTypes;
import org.matsim.core.router.FastAStarLandmarks;
import org.matsim.core.router.NetworkRouting;
	import org.matsim.core.router.RoutingModule;
	import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.util.FastAStarLandmarksFactory;
import org.matsim.core.router.util.FastDijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
	import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.facilities.Facility;

	import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.BikesharingAgentFactory;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.RoutingQuadTreeNetwork;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_BikeTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_BikeTravelDisutilityDir;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BikeTravelTime;



	/**
	 * This wraps a "computer science" {@link LeastCostPathCalculator}, which routes from a node to another node, into something that
	 * routes from a {@link Facility} to another {@link Facility}, as we need in MATSim.
	 * 
	 * @author thibautd
	 * @param <BicycleConfigGroup>
	 */
	public final class NetworkRoutingModuleBicycle<BicycleConfigGroup> implements RoutingModule{
		// I think it makes sense to NOT add the bushwhacking mode directly into here ...
		// ... since it makes sense be able to to route from facility.getLinkId() to facility.getLinkId(). kai, dec'15

		@Inject Scenario scenario;

		@Inject
		public NetworkRoutingModuleBicycle()
		{
		}

		@Override
		public List<? extends PlanElement> calcRoute(
				final Facility<?> fromFacility,
				final Facility<?> toFacility,
				final double departureTime,
				final Person person) {
			
			//BicycleConfigGroup config = (BicycleConfigGroup) scenario.getConfig().getModule("bicycleAttributes");
			//TravelTime btt = new TUG_BikeTravelTime((eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup) config);
			//TravelDisutility btd = new TUG_BikeTravelDisutility((eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup) config);
			
			Leg newLeg = new LegImpl(TransportMode.bike);
			newLeg.setDepartureTime( departureTime );

			Link fromLink = this.scenario.getNetwork().getLinks().get(fromFacility.getLinkId());
			Link toLink = this.scenario.getNetwork().getLinks().get(toFacility.getLinkId());

			/* Remove this and next three lines once debugged. */
			if(fromLink == null || toLink == null){
				Logger.getLogger(NetworkRoutingModuleBicycle.class).error("  ==>  null from/to link for person " + person.getId().toString());
			}
			if (fromLink == null) throw new RuntimeException("fromLink "+fromFacility.getLinkId()+" missing.");
			if (toLink == null) throw new RuntimeException("toLink "+toFacility.getLinkId()+" missing.");

			
			newLeg= routeLeg(
					person,
					newLeg,
					fromLink,
					toLink,
					departureTime);

			return Arrays.asList( newLeg );
		}

		@Override
		public StageActivityTypes getStageActivityTypes() {
			return EmptyStageActivityTypes.INSTANCE;
		}

		@Override
		public String toString() {
			return "[NetworkRoutingModule: mode="+"bike"+"]";
		}
		
		private Path getFullNetworkPath(Person person, Leg leg, Link fromLink, Link toLink, double depTime) 
		{
			BicycleConfigGroup config = (BicycleConfigGroup) scenario.getConfig().getModule("bicycleAttributes");
			TravelTime btt = new TUG_BikeTravelTime((eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup) config);
			TravelDisutility btd = new TUG_BikeTravelDisutility((eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup) config);
			RoutingQuadTreeNetwork rqtn = new RoutingQuadTreeNetwork(scenario.getNetwork());
			Network net = scenario.getNetwork();
			LeastCostPathCalculatorFactory routeAlgoFac = new FastDijkstraFactory();
			LeastCostPathCalculator routeAlgo = routeAlgoFac.createPathCalculator(net, btd, btt);

			Node startNode = fromLink.getToNode();	// start at the end of the "current" link
			Node endNode = toLink.getFromNode(); // the target is the start of the link

			return routeAlgo.calcLeastCostPath(startNode, endNode, depTime, person, null);
		}

		private Leg routeLeg(Person person, Leg leg, Link fromLink, Link toLink, double depTime) {
			
			BicycleConfigGroup config = (BicycleConfigGroup) scenario.getConfig().getModule("bicycleAttributes");
			TravelTime btt = new TUG_BikeTravelTime((eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup) config);
			TravelDisutility btd = new TUG_BikeTravelDisutility((eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup) config);
			RoutingQuadTreeNetwork rqtn = new RoutingQuadTreeNetwork(scenario.getNetwork());
			Network net = scenario.getNetwork();
			Network newNet = rqtn.getCatchmentAreaNetwork(net, fromLink.getCoord(), toLink.getCoord());
			LeastCostPathCalculatorFactory routeAlgoFac = new FastDijkstraFactory();
			LeastCostPathCalculator routeAlgo = routeAlgoFac.createPathCalculator(newNet, btd, btt);
			double travTime = 0;

			Node startNode = fromLink.getToNode();	// start at the end of the "current" link
			Node endNode = toLink.getFromNode(); // the target is the start of the link

//			CarRoute route = null;
//			Path path = null;
			if (toLink != fromLink) {
				Path path = routeAlgo.calcLeastCostPath(startNode, endNode, depTime, person, null);
				if (path == null)
				{
					path = getFullNetworkPath(person, leg, fromLink, toLink, depTime);
				}
				NetworkRoute route = new LinkNetworkRouteImpl(fromLink.getId(), toLink.getId());
				route.setLinkIds(fromLink.getId(), NetworkUtils.getLinkIds(path.links), toLink.getId());
				route.setTravelTime((int) path.travelTime); // yyyy why int?  kai, dec'15
				route.setTravelCost(path.travelCost);
				route.setDistance(RouteUtils.calcDistanceExcludingStartEndLink(route, newNet));
				leg.setRoute(route);
				travTime = (int) path.travelTime; // yyyy why int?  kai, dec'15
			} else {
				// create an empty route == staying on place if toLink == endLink
				// note that we still do a route: someone may drive from one location to another on the link. kai, dec'15
				NetworkRoute route = new LinkNetworkRouteImpl(fromLink.getId(), toLink.getId());
				route.setTravelTime(10);
				route.setDistance(10.0);
				leg.setRoute(route);
				travTime = 10;
			}

			leg.setDepartureTime(depTime);
			leg.setTravelTime(travTime);
			if ( leg instanceof LegImpl ) {
				((LegImpl) leg).setArrivalTime(depTime + travTime); 
				// (not in interface!)
			}
			leg.setTravelTime(travTime);
			return leg;
		}

	}

