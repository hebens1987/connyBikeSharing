
package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.ChangeLegModeConfigGroup;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;
import org.matsim.core.utils.misc.StringUtils;
import org.matsim.population.algorithms.ChooseRandomLegMode;
import org.matsim.population.algorithms.PlanAlgorithm;

/**
 * Changes the transportation mode of all legs in a plan to a randomly chosen
 * different mode (but the same mode for all legs in that plan) given a list
 * of possible modes.
 *
 * By default, the module chooses between "car" and "pt". If other modes should
 * be used, it can be done so in the configuration. Also, this module is able to (optionally)
 * respect car-availability:
 * <pre>
 * &lt;module name="changeLegMode">
 *   &lt!-- provide a comma-separated list of leg modes -->
 *   &lt;param name="modes" value="car,walk,bike" />
 *   &lt;param name="ignoreCarAvailability" value="false" />
 * &lt;/module>
 * </pre>
 *
 * @see ChooseRandomLegMode
 *
 * @author mrieser
 */
public class ResetBSPlanAndChooseNewPlanModeModule extends AbstractMultithreadedModule {

	// (I made the above static final variables public so they can be used in scripts-in-java. kai, jun'15)

	private String[] availableModes;
	private boolean ignoreCarAvailability;

	public  ResetBSPlanAndChooseNewPlanModeModule(final GlobalConfigGroup globalConfigGroup, ChangeLegModeConfigGroup changeLegModeConfigGroup) {
		super(globalConfigGroup.getNumberOfThreads());
		this.availableModes = changeLegModeConfigGroup.getModes();
		this.ignoreCarAvailability = changeLegModeConfigGroup.getIgnoreCarAvailability();
	}
	
	public  ResetBSPlanAndChooseNewPlanModeModule(final int nOfThreads, final String[] modes, final boolean ignoreCarAvailabilty) {
		super(nOfThreads);
		this.availableModes = modes.clone();
		this.ignoreCarAvailability = ignoreCarAvailabilty;
	}

	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		ResetBSPlanAndChooseNewPlanMode algo = new ResetBSPlanAndChooseNewPlanMode(this.availableModes, MatsimRandom.getLocalInstance());
		algo.setIgnoreCarAvailability(this.ignoreCarAvailability);
		return algo;
	}

}