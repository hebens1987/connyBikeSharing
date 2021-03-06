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
package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.stationChoice.CalcProbability.Probability;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.EBikeSharingConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.StationAndType;

/**
 * 
 *hier noch miteinbeziehen 
 *ob Station A zum Abreisezeitpunkt verfügbare Räder
 *ob Station B zum Abreisezeitpunkt verfügbare Stellplätze
 *
 * 
 *
 * @author Hebenstreit
 */
public class CalcProbability
{
	Random random;
	Scenario scenario;

	static final Logger log = Logger.getLogger(BikeSharingStationChoice.class);
	
	/***************************************************************************/
	public CalcProbability(
			Scenario scenario) 
	/***************************************************************************/
	{	
		this.scenario = scenario;
	}

	public class Probability
	{
		public BikeSharingFacility startStationE;
		public BikeSharingFacility endStationE;
		public BikeSharingFacility startStationC;
		public BikeSharingFacility endStationC;
		public double probabilityE;
		public double probabilityC;
	}
	
	/***************************************************************************/
	public Probability calculation (ProbabilityChoice choice)
	/***************************************************************************/
	{
		
		Probability prob = new Probability();
		prob.probabilityC = choice.probabilityC;
		prob.probabilityE = choice.probabilityE;
		
		return prob;
	}
	
	/***************************************************************************/
	public double getProbabilityForStationChangeTake (
			final BikeSharingFacility facilityStart, 
			final BikeSharingFacility facilityEnd, 
			double alpha,
			double gamma)
	/***************************************************************************/
	{
		
		double probability = 0;
		
		double parkingsStart = facilityStart.getFreeParkingSlots();
		double bikesStart = facilityStart.getNumberOfAvailableBikes();
		
		double parkingsEnd = facilityEnd.getFreeParkingSlots();
		double bikesEnd= facilityEnd.getNumberOfAvailableBikes();
		
		
		double probStart = Math.exp(bikesStart)/(Math.exp(bikesStart)+Math.exp(parkingsStart)*alpha);
		double probEnd = Math.exp(parkingsEnd)/(Math.exp(parkingsEnd)+Math.exp(bikesEnd)*gamma);
		
		probability = probStart * probEnd; //as probStart ∧ ProbEnd is valid
		// it is like to cast a dice
		// if one dice is cast, and needs to have a number of pips 6, the probability is 1/6
		// if two dices are cast, and both needs to have a number of pips 6
		// the probability is 1/6 ∧ 1/6, which is 1/6 * 1/6, which results in 1/36.
		return probability;

	}
	
	/***************************************************************************/
	public double getProbabilityForStationChangeReturn (
			final BikeSharingFacility facilityEnd, 
			double alpha,
			double gamma)
	/***************************************************************************/
	{
		double parkingsEnd = facilityEnd.getFreeParkingSlots();
		double bikesEnd= facilityEnd.getNumberOfAvailableBikes();
		
		double probEnd = Math.exp(parkingsEnd)/(Math.exp(parkingsEnd)+Math.exp(bikesEnd));

		return probEnd;

	}
	

	/***************************************************************************/
	public Probability getProbability (
			StationAndType[] sat,
			double searchRadius,
			BikeSharingFacilities c_bs,
			BikeSharingFacilities e_bs,
			Scenario scenario)
	/***************************************************************************/
	{
		Probability outcome = new Probability();
		if (sat != null && sat[0]!= null && sat[1]!= null)
		{		
			final EBikeSharingConfigGroup ebConfig = (EBikeSharingConfigGroup)scenario.getConfig().getModule(EBikeSharingConfigGroup.GROUP_NAME);
			String alphaS = ebConfig.getValue("probabilityTake");
			String gammaS = ebConfig.getValue("probabilityReturn");
			if (alphaS == null) 
			{ 
				System.out.println("No alphaS Value (probabilityTake) for probability Calculation - setting it to default 0.33");
				alphaS = "0.33";
			}
			
			if (gammaS == null) 
			{ 
				System.out.println("No gammaS Value (probabilityReturn)for Probability Calculation - setting it to default 0.33");
				alphaS = "0.33";
			}
			
			double alpha = Double.parseDouble(alphaS);
			double gamma = Double.parseDouble(gammaS);
			
			ProbabilityChoice start = probabilityGetStationOrig(sat[0].station.getCoord(),
					searchRadius,
					false,
					c_bs,e_bs,
					alpha,
					gamma);
			ProbabilityChoice end = probabilityGetStationOrig(sat[1].station.getCoord(),
					searchRadius,
					false,
					c_bs,e_bs,
					alpha,
					gamma);
			
			outcome.endStationC = end.stationC;
			outcome.endStationE = end.stationE;
			outcome.startStationC = start.stationC;
			outcome.startStationE = start.stationE;
			outcome.probabilityC = start.probabilityC * end.probabilityC; 
			outcome.probabilityE = start.probabilityE * end.probabilityE;
		}
		else{
			outcome.probabilityC = 0;
			outcome.endStationC = null;
			outcome.endStationE = null;
			outcome.probabilityE = 0;
			outcome.startStationC = null;
			outcome.startStationE = null;
		}
		return outcome;
		
		
		
	}
	

	/***************************************************************************/
	public  ProbabilityChoice  probabilityGetStationOrig(
			final Coord facility,
			final double maxSearchRadius,
			boolean bikeToTake,
			BikeSharingFacilities bs,
			BikeSharingFacilities e_bs,
			double alpha,
			double gamma) 
	/***************************************************************************/
	{
		double boxesE = 0;
		double boxesC = 0;
		
		if (facility == null) return null;

		BikeSharingFacility bsFacC = null;
		BikeSharingFacility bsFacE  = null;
		double resultEreturn = 0;
		double resultEtake = 0;
		double resultCreturn = 0;
		double resultCtake = 0;

		
			
		ProbabilityChoice choice = new ProbabilityChoice();
		
		Collection<BikeSharingFacility> stationsInRadiusC = 
				bs.getCurrentQuadTree().getDisk(
						facility.getX(),
						facility.getY(),
						maxSearchRadius);
		
		Collection<BikeSharingFacility> stationsInRadiusE = 
				e_bs.getCurrentQuadTree().getDisk(
						facility.getX(),
						facility.getY(),
						maxSearchRadius);
		if (stationsInRadiusC.size() > 0)
		{
			bsFacC = bs.getCurrentQuadTree().getClosest(
				facility.getX(),
				facility.getY());
		}
		if (stationsInRadiusE.size() > 0)
		{
			bsFacE = e_bs.getCurrentQuadTree().getClosest(
				facility.getX(),
				facility.getY());
		}
			
			int numberOfBikesInRadiusE = 0;
			int numberOfBikesInRadiusC = 0;
			
			if (stationsInRadiusC!=null) {numberOfBikesInRadiusC = stationsInRadiusC.size();}
			
			if (stationsInRadiusC!=null)
			{
				List<BikeSharingFacility> bsStations =  new ArrayList<BikeSharingFacility>(stationsInRadiusC);
				
				for (int i = 0; i < numberOfBikesInRadiusC; i++)
				{
					if (bikeToTake)
					{
						double bikes = 0;
						bikes = bsStations.get(i).getNumberOfAvailableBikes();
						boxesC = bsStations.get(i).getTotalBikeNumber();
						
						double resultTemp = resultCtake;
						resultCtake = Math.exp(bikes)/(Math.exp(bikes)+ (Math.exp(boxesC-bikes)* alpha));
						
						if (resultTemp < resultCtake)
						{
							bsFacC = bsStations.get(i);
						}		
					}
					else
					{
						int parking = 0;
						parking = bsStations.get(i).getFreeParkingSlots();
						boxesC = bsStations.get(i).getTotalBikeNumber();
						
						double resultTemp = resultCreturn;
						resultCreturn = Math.exp(parking)/(Math.exp(parking)+ (Math.exp(boxesC-parking)* gamma));
						
						if (resultTemp < resultCreturn)
						{
							bsFacC = bsStations.get(i);
						}
					}
				}
				
				if (bikeToTake)
					choice.probabilityC = resultCtake;
				else
					choice.probabilityC = resultCreturn;
				
				choice.stationC = bsFacC;
				

			}
			
			if (stationsInRadiusE!=null) {numberOfBikesInRadiusE = stationsInRadiusE.size();}
			
			if (stationsInRadiusE!=null)
			{
				List<BikeSharingFacility> bsStations =  new ArrayList<BikeSharingFacility>(stationsInRadiusE);
				
				for (int i = 0; i < numberOfBikesInRadiusE; i++)
				{
					if (bikeToTake)
					{
						int bikes = 0;
						bikes = bsStations.get(i).getNumberOfAvailableBikes();
						boxesE = bsStations.get(i).getTotalBikeNumber();
						
						double resultTemp = resultEtake;
						resultEtake = Math.exp(bikes)/(Math.exp(bikes)+ (Math.exp(boxesE-bikes)* alpha));
						
						if (resultTemp < resultEtake)
						{
							bsFacE = bsStations.get(i);
						}		
					}
					else
					{
						int parking = 0;
						parking = bsStations.get(i).getFreeParkingSlots();
						boxesE = bsStations.get(i).getTotalBikeNumber();
						
						double resultTemp = resultEreturn;
						resultEreturn = Math.exp(parking)/(Math.exp(parking)+ (Math.exp(boxesE-parking)* alpha));
						
						if (resultTemp < resultEreturn)
						{
							bsFacE = bsStations.get(i);
						}
					}
				}
				
				if (bikeToTake)
					choice.probabilityE = resultEtake;
				else
					choice.probabilityE = resultEreturn;

				choice.stationE = bsFacE;
				

			}
			
			choice.nearestStationC = bsFacC;
			choice.nearestStationE = bsFacE;
		
		return choice;
	}
}


