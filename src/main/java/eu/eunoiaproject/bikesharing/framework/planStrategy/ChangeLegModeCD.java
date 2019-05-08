package eu.eunoiaproject.bikesharing.framework.planStrategy;


import org.matsim.core.config.groups.ChangeLegModeConfigGroup;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;
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
public class ChangeLegModeCD extends AbstractMultithreadedModule {

	// (I made the above static final variables public so they can be used in scripts-in-java. kai, jun'15)

	private String[] availableModes;
	private boolean ignoreCarAvailability;

	public ChangeLegModeCD(final GlobalConfigGroup globalConfigGroup, ChangeLegModeConfigGroup changeLegModeConfigGroup) {
		super(globalConfigGroup.getNumberOfThreads());
		this.availableModes = changeLegModeConfigGroup.getModes();
		this.ignoreCarAvailability = changeLegModeConfigGroup.getIgnoreCarAvailability();
	}
	
	public ChangeLegModeCD(final int nOfThreads, final String[] modes, final boolean ignoreCarAvailabilty) {
		super(nOfThreads);
		this.availableModes = modes.clone();
		this.ignoreCarAvailability = ignoreCarAvailabilty;
	}

	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		ChooseRandomLegModeCD algo = new ChooseRandomLegModeCD(this.availableModes, MatsimRandom.getLocalInstance());
		return algo;
	}

}
