package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes;


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
