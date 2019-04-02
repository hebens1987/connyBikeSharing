package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes;

import javax.inject.Inject;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.agents.PersonDriverAgentImpl;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.pt.router.TransitRouterImpl;
import org.matsim.core.mobsim.qsim.agents.BikesharingPersonDriverAgentImpl;

public class BikesharingAgentFactory implements AgentFactory{
	private final Netsim simulation;
	private LeastCostPathCalculator pathCalculator;
	private LeastCostPathCalculatorFactory pathF;
	private Scenario scenario;
    @Inject
    EventsManager eventsManager;
	
	
	public BikesharingAgentFactory(final Netsim simulation, LeastCostPathCalculator pathCalculator, 
			LeastCostPathCalculatorFactory pathF, TransitRouterImpl trImpl, Scenario scenario ) {
		this.simulation = simulation;
		this.pathCalculator = pathCalculator;
		this.pathF = pathF;
		this.scenario = scenario;
	}

	@Override
	public MobsimDriverAgent createMobsimAgentFromPerson(final Person p) {
		
		String type = p.getSelectedPlan().getType();
		
		//VehicleImpl vehicle =  new VehicleImpl(Id.createVehicleId(p.getId().toString()), null);
		//MobsimVehicle veh = (MobsimVehicle) vehicle;
		MobsimDriverAgent agent;
		
		if (type == null)
		{
			System.out.println("There must be a plan type set in the plans file for the use of bike sharing, please do so!");
			System.exit(0);
		}
		QSim qsim = new QSim(scenario, eventsManager);
		
		//Hebenstreit TODO:
		if ((type.equals("ffBikeSharing"))||
			type.equals("eBikeSharing") || type.equals(TransportMode.pt))
		{ 
			agent = new BikesharingPersonDriverAgentImpl(p.getSelectedPlan(), this.simulation,
					 pathCalculator, pathF, null);
		}
		//if (type.equals("pt"))
		//{
		//	agent = TransitAgent.createTransitAgent(p, this.simulation); 
		//}
		else
		{
			agent = new PersonDriverAgentImpl(p.getSelectedPlan(), this.simulation); 
		}
		
		qsim.insertAgentIntoMobsim(agent);
		return agent;
	}
}
