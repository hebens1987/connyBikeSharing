/* *********************************************************************** *
 * project: org.matsim.													   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,     *
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

/**
 * @author hebens
 */
package eu.eunoiaproject.bikesharing.examples.example03configurablesimulation;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.EBikeSharingQsimFactory;
import eu.eunoiaproject.bikesharing.framework.routingBikeSharingFramework.EBikeSharingRoutingModule;
import eu.eunoiaproject.bikesharing.framework.scoring.TUG_LegScoringFunctionBikeAndWalkFactory;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules.TUG_WalkRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.IKK_BikeTravelDisutilityFactory;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BikeTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_EBSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_WalkTravelTime;
import eu.eunoiaproject.freeFloatingBS.FFBikeSharingRoutingModule;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.router.LeastCostPathCalculatorModule;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.router.NetworkRouting;
import org.matsim.core.router.SingleModeNetworksCache;
import org.matsim.pt.router.TransitRouterModule;

class ImplementationModule extends AbstractModule {
	
	private final Config config;

		ImplementationModule( Config config )
		{
			this.config= config;
		}

		//@Override
		 public void install()
		 { 
			this.bind(MainModeIdentifier.class).to(MainModeIdentifierImpl.class);
		    this.install(new LeastCostPathCalculatorModule());
			 BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class ) ;
			 switch( bikeSharingConfig.getRunType() ) {
			    case standard:
				    this.install(new TransitRouterModule());
				    break;
			    case debug:
				    break;
			    default:
			    	throw new RuntimeException( "not implemented" ) ;
		    }
		    this.bind(SingleModeNetworksCache.class).asEagerSingleton();
		
			this.addRoutingModuleBinding(EBConstants.MODE ).to(EBikeSharingRoutingModule.class);
			
			this.addTravelTimeBinding(EBConstants.MODE_FF).to(TUG_BSTravelTime.class); 

			this.addTravelDisutilityFactoryBinding(EBConstants.MODE_FF ).to(IKK_BikeTravelDisutilityFactory.class);  
			this.addRoutingModuleBinding(EBConstants.MODE_FF ).to(FFBikeSharingRoutingModule.class);

			this.addTravelDisutilityFactoryBinding(EBConstants.BS_BIKE).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_BIKE).to(TUG_BSTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_BIKE).toProvider(new NetworkRouting(EBConstants.BS_BIKE));
						
			this.addTravelDisutilityFactoryBinding(EBConstants.BS_E_BIKE).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_E_BIKE).to(TUG_EBSTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_E_BIKE).toProvider(new NetworkRouting(EBConstants.BS_BIKE));

			this.addTravelDisutilityFactoryBinding(EBConstants.BS_WALK).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_WALK).to(TUG_WalkTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_WALK).toProvider(new NetworkRouting(TransportMode.walk));
			
			this.bindScoringFunctionFactory().to(TUG_LegScoringFunctionBikeAndWalkFactory.class);
			
			this.addTravelDisutilityFactoryBinding(TransportMode.bike).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(TransportMode.bike).to(TUG_BikeTravelTime.class); 
			this.addRoutingModuleBinding(TransportMode.bike).toProvider(new NetworkRouting(TransportMode.bike));

			this.addTravelDisutilityFactoryBinding(TransportMode.walk).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(TransportMode.walk).to(TUG_WalkTravelTime.class); 
			this.addRoutingModuleBinding(TransportMode.walk).to(TUG_WalkRoutingModule.class);
			
			this.addTravelDisutilityFactoryBinding(TransportMode.walk+"ing").to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(TransportMode.walk+"ing").to(TUG_WalkTravelTime.class); 
			this.addRoutingModuleBinding(TransportMode.walk+"ing").toProvider(new NetworkRouting(TransportMode.walk));
						
			this.addTravelDisutilityFactoryBinding(EBConstants.BS_WALK_FF).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_WALK_FF).to(TUG_WalkTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_WALK_FF).toProvider(new NetworkRouting(TransportMode.walk));
			
			this.addTravelDisutilityFactoryBinding(EBConstants.BS_BIKE_FF).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_BIKE_FF).to(TUG_WalkTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_BIKE_FF).toProvider(new NetworkRouting(TransportMode.bike));

			this.bindMobsim().toProvider(EBikeSharingQsimFactory.class);
		 } 
}

