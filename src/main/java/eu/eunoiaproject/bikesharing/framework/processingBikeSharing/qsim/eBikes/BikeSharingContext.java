package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes;

import com.google.inject.Inject;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.IKK_ObjectAttributesSingleton;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BikeTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routing.pedestrians.TUG_WalkTravelDisutility;
import eu.eunoiaproject.bikesharing.framework.routing.pedestrians.TUG_WalkTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.pt.router.TransitRouterConfig;
import org.matsim.pt.router.TransitRouterImpl;

import java.util.Map;

public class BikeSharingContext{
	private final LeastCostPathCalculator standardBikePathCalculator;
	private final TransitRouterImpl transitRouter ;
	private final LeastCostPathCalculator sharedBikePathCalculator;
	private final LeastCostPathCalculator walkPathCalculator;

	@Inject private Mobsim qSim;
	@Inject private IKK_ObjectAttributesSingleton instance ;
	private final Scenario scenario;

	@Inject BikeSharingContext(
		  Config config,
		  Map<String,TravelTime> travelTimes,
		  Map<String,TravelDisutilityFactory> travelDisutilityFactories,
		  LeastCostPathCalculatorFactory pathCalculatorFactory,
		  Scenario sc ){
		BicycleConfigGroup confBC = ConfigUtils.addOrGetModule( config, BicycleConfigGroup.GROUP_NAME, BicycleConfigGroup.class );
		{
			TravelTime travelTime = travelTimes.get( TransportMode.bike );
			TravelDisutilityFactory travelDisutilityFactory = travelDisutilityFactories.get( TransportMode.bike );
			TravelDisutility travelDisutility = travelDisutilityFactory.createTravelDisutility( travelTime );
			standardBikePathCalculator = pathCalculatorFactory.createPathCalculator( sc.getNetwork(), travelDisutility, travelTime );
		}
		{
			TravelTime btt = new TUG_BSTravelTime( confBC );
			TravelDisutility btd = new TUG_BikeTravelDisutility( confBC );
			sharedBikePathCalculator = pathCalculatorFactory.createPathCalculator( sc.getNetwork(), btd, btt );
		}
		{
			TravelTime btt = new TUG_WalkTravelTime( confBC );
			TravelDisutility btd = new TUG_WalkTravelDisutility();
			walkPathCalculator = pathCalculatorFactory.createPathCalculator( sc.getNetwork(), btd, btt );
		}
		{
			TransitRouterConfig ctr = new TransitRouterConfig( config );
			transitRouter = new TransitRouterImpl( ctr, sc.getTransitSchedule() ) ;
			// (yyyy the current code operates directly at the level of the transit router, rather than using the trip router.  accepting this for the time being. kai,
			// apr'19)
		}
		this.scenario = sc ;
	}
	public Scenario getScenario() {
		return this.scenario ;
	}

	public LeastCostPathCalculator getStandardBikePathCalculator(){
		return standardBikePathCalculator;
	}
	public LeastCostPathCalculator getSharedBikePathCalculator(){
		return sharedBikePathCalculator;
	}
	public QSim getqSim(){
		return (QSim) qSim;
	}
	public LeastCostPathCalculator getWalkPathCalculator(){
		return walkPathCalculator ;
	}
	public IKK_ObjectAttributesSingleton getInstance(){
		return instance;
	}
	@Deprecated //  try use trip router instead.  kai, apr'19
	public TransitRouterImpl getTransitRouter(){
		return transitRouter;
	}
}
