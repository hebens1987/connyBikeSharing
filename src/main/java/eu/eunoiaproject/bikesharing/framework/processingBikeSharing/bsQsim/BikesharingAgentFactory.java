package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.agents.BikesharingPersonDriverAgentImpl;
import org.matsim.core.mobsim.qsim.agents.PersonDriverAgentImpl;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;

public class BikesharingAgentFactory implements AgentFactory{
	private final BikeSharingContext bikeSharingContext;

	BikesharingAgentFactory( BikeSharingContext bikeSharingContext) {
		this.bikeSharingContext = bikeSharingContext;
	}

	@Override
	public MobsimDriverAgent createMobsimAgentFromPerson(final Person p) {
		
		if (p.getSelectedPlan().getType().equals(TransportMode.car))
		{
			return new PersonDriverAgentImpl (p.getSelectedPlan(),bikeSharingContext.getqSim());
		}
		else
		{
			return new BikesharingPersonDriverAgentImpl( p.getSelectedPlan(), null, bikeSharingContext );
		}
	}
}
