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
package eu.eunoiaproject.bikesharing.examples.example03configurablesimulation;

import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BikeScenarioUtils;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.router.TripRouterModule;
import org.matsim.core.utils.io.UncheckedIOException;
import org.matsim.pt.router.TransitRouterModule;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.utils.objectattributes.ObjectAttributesXmlReader;
import java.io.File;


/**
 * @author thibautd, overworked by hebenstreit
 */
public class RunConfigurableBikesONLYSimulation {

	
	private static final Logger log =
		Logger.getLogger(RunConfigurableBikesONLYSimulation.class);

	/***************************************************************************/
	public static void main(final String... args) 
	/***************************************************************************/
	{
		final String configFile = args[ 0 ];
		//final String configFile = "E:/MATCHSIM_ECLIPSE/matsim-master/playgrounds/thibautd/examples\BikeRouting\haus\config.xml";
		//E:\MATCHSIM_ECLIPSE\matsim-master\playgrounds\thibautd\test\output\eu\eunoiaproject\bikesharing\framework\examples\TestRegressionConfigurableExample\testRunDoesNotFailMultimodal
		
		OutputDirectoryLogging.catchLogEntries();
		//Logger.getLogger( SoftCache.class ).setLevel( Level.TRACE ); 

		final Config config = BikeScenarioUtils.loadConfig( configFile );
		config.addModule( new BicycleConfigGroup());

		failIfExists( config.controler().getOutputDirectory() );

		final Scenario sc = BikeScenarioUtils.loadScenario( config );
		loadTransitInScenario( sc );
		final Controler controler = new Controler( sc );
		
		controler.addOverridingModule(new TripRouterModule());
		controler.addOverridingModule(new TransitRouterModule());//Hebenstreit
		
      
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

	/***************************************************************************/
	private static final void loadTransitInScenario( final Scenario scenario ) 
	/***************************************************************************/
	{
		final Config config = scenario.getConfig();
		// if actual simulation of transit is disabled, the transit schedule
		// is not loaded automatically: we need to do it by hand
		if ( !config.transit().isUseTransit() ) {
			if ( config.transit().getTransitScheduleFile() == null ) {
				log.info( "no schedule file defined in config: not loading any schedule information" );
				return;
			}

			log.info( "read schedule from "+config.transit().getTransitScheduleFile() );
			new TransitScheduleReader( scenario ).readFile( config.transit().getTransitScheduleFile() );

			// this is not necessary in the vast majority of applications.
			if ( config.transit().getTransitLinesAttributesFile() != null ) {
				log.info("loading transit lines attributes from " + config.transit().getTransitLinesAttributesFile());
				new ObjectAttributesXmlReader( scenario.getTransitSchedule().getTransitLinesAttributes() ).parse(
						config.transit().getTransitLinesAttributesFile() );
			}
			if ( config.transit().getTransitStopsAttributesFile() != null ) {
				log.info("loading transit stop facilities attributes from " + config.transit().getTransitStopsAttributesFile() );
				new ObjectAttributesXmlReader( scenario.getTransitSchedule().getTransitStopsAttributes() ).parse(
						config.transit().getTransitStopsAttributesFile() );
			}
		}
		else {
			log.info( "Transit will be simulated." );
		}
	}

}

