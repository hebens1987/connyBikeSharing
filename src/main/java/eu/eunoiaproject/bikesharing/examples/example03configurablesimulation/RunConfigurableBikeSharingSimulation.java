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

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.*;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.ControlerUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.utils.objectattributes.ObjectAttributesXmlReader;

import java.util.Arrays;
import java.util.Collections;
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
		final Config config = prepareConfig( args, InputCase.raster );

		final Scenario sc = prepareScenario( config );

		final Controler controler = prepareControler( sc );

		controler.run();
	}

	static Controler prepareControler(Scenario sc ){
		final Controler controler = new Controler( sc );
		controler.addOverridingModule(new ImplementationModule(sc.getConfig()) );
		return controler;
	}

	static Scenario prepareScenario( Config config ){

		ControlerUtils.checkConfigConsistencyAndWriteToLog( config, "checking config before preparing scenario ..." );

		// to make sure log entries are writen in log file
		OutputDirectoryLogging.catchLogEntries();
		final Scenario sc1 = ScenarioUtils.createScenario( config );
		//		configurePopulationFactory( sc );
		ScenarioUtils.loadScenario( sc1 );
		final Config config1 = sc1.getConfig();

		final EBikeSharingConfigGroup confGroup = (EBikeSharingConfigGroup)
				config1.getModule(EBikeSharingConfigGroup.GROUP_NAME );
		new BikeSharingFacilitiesReader( sc1 ).parse(confGroup.getFacilitiesFile() );


		final BikeSharingFacilities bsFacilities = (BikeSharingFacilities)
			sc1.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME );
		if ( confGroup.getFacilitiesAttributesFile() != null ) {
			new ObjectAttributesXmlReader( bsFacilities.getFacilitiesAttributes() ).parse(
					confGroup.getFacilitiesAttributesFile() );
		}


		new BikeSharingBikesReader( sc1 ).parse(confGroup.getBikeSharingBikes() );

		final BikeSharingBikes bsBikes = (BikeSharingBikes) sc1.getScenarioElement( BikeSharingBikes.ELEMENT_NAME );


		final ActivityFacilities actFacilities = sc1.getActivityFacilities();
		if ( BikeAndEBikeSharingScenarioUtils.intersects(
					actFacilities.getFacilities().keySet(),
					bsFacilities.getFacilities().keySet() ))
		{
			throw new RuntimeException( "ids of bike sharing stations and activity facilities overlap. This will cause problems!"+
					" Make sure Ids do not overlap, for instance by appending \"bs-\" at the start of all bike sharing facilities." );
		}

		if ( BikeAndEBikeSharingScenarioUtils.intersects(
				bsBikes.getFacilities().keySet(),
				bsFacilities.getFacilities().keySet() ))
		{
			throw new RuntimeException( "ids of bike sharing bikes and bike sharing facilities overlap. This will cause problems!"+
				" Make sure Ids do not overlap, for instance by appending \"bs-\" at the start of all bike sharing facilities." );
		}

		if ( BikeAndEBikeSharingScenarioUtils.intersects(
				bsBikes.getFacilities().keySet(),
				actFacilities.getFacilities().keySet() ))
		{
			throw new RuntimeException( "ids of bike sharing bikes and activity facilities overlap. This will cause problems!"+
				" Make sure Ids do not overlap, for instance by appending \"bs-\" at the start of all bike sharing facilities." );
		}

		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( sc1.getConfig(), BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class ) ;
		switch( bikeSharingConfig.getRunType() ) {
			case standard:
				break;
			case debug:
				sc1.getPopulation().getPersons().entrySet().removeIf( entry -> MatsimRandom.getRandom().nextDouble() < 0.9 ) ;
				break;
			default:
				throw new RuntimeException( "not implemented" ) ;
		}

		// ---
		return sc1;
	}

	static Config prepareConfig( String [] args, InputCase user ){
		OutputDirectoryLogging.catchLogEntries();

		String configFile ;
		switch( user ) {
			case fromArgs:
				configFile = args[0] ;
				//final String configFile = "E:/MATCHSIM_ECLIPSE/matsim-master/playgrounds/thibautd/examples\BikeRouting\haus\config.xml";
				//E:\MATCHSIM_ECLIPSE\matsim-master\playgrounds\thibautd\test\output\eu\eunoiaproject\bikesharing\framework\examples\TestRegressionConfigurableExample\testRunDoesNotFailMultimodal
				break;
			case inputDiss:
				configFile = "./scenarios/Input_Diss/config_bs.xml" ;
				break;
			case connyInputDiss:
				configFile = "C:/Users/hebens/Documents/Input_Diss/config_bs.xml" ;
				break;
			case raster:
				configFile = "./scenarios/RasterBsp/configRaster.xml" ;
				break ;
			default:
				throw new RuntimeException("not implemented") ;
		}
		
		final Config config = ConfigUtils.loadConfig( configFile ) ;

		{
			PlanCalcScoreConfigGroup.ActivityParams params1 = new PlanCalcScoreConfigGroup.ActivityParams( "bs_walk interaction" ) ;
			params1.setScoringThisActivityAtAll( false );
			config.planCalcScore().addActivityParams( params1 );
		}


		if ( config.planCalcScore().getActivityParams( EBConstants.INTERACTION_TYPE_BS + "_r" ) == null )
		{

			final PlanCalcScoreConfigGroup.ActivityParams params_t = new PlanCalcScoreConfigGroup.ActivityParams(EBConstants.INTERACTION_TYPE_BS + "_t" );
			params_t.setScoringThisActivityAtAll(false);
			config.planCalcScore().addActivityParams( params_t );

			final PlanCalcScoreConfigGroup.ActivityParams params_r = new PlanCalcScoreConfigGroup.ActivityParams(EBConstants.INTERACTION_TYPE_BS + "_r" );
			params_r.setScoringThisActivityAtAll(false);
			config.planCalcScore().addActivityParams( params_r );

			final PlanCalcScoreConfigGroup.ActivityParams bsWalkInteract = new PlanCalcScoreConfigGroup.ActivityParams("s_walk interaction");
			bsWalkInteract.setScoringThisActivityAtAll(false);
			// yyyyyy never set???
		}


		if ( config.planCalcScore().getActivityParams( EBConstants.INTERACTION_TYPE_FF ) == null )
		{
			// not so nice... // why not?  kai
			final PlanCalcScoreConfigGroup.ActivityParams params1 = new PlanCalcScoreConfigGroup.ActivityParams( EBConstants.INTERACTION_TYPE_FF );
			params1.setTypicalDuration( 60 );

			params1.setOpeningTime( 0 );
			params1.setClosingTime( 0 );
			// yyyyyy why opening/closing time 0?  Will mean that the activity is always heavily penalized (since outside opening times).

			config.planCalcScore().addActivityParams( params1 );
		}

		if ( config.planCalcScore().getActivityParams( "wait" ) == null )
		{
			// not so nice... // why not?  kai
			final PlanCalcScoreConfigGroup.ActivityParams params1 = new PlanCalcScoreConfigGroup.ActivityParams( "wait" );
			params1.setTypicalDuration( 60 );
			params1.setOpeningTime( 0 );
			params1.setClosingTime( 0 );
			// yyyyyy why opening/closing time 0?  Will mean that the activity is always heavily penalized (since outside opening times).

			config.planCalcScore().addActivityParams( params1 );
		}

		ConfigUtils.addOrGetModule( config, BicycleConfigGroup.GROUP_NAME, BicycleConfigGroup.class ) ;
		ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class ) ;
		ConfigUtils.addOrGetModule( config, EBikeSharingConfigGroup.GROUP_NAME, EBikeSharingConfigGroup.class ) ;

		config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );

		config.global().setNumberOfThreads( 8 );
		
		//Does not use the implemented routing modules anymore - just uses Network Route
		//Can I use a combination? TODO:
		config.qsim().setMainModes( new HashSet<>( Arrays.asList( TransportMode.car, TransportMode.bike,
			  EBConstants.BS_BIKE, EBConstants.BS_BIKE_FF, EBConstants.BS_E_BIKE, EBConstants.BS_WALK, EBConstants.BS_WALK_FF ) ) ) ;

		{
			PlansCalcRouteConfigGroup.ModeRoutingParams params = new PlansCalcRouteConfigGroup.ModeRoutingParams( TransportMode.walk );
			params.setTeleportedModeSpeed( 3.0 / 3.6 ); // 3.0 km/h --> m/s
			config.plansCalcRoute().addModeRoutingParams( params );
			// yy this will clear all pre-existing teleportation routing as a side effect!!!!! :-(
			// yyyyyy need walk teleported to make the transit router config happy :-(
		}
		{
			PlansCalcRouteConfigGroup.ModeRoutingParams params = new PlansCalcRouteConfigGroup.ModeRoutingParams( TransportMode.pt );
			params.setTeleportedModeFreespeedFactor( 2.0 );
			config.plansCalcRoute().addModeRoutingParams( params );
		}

		config.plansCalcRoute().setNetworkModes( Collections.singleton( TransportMode.car ) ) ;
		// (all other routing is defined explicity later)

		config.travelTimeCalculator().setSeparateModes( true );

		config.controler().setRoutingAlgorithmType( ControlerConfigGroup.RoutingAlgorithmType.FastAStarLandmarks );

		config.plansCalcRoute().setInsertingAccessEgressWalk(true );
		
		return config;
	}

}

