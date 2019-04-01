package eu.parking2019;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.agents.DefaultAgentFactory;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;

public class ParkingAgentFactory implements AgentFactory{
	private final Netsim simulation;
	private LeastCostPathCalculator pathCalculator;
	private LeastCostPathCalculatorFactory pathF;
	
	
	
	public ParkingAgentFactory(final Netsim simulation, LeastCostPathCalculator pathCalculator, 
			LeastCostPathCalculatorFactory pathF) {
		this.simulation = simulation;
		this.pathCalculator = pathCalculator;
		this.pathF = pathF;
	}
	

	@Override
	public MobsimDriverAgent createMobsimAgentFromPerson(final Person p) {
		
		String type = p.getSelectedPlan().getType();
		
		MobsimDriverAgent agent;
		
		if (type == null)
		{
			System.out.println("There must be a plan type set in the plans file for the use parking!");
			System.exit(0);
		}
		
		if (type.equals("car_p"))
		{
			//creates ParkingPersonDriverAgent - if the plan Type is set to car_p
			agent = new ParkingAgentImpl(p.getSelectedPlan(), this.simulation,
					 pathCalculator, pathF); 
		}

		else 
		{
			DefaultAgentFactory da = new DefaultAgentFactory(simulation);
			agent = da.createMobsimAgentFromPerson(p);
		}
		
		
		return agent;
	}
}
