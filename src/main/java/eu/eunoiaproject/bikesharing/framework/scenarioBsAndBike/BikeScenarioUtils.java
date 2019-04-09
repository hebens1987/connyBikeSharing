package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;
//package eu.eunoiaproject.bikesharing.framework.scenario.bicycles;
//
//import eu.eunoiaproject.bikesharing.framework.EBConstants;
//import eu.eunoiaproject.bikesharing.framework.routing.bikeSharing.BikeSharingRouteFactory;
//
//import org.matsim.api.core.v01.Scenario;
//import org.matsim.api.core.v01.TransportMode;
//import org.matsim.core.config.Config;
//import org.matsim.core.config.ConfigGroup;
//import org.matsim.core.config.ConfigUtils;
//import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
//import org.matsim.core.controler.OutputDirectoryLogging;
//import org.matsim.core.population.PopulationFactoryImpl;
//import org.matsim.core.population.routes.LinkNetworkRouteImpl;
//import org.matsim.core.scenario.ScenarioUtils;
//
//import java.util.Arrays;
//
//
///**
// * Provides helper methods to load a bike sharing scenario.
// * Using this class is by no means necessary, but simplifies
// * the writing of scripts.
// *
// * @author thibautd
// */
//public class BikeScenarioUtils {
//	public static final String LINK_SLOPES_ELEMENT_NAME = "linkSlopes";
//
//	public static Config loadConfig( final String fileName , final ConfigGroup... additionalModules ) {
//		final ConfigGroup[] modules = Arrays.copyOf( additionalModules , additionalModules.length);
//		final Config config = ConfigUtils.loadConfig(
//				fileName,
//				modules );
//
//		config.planCalcScore().getOrCreateModeParams(TransportMode.bike);
//		config.planCalcScore().getOrCreateModeParams(TransportMode.pt);
//		config.planCalcScore().getOrCreateModeParams(TransportMode.transit_walk);
//		config.planCalcScore().getOrCreateModeParams(TransportMode.car);
//		config.planCalcScore().getOrCreateModeParams(EBConstants.BS_BIKE);
//		config.planCalcScore().getOrCreateModeParams(EBConstants.BS_E_BIKE);
//		config.planCalcScore().getOrCreateModeParams(EBConstants.BS_WALK);
//		config.planCalcScore().getOrCreateModeParams(TransportMode.walk);
//
//
//		if ( config.planCalcScore().getActivityParams( EBConstants.INTERACTION_TYPE_BS + "_r" ) == null )
//		{
//			// not so nice...
//			final ActivityParams params = new ActivityParams( TransportMode.pt );
//			final ActivityParams params_t = new ActivityParams(EBConstants.INTERACTION_TYPE_BS + "_t" );
//			final ActivityParams params_r = new ActivityParams(EBConstants.INTERACTION_TYPE_BS + "_r" );
//
//			params_r.setClosingTime(params.getClosingTime());
//			params_r.setEarliestEndTime(params.getEarliestEndTime());
//			params_r.setLatestStartTime(params.getLatestStartTime());
//			params_r.setMinimalDuration(params.getMinimalDuration());
//			params_r.setOpeningTime(params.getOpeningTime());
//			params_r.setPriority(params.getPriority());
//			params_r.setScoringThisActivityAtAll(false);
//			params_r.setTypicalDuration(params.getTypicalDuration());
//			params_r.setTypicalDurationScoreComputation(params.getTypicalDurationScoreComputation());
//
//			config.planCalcScore().addActivityParams( params_t );
//			config.planCalcScore().addActivityParams( params_r );
//
//
//
//		}
//
//		if ( config.planCalcScore().getActivityParams( EBConstants.INTERACTION_TYPE_FF ) == null )
//		{
//			// not so nice...
//			final ActivityParams params = new ActivityParams( EBConstants.INTERACTION_TYPE_FF );
//			params.setTypicalDuration( 60 );
//			config.planCalcScore().addActivityParams( params );
//		}
//
//		if ( config.planCalcScore().getActivityParams( "wait" ) == null )
//		{
//			// not so nice...
//			final ActivityParams params = new ActivityParams( "wait" );
//			params.setTypicalDuration( 60 );
//			params.setOpeningTime( 0 );
//			params.setClosingTime( 0 );
//			config.planCalcScore().addActivityParams( params );
//		}
//
//		return config;
//	}
//
//	public static Scenario loadScenario( final Config config ) {
//		// to make sure log entries are writen in log file
//		OutputDirectoryLogging.catchLogEntries();
//		final Scenario sc = ScenarioUtils.createScenario( config );
//		configurePopulationFactory( sc );
//		ScenarioUtils.loadScenario( sc );
//		return sc;
//	}
//
//
//	public static Scenario loadScenario( final String configFile , final ConfigGroup... modules ) {
//		return loadScenario( loadConfig( configFile , modules) );
//	}
//
//	public static void configurePopulationFactory( final Scenario scenario ) {
//
//		((PopulationFactoryImpl) scenario.getPopulation().getFactory()).setRouteFactory( LinkNetworkRouteImpl.class , new BikeSharingRouteFactory() );
//		//((PopulationFactoryImpl) scenario.getPopulation().getFactory()).setRouteFactory( LinkNetworkRouteImpl.class , new LinkNetworkRouteFactory() );
//	}
//
//
//}
//
