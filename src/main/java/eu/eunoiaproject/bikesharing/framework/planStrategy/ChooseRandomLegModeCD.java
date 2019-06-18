package eu.eunoiaproject.bikesharing.framework.planStrategy;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.HasPlansAndId;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.ReplanningContext;
import org.matsim.population.algorithms.ChooseRandomLegMode;
import org.matsim.population.algorithms.PlanAlgorithm;

public class ChooseRandomLegModeCD implements PlanAlgorithm
{
	private final String[] possibleModesOrig;
	private boolean ignoreCarAvailability = true;
	private final Random rng;
	
	public ChooseRandomLegModeCD(final String[] possibleModes, final Random rng) {
		this.possibleModesOrig = possibleModes.clone();
		this.rng = rng;
	}

	public void setIgnoreCarAvailability(final boolean ignoreCarAvailability) {
		this.ignoreCarAvailability = ignoreCarAvailability;
	}

	@Override
	public void run(final Plan plan)
	{
		final Random rng = MatsimRandom.getLocalInstance();
		CheckReplannedPlans cpr = new CheckReplannedPlans();
		boolean dontUseBike = cpr.planLegLongerThan(plan.getPlanElements(), 20000);
		boolean dontUseWalk = cpr.planLegLongerThan(plan.getPlanElements(), 10000);
		String[] possibleModes = possibleModesOrig.clone();

		for (int i = 0; i < possibleModes.length; i++)
		{
			if (possibleModes[i].equals("bike"))
			{
				if (dontUseBike)
				{
					possibleModes = (String[]) ArrayUtils.remove(possibleModes, i);
				}
			}
			if (possibleModes[i].equals("walking"))
			{
				if (dontUseWalk)
				{
					possibleModes = (String[]) ArrayUtils.remove(possibleModes, i);
				}
			}
		}
		ChooseRandomLegMode crlm = new ChooseRandomLegMode(possibleModes, rng);
		crlm.run(plan);
	}

}
