package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim;

import java.util.List;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.agents.BikesharingPersonDriverAgentImpl;
import org.matsim.core.mobsim.qsim.agents.PersonDriverAgentImpl;
import org.matsim.core.mobsim.qsim.agents.TransitAgent;
import org.matsim.core.mobsim.qsim.agents.TransitAgentImpl;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;

public class BikesharingAgentFactory implements AgentFactory{
	private final BikeSharingContext bikeSharingContext;

	BikesharingAgentFactory( BikeSharingContext bikeSharingContext) {
		this.bikeSharingContext = bikeSharingContext;
	}

	@Override
	public MobsimDriverAgent createMobsimAgentFromPerson(final Person p) {
		
		/*if (p.getSelectedPlan().getType().equals(TransportMode.car))
		{
			return new PersonDriverAgentImpl(p.getSelectedPlan(), bikeSharingContext.getqSim()); 
		}*/
		
		List<PlanElement> planelems = p.getSelectedPlan().getPlanElements();
		
		for (int i = 0; i < planelems.size(); i++)
		{
			if (planelems.get(i) instanceof Leg)
			{
				Leg leg = (Leg)planelems.get(i);
				if (leg.getMode().equals("eBikeSharing")) 
				{
					p.getSelectedPlan().setType("eBikeSharing");
					break;
				}
				else
				{
					p.getSelectedPlan().setType("other");
				}
			}
		}
		
		
		return new BikesharingPersonDriverAgentImpl( p.getSelectedPlan(), null, bikeSharingContext );
		/*
		if (p.getSelectedPlan().getType().equals("eBikeSharing"))
		{
			return new BikesharingPersonDriverAgentImpl( p.getSelectedPlan(), null, bikeSharingContext );
		}
		else if (p.getSelectedPlan().getType().equals(TransportMode.pt))
		{
			return TransitAgent.createTransitAgent(p, bikeSharingContext.getqSim()); 
		}
		else
		{
			return new PersonDriverAgentImpl(p.getSelectedPlan(), bikeSharingContext.getqSim()); 
		}
		*/
	}
}
