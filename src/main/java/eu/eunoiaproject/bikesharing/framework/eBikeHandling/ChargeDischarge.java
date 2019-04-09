package eu.eunoiaproject.bikesharing.framework.eBikeHandling;

import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;
import org.matsim.core.mobsim.qsim.agents.BikesharingPersonDriverAgentImpl;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikesE;

import org.matsim.core.mobsim.qsim.agents.BSRunner;

public class ChargeDischarge {
	
	/***************************************************************************/
	/**This method does the charging of the e-bike
	 * and it returns the actual value of the charging state 
	 * it can return a negative value, but the soc of the bikes
	 * will be at maximum set to zero**/
	public static double actSoc (
			BikesE bike, 
			double t_1,
			BasicPlanAgentImpl agent)//time where the bike is taken
	/***************************************************************************/
	{
	 	double b = bike.getBatteryChargeCapacity();		//Ah of battery at 100% charging-state
	 	double v = bike.getVoltage();					//Voltage V
	 	double c = b/v; 								//Capacity
	 	double t_0 = bike.getTime();					//Last timestamp of soc actualization
	 	boolean status = bike.getInfoIfBikeInStation(); //if true - bike is in station - if false bike is on the road
	 	double km_full_empty = bike.getKmFullToEmpty();	//distance of battery from full to empty in km
	 	double resistance = bike.getOhmicResistance();	//ohmic resistance
	 	double soc = bike.getStateOfCharge ();			//state of charge of the battery
	 	
		BSRunner bsr = new BSRunner();
		if (agent != null)
		{
			bsr.planComparison(agent); //when relocation, agent equals null
		}
	 	//t0 = last time of actualization, t1 = actual time
	 	if (t_1 - t_0 < 0)
	 	{
	 		System.out.println("ERROR: something wrong with the storage of the last actualization time");
	 		System.exit(0);
	 	}
	 	//###################################################
		//---- charging is implemented as exponential function (typical charging curve)
		if (status) // bicycle is in station --> status = 1
		{
	
			double duration = -(resistance * c * (Math.log(1-(soc)))) 	//duration from actual soc to zero load stage in h
						          + (t_1 - t_0)/3600; 					//duration from last actualization to actual time stamp in h
				
			//soc = (1 - (Math.pow(Math.exp(1),((-1/(resistance*c))* duration)))); //charging curve
			soc = 1 - Math.exp(-duration/(resistance*c));
			if (soc > 0.995)
			{
				soc = 1;
			}
		}
		//###################################################
		//---- discharging is implemented as linear loss ----
		else if (!(status)) // bicycle is not in station --> status = 0
		{
			double duration = (t_1 - t_0)/3600; 		//duration of discharge in h
			double dur_full_empty_h = km_full_empty/15; //1. assumption = 15 km/h average speed, 2. assumption linear discharge
			soc = (soc  - (duration/dur_full_empty_h)); //reduce soc in a linear way
			if ((soc < 0) && (agent != null))
			{
//				BikesharingPersonDriverAgentImpl agent2 = new BikesharingPersonDriverAgentImpl(agent);
//				int index = agent2.getCurrentPlanElementIndex(agent) ;
				int index = agent.getCurrentPlan().getPlanElements().indexOf( agent.getCurrentPlanElement() ) ;
				Leg legTest = (Leg)agent.getNextPlanElement(); // XXXXX Hebenstreit: ist das beides das Gleiche?
				Leg leg = (Leg)agent.getCurrentPlan().getPlanElements().get(index+1);
				System.out.println("No battery support for " + agent.getPerson().getId() + " since: " + (-soc * dur_full_empty_h) + " hours (dur_full_empty: " + dur_full_empty_h + ") (leg travelTime: " + leg.getTravelTime());
				leg.setTravelTime(leg.getTravelTime()+(-soc * dur_full_empty_h*3600));
				leg.getRoute().setTravelTime(leg.getTravelTime()+(-soc * dur_full_empty_h*3)); 
				soc = 0;
			}
			t_0 = t_1; //update last actualization time 
		}
		else 
		{
			System.out.println("ERROR: status neither *charge* nor *discharge*");
			System.exit(0);
		}
		
	if (soc > 0)
	{
		bike.setStateOfCharge(soc);
	}
	else
	{
		bike.setStateOfCharge(0);
	}
	bike.setInfoIfBikeInStation(true);
	int newTime = (int)t_1;
	bike.setTime(newTime);	
	if (agent != null)
	{
		bsr.planComparison(agent);
	}
	return soc;
	}
	

}
