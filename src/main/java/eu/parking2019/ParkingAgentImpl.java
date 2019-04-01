package eu.parking2019;

import java.util.List;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacilities;


public class ParkingAgentImpl extends PersonParkingDriverAgentImpl{

	private final static Logger log = Logger.getLogger(ParkingAgentImpl.class);

	static Scenario scenario;
	
	private static LeastCostPathCalculator pathCalculator;
	
	BikeSharingFacilities bsFac;

	static LeastCostPathCalculatorFactory pathF ;

	/***************************************************************************/
	/**
	 * Hebenstreit: This class implements a ParkingPersonDriverAgent
	 * **/
	public ParkingAgentImpl(
			final Plan plan, 
			final Netsim simulation, 
			LeastCostPathCalculator pathCalculator, 
			LeastCostPathCalculatorFactory pathF)
	/***************************************************************************/
	{
		super(plan, simulation);
		ParkingAgentImpl.scenario = simulation.getScenario();
		ParkingAgentImpl.pathCalculator = pathCalculator;
		ParkingAgentImpl.pathF = pathF;

		//this.basicDelegate = new BasicPlanAgentImpl(plan, scenario, simulation.getEventsManager(), simulation.getSimTimer()); 
		//this.transitAgentDelegate = new TransitAgentImpl(basicDelegate) ;
		//this.driverAgentDelegate = new PlanBasedDriverAgentImpl(basicDelegate) ;
		//this.personDriverAgent = new PersonDriverAgentImpl(plan, simulation);


		if ( scenario.getConfig().qsim().getNumberOfThreads() != 1 ) {
			throw new RuntimeException("does not work with multiple qsim threads (will use same instance of router)") ; 
		}	
		this.basicAgentDelegate.getModifiablePlan() ; // this lets the agent make a full copy of the plan, which can then be modified
	}

	// -----------------------------------------------------------------------------------------------------------------------------
	/***************************************************************************/
	/**Hebenstreit: We look at the PlanElement Activity and decide what to do 
	 * when the activity ends, **/
	@Override
	public void endActivityAndComputeNextState(final double now) 
	/***************************************************************************/
	{
		//PlanElement last = this.basicAgentDelegate.getCurrentPlan().getPlanElements().get(
		//		this.basicAgentDelegate.getCurrentPlan().getPlanElements().size()-1);//Hebenstreit
		String actPlanMode = this.basicAgentDelegate.getCurrentPlan().getType();
		if (actPlanMode.equals("car_p"))
		{
			PlanElement thisElem = this.basicAgentDelegate.getCurrentPlanElement();
			if (thisElem instanceof Activity)
			{
				Activity thisAct = (Activity) thisElem;
				int currIndex = this.basicAgentDelegate.getCurrentPlanElementIndex();
				List<PlanElement> peList = this.basicAgentDelegate.getCurrentPlan().getPlanElements();
				if (currIndex == 0)
				{
					for (int i = 0; i < peList.size(); i++)
					{
						if (peList.get(i) instanceof Leg)
						{
							Leg leg = (Leg) peList.get(i);
							if ((leg.getMode().equals(TransportMode.access_walk))||(leg.getMode().equals(TransportMode.egress_walk)))
							{
								peList.remove(peList.get(i));
								i--;
							}
							
							if ((leg.getMode().equals(TransportMode.pt))&& (basicAgentDelegate.getCurrentPlan().getType().equals("car_p")))
							{
								leg.setMode(TransportMode.car); //Hebenstreit: warum wechselt mein Programm auf PT!?
							}
						}
							
					}
				}
				int endIndex = this.basicAgentDelegate.getCurrentPlan().getPlanElements().size()-1;
				if (!(currIndex==endIndex))
				{
				ParkingEndHandler pdh = new ParkingEndHandler();
				pdh.insertEgressLeg(now, thisAct, scenario, basicAgentDelegate);
				}
			}
			
		}
		planComparison(basicAgentDelegate);
		
		if (!this.getState().equals(State.ABORT))
		{
			//System.out.println("Curr_Index: " + basicAgentDelegate.getCurrentPlanElementIndex() + " planSize: " + basicAgentDelegate.getCurrentPlan().getPlanElements().size());
				
			if (this.basicAgentDelegate.getCurrentPlanElement() instanceof Activity)
			{
				//System.out.println("endAct " + basicAgentDelegate.getPerson().getId().toString());
				this.basicAgentDelegate.endActivityAndComputeNextState(now);
			}
		}
	}
	
	@Override
	public void endLegAndComputeNextState(double now) {
		
		planComparison(basicAgentDelegate);
		
		if (this.basicAgentDelegate.getCurrentPlanElement() instanceof Leg)
		{
			//System.out.println("endLeg " + basicAgentDelegate.getPerson().getId().toString());
			Leg leg = (Leg)this.basicAgentDelegate.getCurrentPlanElement();
			Activity nextAct = (Activity)this.basicAgentDelegate.getNextActivity();
			if (leg.getMode().contains("car"))
			{
				ParkingStartHandler psh = new ParkingStartHandler();
				psh.insertAccessLeg(now, nextAct.getCoord(), scenario, basicAgentDelegate, nextAct);
			}
			basicAgentDelegate.endLegAndComputeNextState(now);
		}
	}
	
	public void planComparison(BasicParkingPlanAgentImpl basicAgentDelegate)
	/***************************************************************************/
	{
		//setzt alle verschiedenen PlanElement Listen gleich! TODO: Hebenstreit
		List<PlanElement>pe1 = basicAgentDelegate.getCurrentPlan().getPlanElements(); 
		//List<PlanElement> pe2 = basicAgentDelegate.getPerson().getSelectedPlan().getPlanElements();//this will be replaced by pe1;
		basicAgentDelegate.setAllPlanElement(pe1);
		basicAgentDelegate.setCurrentPlanElementIndex(basicAgentDelegate.getCurrentPlanElementIndex());
	}
}