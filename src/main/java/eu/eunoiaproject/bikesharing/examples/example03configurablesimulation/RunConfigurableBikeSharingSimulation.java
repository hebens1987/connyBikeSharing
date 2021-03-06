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
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeAndEBikeSharingScenarioUtils;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingBikes;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.config.groups.ControlerConfigGroup.RoutingAlgorithmType;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.router.util.DijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.utils.io.UncheckedIOException;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.utils.objectattributes.ObjectAttributesXmlReader;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;


/**
 * @author thibautd, overworked by hebenstreit
 */
public class RunConfigurableBikeSharingSimulation {

	
	private static final Logger log =
		Logger.getLogger(RunConfigurableBikeSharingSimulation.class);

	enum InputCase{fromArgs, inputDiss, raster, connyInputDiss }

	/***************************************************************************/
	public static void main(final String... args) 
	/***************************************************************************/
	{
		final Config config = prepareConfig( args, InputCase.connyInputDiss );
		final Scenario sc = prepareScenario( config );
		final Controler controler = prepareControler( sc );
		//RoutingAlgorithmType rat = config.controler().getRoutingAlgorithmType();
        //if (config.controler().getRoutingAlgorithmType().equals(ControlerConfigGroup.RoutingAlgorithmType.Dijkstra)) {
        //    bind(LeastCostPathCalculatorFactory.class).to(DijkstraFactory.class);
		controler.run();
	}

	static Controler prepareControler(Scenario sc ){
		final Controler controler = new Controler( sc );
		controler.addOverridingModule(new ImplementationModule(sc.getConfig(), sc) );
		return controler;
	}

	static Scenario prepareScenario( Config config ){
		final Scenario sc = BikeAndEBikeSharingScenarioUtils.loadScenario( config );
		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( sc.getConfig(), BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class ) ;
		switch( bikeSharingConfig.getRunType() ) {
			case standard:
				loadTransitInScenario( sc );
				BikeSharingBikes bSharingVehicles = (BikeSharingBikes) 
						sc.getScenarioElement( BikeSharingBikes.ELEMENT_NAME);
				bSharingVehicles.generatePTRouterForBS(sc);
				break;
			case debug:
				sc.getPopulation().getPersons().entrySet().removeIf( entry -> MatsimRandom.getRandom().nextDouble() < 0.9 ) ;
				break;
			default:
				throw new RuntimeException( "not implemented" ) ;
		}

		return sc;
	}

	static Config prepareConfig( String [] args, InputCase user ){
		String configFile ;
		switch( user ) {
			case fromArgs:
				configFile = args[0] ;
				break;
			case inputDiss:
				configFile = "./scenarios/Input_Diss/config_bs.xml" ;
				break;
			case connyInputDiss:
				configFile = "D:/BikeRouting/Wien_Gesamt/config_bs.xml" ;
				break;
			case raster:
				configFile = "./scenarios/RasterBsp/configRaster.xml" ;
				break ;
			default:
				throw new RuntimeException("not implemented") ;
		}
		
		
		OutputDirectoryLogging.catchLogEntries();

		final Config config = BikeAndEBikeSharingScenarioUtils.loadConfig( configFile );
		config.addModule( new BicycleConfigGroup() );

		config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );

		config.global().setNumberOfThreads( 8 );

		config.qsim().setMainModes( new HashSet<>( Arrays.asList( TransportMode.car, TransportMode.bike, "bs", "e_bs") ) ) ;
		
		config.travelTimeCalculator().setSeparateModes( true ); 
		failIfExists( config.controler().getOutputDirectory() );
		
		config.controler().setRoutingAlgorithmType( ControlerConfigGroup.RoutingAlgorithmType.FastAStarLandmarks );

		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class ) ;
		config.plansCalcRoute().setInsertingAccessEgressWalk(true);
		switch( bikeSharingConfig.getRunType() ) {
			case standard:
				config.transitRouter().setMaxBeelineWalkConnectionDistance( 200 );
				break;
			case debug:
				config.controler().setLastIteration( 1 );
				config.transit().setUseTransit( false );
				break;
			default:
				throw new RuntimeException( "not implemented" ) ;
		}

		
		return config;
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

