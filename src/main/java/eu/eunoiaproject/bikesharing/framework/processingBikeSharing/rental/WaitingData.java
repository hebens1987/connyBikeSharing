package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacility;


public class WaitingData
{
	public BasicPlanAgentImpl bpAgent;
	public double time;
	public Id<Person> personId;
	public BikeSharingFacility bsFac;

	public WaitingData()
	{}
}