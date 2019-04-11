/* *********************************************************************** *
 * project: org.matsim.*
 * BikeSharingTripRouterFactory.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package eu.eunoiaproject.bikesharing.framework.routingBikeSharingFramework;

import eu.eunoiaproject.bikesharing.framework.EBConstants;

//import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.router.NetworkRouting;


/**
 * Builds a  trip router factory for bike sharing simulations.
 * Inclues bike-sharing, e-bike-sharing, public transport usage
 * @author Hebenstreit
 */
public class EBikeSharingTripRouterModule extends AbstractModule {
	
	//private boolean routePtUsingSchedule = false;

	//private final Scenario scenario;


	public EBikeSharingTripRouterModule() 
	{
	}

	@Override
	public void install() {
		addRoutingModuleBinding( EBConstants.MODE ).to(EBikeSharingRoutingModule.class);
		addRoutingModuleBinding(TransportMode.bike).toProvider(new NetworkRouting(TransportMode.bike));
		addRoutingModuleBinding( TransportMode.walk ).toProvider(new NetworkRouting(TransportMode.walk));
		addRoutingModuleBinding( EBConstants.BS_BIKE ).toProvider(new NetworkRouting(TransportMode.bike));
		addRoutingModuleBinding( EBConstants.BS_E_BIKE ).toProvider(new NetworkRouting(TransportMode.bike));
		addRoutingModuleBinding( EBConstants.BS_WALK).toProvider(new NetworkRouting(TransportMode.walk));
		bind( MainModeIdentifier.class ).toInstance(
					new EBikeSharingModeIdentifier(
						 new MainModeIdentifierImpl() ) );
	}
}
