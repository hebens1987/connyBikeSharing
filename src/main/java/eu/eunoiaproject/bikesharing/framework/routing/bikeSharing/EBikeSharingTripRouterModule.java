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
package eu.eunoiaproject.bikesharing.framework.routing.bikeSharing;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BSBikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BSEBikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routing.pedestrians.TUG_WalkRoutingModule;
//import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;


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
		addRoutingModuleBinding( TransportMode.bike ).to(TUG_BikeRoutingModule.class);
		addRoutingModuleBinding( TransportMode.walk ).to(TUG_WalkRoutingModule.class);
		addRoutingModuleBinding( EBConstants.BS_BIKE ).to(TUG_BSBikeRoutingModule.class);
		addRoutingModuleBinding( EBConstants.BS_E_BIKE ).to(TUG_BSEBikeRoutingModule.class);
		addRoutingModuleBinding( EBConstants.BS_WALK).to(TUG_WalkRoutingModule.class);
		bind( MainModeIdentifier.class ).toInstance(
					new EBikeSharingModeIdentifier(
						 new MainModeIdentifierImpl() ) );
	}
}
