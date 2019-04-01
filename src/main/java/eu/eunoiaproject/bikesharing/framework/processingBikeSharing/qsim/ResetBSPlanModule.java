
package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim;

import org.matsim.core.config.Config;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;
import org.matsim.population.algorithms.PlanAlgorithm;


public class ResetBSPlanModule extends AbstractMultithreadedModule {

	public ResetBSPlanModule(Config config) {
		super(config.global());
	}

	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		return new ResetBSPlan();
	}
}
