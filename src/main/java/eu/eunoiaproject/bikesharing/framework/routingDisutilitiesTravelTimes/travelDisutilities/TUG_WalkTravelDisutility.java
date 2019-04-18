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
package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;

/* 
* 
* in this class disutility per link is calculated for routing:
*/ 


public class TUG_WalkTravelDisutility implements TravelDisutility {

	int linkCount=0;
	double individualDis;   
	private final static Logger log = Logger.getLogger(TUG_WalkTravelDisutility.class);

	ObjectAttributes personAttributes;
	BicycleConfigGroup bikeConfigGroup;  
	PlanCalcScoreConfigGroup cnScoringGroup; // Hebenstreit: ist das hier das Richtige?
    
	/***************************************************************************/
	public TUG_WalkTravelDisutility(
			BicycleConfigGroup bikeConfigGroup) 
	/***************************************************************************/
	{	
			IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup, false);
			personAttributes = bts.getPersonAttributes();
	}
    
	/***************************************************************************/
	@Override
	public double getLinkTravelDisutility(
			Link link, double time, Person person, Vehicle vehicle) 
	/***************************************************************************/
	{                     
		if (link.getAllowedModes().contains(TransportMode.walk))
		{
			double disutility = link.getLength();
			return disutility;
		}
		else
			return Double.POSITIVE_INFINITY;
	}

	/***************************************************************************/
	@Override
	public double getLinkMinimumTravelDisutility(Link link) 
	/***************************************************************************/
	{
		return 0;
	}
}
