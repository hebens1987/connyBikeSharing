package eu.eunoiaproject.bikesharing.framework.planStrategy;

import org.matsim.core.config.groups.ChangeLegModeConfigGroup;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.replanning.modules.ChangeLegMode;
import org.matsim.core.replanning.modules.ReRoute;
import org.matsim.core.replanning.modules.TripsToLegsModule;
import org.matsim.core.replanning.selectors.RandomPlanSelector;
import org.matsim.core.router.TripRouter;
import org.matsim.facilities.ActivityFacilities;

import javax.inject.Inject;
import javax.inject.Provider;

public class ChangeTripModeCD implements Provider<PlanStrategy> {

	private final GlobalConfigGroup globalConfigGroup;
	private final ChangeLegModeConfigGroup changeLegModeConfigGroup;
	private Provider<TripRouter> tripRouterProvider;
	private ActivityFacilities activityFacilities;

	@Inject
    protected ChangeTripModeCD(GlobalConfigGroup globalConfigGroup, ChangeLegModeConfigGroup changeLegModeConfigGroup, ActivityFacilities activityFacilities, Provider<TripRouter> tripRouterProvider) {
		this.globalConfigGroup = globalConfigGroup;
		this.changeLegModeConfigGroup = changeLegModeConfigGroup;
		this.activityFacilities = activityFacilities;
		this.tripRouterProvider = tripRouterProvider;
	}

    @Override
	public PlanStrategy get() {
		PlanStrategyImpl strategy = new PlanStrategyImpl(new RandomPlanSelector());
		strategy.addStrategyModule(new TripsToLegsModule(tripRouterProvider, globalConfigGroup));
		strategy.addStrategyModule(new ChangeLegModeCD(globalConfigGroup, changeLegModeConfigGroup));
		strategy.addStrategyModule(new ReRoute(activityFacilities, tripRouterProvider, globalConfigGroup));
		return strategy;
	}

}