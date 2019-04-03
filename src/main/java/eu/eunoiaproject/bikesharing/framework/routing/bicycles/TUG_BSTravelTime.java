package eu.eunoiaproject.bikesharing.framework.routing.bicycles;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.TravelTime;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.IKK_ObjectAttributesSingleton;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;
/**
* in this class traveltime is calculated depending on the following parameters:
* speed of infrastructure
* desired speed of a person
* @author hebenstreit
*/
public class TUG_BSTravelTime implements TravelTime 
{
	//@Inject
	//IKK_BikeConfigGroup bikeConfigGroup;   
	ObjectAttributes bikeLinkAttributes;
	ObjectAttributes usergroupAttributes;
	ObjectAttributes personAttributes;
	
	private final static Logger log = Logger.getLogger(TUG_BSTravelTime.class);

	/***************************************************************************/
	@Inject
	@Singleton
	public
	TUG_BSTravelTime(BicycleConfigGroup bikeConfigGroup)
	/***************************************************************************/
	{	 
		IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup,false);
		bikeLinkAttributes = bts.getBikeLinkAttributes();
		personAttributes = bts.getPersonAttributes();
	}
	
	/***************************************************************************/
	@Override
	public double getLinkTravelTime(
			Link link, double time, Person person, Vehicle vehicle) 
	/***************************************************************************/
	{
		double v = 0;
		// bikeSpeed in km/h
		double lenOfLink = link.getLength();
		//System.out.println(link.getId().toString());
		double bikeSpeedOfPerson = 0;
		if (personAttributes.getAttribute(person.getId().toString(), "bikeSpeed") == null)
		{
			bikeSpeedOfPerson = 4.5/1.125; //13.333 km/h
			log.warn("For Person with ID: " + person.getId() + " no specific input personal speed was allocated");
		}
		else
		{
			bikeSpeedOfPerson = ((double) personAttributes.getAttribute(person.getId().toString(), "bikeSpeed")); // m/s
			bikeSpeedOfPerson = bikeSpeedOfPerson/1.125;
			if (bikeSpeedOfPerson > 20/3.6) {bikeSpeedOfPerson = 20/3.6;}
		}
		
		double bikeSpeedOfInfrastructure = 0;
		if (bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed") == null)
		{
			if (!(link.getAllowedModes().contains(TransportMode.bike)))
			{
				if (link.getAllowedModes().contains(TransportMode.walk))
				{
					bikeSpeedOfInfrastructure = 1.0; //TODO: Hebenstreit
					//For Link with ID: " + link.getId() + " using a walk link with very slow speed
				}
				
				else 
				{ 
					v = 0.0000001;
					//For Link with ID: " + link.getId() + " using any other link than mode bike or walk
				}
			}
			else if (link.getAllowedModes().contains(TransportMode.bike))
			{
				bikeSpeedOfInfrastructure = 3.0; //TODO: Hebenstreit
				log.warn("For Link with ID: " + link.getId() + " no specific input link was allocated, using a max value of 3 m/s");
			}
		}
		
		else
		{
			bikeSpeedOfInfrastructure = ((double) bikeLinkAttributes.getAttribute(link.getId().toString(), "maxSpeed"))/3.6; // m/s
		}
		
		//log.info("BIKETRAVELTIME speed link: " + link.getId() + "pers: " + person.getId() + " person-speed:  " + bikeSpeedOfPerson + " infra-speed: " + bikeSpeedOfInfrastructure);
		//log.info(Arrays.toString(Thread.currentThread().getStackTrace()));

		double velocityperson = bikeSpeedOfPerson;
		double velocityinfrastructure = bikeSpeedOfInfrastructure;


    	if(velocityinfrastructure <= velocityperson)
    	{
    		v=velocityinfrastructure;
    	}
    	else
    	{
    		v=velocityperson;
    	}
    		
    	if (v == 0) 
    	{
    	   v = 1.0;//minimale Geschwindigkeit = 6.5 km/h!
    	} 
		double traveltime = lenOfLink / v;
		return traveltime;
	}
}

