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

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.utils.objectattributes.ObjectAttributes;

public class TravelTimeHelper 
{

	private final static Logger log = Logger.getLogger(TravelTimeHelper.class);
	
	public double travelTimeGetter (Person person, Link link, 
			ObjectAttributes personAttributes, ObjectAttributes bikeLinkAttributes,
			double lowestSpeed, double highestSpeed, double speedReduction)
	{
		boolean isFastCycleLane = false;
		double v = 0;
		// bikeSpeed in km/h
		double lenOfLink = link.getLength();
		//System.out.println(link.getId().toString());
		double bikeSpeedOfPerson = 0;
		if (personAttributes.getAttribute(person.getId().toString(), "bikeSpeed") == null)
		{
			bikeSpeedOfPerson = (highestSpeed + lowestSpeed)/2;
			log.warn("For Person with ID: " + person.getId() + " no specific input personal speed was allocated");
		}
		else
		{
			bikeSpeedOfPerson = ((double) personAttributes.getAttribute(person.getId().toString(), "bikeSpeed")) - speedReduction; // m/s
			
			if (bikeSpeedOfPerson > highestSpeed) {bikeSpeedOfPerson = highestSpeed;}
			else if (bikeSpeedOfPerson < lowestSpeed ) {bikeSpeedOfPerson = lowestSpeed;}
		}
		
		double bikeSpeedOfInfrastructure = 0;
		if (bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed") == null)
		{
			if (!(link.getAllowedModes().contains(TransportMode.bike)))
			{
				if (link.getAllowedModes().contains(TransportMode.walk))
				{
					bikeSpeedOfInfrastructure = 1.0; //TODO: Hebenstreit
					//For Link with ID: " + link.getId() + " using a walk link with very slow speed
				}
				
				else 
				{ 
					v = 0.0000000001;
					//For Link with ID: " + link.getId() + " using any other link than mode bike or walk
				}
			}
			else if (link.getAllowedModes().contains(TransportMode.bike))
			{
				bikeSpeedOfInfrastructure = 5.0; //18 km/h
				log.warn("For Link with ID: " + link.getId() + " no specific input link was allocated, using a max value of 5 m/s");
			}
		}
		
		else
		{
			isFastCycleLane = (boolean) bikeLinkAttributes.getAttribute(link.getId().toString(), "interaction");
			bikeSpeedOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed")); // m/s
		}


    	if(bikeSpeedOfInfrastructure <= bikeSpeedOfPerson)
    	{
    		v=bikeSpeedOfInfrastructure;
    	}
    	else
    	{
    		v=bikeSpeedOfPerson;
    	}
    	
    	if (isFastCycleLane)
    	{
    		v = v + 1.5;
    		if ( v < 4 ) v = 4;
    		else if (v > 8) v = 8;
    	}
    	
    	double traveltime = lenOfLink / v;
		return traveltime;
	}
}
