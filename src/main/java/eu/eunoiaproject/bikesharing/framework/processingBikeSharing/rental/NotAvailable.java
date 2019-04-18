/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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
package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;

public class NotAvailable {
	
	/***************************************************
	 * This method implements how agents act, if no bikeParking is
	 * available at the bike-sharing station
	 * a) search a new Station within near distance
	 * b) wait a certain time - or until a bike is available
	 * c) abort **/
	public static String randomChoiceFirstly( boolean wantsToTakeBike, 
			BikeSharingFacility bsFac, 
			double likely, 
			BikeSharingFacility stationOld)
	/***************************************************************************/
	{
		double random = Math.random(); //chooses a number between 0 and 1
		String whatToChoose = "";
		
		if (bsFac != null)
		{
			if (likely > 0.66)
			{
				if (wantsToTakeBike == true) //taking a bike
				{
					if (random <= 0.4)
					{
						whatToChoose = "chooseNewStation";
					}
				
					else if (random <= 0.70)
					{
						whatToChoose = "wait";
					}
				
					else
					{
						whatToChoose = "changeMode";
					}
				}
		
				if (wantsToTakeBike == false) // taking a bike
				{
					if (random <= 0.6)
					{
						whatToChoose = "chooseNewStation";
					}
					
					else if (random <= 1)
					{
						whatToChoose = "wait";
					}
				}
			}
			
			else
			{
				if (wantsToTakeBike == true)
				{
					if (random <= 0.5)
					{
						whatToChoose = "wait";
					}
					else
					{
						whatToChoose = "changeMode";
					}
				}
				if (wantsToTakeBike == false)
				{
					whatToChoose = "wait";
				}
			}
		}
		
		else if (bsFac == null) //no new station was found
		{
			if (wantsToTakeBike == true)
			{
				if (random < 0.5)
				{
					whatToChoose = "wait";
				}
				
				else
				{
					whatToChoose = "changeMode";
				}
					
			}	
			
			if (wantsToTakeBike == false)
			{
				whatToChoose = "wait";
			}
		}

		/*if (bsFac != null)
		{
			if (whatToChoose.equals("wait") || whatToChoose.equals("changeMode"))
			{
				System.out.println(whatToChoose + " _ " + stationOld.getId().toString());
			}
			else
				System.out.println(whatToChoose + " _ " + bsFac.getId().toString());
		}
		
		else 
		System.out.println(whatToChoose + " _ " + stationOld);*/
			
		return whatToChoose;
	}
}