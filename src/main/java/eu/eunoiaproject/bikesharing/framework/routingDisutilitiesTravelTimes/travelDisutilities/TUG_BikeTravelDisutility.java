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


import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BikeTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.scoring.functions.CharyparNagelScoringParameters;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

/**
* in this class disutility per link is calculated for routing depending on four parameter groups:
* gradient, safety, comfort, environment, additional
* multiplied by length and divided through speed
* 
* @author hebenstreit
*/


public class TUG_BikeTravelDisutility implements TravelDisutility
{
	//private final static Logger log = Logger.getLogger(TUG_BikeTravelDisutility.class);
	private ObjectAttributes bikeLinkAttributes;
	private ObjectAttributes personAttributes;
	private BicycleConfigGroup bikeConfigGroup;
	private Config config2;
    
	/***************************************************************************/
	public TUG_BikeTravelDisutility(
		  BicycleConfigGroup config, Config config2)
	/***************************************************************************/
	{
			IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(config, false);
			bikeLinkAttributes = bts.getBikeLinkAttributes();
			personAttributes = bts.getPersonAttributes();
			this.bikeConfigGroup = config;
			this.config2 = config2;
	}


/***************************************************************************/
   @Override
   public double getLinkTravelDisutility(
		   Link link, double time, Person person, Vehicle vehicle) 
   /***************************************************************************/
   {
	   
	   	double du_Type = TravelDisutilityHelper.getDisutilityForLinkAttributes(
	   				bikeLinkAttributes, personAttributes,bikeConfigGroup,link,person,"bike");
		PlanCalcScoreConfigGroup cn = (PlanCalcScoreConfigGroup) config2.getModule("planCalcScore");
		ModeParams modeParams = cn.getOrCreateModeParams(TransportMode.bike);
		double utilDist = modeParams.getMarginalUtilityOfDistance();
		double utilTime = modeParams.getMarginalUtilityOfTraveling()/3600;
		TUG_BikeTravelTime btt = new TUG_BikeTravelTime(bikeConfigGroup);
		double linkTravelTimeBikes = btt.getLinkTravelTime(link, time, person, vehicle);

		 if (du_Type < 0.1) 
		 {
			 du_Type = 0.1 * link.getLength();;
		 } //Falls ALLES NULL wird über die Länge geroutet!
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
