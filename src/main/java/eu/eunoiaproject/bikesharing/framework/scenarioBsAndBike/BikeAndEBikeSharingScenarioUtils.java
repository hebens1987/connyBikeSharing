/* *********************************************************************** *
 * project: org.matsim.*
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
package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;

import eu.eunoiaproject.bikesharing.framework.EBConstants;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.utils.objectattributes.ObjectAttributesXmlReader;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides helper methods to load a bike sharing scenario.
 * Using this class is by no means necessary, but simplifies
 * the writing of scripts. 
 *
 * @author thibautd
 */
public class BikeAndEBikeSharingScenarioUtils {
//	public static final String LINK_SLOPES_ELEMENT_NAME = "linkSlopes";
//	static BikeSharingFacilities fac;

	public static Config loadConfig( final String fileName , final ConfigGroup... additionalModules ) {
		final ConfigGroup[] modules = Arrays.copyOf( additionalModules , additionalModules.length + 1 );
		modules[ modules.length - 1 ] = new EBikeSharingConfigGroup();
		final Config config = ConfigUtils.loadConfig(
				fileName,
				modules );

		{
			ActivityParams params = new ActivityParams( "bsWalk interaction" ) ;
			params.setScoringThisActivityAtAll( false );
			config.planCalcScore().addActivityParams( params );
		}
		
		
		if ( config.planCalcScore().getActivityParams( EBConstants.INTERACTION_TYPE_BS + "_r" ) == null ) 
		{
			// not so nice...
//			final ActivityParams params = new ActivityParams( TransportMode.pt );
			// never used

			final ActivityParams params_t = new ActivityParams(EBConstants.INTERACTION_TYPE_BS + "_t" );
			final ActivityParams params_r = new ActivityParams(EBConstants.INTERACTION_TYPE_BS + "_r" );
			
//			params_t.setClosingTime(params.getClosingTime());
//			params_t.setEarliestEndTime(params.getEarliestEndTime());
//			params_t.setLatestStartTime(params.getLatestStartTime());
//			params_t.setMinimalDuration(params.getMinimalDuration());
//			params_t.setOpeningTime(params.getOpeningTime());
//			params_t.setPriority(params.getPriority());
			params_t.setScoringThisActivityAtAll(false);
//			params_t.setTypicalDuration(params.getTypicalDuration());
//			params_t.setTypicalDurationScoreComputation(params.getTypicalDurationScoreComputation());
			
//			params_r.setClosingTime(params.getClosingTime());
//			params_r.setEarliestEndTime(params.getEarliestEndTime());
//			params_r.setLatestStartTime(params.getLatestStartTime());
//			params_r.setMinimalDuration(params.getMinimalDuration());
//			params_r.setOpeningTime(params.getOpeningTime());
//			params_r.setPriority(params.getPriority());
			params_r.setScoringThisActivityAtAll(false);
//			params_r.setTypicalDuration(params.getTypicalDuration());
//			params_r.setTypicalDurationScoreComputation(params.getTypicalDurationScoreComputation());
			
			config.planCalcScore().addActivityParams( params_t );
			config.planCalcScore().addActivityParams( params_r );
			
			final ActivityParams bsWalkInteract = new ActivityParams("bsWalk interaction");
//			bsWalkInteract.setClosingTime(params.getClosingTime());
//			bsWalkInteract.setEarliestEndTime(params.getEarliestEndTime());
//			bsWalkInteract.setLatestStartTime(params.getLatestStartTime());
//			bsWalkInteract.setMinimalDuration(params.getMinimalDuration());
//			bsWalkInteract.setOpeningTime(params.getOpeningTime());
//			bsWalkInteract.setPriority(params.getPriority());
			bsWalkInteract.setScoringThisActivityAtAll(false);
//			bsWalkInteract.setTypicalDuration(params.getTypicalDuration());
//			bsWalkInteract.setTypicalDurationScoreComputation(params.getTypicalDurationScoreComputation());
		}
		
		
		if ( config.planCalcScore().getActivityParams( EBConstants.INTERACTION_TYPE_FF ) == null ) 
		{
			// not so nice...
			final ActivityParams params = new ActivityParams( EBConstants.INTERACTION_TYPE_FF );
			params.setTypicalDuration( 60 );
			params.setOpeningTime( 0 );
			params.setClosingTime( 0 );
			config.planCalcScore().addActivityParams( params );
		}
		
		if ( config.planCalcScore().getActivityParams( "wait" ) == null ) 
		{
			// not so nice...
			final ActivityParams params = new ActivityParams( "wait" );
			params.setTypicalDuration( 60 );
			params.setOpeningTime( 0 );
			params.setClosingTime( 0 );
			config.planCalcScore().addActivityParams( params );
		}

		return config;
	}

	public static Scenario loadScenario( final Config config ) {
		// to make sure log entries are writen in log file
		OutputDirectoryLogging.catchLogEntries();
		final Scenario sc = ScenarioUtils.createScenario( config );
//		configurePopulationFactory( sc );
		ScenarioUtils.loadScenario( sc );
		loadBikeSharingPart( sc );
		return sc;
	}
	
	

	private static void loadBikeSharingPart( final Scenario sc ) {
		final Config config = sc.getConfig();
		
		final EBikeSharingConfigGroup confGroup = (EBikeSharingConfigGroup)
				config.getModule(EBikeSharingConfigGroup.GROUP_NAME);
		new BikeSharingFacilitiesReader (sc).parse(confGroup.getFacilitiesFile());
		
		
		final BikeSharingFacilities bsFacilities = (BikeSharingFacilities)
			sc.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME );
		if ( confGroup.getFacilitiesAttributesFile() != null ) {
			new ObjectAttributesXmlReader( bsFacilities.getFacilitiesAttributes() ).parse(
					confGroup.getFacilitiesAttributesFile() );
		}
		

		new BikeSharingBikesReader (sc).parse(confGroup.getBikeSharingBikes());
		
		final BikeSharingBikes bsBikes = (BikeSharingBikes) sc.getScenarioElement( BikeSharingBikes.ELEMENT_NAME );
		

		final ActivityFacilities actFacilities = sc.getActivityFacilities();
		if ( intersects(
					actFacilities.getFacilities().keySet(),
					bsFacilities.getFacilities().keySet())) 
		{
			throw new RuntimeException( "ids of bike sharing stations and activity facilities overlap. This will cause problems!"+
					" Make sure Ids do not overlap, for instance by appending \"bs-\" at the start of all bike sharing facilities." );
		}	
		
		if ( intersects(
				bsBikes.getFacilities().keySet(),
				bsFacilities.getFacilities().keySet())) 
		{
			throw new RuntimeException( "ids of bike sharing bikes and bike sharing facilities overlap. This will cause problems!"+
				" Make sure Ids do not overlap, for instance by appending \"bs-\" at the start of all bike sharing facilities." );
		}	
		
		if ( intersects(
				bsBikes.getFacilities().keySet(),
				actFacilities.getFacilities().keySet())) 
		{
			throw new RuntimeException( "ids of bike sharing bikes and activity facilities overlap. This will cause problems!"+
				" Make sure Ids do not overlap, for instance by appending \"bs-\" at the start of all bike sharing facilities." );
		}	

	}

	private static boolean intersects( //origin thibaudth (CollectionUtils in socnetsim)(
						     final Collection<?> c1,
						     final Collection<?> c2 ) {
		for ( Object o : c1 ) {
			if ( c2.contains( o ) ) return true;
		}
		return false;
	}
	
//	public static Scenario loadScenario( final String configFile , final ConfigGroup... modules ) {
//		return loadScenario( loadConfig( configFile , modules) );
//	}

//	public static void configurePopulationFactory( final Scenario scenario ) {
//
//		((PopulationFactoryImpl) scenario.getPopulation().getFactory()).setRouteFactory( LinkNetworkRouteImpl.class , new BikeSharingRouteFactory() );
//		//((PopulationFactoryImpl) scenario.getPopulation().getFactory()).setRouteFactory( LinkNetworkRouteImpl.class , new LinkNetworkRouteFactory() );
//	}
	
	


}

