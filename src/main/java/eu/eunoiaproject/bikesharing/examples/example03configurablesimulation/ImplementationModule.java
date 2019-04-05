package eu.eunoiaproject.bikesharing.examples.example03configurablesimulation;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.IKK_ObjectAttributesSingleton;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.ResetBSPlanAndChooseNewPlanModeModuleStrategy;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.BikeSharingContext;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes.EBikeSharingQsimFactory;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.IKK_BikeTravelDisutilityFactory;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BSBikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BSEBikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_BikeTravelTime;
import eu.eunoiaproject.bikesharing.framework.routing.bicycles.TUG_EBSTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scoring.TUG_LegScoringFunctionBikeAndWalkFactory;
import eu.eunoiaproject.bikesharing.framework.routing.bikeSharing.EBikeSharingRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routing.pedestrians.TUG_WalkRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routing.pedestrians.TUG_WalkTravelDisutilityFactory;
import eu.eunoiaproject.bikesharing.framework.routing.pedestrians.TUG_WalkTravelTime;
import eu.eunoiaproject.freeFloatingBS.FFBikeSharingRoutingModule;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.router.LeastCostPathCalculatorModule;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.router.NetworkRouting;
import org.matsim.core.router.SingleModeNetworksCache;
import org.matsim.pt.router.TransitRouterModule;

class ImplementationModule extends AbstractModule {

	private final Config config;

	ImplementationModule( Config config )
	{
		this.config= config;
	}

	//@Override
	public void install()
	{
		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class ) ;
		switch( bikeSharingConfig.getRunType() ) {
			case standard:
				this.install(new TransitRouterModule());
				break;
			case debug:
				break;
			default:
				throw new RuntimeException( "not implemented" ) ;
		}

		this.addPlanStrategyBinding("ResetBSPlan").to( ResetBSPlanAndChooseNewPlanModeModuleStrategy.class ) ;
		//XXXXX Hebenstreit: does this have any effect?

		// context (shared container):
		this.bind( BikeSharingContext.class ) ;

		// changed mobsim (contains changed agent source to insert bike sharing agents):
		this.bindMobsim().toProvider(EBikeSharingQsimFactory.class);

		// global attributes.  I think we could now use guice of this
		this.bind( IKK_ObjectAttributesSingleton.class ) ;
		// yyyyyy find other solution!!!!

		// scoring:
		this.bindScoringFunctionFactory().to(TUG_LegScoringFunctionBikeAndWalkFactory.class);

		// Everything else is routing:
		this.bind(SingleModeNetworksCache.class).asEagerSingleton();
		// yy the above may be needed, but why?

		this.bind(MainModeIdentifier.class).to(MainModeIdentifierImpl.class);
		// needed?

		this.install(new LeastCostPathCalculatorModule());
		// needed?

//		this.addTravelDisutilityFactoryBinding(TransportMode.pt).to(TUG_WalkTravelDisutilityFactory.class);
		// yyyy the above was not commented out.  But I don't think that it makes sense. kai, apr'19

		//TODO: Hebenstreit - own Travel Time for Bike Sharing (eventually own Disutility for E-BikeSharing!!!)

		this.addTravelDisutilityFactoryBinding(EBConstants.MODE ).to(TUG_WalkTravelDisutilityFactory.class);
		this.addRoutingModuleBinding(EBConstants.MODE ).to(EBikeSharingRoutingModule.class);
		// yyyyyy how is this working without having defined a travel time binding?  kai, apr'19

		this.addTravelTimeBinding(EBConstants.MODE_FF).to(TUG_BikeTravelTime.class);
		//TODO: Hebenstreit - own Travel Time for Bike Sharing (eventually own Disutility for E-BikeSharing!!!)
		this.addTravelDisutilityFactoryBinding(EBConstants.MODE_FF ).to(TUG_WalkTravelDisutilityFactory.class);
		this.addRoutingModuleBinding(EBConstants.MODE_FF ).to(FFBikeSharingRoutingModule.class);

		//this.addRoutingModuleBinding(EBConstants.BS_BIKE).toProvider(new NetworkRouting(EBConstants.BS_BIKE));
		this.addTravelDisutilityFactoryBinding(EBConstants.BS_BIKE).to(IKK_BikeTravelDisutilityFactory.class);
		this.addTravelTimeBinding(EBConstants.BS_BIKE).to(TUG_BSTravelTime.class);
		this.addRoutingModuleBinding(EBConstants.BS_BIKE).to(TUG_BSBikeRoutingModule.class);

		//this.addRoutingModuleBinding(EBConstants.BS_E_BIKE).toProvider(new NetworkRouting(EBConstants.BS_E_BIKE));
		this.addTravelDisutilityFactoryBinding(EBConstants.BS_E_BIKE).to(IKK_BikeTravelDisutilityFactory.class);
		this.addTravelTimeBinding(EBConstants.BS_E_BIKE).to(TUG_EBSTravelTime.class);
		this.addRoutingModuleBinding(EBConstants.BS_E_BIKE).to(TUG_BSEBikeRoutingModule.class);

		this.addTravelDisutilityFactoryBinding(EBConstants.BS_WALK).to(TUG_WalkTravelDisutilityFactory.class);
		this.addTravelTimeBinding(EBConstants.BS_WALK).to(TUG_WalkTravelTime.class);
		this.addRoutingModuleBinding(EBConstants.BS_WALK).toProvider(new NetworkRouting(EBConstants.BS_WALK));

		this.addTravelDisutilityFactoryBinding(TransportMode.bike).to(IKK_BikeTravelDisutilityFactory.class);
		this.addTravelTimeBinding(TransportMode.bike).to(TUG_BikeTravelTime.class);
		this.addRoutingModuleBinding(TransportMode.bike).to(TUG_BikeRoutingModule.class);
		//this.addRoutingModuleBinding(TransportMode.bike).toProvider(new NetworkRouting(TransportMode.bike));

		//addTravelDisutilityFactoryBinding(TransportMode.walk).to(TUG_WalkTravelDisutilityFactory.class);
		//addTravelTimeBinding(TransportMode.walk).to(TUG_WalkTravelTime.class);
		//addRoutingModuleBinding(TransportMode.walk).toProvider(new NetworkRouting(TransportMode.bike));//Hebenstreit - war auskommentiert
		//addRoutingModuleBinding("walk").to(TUG_WalkRoutingModule.class);NetworkRouting

		this.addTravelDisutilityFactoryBinding(TransportMode.walk).to(TUG_WalkTravelDisutilityFactory.class);
		this.addTravelTimeBinding(TransportMode.walk).to(TUG_WalkTravelTime.class);
		// yyyy the above two lines are not used directly, but the walk bindings are used elsewhere.
		//			this.addRoutingModuleBinding(TransportMode.walk).to(TUG_WalkRoutingModule.class);

		this.addTravelDisutilityFactoryBinding(EBConstants.BS_WALK_FF).to(TUG_WalkTravelDisutilityFactory.class);
		this.addTravelTimeBinding(EBConstants.BS_WALK_FF).to(TUG_WalkTravelTime.class);
		this.addRoutingModuleBinding(EBConstants.BS_WALK_FF).toProvider(new NetworkRouting(TransportMode.walk));

		this.addTravelDisutilityFactoryBinding(EBConstants.BS_BIKE_FF).to(TUG_WalkTravelDisutilityFactory.class);
		this.addTravelTimeBinding(EBConstants.BS_BIKE_FF).to(TUG_WalkTravelTime.class);
		this.addRoutingModuleBinding(EBConstants.BS_BIKE_FF).toProvider(new NetworkRouting(TransportMode.bike));

	}

}

