package eu.eunoiaproject.bikesharing.framework.routing.bicycles;


//import org.apache.log4j.Logger;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.IKK_ObjectAttributesSingleton;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
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
	private ObjectAttributes bikeLinkAttributes;
	private ObjectAttributes usergroupAttributes;
	private ObjectAttributes personAttributes;
	private BicycleConfigGroup bikeConfigGroup;

	/***************************************************************************/
	TUG_EBSTravelDisutility(
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
                               
	   //Abfangen falls Input-File nicht existiert oder es keines gibt!
	   if ((bikeLinkAttributes == null) || (usergroupAttributes == null) 
			   || (personAttributes == null) || (bikeConfigGroup == null))
	   {
		   return link.getLength();
	   }    		
	   else
	   {               
		   // Einlesen aus Person_Attributes                                         
		  // double bikeSpeedOfPerson = ((double) personAttributes.getAttribute(person.getId().toString(), "bikeSpeed")) ;
		   //String bikeTypeOfPerson = ((String) personAttributes.getAttribute(person.getId().toString(), "bikeType"));  
		   int routingType = ((int) personAttributes.getAttribute(person.getId().toString(), "routingType")); 

		   // Einlesen aus Bike_Attributes  
		   if (bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed") == null)
		   {
			   	return link.getLength() * 100000; //TODO: Hebenstreit
		   }
			  
		  // double bikeSpeedOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed")); 
		   double bikeSafetyOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "safety")); 
		   double bikeSlopeOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "slope")); 
		   double bikeComfortOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "comfort"));                      
		   double bikeSurroundingOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "surrounding"));  
		   double bikeAmountOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "amount"));  

		   String [] amountShare = null;
		   String [] slopeShare = null;
		   String [] surroundingShare = null;
		   String [] safetyShare = null;
		   String [] comfortShare = null;
		   String [] bikeTypeGroupArr = null;
		               
		   // Einlesen aus Config (TravelDisutil): 
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
		   
		   double am = 0;
		   double sl = 0;                               
		   double surr = 0;
		   double saf = 0;                		  
		   double comf = 0;
		   
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
				   + (comf * bikeComfortOfInfrastructure));//*lenOfLink/v;
		   
		   TUG_EBSTravelTime btt = new TUG_EBSTravelTime(bikeConfigGroup);
		   double linkTravelTimeBikes = btt.getLinkTravelTime(link, time, person, vehicle);
			
		  // double lenOfLink = link.getLength();
		  // travelTime = lenOfLink/v; 

		   if (du_Type < 0.1) { du_Type = 0.1;} //Falls ALLES NULL wird über die Länge geroutet!
		   double disutilityBikesPerLink = linkTravelTimeBikes * du_Type;
   
		   return disutilityBikesPerLink ;
	   }             
   }

   /***************************************************************************/
   @Override
   public double getLinkMinimumTravelDisutility(Link link) 
   /***************************************************************************/
   {
	   return 0.1;
   }

}
