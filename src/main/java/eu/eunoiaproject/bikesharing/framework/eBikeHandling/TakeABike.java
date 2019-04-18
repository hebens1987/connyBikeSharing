/* *********************************************************************** *
 * project: org.matsim.													   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,     *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

/**
 * @author hebens
 */

package eu.eunoiaproject.bikesharing.framework.eBikeHandling;


import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;
import org.matsim.vehicles.Vehicle;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.Bikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikesE;

import org.matsim.core.mobsim.qsim.agents.BSRunner;


public class TakeABike 
{
	private final static Logger log = Logger.getLogger(TakeABike.class);

	public TakeABike(BikeSharingFacilities facilities) 
	/***************************************************************************/
	{
	}
	
	/***************************************************************************/
	public static void checkIfRelocationNecessary(BikeSharingFacility station, double now, BikeSharingFacilities facilities)
	/***************************************************************************/
	{
		if (facilities.station_in_need_of_relocation == null)
		{ 
			facilities.station_in_need_of_relocation = new ArrayList <BikeSharingFacility>();
		}

		if (station.getFreeParkingSlots() <= 2)
		{
			if (!(facilities.station_in_need_of_relocation.contains(station)))
			{
					station.setTimeOfRelocation(now);
					facilities.station_in_need_of_relocation.add(station);
			}
		}
		
		else if (station.getNumberOfAvailableBikes() <= 2)
		{
			if (!(facilities.station_in_need_of_relocation.contains(station)))
			{
				station.setTimeOfRelocation(now);
				facilities.station_in_need_of_relocation.add(station);
			}
		}
	}
	
	/***************************************************************************/
	/** TakeEBike - if bike.getType.equals("e") == true                       **/
	public static BikesE takeEBike(
			BikeSharingFacility station, 
			Map<Id<Vehicle>,BikesE> allEbikes, 
			double time,
			BasicPlanAgentImpl agent,
			BikeSharingFacilities facilities,
			Leg leg)
	/***************************************************************************/
	{
		if (leg != null)
		{
			leg.setMode(EBConstants.BS_E_BIKE); //can be null when bikes are relocated
		}
		BSRunner runner = new BSRunner();
		if (agent != null)
		{
			runner.planComparison(agent);//as at relocation, there is no agent
		}
		String [][] cycleSOC;
		double stateOfCharge;
		Id<BikesE> idOfBikeToTake = null;;
		BikesE eBike = null;	
			
		//##############################################################
		if (station.getCycles_in_station() == null)
		{
			return null;
		}
		
		int statNum = station.getNumberOfAvailableBikes();
		int len = station.getCycles_in_station().length;
		
		if (statNum != len)
		{
			log.warn("Number of available bikes and number of Cycles in Station (" +station.getStationId() + ") not equal !!!" );
			System.exit(0);
			return null;
		}

		
		else if (station.getNumberOfAvailableBikes() == 1)
		{
			Id<BikesE> idToTest = Id.create(station.getCycles_in_station()[0], BikesE.class);
			eBike = allEbikes.get(idToTest);
		
			station.setCycles_in_station(null);
			eBike.setInfoIfBikeInStation(false);
			station.setNumberOfAvailableBikes(station.getNumberOfAvailableBikes()-1);
			station.setFreeParkingSlots(station.getFreeParkingSlots()+1);
			TakeABike.checkIfRelocationNecessary(station, time, facilities);
			return eBike;
		}
		
		else
		{
			cycleSOC = new String[len][2]; 	
			//initialize array of bikes at station
				
			for (int j = 0; j < len; j++) 	//getting all Bikes
			{		
				//get one bike which is currently at the Station (station-bikes)
				String [] bikesInStation = station.getCycles_in_station();

				//Call ChargeDischarge, to actualize all bikes in the specific Station!
				Id<Bikes> idToTest = Id.create(bikesInStation[j], Bikes.class);
				eBike = allEbikes.get(idToTest);
				if (eBike == null)
				{
					TakeABike.checkIfRelocationNecessary(station, time, facilities);
					return null;
				}
				ChargeDischarge.actSoc(eBike, time, agent); //renews SOC and time of last act
									
				//Search for the Bike with the highest SOC
				//remember the ID of it
				//delete ID from list and actualize Bike-ID-List for Station
				stateOfCharge = eBike.getStateOfCharge();
				cycleSOC[j][0] = Double.toString(stateOfCharge);
				cycleSOC[j][1] = eBike.getBikeId().toString();
			}
									
			if (cycleSOC[len-1][0] != null)
			{
				//BUBBLE-SORT for ARRAY [][] from biggest SOC to smallest
				double tempA;
				String tempB;
				for(int m=1; m<len; m++) 
				{
						for(int n=0; n<len-m; n++) 
						{
							if(Double.parseDouble(cycleSOC[n][0]) < Double.parseDouble(cycleSOC[n+1][0])) 
							{
								tempA= Double.parseDouble(cycleSOC[n][0]);
								tempB = cycleSOC[n][1];
								cycleSOC[n][0]=cycleSOC[n+1][0];
								cycleSOC[n][1]=cycleSOC[n+1][1];
								cycleSOC[n+1][0]=Double.toString(tempA);
								cycleSOC[n+1][1]=tempB;
							}
						}
				}
			}
				
				idOfBikeToTake = Id.create( cycleSOC[0][1], BikesE.class );
				//remember id of bike --> this bike will be taken
				
				String [] actBikeList = new String [len-1]; 
										
				//set id of taken Bike to null
				for (int e = 1; e < len; e++)
				{
					actBikeList[e-1] = cycleSOC[e][1];
				}
				
				eBike = allEbikes.get(idOfBikeToTake);
				
				if (eBike.getBatteryChargeCapacity() >= 0.1)
				{
					station.setCycles_in_station(actBikeList);
					eBike.setInfoIfBikeInStation(false);
					station.setNumberOfAvailableBikes(station.getNumberOfAvailableBikes()-1);
					station.setFreeParkingSlots(station.getFreeParkingSlots()+1);
					eBike.setTime(time);
					if (agent != null) 
					{
						runner.planComparison(agent); //as in relocation, agent equals null
					}
					TakeABike.checkIfRelocationNecessary(station, time, facilities);
					return eBike;
				}
				else
				{
					if (agent != null)
					{
						runner.planComparison(agent); //as at relocation, there is no agent
					}
					TakeABike.checkIfRelocationNecessary(station, time, facilities);
					return null;
				}
		}
	}
	
	
	/***************************************************************************/
	/** TakeBike - if bike.getType.equals("c") == true                        **/
	public static Bikes takeCBike(
			BikeSharingFacility station, 
			Map<Id<Vehicle>,Bikes> allBikes, 
			double time,
			BikeSharingFacilities facilities) //time, where the bike is)
	/***************************************************************************/
	{
		if (station.getNumberOfAvailableBikes() == 0)
		{
			TakeABike.checkIfRelocationNecessary(station, time, facilities);
			return null;
		}
		
		int cyclesInStat = station.getCycles_in_station().length;
		int statNum = station.getNumberOfAvailableBikes();
		
		if (statNum != cyclesInStat)
		{
			System.out.println("[Info 182:] Conventional Bicycle Number of Available Bikes and Cycles in Station not equal");
			System.out.println("[Info 183:]" + station.getStationId());
			System.exit(0);
		}

		else
		{
			Bikes bike;

			Id <Bikes> bikeIDStation = Id.create(station.getCycles_in_station()[0], Bikes.class);
			bike = allBikes.get(bikeIDStation);
			
			if (bike == null)
			{
				System.out.println("[Info:] Make sure the bikes in the bicycle file and bike-sharing-facilities file match ( Id: " + bikeIDStation + ")");
				System.exit(0);

			}

			String [] actBikeList = null;
			
			if (statNum > 0)
			{
				if (statNum == 1)
				{
					actBikeList = null;
				}
				else
				{
					actBikeList = new String [cyclesInStat-1]; 
				
					for (int i = 0; i < statNum-1; i++ )
					{
						actBikeList[i] = station.getCycles_in_station()[i+1];
					}
				}
			}

			station.setCycles_in_station(actBikeList);
			station.setNumberOfAvailableBikes(station.getNumberOfAvailableBikes()-1);
			station.setFreeParkingSlots(station.getFreeParkingSlots()+1);
			
			bike.setInfoIfBikeInStation(false);
			bike.setInStation(null);
			bike.setTime(time);
			TakeABike.checkIfRelocationNecessary(station, time, facilities);
			return bike;
		}
		TakeABike.checkIfRelocationNecessary(station, time, facilities);
		return null;
	}
}
