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

import java.util.List;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.utils.objectattributes.ObjectAttributes;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.IKK_ObjectAttributesSingleton;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;



public class BSAttribsAgent
{
	BSAttribsAgent ()
	{
	}
	

	public static synchronized BSAtt getPersonAttributes(Person person, Scenario scenario)
	{
		final BicycleConfigGroup bikeConfigGroup= (BicycleConfigGroup)scenario.getConfig().getModule(BicycleConfigGroup.GROUP_NAME);
		IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup);
		ObjectAttributes personAttributes = bts.getPersonAttributes();
		int routingType = 0;
		if (personAttributes.getAttribute(person.getId().toString(), "routingType") != null)
		{
			routingType = ((int) personAttributes.getAttribute(person.getId().toString(), "routingType"));
		
		}

		BSAttribs att = new BSAttribs();
		List<BSAtt> attList = att.getBSAttribs(scenario);
		BSAtt bs = new BSAtt();
			
			
		for (int i = 0; i < attList.size(); i++)
		{
			if (attList.get(i).userGroupType == routingType)
			{
				bs.maxBSTripLength = attList.get(i).maxBSTripLength;
				bs.maxSearchRadius = attList.get(i).maxSearchRadius;
				bs.ptSearchRadius = attList.get(i).ptSearchRadius;
				bs.searchRadius = attList.get(i).searchRadius;
				bs.userGroupType = attList.get(i).userGroupType;
			}
		}
	
	return bs;
	}
}
