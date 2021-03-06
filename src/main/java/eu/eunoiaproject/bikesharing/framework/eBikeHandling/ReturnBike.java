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

import org.matsim.api.core.v01.Id;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;

import eu.eunoiaproject.bikesharing.framework.events.EBikeBatteryGoneEmpty;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.Bikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikesE;

import org.matsim.core.mobsim.qsim.agents.BSRunner;


public class ReturnBike {
	
	
	public ReturnBike() 
	/***************************************************************************/
			{
			}
	
	/***************************************************************************/
	public static boolean returnBikeE(
			BikeSharingFacility station, BikesE ebike, double actualTime, 
			BasicPlanAgentImpl agent, BikeSharingFacilities facilities)
	/***************************************************************************/
	{
		BSRunner runner = new BSRunner();
		if (agent != null)
		{
			runner.planComparison(agent);
		}
		
		if (station.getFreeParkingSlots() == 0)
		{
			TakeABike.checkIfRelocationNecessary(station, actualTime, facilities);
			return false; //bike could not be returned
		}
		else
		{
			if (ebike != null)
			{
				String idOfBikeToReturn = ebike.getBikeId().toString();
				// returning a bike means +1 for available and -1 for empty
				String[] bikesAtStation = station.getCycles_in_station();
				String [] addBikeToStation;
				if (bikesAtStation == null)
				{
					addBikeToStation = new String [1];
					addBikeToStation[0] = ebike.getId().toString();
					station.setCycles_in_station(addBikeToStation);
				}
				else
				{
					addBikeToStation = new String [bikesAtStation.length+1];
				
			
					//adding the bike which was returned to the station
					for (int i = 0; i < addBikeToStation.length; i++)
					{
						if (i < bikesAtStation.length)
						{
							addBikeToStation[i] = bikesAtStation[i];
						}	
				
						else
						{
							addBikeToStation [i] = idOfBikeToReturn;
						}						
					}
					station.setCycles_in_station(addBikeToStation);
				}
			
				// for e-bikes also discharging must be considered
				double soc = ChargeDischarge.actSoc(ebike, actualTime, agent);
				if (agent != null)
				{
					if (soc < 0) 
					{
						agent.getEvents().processEvent(new EBikeBatteryGoneEmpty(actualTime, ebike, agent.getPerson()));
						
					}
				}
				
				ebike.setInfoIfBikeInStation(true);
				ebike.setInStation(Id.create(station.getStationId().toString(), BikeSharingFacility.class));
				station.setNumberOfAvailableBikes(station.getNumberOfAvailableBikes()+1);
				station.setFreeParkingSlots(station.getFreeParkingSlots()-1);
				ebike.setTime(actualTime);
				
				TakeABike.checkIfRelocationNecessary(station, actualTime, facilities);
				
				return true; //bike could be returned
			}
			TakeABike.checkIfRelocationNecessary(station, actualTime, facilities);
			return false; //bike could be returned
		}

	}
	
	/***************************************************************************/
	public static boolean returnBikeC(
			BikeSharingFacility station, Bikes bike, 
			double actualTime, BikeSharingFacilities facilities)
	/***************************************************************************/
	{
		
		if (station.getFreeParkingSlots() == 0) 
		{
			TakeABike.checkIfRelocationNecessary(station, actualTime, facilities);
			return false; //bike could not be returned
		}
		else
		{
			String idOfBikeToReturn = bike.getBikeId().toString();
			if (station.getCycles_in_station() == null)
			{
				String [] addBikeToStation = new String[1];
				addBikeToStation[0] = idOfBikeToReturn;
				station.setCycles_in_station(addBikeToStation);
			}
			else
			{
				String[] bikesAtStation = station.getCycles_in_station();
				String [] addBikeToStation = new String [bikesAtStation.length+1];
			
				//adding the bike which was returned to the station
				for (int i = 0; i < bikesAtStation.length+1; i++)
				{
					if (i < bikesAtStation.length)
					{
						addBikeToStation[i] = bikesAtStation[i];
						//writes the already existing bikes into String Array
					}	
					else
					{
						addBikeToStation [i] = idOfBikeToReturn;
						// writes the returned Bike into the String Array
					}						
				}
			station.setCycles_in_station(addBikeToStation);
			}
		station.setNumberOfAvailableBikes(station.getNumberOfAvailableBikes()+1);
		station.setFreeParkingSlots(station.getFreeParkingSlots()-1);
		bike.setInfoIfBikeInStation(true);
		bike.setInStation(Id.create(station.getStationId().toString(), BikeSharingFacility.class));
		bike.setTime(actualTime);
		TakeABike.checkIfRelocationNecessary(station, actualTime, facilities);
		return true;
	
		}
	}
}

