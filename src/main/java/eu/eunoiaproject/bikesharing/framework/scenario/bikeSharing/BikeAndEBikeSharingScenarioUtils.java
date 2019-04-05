package eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.routing.bikeSharing.BikeSharingRouteFactory;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.population.PopulationFactoryImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.router.NetworkRoutingInclAccessEgressModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.utils.objectattributes.ObjectAttributesXmlReader;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

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


	public static boolean intersects( //origin thibaudth (CollectionUtils in socnetsim)(
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

