package eu.eunoiaproject.bikesharing.framework.routing.bicycles;



import org.matsim.api.core.v01.TransportMode;
//import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.router.util.TravelTime;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.IKK_ObjectAttributesSingleton;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;

/**
* in this class the felt travel time - used for scoring is calculated
* travel time gets increased if poor cycle conditions are existing on a link
* tavel time gets decreased if good cycle conditions are existing on a link
* 
* @autor hebenstreit
*/


public class TUG_BikeFeltTravelTime implements TravelTime
{
	int linkCount=0;
	double individualDis; 
	//private final static Logger log = Logger.getLogger(TUG_BikeFeltTravelTime.class);
	ObjectAttributes bikeLinkAttributes;
	ObjectAttributes usergroupAttributes;
	ObjectAttributes personAttributes;
	BicycleConfigGroup bikeConfigGroup;
	TUG_FeltTravelTime ftt;
    
	
	/***************************************************************************/
	TUG_BikeFeltTravelTime(
			BicycleConfigGroup bikeConfigGroup) 
	/***************************************************************************/
	{
			IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup);
			bikeLinkAttributes = bts.getBikeLinkAttributes();
			usergroupAttributes = bts.getUsergroupAttributes();
			personAttributes = bts.getPersonAttributes();
			this.bikeConfigGroup = bikeConfigGroup;
	}
	
	/***************************************************************************/
	public double [] getLinkTravelDisutility(
			Link link, double time, Person person, Vehicle vehicle) 
	/***************************************************************************/
	{   
				//String test = bikeConfigGroup.getValue("bicyclelinkAttributesInput");
		 		//System.out.println(test); //Hebenstreit - bikeConfigGroup --> NullPointer
		   		double bikeSpeedOfPerson = 0;
		   		int routingType = -1;
			   // Einlesen aus Person_Attributes 
				if (personAttributes == null)
				{
					bikeSpeedOfPerson = 16/3.6;
				}
				else
				{
					bikeSpeedOfPerson = ((double) personAttributes.getAttribute(person.getId().toString(), "bikeSpeed")) ;
					//String bikeTypeOfPerson = ((String) personAttributes.getAttribute(person.getId().toString(), "bikeType"));  
					routingType = ((int) personAttributes.getAttribute(person.getId().toString(), "routingType"));
				}

			   // Einlesen aus Bike_Attributes
			   double bikeSpeedOfInfrastructure = -1; 
			   double bikeSafetyOfInfrastructure = -1;
			   double bikeSlopeOfInfrastructure = -1;
			   double bikeComfortOfInfrastructure = -1;
			   double bikeSurroundingOfInfrastructure = -1;
			   double bikeAmountOfInfrastructure = -1;

			   if ((bikeLinkAttributes == null) || (personAttributes == null))
			   {
				   double[] percTravelTimeAndTravelLength = new double[2];
				   double travelTimeOrig = 0;
				   double lenOfLink = link.getLength();
				   if (link.getAllowedModes().contains(TransportMode.bike))
				   {
					   travelTimeOrig = lenOfLink/bikeSpeedOfPerson; //no infra type defined, set default to 15 km/h
				   }
				   else
				   {
					   travelTimeOrig = lenOfLink/1.1; //agent walks the bike
				   }
				   percTravelTimeAndTravelLength[0] = travelTimeOrig;
				   percTravelTimeAndTravelLength[1] = lenOfLink;
				   return percTravelTimeAndTravelLength;
			   }
			   else
			   {
				   bikeSpeedOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed")); 
				   bikeSafetyOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "safety")); 
				   bikeSlopeOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "slope")); 
				   bikeComfortOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "comfort"));                      
				   bikeSurroundingOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "surrounding"));  
				   bikeAmountOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "amount"));  
			   }
	                                                 
			   double velocityperson = bikeSpeedOfPerson;
			   double velocityinfrastructure = bikeSpeedOfInfrastructure/3.6;
			   double v = 0;
			   
			   if(velocityinfrastructure <= velocityperson){v=velocityinfrastructure;}
			   else {v=velocityperson;}
			                     
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
						   break;
					   }
				   }
			   	}

			   if(velocityinfrastructure <= velocityperson){v=velocityinfrastructure;}
			   else {v=velocityperson;}

                   
			   TUG_BikeTravelTime wtt = new TUG_BikeTravelTime(bikeConfigGroup);
			   double travelTime = wtt.getLinkTravelTime(link, time, person, vehicle);
			   double lenOfLink = link.getLength();
			   //double du_Type = 0;
	                                                       
			   double safetyFactor = 1;
			   double comfortFactor = 1;
			   double surroundingFactor = 1;
			   double speedChange = 1;
			   double amountFactor = 1;
	                               
			   //---- safety ----
			   if (bikeSafetyOfInfrastructure <= 2.5) { safetyFactor = 0.5;}
			   else if ((bikeSafetyOfInfrastructure > 2.5) && (bikeSafetyOfInfrastructure <=3.5)){ safetyFactor = 0.75;}
			   else if ((bikeSafetyOfInfrastructure > 3.5) && (bikeSafetyOfInfrastructure <=4.5)){ safetyFactor = 0.875;}
			   else if ((bikeSafetyOfInfrastructure > 4.5) && (bikeSafetyOfInfrastructure <=5.5)){ safetyFactor = 1;}
			   else if ((bikeSafetyOfInfrastructure > 5.5) && (bikeSafetyOfInfrastructure <=6.5)){ safetyFactor = 1.125;}
			   else if ((bikeSafetyOfInfrastructure > 6.5) && (bikeSafetyOfInfrastructure <=7.5)){ safetyFactor = 1.25;}
			   else { safetyFactor = 1.35;}
	                               
			   //---- slope (speed) ----
			   if (bikeSlopeOfInfrastructure <= 2.5) { speedChange = +0.5;}
			   else if ((bikeSlopeOfInfrastructure > 2.5) && (bikeSlopeOfInfrastructure <=3.5)){ speedChange = +0.33;}
			   else if ((bikeSlopeOfInfrastructure > 3.5) && (bikeSlopeOfInfrastructure <=4.5)){ speedChange = +0.15;}
			   else if ((bikeSlopeOfInfrastructure > 4.5) && (bikeSlopeOfInfrastructure <=5.5)){ speedChange = 0;}
			   else if ((bikeSlopeOfInfrastructure > 5.5) && (bikeSlopeOfInfrastructure <=6.5)){ speedChange = -0.15;}
			   else if ((bikeSlopeOfInfrastructure > 6.5) && (bikeSlopeOfInfrastructure <=7.5)){ speedChange = -0.33;}
			   else { speedChange = -0.5;}
	                                                  		   
			   // ---- comfort ----
			   if (bikeComfortOfInfrastructure <= 2.5) { comfortFactor = 0.5;}
			   else if ((bikeComfortOfInfrastructure > 2.5) && (bikeComfortOfInfrastructure <=3.5)){ comfortFactor = 0.75;}
			   else if ((bikeComfortOfInfrastructure > 3.5) && (bikeComfortOfInfrastructure <=4.5)){ comfortFactor = 0.875;}
			   else if ((bikeComfortOfInfrastructure > 4.5) && (bikeComfortOfInfrastructure <=5.5)){ comfortFactor = 1;}
			   else if ((bikeComfortOfInfrastructure > 5.5) && (bikeComfortOfInfrastructure <=6.5)){ comfortFactor = 1.125;}
			   else if ((bikeComfortOfInfrastructure > 6.5) && (bikeComfortOfInfrastructure <=7.5)){ comfortFactor = 1.25;}
			   else { comfortFactor = 1.35;}
	                            		   
			   // ---- surrounding ----
			   if (bikeSurroundingOfInfrastructure <= 2.5) { surroundingFactor = 0.5;}
			   else if ((bikeSurroundingOfInfrastructure > 2.5) && (bikeSurroundingOfInfrastructure <=3.5)){ surroundingFactor = 0.80;}
			   else if ((bikeSurroundingOfInfrastructure > 3.5) && (bikeSurroundingOfInfrastructure <=4.5)){ surroundingFactor = 0.92;}
			   else if ((bikeSurroundingOfInfrastructure > 4.5) && (bikeSurroundingOfInfrastructure <=5.5)){ surroundingFactor = 1;}
			   else if ((bikeSurroundingOfInfrastructure > 5.5) && (bikeSurroundingOfInfrastructure <=6.5)){ surroundingFactor = 1.05;}
			   else if ((bikeSurroundingOfInfrastructure > 6.5) && (bikeSurroundingOfInfrastructure <=7.5)){ surroundingFactor = 1.1;}
			   else { surroundingFactor = 1.2;}
	                               
			   // ---- surrounding ----
			   if (bikeAmountOfInfrastructure <= 2.5) { amountFactor = 0.75;}
			   else if ((bikeAmountOfInfrastructure > 2.5) && (bikeAmountOfInfrastructure <=3.5)){ amountFactor = 0.825;}
			   else if ((bikeAmountOfInfrastructure > 3.5) && (bikeAmountOfInfrastructure <=4.5)){ amountFactor = 0.95;}
			   else if ((bikeAmountOfInfrastructure > 4.5) && (bikeAmountOfInfrastructure <=5.5)){ amountFactor = 1;}
			   else if ((bikeAmountOfInfrastructure > 5.5) && (bikeAmountOfInfrastructure <=6.5)){ amountFactor = 1.05;}
			   else if ((bikeAmountOfInfrastructure > 6.5) && (bikeAmountOfInfrastructure <=7.5)){ amountFactor = 1.1;}
			   else { amountFactor = 1.2;}
	                               
			   double perceivedTravelTime = 0;
                     
			   //feltTravelTime = travelTime * (safetyFactor +comfortFactor + surroundingFactor + amountFactor)/ 4;
			   //double feltTravelTimeCalc = lenOfLink * (safetyFactor +comfortFactor + surroundingFactor + amountFactor)/ 4 / (v*speedChange);
	           //ergibt gleiches Ergebnis!
			   
			   double changes = ((safetyFactor * saf)
			   			+ (comfortFactor * comf )
			   			+ (surroundingFactor * surr )
			   			+ (amountFactor * am));
			   //log.info("###### CHANGES: " + changes);     

			   perceivedTravelTime = (lenOfLink/(v + speedChange))* changes;	  
			   //TODO: Hebenstreit - wenn Slope einen bestimmten Wert überschreiten --> Berücksichtigen von SL
			   
			   //log.info("###### FeltTravelTime link: " + link.getId() + "  pers:  " + person.getId() + "    " + feltTravelTime);     
			   //log.info("######     TravelTime link: " + link.getId() + "  pers:  " + person.getId() + "    " + travelTime);  

			   double travelTimeOrig = lenOfLink/v;
			   double factor = perceivedTravelTime/travelTimeOrig;
			   double[] percTavelTimeAndTravelLength = new double[2];
			   percTavelTimeAndTravelLength[0] = perceivedTravelTime;
			   percTavelTimeAndTravelLength[1] = lenOfLink * factor;
			   return percTavelTimeAndTravelLength; 
            
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
