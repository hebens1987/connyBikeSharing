/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

package eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing;

//import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;

public class BikeAgent
{
	Id<Person> personId = null;
	Bikes bike = null;
	BikesE ebike = null;
	
	//private static final Logger log = Logger.getLogger(BikeAgent.class);

	public static BikeAgent createBikeAgent(Id<Person> p, Bikes bike, BikesE ebike) {
        return new BikeAgent(p, bike, ebike);
	}
	
	public BikeAgent(
			final Id<Person> p, 
			Bikes bike, 
			BikesE ebike) {
		this.personId = p;
		this.bike = bike;
		this.ebike = ebike;

		
	}
	
	public BikeAgent() 
	{
	}
	
	public void setPersonId (Id<Person> personId)
	{
		this.personId = personId;
	}
	
	public Id<Person> getPersonId ()
	{
		return personId;
	}
	
	public void setBike (Bikes bike)
	{
		this.bike = bike;
	}
	
	public Bikes getBike ()
	{
		return bike;
	}
	
	public void setBikeE (BikesE ebike)
	{
		this.ebike = ebike;
	}
	
	public BikesE getBikeE ()
	{
		return ebike;
	}
	
}

	