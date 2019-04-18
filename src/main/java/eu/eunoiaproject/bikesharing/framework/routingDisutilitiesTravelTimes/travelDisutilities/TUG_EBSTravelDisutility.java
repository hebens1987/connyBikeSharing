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


import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_EBSTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

/**
* in this class disutility per link is calculated for routing depending on four parameter groups:
* gradient, safety, comfort, environment, additional
* multiplied by length and divided through speed
* 
* @author hebenstreit
*/


public class TUG_EBSTravelDisutility implements TravelDisutility
{
	//private final static Logger log = Logger.getLogger(TUG_BikeTravelDisutility.class);
	ObjectAttributes bikeLinkAttributes;
	ObjectAttributes usergroupAttributes;
	ObjectAttributes personAttributes;
	BicycleConfigGroup bikeConfigGroup;

	/***************************************************************************/
	public TUG_EBSTravelDisutility(
		  BicycleConfigGroup bikeConfigGroup )
	/***************************************************************************/
	{
			IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup,false);
			bikeLinkAttributes = bts.getBikeLinkAttributes();
			usergroupAttributes = bts.getUsergroupAttributes();
			personAttributes = bts.getPersonAttributes();
			this.bikeConfigGroup = bikeConfigGroup;
	}

   /***************************************************************************/
   @Override
   public double getLinkTravelDisutility(
		   Link link, double time, Person person, Vehicle vehicle) 
   /***************************************************************************/
   {
	   	  double du_Type = TravelDisutilityHelper.getDisutilityForLinkAttributes(
 				bikeLinkAttributes, personAttributes,bikeConfigGroup,link,person);  
	   
		   TUG_EBSTravelTime btt = new TUG_EBSTravelTime(bikeConfigGroup);
		   double linkTravelTimeBikes = btt.getLinkTravelTime(link, time, person, vehicle);
			
		  // double lenOfLink = link.getLength();
		  // travelTime = lenOfLink/v; 

		   if (du_Type < 0.1) { du_Type = 0.1;} //Falls ALLES NULL wird über die Länge geroutet!
		   double disutilityBikesPerLink = linkTravelTimeBikes * du_Type;
   
		   return disutilityBikesPerLink ;       
   }

   /***************************************************************************/
   @Override
   public double getLinkMinimumTravelDisutility(Link link) 
   /***************************************************************************/
   {
	   return 0.1;
   }

}
