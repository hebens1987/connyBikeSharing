
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
 * *********************************************************************** */package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes;


import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.TravelTime;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;

public class TUG_WalkTravelTime implements TravelTime {

	@Inject
	BicycleConfigGroup bikeConfigGroup;   
	ObjectAttributes bikeLinkAttributes;

	private final static Logger log = Logger.getLogger(TUG_WalkTravelTime.class);

	/***************************************************************************/
	@Inject
	@Singleton
	public
	TUG_WalkTravelTime(BicycleConfigGroup bikeConfigGroup) 
	/***************************************************************************/
	{
		IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup, false);
		bikeLinkAttributes = bts.getBikeLinkAttributes();
	}
	
	/***************************************************************************/
	@Override
	public double getLinkTravelTime(
			Link link, double time, Person person, Vehicle vehicle) 
	/***************************************************************************/
	{
		// bikeSpeed in km/h
		double lenOfLink = link.getLength();
		double speed = 1.3;
		if (bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed")!= null)
		{
			double speedInfra = (double) bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed");
			if (speed > speedInfra)
			{
				speed = speedInfra;
			}
		}
		double traveltime = lenOfLink / speed; //Hebenstreit: uebernehmen aus Input-File!?
		return traveltime;
	}

}


