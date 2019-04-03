package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.agents.BikesharingPersonDriverAgentImpl;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;

public class BikesharingAgentFactory implements AgentFactory{
	private final Netsim simulation;
	private LeastCostPathCalculator standardBikePathCalculator;
	private LeastCostPathCalculatorFactory pathF;

	BikesharingAgentFactory( final Netsim simulation, LeastCostPathCalculator standardBikePathCalculator, LeastCostPathCalculatorFactory pathF ) {
		this.simulation = simulation;
		this.standardBikePathCalculator = standardBikePathCalculator;
		this.pathF = pathF;
	}

	@Override
	public MobsimDriverAgent createMobsimAgentFromPerson(final Person p) {
		
		String type = p.getSelectedPlan().getType();
		
		//VehicleImpl vehicle =  new VehicleImpl(Id.createVehicleId(p.getId().toString()), null);
		//MobsimVehicle veh = (MobsimVehicle) vehicle;
		MobsimDriverAgent agent;
		
		if (type == null)
		{
			throw new RuntimeException("There must be a plan type set in the plans file for the use of bike sharing, please do so!");
//			System.exit(0);
			// if System.exit(...) is encountered in regression test, one gets weird behavior.  kai, apr'19
		}
//		QSim qsim = new QSim(scenario, eventsManager);
		
		//Hebenstreit TODO:
//		if ((type.equals("ffBikeSharing"))||
//			type.equals("eBikeSharing") || type.equals(TransportMode.pt))
		{ 
			agent = new BikesharingPersonDriverAgentImpl(p.getSelectedPlan(), this.simulation,
				  standardBikePathCalculator, pathF, null);
		}
		//if (type.equals("pt"))
		//{
		//	agent = TransitAgent.createTransitAgent(p, this.simulation); 
		//}
//		else
//		{
//			agent = new PersonDriverAgentImpl(p.getSelectedPlan(), this.simulation);
//		}
		
//		qsim.insertAgentIntoMobsim(agent);
		return agent;
	}
}
