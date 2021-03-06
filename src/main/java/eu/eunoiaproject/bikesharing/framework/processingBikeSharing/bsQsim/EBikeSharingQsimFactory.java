package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim;


import java.util.Collection;
import java.util.Map;

import eu.eunoiaproject.bikesharing.examples.example03configurablesimulation.BikeSharingConfigGroup;
import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_BSTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_BikeTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_EBSTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.TUG_WalkTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BikeTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_EBSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_WalkTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.mobsim.qsim.ActivityEngine;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.TeleportationEngine;
import org.matsim.core.mobsim.qsim.agents.PopulationAgentSource;
import org.matsim.core.mobsim.qsim.interfaces.DepartureHandler;
import org.matsim.core.mobsim.qsim.pt.TransitQSimEngine;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetsimEngine;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetsimEnginePlugin;
import org.matsim.core.router.NetworkRouting;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;


import com.google.inject.Inject;
import com.google.inject.Provider;
//import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacilities;

/**
 * Builds the most simple bike-sharing-aware QSim possible.
 * It just adds a BikeSharingEngine, but no relocation of bikes is done:
 * bike relocate only by being moved by bike sharing users.
 * Also, normal "dumb" agents are used: agents just wait at a bike sharing station
 * until a bike (resp. a free slot) is available.
 *
 * @author thibautd
 */
public class EBikeSharingQsimFactory implements Provider<Mobsim>{


	@Inject private  Scenario sc;
	@Inject private  EventsManager eventsManager;
	@Inject public LeastCostPathCalculatorFactory pathCalculatorFactory ;
	@Inject private Map<String,TravelDisutilityFactory> travelDisutilityFactories ;
	@Inject public Map<String,TravelTime> travelTimes ;
//	public LeastCostPathCalculator lcp;
	
	

	@Override
	public Mobsim get() {
		
		QSimConfigGroup conf = sc.getConfig().qsim();
		if (conf == null) {
			throw new NullPointerException("There is no configuration set for the QSim. Please add the module 'qsim' to your config file.");
		}
		QSim qSim = new QSim(sc, eventsManager);
				
		ActivityEngine activityEngine = new ActivityEngine(eventsManager, qSim.getAgentCounter());
		qSim.addMobsimEngine(activityEngine);
		qSim.addActivityHandler(activityEngine);

		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( sc.getConfig(), BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class ) ;
		switch( bikeSharingConfig.getRunType() ) {
			case standard:
				TransitQSimEngine transitEngine = new TransitQSimEngine(qSim);
				qSim.addMobsimEngine(transitEngine);
				qSim.addDepartureHandler(transitEngine);
				qSim.addAgentSource(transitEngine);
				break;
			case debug:
				break;
			default:
				throw new RuntimeException( "not implemented" ) ;
		}
		TeleportationEngine tpe = new TeleportationEngine(sc, eventsManager);
		qSim.addMobsimEngine(tpe);
		//qSim.addDepartureHandler(tpe); 
		//adding this also as departure handler ends up having it added twice, so don't do that. kai, apr'19
		
		
		QNetsimEngine qnet = new QNetsimEngine(qSim);
		qSim.addDepartureHandler(qnet.getDepartureHandler());
		qSim.addMobsimEngine(qnet);
		DepartureHandler dep = qnet.getDepartureHandler();
		//VehicularDepartureHandler veh = new VehicularDepartureHandler();
		
		BicycleConfigGroup confBC = (BicycleConfigGroup) qSim.getScenario().getConfig().getModule("bicycleAttributes");

		BikeSharingContext.Builder builder = new BikeSharingContext.Builder() ;
		{
			TravelTime travelTime = new TUG_BikeTravelTime(confBC);
			TravelDisutility travelDisutility = new TUG_BikeTravelDisutility(confBC, qSim.getScenario().getConfig());
			LeastCostPathCalculator standardBikePathCalculator = pathCalculatorFactory.createPathCalculator( sc.getNetwork(), travelDisutility, travelTime );
			builder.setStandardBikePathCalculator( standardBikePathCalculator ) ;
		}
		{
			TravelTime travelTime = new TUG_BSTravelTime(confBC);
			TravelDisutility travelDisutility = new TUG_BSTravelDisutility(confBC);
			LeastCostPathCalculator sharedBikePathCalculator = pathCalculatorFactory.createPathCalculator( sc.getNetwork(), travelDisutility, travelTime );
			builder.setSharedBikePathCalculator( sharedBikePathCalculator ) ;
		}
		{
			TravelTime btt = new TUG_EBSTravelTime(confBC);
			TravelDisutility btd = 	new TUG_EBSTravelDisutility(confBC);
			LeastCostPathCalculator calc = pathCalculatorFactory.createPathCalculator( sc.getNetwork(), btd, btt ) ;
			builder.setSharedEBikePathCalculator( calc ) ;
		}
		{
			TravelTime btt = new TUG_WalkTravelTime(confBC);
			TravelDisutility btd = new TUG_WalkTravelDisutility(confBC);
			LeastCostPathCalculator routeAlgo = pathCalculatorFactory.createPathCalculator(sc.getNetwork(), btd, btt);;
			builder.setWalkPathCalculator( routeAlgo ) ;
		}

		builder.setQSim( qSim ) ;
		builder.setObjectAttributesSingleton( IKK_ObjectAttributesSingleton.getInstance(confBC, false) ) ;
		BikeSharingContext context = builder.build();


		
		BikesharingAgentFactory agentFactory = new BikesharingAgentFactory( context );
		PopulationAgentSource agentSource = new PopulationAgentSource(sc.getPopulation(), agentFactory, qSim);
		qSim.addAgentSource(agentSource);
		
		
		BsMobsimEngine bs2Engine = new BsMobsimEngine(
				sc, eventsManager, pathCalculatorFactory, travelDisutilityFactories, travelTimes, qSim);
		qSim.addMobsimEngine(bs2Engine );
		
		return qSim;
	}
		
}
