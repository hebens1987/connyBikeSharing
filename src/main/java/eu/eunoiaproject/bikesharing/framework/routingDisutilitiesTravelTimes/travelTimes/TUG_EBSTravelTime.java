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
package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.TravelTime;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;
/**
* in this class traveltime is calculated depending on the following parameters:
* speed of infrastructure
* desired speed of a person
* @author hebenstreit
*/
public class TUG_EBSTravelTime implements TravelTime 
{
	//@Inject
	//IKK_BikeConfigGroup bikeConfigGroup;   
	ObjectAttributes bikeLinkAttributes;
	ObjectAttributes usergroupAttributes;
	ObjectAttributes personAttributes;
	
	private final static Logger log = Logger.getLogger(TUG_BSTravelTime.class);

	/***************************************************************************/
	@Inject
	@Singleton
	public
	TUG_EBSTravelTime(BicycleConfigGroup bikeConfigGroup)
	/***************************************************************************/
	{	 
		IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup,false);
		bikeLinkAttributes = bts.getBikeLinkAttributes();
		personAttributes = bts.getPersonAttributes();
	}
	
	/***************************************************************************/
	@Override
	public double getLinkTravelTime(
			Link link, double time, Person person, Vehicle vehicle) 
	/***************************************************************************/
	{
		TravelTimeHelper tth = new TravelTimeHelper();
		return tth.travelTimeGetter(person, link, personAttributes, bikeLinkAttributes, 15/3.6, 25/3.6, -2/3.6);
		
	}
}

