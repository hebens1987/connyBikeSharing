package eu.parking2019;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.population.PopulationFactoryImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.scenario.ScenarioUtils;
import java.util.Arrays;


/**
 * Provides helper methods to load a bike sharing scenario.
 * Using this class is by no means necessary, but simplifies
 * the writing of scripts. 
 *
 * @author thibautd
 */
public class ScenarioUtilsParking {
	public static final String LINK_SLOPES_ELEMENT_NAME = "linkSlopes";

	public static Config loadConfig( final String fileName , final ConfigGroup... additionalModules ) {
		final ConfigGroup[] modules = Arrays.copyOf( additionalModules , additionalModules.length);
		final Config config = ConfigUtils.loadConfig(
				fileName,
				modules );

		return config;
	}

	public static Scenario loadScenario( final Config config ) {
		// to make sure log entries are writen in log file
		OutputDirectoryLogging.catchLogEntries();
		final Scenario sc = ScenarioUtils.createScenario( config );
		//configurePopulationFactory( sc );
		ScenarioUtils.loadScenario( sc );
		return sc;
	}

	public static Scenario loadScenario( final String configFile , final ConfigGroup... modules ) {
		return loadScenario( loadConfig( configFile , modules) );
	}

	//public static void configurePopulationFactory( final Scenario scenario ) {

		//((PopulationFactoryImpl) scenario.getPopulation().getFactory()).setRouteFactory( LinkNetworkRouteImpl.class , new BikeSharingRouteFactory() );
		//((PopulationFactoryImpl) scenario.getPopulation().getFactory()).setRouteFactory( LinkNetworkRouteImpl.class , new LinkNetworkRouteFactory() );
	//}


}

