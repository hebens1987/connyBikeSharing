package eu.eunoiaproject.bikesharing.framework.routingDisutilitiesTravelTimes.travelTimes;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.TravelTime;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;
/**
* in this class traveltime is calculated depending on the following parameters:
* speed of infrastructure
* desired speed of a person
* @author hebenstreit
*/
public class TUG_BikeTravelTime implements TravelTime 
{
	//@Inject
	//IKK_BikeConfigGroup bikeConfigGroup;   
	ObjectAttributes bikeLinkAttributes;
	ObjectAttributes usergroupAttributes;
	ObjectAttributes personAttributes;
	int counter = 0;
	
	private final static Logger log = Logger.getLogger(TUG_BikeTravelTime.class);

	/***************************************************************************/
	@Inject
	@Singleton
	public
	TUG_BikeTravelTime(BicycleConfigGroup bikeConfigGroup)
	/***************************************************************************/
	{	 
		//IKK_ObjectAttributesSingleton bts = new IKK_ObjectAttributesSingleton(bikeConfigGroup);
		IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup, false);
		bikeLinkAttributes = bts.getBikeLinkAttributes();
		personAttributes = bts.getPersonAttributes();
	}
	
	/***************************************************************************/
	@Override
	public double getLinkTravelTime(
			Link link, double time, Person person, Vehicle vehicle) 
	/***************************************************************************/
	{
		TravelTimeHelper tth = new TravelTimeHelper();
		return tth.travelTimeGetter(person, link, personAttributes, bikeLinkAttributes, 8/3.6, 30/3.6, 0);
		
	}
}

