package eu.eunoiaproject.bikesharing.examples.example03configurablesimulation;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim.EBikeSharingQsimFactory;
import eu.eunoiaproject.bikesharing.framework.routingBikeSharingFramework.EBikeSharingRoutingModule;
import eu.eunoiaproject.bikesharing.framework.scoring.TUG_LegScoringFunctionBikeAndWalkFactory;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules.TUG_BSBikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules.TUG_BSEBikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules.TUG_BikeRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules.TUG_WalkRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities.IKK_BikeTravelDisutilityFactory;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BikeTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_EBSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_WalkTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.ResetBSPlanAndChooseNewPlanModeModuleStrategy;
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
			this.bind(MainModeIdentifier.class).to(MainModeIdentifierImpl.class);
		    this.install(new LeastCostPathCalculatorModule());
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
		    this.bind(SingleModeNetworksCache.class).asEagerSingleton();
		    //this.addTravelDisutilityFactoryBinding(TransportMode.pt).to(IKK_BikeTravelDisutilityFactory.class);
//		    PlanCalcScoreConfigGroup plansCalc = (PlanCalcScoreConfigGroup) config.getModule(PlanCalcScoreConfigGroup.GROUP_NAME);
			this.addPlanStrategyBinding("ResetBSPlan").to((Class<? extends PlanStrategy>) ResetBSPlanAndChooseNewPlanModeModuleStrategy.class ) ;
			//XXXXX Hebenstreit: does this have any effect?
		
			//TODO: Hebenstreit - own Travel Time for Bike Sharing (eventually own Disutility for E-BikeSharing!!!)
			
			//this.addTravelDisutilityFactoryBinding(EBConstants.MODE ).to(IKK_BikeTravelDisutilityFactory.class);  
			this.addRoutingModuleBinding(EBConstants.MODE ).to(EBikeSharingRoutingModule.class);
			
			this.addTravelTimeBinding(EBConstants.MODE_FF).to(TUG_BSTravelTime.class); 
			//TODO: Hebenstreit - own Travel Time for Bike Sharing (eventually own Disutility for E-BikeSharing!!!)
			this.addTravelDisutilityFactoryBinding(EBConstants.MODE_FF ).to(IKK_BikeTravelDisutilityFactory.class);  
			this.addRoutingModuleBinding(EBConstants.MODE_FF ).to(FFBikeSharingRoutingModule.class);

			//this.addRoutingModuleBinding(EBConstants.BS_BIKE).toProvider(new NetworkRouting(EBConstants.BS_BIKE));
			this.addTravelDisutilityFactoryBinding(EBConstants.BS_BIKE).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_BIKE).to(TUG_BSTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_BIKE).to(TUG_BSBikeRoutingModule.class);
			
			//this.addRoutingModuleBinding(EBConstants.BS_E_BIKE).toProvider(new NetworkRouting(EBConstants.BS_E_BIKE));
			this.addTravelDisutilityFactoryBinding(EBConstants.BS_E_BIKE).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_E_BIKE).to(TUG_EBSTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_E_BIKE).to(TUG_BSEBikeRoutingModule.class);

			this.addTravelDisutilityFactoryBinding(EBConstants.BS_WALK).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_WALK).to(TUG_WalkTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_WALK).to(TUG_WalkRoutingModule.class);
			
			this.bindScoringFunctionFactory().to(TUG_LegScoringFunctionBikeAndWalkFactory.class);
			
			this.addTravelDisutilityFactoryBinding(TransportMode.bike).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(TransportMode.bike).to(TUG_BikeTravelTime.class); 
			this.addRoutingModuleBinding(TransportMode.bike).to(TUG_BikeRoutingModule.class);
			//this.addRoutingModuleBinding(TransportMode.bike).toProvider(new NetworkRouting(TransportMode.bike));
	
			//addTravelDisutilityFactoryBinding(TransportMode.walk).to(TUG_WalkTravelDisutilityFactory.class); 
			//addTravelTimeBinding(TransportMode.walk).to(TUG_WalkTravelTime.class); 
			//addRoutingModuleBinding(TransportMode.walk).toProvider(new NetworkRouting(TransportMode.bike));//Hebenstreit - war auskommentiert
			//addRoutingModuleBinding("walk").to(TUG_WalkRoutingModule.class);NetworkRouting
			
			this.addTravelDisutilityFactoryBinding(TransportMode.walk).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(TransportMode.walk).to(TUG_WalkTravelTime.class); 
			this.addRoutingModuleBinding(TransportMode.walk).to(TUG_WalkRoutingModule.class);
			//this.addRoutingModuleBinding(TransportMode.walk).toProvider(new NetworkRouting(TransportMode.walk));
			
			this.addTravelDisutilityFactoryBinding(EBConstants.BS_WALK_FF).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_WALK_FF).to(TUG_WalkTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_WALK_FF).to(TUG_WalkRoutingModule.class);

			this.addTravelDisutilityFactoryBinding(EBConstants.BS_BIKE_FF).to(IKK_BikeTravelDisutilityFactory.class); 
			this.addTravelTimeBinding(EBConstants.BS_BIKE_FF).to(TUG_WalkTravelTime.class); 
			this.addRoutingModuleBinding(EBConstants.BS_BIKE_FF).to(TUG_BikeRoutingModule.class);

			this.bindMobsim().toProvider(EBikeSharingQsimFactory.class);
			
		 } 
		 

//		TripRouter create(Map<String, Provider<RoutingModule>> routingModules, MainModeIdentifier mainModeIdentifier) {
//		        TripRouter tripRouter = new TripRouter();
//		        for (Map.Entry<String, Provider<RoutingModule>> entry : routingModules.entrySet()) {
//		            tripRouter.setRoutingModule(entry.getKey(), entry.getValue().get());
//		        }
//		        tripRouter.setMainModeIdentifier(mainModeIdentifier);
//		        return tripRouter;
//		    }
}

