package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.utils.objectattributes.ObjectAttributes;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;

public class TravelDisutilityHelper {

	static double getDisutilityForLinkAttributes 
		(ObjectAttributes bikeLinkAttributes, 
		ObjectAttributes personAttributes,
		BicycleConfigGroup bikeConfigGroup,
		Link link, Person person)
{
	 String [] amountShare = null;
	 String [] slopeShare = null;
	 String [] surroundingShare = null;
	 String [] safetyShare = null;
	 String [] comfortShare = null;
	 String [] bikeTypeGroupArr = null;

	   if ((bikeLinkAttributes == null) || (personAttributes == null) || (bikeConfigGroup == null))
	   {
		   return link.getLength();
	   }    		
	   else
	   {               
		   // Einlesen aus Person_Attributes                                         
		  // double bikeSpeedOfPerson = ((double) personAttributes.getAttribute(person.getId().toString(), "bikeSpeed")) ;
		   //String bikeTypeOfPerson = ((String) personAttributes.getAttribute(person.getId().toString(), "bikeType"));  
		   
		   // Einlesen aus Bike_Attributes  
		   if (bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed") == null)
		   {
			   	return link.getLength(); //TODO: Hebenstreit
		   }
		   if (personAttributes.getAttribute(person.getId().toString(), "routingType")==null)
			{
				return link.getLength(); //TODO: Hebenstreit
			}
		   int routingType = ((int) personAttributes.getAttribute(person.getId().toString(), "routingType")); 
			  
		  // double bikeSpeedOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed")); 
		   double bikeSafetyOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "safety")); 
		   double bikeSlopeOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "slope")); 
		   double bikeComfortOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "comfort"));                      
		   double bikeSurroundingOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "surrounding"));  
		   double bikeAmountOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "amount"));  

            
		   // Einlesen aus Config (TravelDisutil): 
		   
		   double am = 0;
		   double sl = 0;                               
		   double surr = 0;
		   double saf = 0;                		  
		   double comf = 0;
		   
		   if (bikeTypeGroupArr == null) //if not filled by now, fill it
		   {
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
			}
		   
		   if(bikeTypeGroupArr!= null)
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
                                            
		   double du_Type = ((am * bikeAmountOfInfrastructure) 
				   + (sl * bikeSlopeOfInfrastructure) 
				   + (surr * bikeSurroundingOfInfrastructure) 
				   + (saf * bikeSafetyOfInfrastructure) 
				   + (comf * bikeComfortOfInfrastructure))/5;//*lenOfLink/v;
		   return du_Type;
	   	}
	}
}
