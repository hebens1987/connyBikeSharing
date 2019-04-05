///* *********************************************************************** *
// * project: org.matsim.*
// * BikeSharingRouteFactory.java
// *                                                                         *
// * *********************************************************************** *
// *                                                                         *
// * copyright       : (C) 2014 by the members listed in the COPYING,        *
// *                   LICENSE and WARRANTY file.                            *
// * email           : info at matsim dot org                                *
// *                                                                         *
// * *********************************************************************** *
// *                                                                         *
// *   This program is free software; you can redistribute it and/or modify  *
// *   it under the terms of the GNU General Public License as published by  *
// *   the Free Software Foundation; either version 2 of the License, or     *
// *   (at your option) any later version.                                   *
// *   See also COPYING, LICENSE and WARRANTY file                           *
// *                                                                         *
// * *********************************************************************** */
//package eu.eunoiaproject.bikesharing.framework.scenario.bicycles;
//
//import org.apache.log4j.Logger;
//import org.matsim.api.core.v01.Id;
//import org.matsim.api.core.v01.TransportMode;
//import org.matsim.api.core.v01.network.Link;
//import org.matsim.api.core.v01.population.Route;
//import org.matsim.core.population.routes.LinkNetworkRouteImpl;
//import org.matsim.core.population.routes.RouteFactory;
//
///**
// * @author thibautd
// */
//public class BikeRouteFactory implements RouteFactory {
//	private static final Logger log =
//		Logger.getLogger(BikeRouteFactory.class);
//
//
//	@Override
//	public Route createRoute(
//			final Id<Link> startLinkId,
//			final Id<Link> endLinkId ) {
//		if ( log.isTraceEnabled() ) log.trace( "creating bike route between links "+startLinkId+" and "+endLinkId );
//		return new LinkNetworkRouteImpl( startLinkId , endLinkId );
//	}
//
//	@Override
//	public String getCreatedRouteType() {
//		return TransportMode.bike;
//	}
//
//}
//
