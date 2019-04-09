package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;

import java.util.List;
import java.util.Random;

import javax.inject.Named;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.population.routes.GenericRouteImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.population.algorithms.PlanAlgorithm;

import eu.eunoiaproject.bikesharing.framework.routingBikeSharingFramework.EBikeSharingRoutingModule;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.routingModules.TUG_BikeRoutingModule;

/**
 * Changes the transportation mode of all legs in a plan to a randomly chosen
 * different mode (but the same mode for all legs in that plan) given a list
 * of possible modes.
 *
 * @author mrieser
 */
public class ResetBSPlanAndChooseNewPlanMode implements PlanAlgorithm {

	private final String[] possibleModes;
	private boolean ignoreCarAvailability = true;

	private final Random rng;

	/**
	 * @param possibleModes
	 * @param rng The random number generator used to draw random numbers to select another mode.
	 * @see TransportMode
	 * @see MatsimRandom
	 */
	public ResetBSPlanAndChooseNewPlanMode(final String[] possibleModes, final Random rng) {
		this.possibleModes = possibleModes.clone();
		this.rng = rng;
	}

	public void setIgnoreCarAvailability(final boolean ignoreCarAvailability) {
		this.ignoreCarAvailability = ignoreCarAvailability;
	}

	@Override
	public void run(final Plan plan) {
		List<PlanElement> tour = plan.getPlanElements();
		String newMode= changeToRandomLegMode(tour, plan);
		plan.setType(newMode);
	}

	private String changeToRandomLegMode(final List<PlanElement> tour, final Plan plan) 
	{
		final String currentMode = plan.getType();
		String newMode = "non";
		if (tour.size() > 1) {
			boolean forbidCar = false;
			if (!this.ignoreCarAvailability) {
				String carAvail = PersonUtils.getCarAvail(plan.getPerson());
				if ("never".equals(carAvail)) {
					forbidCar = true;
				}
			}

			while (true) {
				int newModeIdx = chooseModeOtherThan(currentMode);
				newMode = this.possibleModes[newModeIdx];
				if (!(forbidCar && TransportMode.car.equals(newMode))) {
					break;
				} else {
					if (this.possibleModes.length == 2) {
						newMode = currentMode; // there is no other mode available
						break;
					}
				}
			}

			boolean longerThan25km = checkPlan(tour, currentMode); 
			if (longerThan25km)
			{
				if (newMode.contains(TransportMode.walk) || newMode.equals(TransportMode.bike))
				{
					if (currentMode.equals(TransportMode.car)) {newMode = TransportMode.pt;}
					else if (currentMode.equals(TransportMode.pt)) {newMode = TransportMode.car;}
					else
					{
						double coincidence = Math.random();
						if (coincidence < 0.5)
						{
							newMode = TransportMode.pt;
						}
						else
						{
							newMode = TransportMode.walk;
						}
					}
				}
			}
			changeLegModeTo(tour, newMode);
			
		}
		return newMode;
	}


	private boolean checkPlan (final List<PlanElement> list, String mode)
	{

			for (int i = 0; i < list.size()-1; i++)
			{
				if (list.get(i) instanceof Activity)
				{
					if ((((Activity) list.get(i)).getType().contains("interaction")) ||
							(((Activity) list.get(i)).getType().equals("wait")))
					{
						list.remove(i);
						i--;
					}
					
					else
					{
						if (i > 0)
						{
							Activity act = (Activity) list.get(i);
							act.setStartTime(Double.NEGATIVE_INFINITY);
						}
					}
				}
			}
//			for(int i = 1; i < list.size()-1; i++)
//			{
//				Id<Link> firstLink = null;
//				Id<Link> lastLink = null;
//				while (list.get(i) instanceof Leg)
//				{
//					if (firstLink == null)
//					{
//						firstLink = ((Leg)list.get(i)).getRoute().getStartLinkId();
//					}
//					lastLink = ((Leg)list.get(i)).getRoute().getEndLinkId();
//					list.remove(i);
//				}
//				{
//					Leg leg = new LegImpl (mode);
//					NetworkRoute route = new LinkNetworkRouteImpl(firstLink, lastLink);
//					leg.setRoute(route);
//					list.add(i,leg);
//					i++;	
//				}
//			}

		
		Activity old = null;
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i) instanceof Activity)
			{
				if (old == null)
				{
					old = (Activity)list.get(i);
					if (old == null)
					{
						System.out.println("null");
					}
				}
				
				else
				{
					Activity act = (Activity)list.get(i);
					
					if (act.getCoord() == null)
					{
						System.out.println("null");
					}
					double beeline = 
                        CoordUtils.calcEuclideanDistance(old.getCoord(), act.getCoord());
					if (beeline > 25000)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}


	private void changeLegModeTo(final List<PlanElement> tour, final String newMode) {
		for (PlanElement pe : tour) {
			if (pe instanceof Leg) {
				((Leg) pe).setMode(newMode);
			}
		}
	}

	private int chooseModeOtherThan(final String currentMode) {
		int newModeIdx = this.rng.nextInt(this.possibleModes.length - 1);
		for (int i = 0; i <= newModeIdx; i++) {
			if (this.possibleModes[i].equals(currentMode)) {
				/* if the new Mode is after the currentMode in the list of possible
				 * modes, go one further, as we have to ignore the current mode in
				 * the list of possible modes. */
				newModeIdx++;
				break;
			}
		}
		return newModeIdx;
	}

}
