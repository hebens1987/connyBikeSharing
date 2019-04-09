package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelDisutilities;


import eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes.TUG_BikeTravelTime;
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


public class TUG_BikeTravelDisutility implements TravelDisutility
{
	//private final static Logger log = Logger.getLogger(TUG_BikeTravelDisutility.class);
	private ObjectAttributes bikeLinkAttributes;
	private ObjectAttributes personAttributes;
	private BicycleConfigGroup bikeConfigGroup;
    
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

	   	  double du_Type = TravelDisutilityHelper.getDisutilityForLinkAttributes(
	   				bikeLinkAttributes, personAttributes,bikeConfigGroup,link,person);
		
		   TUG_BikeTravelTime btt = new TUG_BikeTravelTime(bikeConfigGroup);
		   double linkTravelTimeBikes = btt.getLinkTravelTime(link, time, person, vehicle);

		   if (du_Type < 0.1) 
		   {
			   du_Type = 0.1;
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
