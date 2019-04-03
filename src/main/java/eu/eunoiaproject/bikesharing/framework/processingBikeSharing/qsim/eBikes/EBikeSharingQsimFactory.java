package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes;


import java.util.Map;

import eu.eunoiaproject.bikesharing.examples.example03configurablesimulation.BikeSharingConfigGroup;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.api.experimental.events.EventsManager;
//import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.mobsim.qsim.ActivityEngine;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.TeleportationEngine;
import org.matsim.core.mobsim.qsim.agents.PopulationAgentSource;
import org.matsim.core.mobsim.qsim.pt.TransitQSimEngine;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetsimEngine;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;


import com.google.inject.Inject;
import com.google.inject.Provider;

import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingBikes;
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
		
		
		//QSim qSimOrig = QSimUtils.createDefaultQSim(sc, eventsManager);
		QSim qSim = new QSim(sc, eventsManager);
		
		

		//QSim qSim = new QSim(sc, eventsManager);
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

		TeleportationEngine tp = new TeleportationEngine(sc, eventsManager);
		qSim.addMobsimEngine(tp);
		qSim.addDepartureHandler(tp);
		


		//ConfigGroup confGroup = sc.getConfig().getModule("bikeSharingFacilities");
		//final BikeSharingFacilities bsFacilities = (BikeSharingFacilities) sc.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME );
		final BikeSharingBikes bsBikes = (BikeSharingBikes) sc.getScenarioElement( BikeSharingBikes.ELEMENT_NAME );
		
		//EBikeSharingManager ebsManager = new EBikeSharingManagerImpl(confGroup,sc);

		TravelTime travelTime = travelTimes.get( TransportMode.bike ) ;

		TravelDisutilityFactory travelDisutilityFactory = travelDisutilityFactories.get( TransportMode.bike) ;
		TravelDisutility travelDisutility = travelDisutilityFactory.createTravelDisutility(travelTime) ;	

		LeastCostPathCalculator pathCalculator = pathCalculatorFactory.createPathCalculator(sc.getNetwork(), travelDisutility, travelTime ) ;
//		lcp = pathCalculator;
		
		QNetsimEngine qnet = new QNetsimEngine(qSim);
		qSim.addMobsimEngine(qnet);
		qSim.addDepartureHandler(qnet.getDepartureHandler());
		//qSim.addDepartureHandler(new QNetsimEngineRunner());
		//qSim.addDepartureHandler(qnet);
		
		
		

		//bsBikes.generatePTRouterForBS(sc);

		BikesharingAgentFactory agentFactory = new BikesharingAgentFactory( qSim, pathCalculator, pathCalculatorFactory, sc );

		PopulationAgentSource agentSource = new PopulationAgentSource(sc.getPopulation(), agentFactory, qSim);
		qSim.addAgentSource(agentSource);
		//QNetsimEngineModule.configure(qSim); //Hebenstreit (15.06.2018)
		BsMobsimEngine bs2Engine = new BsMobsimEngine(sc, eventsManager, pathCalculatorFactory, travelDisutilityFactories, travelTimes, qSim);
		qSim.addMobsimEngine(bs2Engine );
		//qSim.addDepartureHandler(bs2Engine);


        //System.out.println("BikesharingQSimFactory allocated");

		return qSim;
	}
		
}
