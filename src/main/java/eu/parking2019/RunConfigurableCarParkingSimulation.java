/* *********************************************************************** *
 * project: org.matsim.*
 * RunZurichBikeSharingSimulation.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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
package eu.parking2019;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.router.NetworkRouting;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.TripRouterModule;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutilityFactory;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;
import org.matsim.core.utils.io.UncheckedIOException;

import com.google.inject.Provider;
import com.google.inject.name.Names;

import java.io.File;


/**
 * @author thibautd, overworked by hebenstreit
 */
public class RunConfigurableCarParkingSimulation {

	
	private static final Logger log =
		Logger.getLogger(RunConfigurableCarParkingSimulation.class);

	/***************************************************************************/
	public static void main(final String... args) 
	/***************************************************************************/
	{
		final String configFile = args[ 0 ];
		//final String configFile = "E:/MATCHSIM_ECLIPSE/matsim-master/playgrounds/thibautd/examples\BikeRouting\haus\config.xml";
		//E:\MATCHSIM_ECLIPSE\matsim-master\playgrounds\thibautd\test\output\eu\eunoiaproject\bikesharing\framework\examples\TestRegressionConfigurableExample\testRunDoesNotFailMultimodal
		
		OutputDirectoryLogging.catchLogEntries();
		//Logger.getLogger( SoftCache.class ).setLevel( Level.TRACE );

		final Config config = ScenarioUtilsParking.loadConfig( configFile );

		failIfExists( config.controler().getOutputDirectory() );

		final Scenario sc = ScenarioUtilsParking.loadScenario( config );

		final Controler controler = new Controler( sc );
		
		controler.addOverridingModule(new TripRouterModule());
		installParking( controler );
      
		//////////////////////////////////////////////////////////

		//installBikes(controler);
		controler.run();


	}


	/***************************************************************************/
	private static void failIfExists(final String outdir) 
	/***************************************************************************/
	{
		final File file = new File( outdir +"/" );
		if ( file.exists() && file.list().length > 0 ) {
new UncheckedIOException( "Directory "+outdir+" exists and is not empty!" );
		}
	}
	
	public static void installParking(Controler controler) {
		
		controler.addOverridingModule(new AbstractModule() { 
			@Override
			 public void install() 
			 {
				           
				this.addTravelDisutilityFactoryBinding(TransportMode.bike).to(OnlyTimeDependentTravelDisutilityFactory.class); 
				this.addTravelTimeBinding(TransportMode.bike).to(FreeSpeedTravelTime.class);
				this.addRoutingModuleBinding(TransportMode.bike).toProvider(new NetworkRouting(TransportMode.bike));
		
				addTravelDisutilityFactoryBinding(TransportMode.walk).to(OnlyTimeDependentTravelDisutilityFactory.class); 
				addTravelTimeBinding(TransportMode.walk).to(FreeSpeedTravelTime.class); 
				this.addRoutingModuleBinding(TransportMode.walk).toProvider(new NetworkRouting(TransportMode.walk));
				
				bindMobsim().toProvider(ParkingQsimFactory.class);
			 }  
		});
		
	}
}

