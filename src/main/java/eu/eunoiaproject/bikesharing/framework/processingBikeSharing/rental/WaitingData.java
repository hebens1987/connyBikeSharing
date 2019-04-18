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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.qsim.agents.BasicPlanAgentImpl;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;


public class WaitingData
{
	public BasicPlanAgentImpl bpAgent;
	public double time;
	public Id<Person> personId;
	public BikeSharingFacility bsFac;

	public WaitingData()
	{}
}