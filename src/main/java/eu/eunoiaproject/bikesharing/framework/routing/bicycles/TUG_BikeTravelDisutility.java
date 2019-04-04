package eu.eunoiaproject.bikesharing.framework.routing.bicycles;


//import org.apache.log4j.Logger;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.IKK_ObjectAttributesSingleton;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;
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


public class TUG_BikeTravelDisutility implements TravelDisutility
{
	//private final static Logger log = Logger.getLogger(TUG_BikeTravelDisutility.class);
	private ObjectAttributes bikeLinkAttributes;
	private ObjectAttributes personAttributes;
	private BicycleConfigGroup bikeConfigGroup;
	private String [] amountShare = null;
	private String [] slopeShare = null;
	private String [] surroundingShare = null;
	private String [] safetyShare = null;
	private String [] comfortShare = null;
	private String [] bikeTypeGroupArr = null;
    
	/***************************************************************************/
	public TUG_BikeTravelDisutility(
		  BicycleConfigGroup bikeConfigGroup )
	/***************************************************************************/
	{
			IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup, false);
			bikeLinkAttributes = bts.getBikeLinkAttributes();
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
					   this.amountShare = BT_Amount.split(",");
				   }
				   if (bikeConfigGroup.getValue("duSlopeBiketype_Input")!=null)
				   {
					   String BT_Slope = bikeConfigGroup.getValue("duSlopeBiketype_Input");
					   this.slopeShare = BT_Slope.split(",");
				   }
				   if (bikeConfigGroup.getValue("duSurroundingBiketype_Input")!=null)
				   {
					   String BT_Surrounding = bikeConfigGroup.getValue("duSurroundingBiketype_Input");
					   this.surroundingShare = BT_Surrounding.split(",");
				   }
				   if (bikeConfigGroup.getValue("duSafetyBiketype_Input")!=null)
				   {
					   String BT_Safety = bikeConfigGroup.getValue("duSafetyBiketype_Input");
					   this.safetyShare = BT_Safety.split(",");
				   }
				   if (bikeConfigGroup.getValue("duComfortBiketype_Input")!=null)
				   {
					   String BT_Comfort = bikeConfigGroup.getValue("duComfortBiketype_Input");
					   this.comfortShare = BT_Comfort.split(",");
				   }
				   if (bikeConfigGroup.getValue("bikeTypeGroup_Input")!=null)
				   {
					   String bikeTypeGroup = bikeConfigGroup.getValue("bikeTypeGroup_Input");
					   this.bikeTypeGroupArr = bikeTypeGroup.split(",");
				   }
			}
		   
		   if(bikeTypeGroupArr!= null)
		   {                 
			   for (int k = 0; k < bikeTypeGroupArr.length; k++)
			   {
				   if (routingType == Integer.parseInt(bikeTypeGroupArr[k]))
				   {
					   if (this.amountShare!= null) 
					   {
						   am = Double.parseDouble(this.amountShare[k]);
					   }
					   if (this.slopeShare != null)
					   {
						   sl = Double.parseDouble(this.slopeShare[k]);
					   }
					   if (this.surroundingShare != null)
					   {
						   surr = Double.parseDouble(this.surroundingShare[k]);
					   }
					   if(this.safetyShare != null)
					   {
						   saf = Double.parseDouble(this.safetyShare[k]);
					   }
					   if (this.comfortShare != null)
					   {
						   comf = Double.parseDouble(this.comfortShare[k]);
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
		   
		   TUG_BikeTravelTime btt = new TUG_BikeTravelTime(bikeConfigGroup);
		   double linkTravelTimeBikes = btt.getLinkTravelTime(link, time, person, vehicle);
			
		  // double lenOfLink = link.getLength();
		  // travelTime = lenOfLink/v; 

		   if (du_Type < 0.1) 
		   {
			   du_Type = 0.1;
		   } //Falls ALLES NULL wird über die Länge geroutet!
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
