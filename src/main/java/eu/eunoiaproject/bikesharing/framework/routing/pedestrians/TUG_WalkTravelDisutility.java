package eu.eunoiaproject.bikesharing.framework.routing.pedestrians;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.IKK_ObjectAttributesSingleton;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;

/* 
* 
* in this class disutility per link is calculated for routing:
*/ 


public class TUG_WalkTravelDisutility implements TravelDisutility {

	int linkCount=0;
	double individualDis;   
	private final static Logger log = Logger.getLogger(TUG_WalkTravelDisutility.class);

	ObjectAttributes personAttributes;
	BicycleConfigGroup bikeConfigGroup;  
	PlanCalcScoreConfigGroup cnScoringGroup; // Hebenstreit: ist das hier das Richtige?
    
	/***************************************************************************/
	public TUG_WalkTravelDisutility(
			BicycleConfigGroup bikeConfigGroup, 
			PlanCalcScoreConfigGroup cnScoringGroup) 
	/***************************************************************************/
	{	
			IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bikeConfigGroup);
			personAttributes = bts.getPersonAttributes();
	}
    
	/***************************************************************************/
	@Override
	public double getLinkTravelDisutility(
			Link link, double time, Person person, Vehicle vehicle) 
	/***************************************************************************/
	{                     
		double disutility = link.getLength();
		return disutility;
	}

	/***************************************************************************/
	@Override
	public double getLinkMinimumTravelDisutility(Link link) 
	/***************************************************************************/
	{
		return 0;
	}
}
