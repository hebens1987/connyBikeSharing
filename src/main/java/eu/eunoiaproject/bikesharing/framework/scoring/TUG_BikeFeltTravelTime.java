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
package eu.eunoiaproject.bikesharing.framework.scoring;


import eu.eunoiaproject.bikesharing.framework.EBConstants;

import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BSTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BikeTravelTime;
import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_EBSTravelTime;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.TravelTime;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

//import org.apache.log4j.Logger;

/**
* in this class the felt travel time - used for scoring is calculated
* travel time gets increased if poor cycle conditions are existing on a link
* tavel time gets decreased if good cycle conditions are existing on a link
* 
* @autor hebenstreit
*/


class TUG_BikeFeltTravelTime implements TravelTime
{
	//private final static Logger log = Logger.getLogger(TUG_BikeFeltTravelTime.class);
	private ObjectAttributes bikeLinkAttributes;
	private ObjectAttributes personAttributes;
	private BicycleConfigGroup bikeConfigGroup;

	
	/***************************************************************************/
	public TUG_BikeFeltTravelTime(
			BicycleConfigGroup bikeConfigGroup) 
	/***************************************************************************/
	{
			IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup, false);
			bikeLinkAttributes = bts.getBikeLinkAttributes();
		personAttributes = bts.getPersonAttributes();
			this.bikeConfigGroup = bikeConfigGroup;
	}
	
	/***************************************************************************/
	double [] getLinkTravelDisutility(
		  Link link, double time, Person person, Vehicle vehicle, String mode, double travelDistance)
	/***************************************************************************/
	{   
		boolean isFastCycleLane = false;	
		int routingType = -1;
		   		TravelTime tt = null;
		   		
		   		if (mode.equals(EBConstants.BS_E_BIKE))
		   		{
		   			tt = new TUG_EBSTravelTime(bikeConfigGroup);
		   		}
		   		else if ((mode.equals(EBConstants.BS_BIKE))||(mode.equals(EBConstants.BS_BIKE_FF)))
		   		{
		   			tt = new TUG_BSTravelTime(bikeConfigGroup);
		   		}
		   		else 
		   		{
		   			tt = new TUG_BikeTravelTime(bikeConfigGroup);
		   		}
		   		double travelTime = tt.getLinkTravelTime(link, time, person, vehicle);
		   		
				if (personAttributes.getAttribute(person.getId().toString(), "routingType") != null)
				{
					routingType = ((int) personAttributes.getAttribute(person.getId().toString(), "routingType"));
				}

			   // Einlesen aus Bike_Attributes
			   double bikeSafetyOfInfrastructure = -1;
			   double bikeSlopeOfInfrastructure = -1;
			   double bikeComfortOfInfrastructure = -1;
			   double bikeSurroundingOfInfrastructure = -1;
			   double bikeAmountOfInfrastructure = -1;

			   
				   if (bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed") == null) 
				   {
					   bikeSafetyOfInfrastructure = 5;
					   bikeSlopeOfInfrastructure = 5;
					   bikeComfortOfInfrastructure = 5;               
					   bikeSurroundingOfInfrastructure = 5;
					   bikeAmountOfInfrastructure = 5;  
				   }
				   else
				   {
					   isFastCycleLane = (boolean) bikeLinkAttributes.getAttribute(link.getId().toString(), "interaction");
					   bikeSafetyOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "safety"));
					   bikeSlopeOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "slope")); 
					   bikeComfortOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "comfort"));                      
					   bikeSurroundingOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "surrounding"));  
					   bikeAmountOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "amount"));  
				   }
                                                	                     
			   // Einlesen aus Config (siehe TravelDisutil): FEHLT NOCH: TODO: Hebenstreit
			   String [] amountShare = null;
			   String [] slopeShare = null;
			   String [] surroundingShare = null;
			   String [] safetyShare = null;
			   String [] comfortShare = null;
			   String [] bikeTypeGroupArr = null;
			   

			   if (bikeConfigGroup.getValue("duAmountBiketype_Input")!=null)
			   {
				   String BT_Amount = bikeConfigGroup.getValue("duAmountBiketype_Input");
				   amountShare = BT_Amount.split(",");
			   }
			   if (bikeConfigGroup.getValue("duSlopeBiketype_Input")!=null)
			   {
				   String BT_Slope = bikeConfigGroup.getValue("duSlopeBiketype_Input");
				   slopeShare = BT_Slope.split(",");
			   }
			   if (bikeConfigGroup.getValue("duSurroundingBiketype_Input")!=null)
			   {
				   String BT_Surrounding = bikeConfigGroup.getValue("duSurroundingBiketype_Input");
				   surroundingShare = BT_Surrounding.split(",");
			   }
			   if (bikeConfigGroup.getValue("duSafetyBiketype_Input")!=null)
			   {
				   String BT_Safety = bikeConfigGroup.getValue("duSafetyBiketype_Input");
				   safetyShare = BT_Safety.split(",");
			   }
			   if (bikeConfigGroup.getValue("duComfortBiketype_Input")!=null)
			   {
				   String BT_Comfort = bikeConfigGroup.getValue("duComfortBiketype_Input");
				   comfortShare = BT_Comfort.split(",");
			   }
			   if (bikeConfigGroup.getValue("bikeTypeGroup_Input")!=null)
			   {
				   String bikeTypeGroup = bikeConfigGroup.getValue("bikeTypeGroup_Input");
				   bikeTypeGroupArr = bikeTypeGroup.split(",");
			   }
			   
			   double am = 0.25;
			   double sl = 1;                               
			   double surr = 0.25;
			   double saf = 0.25;                		  
			   double comf = 0.25;
			   
			   if((bikeTypeGroupArr!= null)&& (routingType > -1))
			   {                   
				   for (int k = 0; k < bikeTypeGroupArr.length; k++)
				   {
					   if (routingType == Integer.parseInt(bikeTypeGroupArr[k]))
					   {
						   if (amountShare!= null) 
						   {
							   am = Double.parseDouble(amountShare[k]);
						   }
						   if (slopeShare != null)
						   {
						   sl = Double.parseDouble(slopeShare[k]);
						   }
						   if (surroundingShare != null)
						   {
						   surr = Double.parseDouble(surroundingShare[k]);
						   }
						   if(safetyShare != null)
						   {
							   saf = Double.parseDouble(safetyShare[k]);
						   }
						   if (comfortShare != null)
						   {
						   comf = Double.parseDouble(comfortShare[k]);
						   }
						   if (slopeShare != null)
						   {
						   sl = Double.parseDouble(comfortShare[k]);
						   }
						   break;
					   }
				   }
			   	}


			   //double du_Type = 0;
	                                                       
			   double safetyFactor = 1;
			   double comfortFactor = 1;
			   double surroundingFactor = 1;
			   double speedChange = 1;
			   double amountFactor = 1;
	                               
			   //---- safety ----
			   if (bikeSafetyOfInfrastructure <= 2.5) { safetyFactor = 0.5;}
			   else if ((bikeSafetyOfInfrastructure > 2.5) && (bikeSafetyOfInfrastructure <=3.5)){ safetyFactor = 0.9;}
			   else if ((bikeSafetyOfInfrastructure > 3.5) && (bikeSafetyOfInfrastructure <=4.5)){ safetyFactor = 0.95;}
			   else if ((bikeSafetyOfInfrastructure > 4.5) && (bikeSafetyOfInfrastructure <=5.5)){ safetyFactor = 1;}
			   else if ((bikeSafetyOfInfrastructure > 5.5) && (bikeSafetyOfInfrastructure <=6.5)){ safetyFactor = 1.05;}
			   else if ((bikeSafetyOfInfrastructure > 6.5) && (bikeSafetyOfInfrastructure <=7.5)){ safetyFactor = 1.5;}
			   else { safetyFactor = 1.35;}
	                               
			   //---- slope (speed) ----
			   if (bikeSlopeOfInfrastructure <= 2.5) { speedChange = 0.85;}
			   else if ((bikeSlopeOfInfrastructure > 2.5) && (bikeSlopeOfInfrastructure <=3.5)){ speedChange = 0.9;}
			   else if ((bikeSlopeOfInfrastructure > 3.5) && (bikeSlopeOfInfrastructure <=4.5)){ speedChange = 0.95;}
			   else if ((bikeSlopeOfInfrastructure > 4.5) && (bikeSlopeOfInfrastructure <=5.5)){ speedChange = 1;}
			   else if ((bikeSlopeOfInfrastructure > 5.5) && (bikeSlopeOfInfrastructure <=6.5)){ speedChange = 1.05;}
			   else if ((bikeSlopeOfInfrastructure > 6.5) && (bikeSlopeOfInfrastructure <=7.5)){ speedChange = 1.10;}
			   else { speedChange = 1.15;}
	                                                  		   
			   // ---- comfort ----
			   if (bikeComfortOfInfrastructure <= 2.5) { comfortFactor = 0.85;}
			   else if ((bikeComfortOfInfrastructure > 2.5) && (bikeComfortOfInfrastructure <=3.5)){ comfortFactor = 0.9;}
			   else if ((bikeComfortOfInfrastructure > 3.5) && (bikeComfortOfInfrastructure <=4.5)){ comfortFactor = 0.95;}
			   else if ((bikeComfortOfInfrastructure > 4.5) && (bikeComfortOfInfrastructure <=5.5)){ comfortFactor = 1;}
			   else if ((bikeComfortOfInfrastructure > 5.5) && (bikeComfortOfInfrastructure <=6.5)){ comfortFactor = 1.05;}
			   else if ((bikeComfortOfInfrastructure > 6.5) && (bikeComfortOfInfrastructure <=7.5)){ comfortFactor = 1.1;}
			   else { comfortFactor = 1.35;}
	                            		   
			   // ---- surrounding ----
			   if (bikeSurroundingOfInfrastructure <= 2.5) { surroundingFactor = 0.5;}
			   else if ((bikeSurroundingOfInfrastructure > 2.5) && (bikeSurroundingOfInfrastructure <=3.5)){ surroundingFactor = 0.90;}
			   else if ((bikeSurroundingOfInfrastructure > 3.5) && (bikeSurroundingOfInfrastructure <=4.5)){ surroundingFactor = 0.95;}
			   else if ((bikeSurroundingOfInfrastructure > 4.5) && (bikeSurroundingOfInfrastructure <=5.5)){ surroundingFactor = 1;}
			   else if ((bikeSurroundingOfInfrastructure > 5.5) && (bikeSurroundingOfInfrastructure <=6.5)){ surroundingFactor = 1.05;}
			   else if ((bikeSurroundingOfInfrastructure > 6.5) && (bikeSurroundingOfInfrastructure <=7.5)){ surroundingFactor = 1.1;}
			   else { surroundingFactor = 1.2;}
	                               
			   // ---- surrounding ----
			   if (bikeAmountOfInfrastructure <= 2.5) { amountFactor = 0.75;}
			   else if ((bikeAmountOfInfrastructure > 2.5) && (bikeAmountOfInfrastructure <=3.5)){ amountFactor = 0.9;}
			   else if ((bikeAmountOfInfrastructure > 3.5) && (bikeAmountOfInfrastructure <=4.5)){ amountFactor = 0.95;}
			   else if ((bikeAmountOfInfrastructure > 4.5) && (bikeAmountOfInfrastructure <=5.5)){ amountFactor = 1;}
			   else if ((bikeAmountOfInfrastructure > 5.5) && (bikeAmountOfInfrastructure <=6.5)){ amountFactor = 1.05;}
			   else if ((bikeAmountOfInfrastructure > 6.5) && (bikeAmountOfInfrastructure <=7.5)){ amountFactor = 1.1;}
			   else { amountFactor = 1.2;}
	                               
			   double perceivedTravelTime = 0;
                     
			   //feltTravelTime = travelTime * (safetyFactor +comfortFactor + surroundingFactor + amountFactor)/ 4;
			   //double feltTravelTimeCalc = lenOfLink * (safetyFactor +comfortFactor + surroundingFactor + amountFactor)/ 4 / (v*speedChange);
	           //ergibt gleiches Ergebnis!
			   
			   if (saf+comf+surr+am+sl > 1)
			   {
				   saf = saf/(saf+comf+surr+am+sl);
				   comf = comf/(saf+comf+surr+am+sl);
				   surr = surr/(saf+comf+surr+am+sl);
				   am = am/(saf+comf+surr+am+sl);
				   sl = sl/(saf+comf+surr+am+sl);
			   }
			   
			   double changes = ((safetyFactor * saf)
			   			+ (comfortFactor * comf )
			   			+ (surroundingFactor * surr )
			   			+ (amountFactor * am)
			   			+ (speedChange * sl));
			   //log.info("###### CHANGES: " + changes);     

			   if (changes < 0.85)
			   {
				   changes = 0.85;
			   }
			   else if (changes > 1.25)
			   {
				   changes = 1.25;
			   }

			   perceivedTravelTime = travelTime * changes;	  
			   //TODO: Hebenstreit - wenn Slope einen bestimmten Wert überschreiten --> Berücksichtigen von SL
			   
			   //log.info("###### FeltTravelTime link: " + link.getId() + "  pers:  " + person.getId() + "    " + feltTravelTime);     
			   //log.info("######     TravelTime link: " + link.getId() + "  pers:  " + person.getId() + "    " + travelTime);  

			   double[] percTavelTimeAndTravelLength = new double[2];
			   percTavelTimeAndTravelLength[0] =perceivedTravelTime;//perceivedTravelTime;
			   percTavelTimeAndTravelLength[1] = travelDistance;
			   
			   if (isFastCycleLane)
			   {
				   percTavelTimeAndTravelLength[0] = perceivedTravelTime * 0.8;
				   percTavelTimeAndTravelLength[1] = travelDistance * 0.9;
			   }
			   
			   return percTavelTimeAndTravelLength; //does perceive length and duration differently
            
	   }

	/***************************************************************************/
	@Override
	public double getLinkTravelTime(
			Link link, double time, Person person, Vehicle vehicle)
	/***************************************************************************/
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
