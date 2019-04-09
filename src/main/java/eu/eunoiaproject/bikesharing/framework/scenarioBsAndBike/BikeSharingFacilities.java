/* *********************************************************************** *
 * project: org.matsim.*
 * BikeSharingFacilities.java
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
package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.WaitingData;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.internal.MatsimToplevelContainer;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.utils.objectattributes.ObjectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The scenario element containing information about bike sharing facilities.
 * @author thibautd
 */
public class BikeSharingFacilities implements MatsimToplevelContainer {
	private static final Logger log =
		Logger.getLogger(BikeSharingFacilities.class);
	
	//private QuadTree<BikeSharingFacility> quadTree = null;

	public static final String ELEMENT_NAME = "bikeSharingFacility";
	public Map<Id<BikeSharingFacility>, BikeSharingFacility> facilities =
		new LinkedHashMap< >();
	private final Map<Id<BikeSharingFacility>, BikeSharingFacility> unmodifiableFacilities = new LinkedHashMap< >();
	private final ObjectAttributes facilitiesAttributes = new ObjectAttributes();
	
	private final QuadTreeRebuilder<BikeSharingFacility> quadTreeBuilder = new QuadTreeRebuilder< >();
	
	private final Map<String, String> metadata = new LinkedHashMap< >();

	public void addFacility( final BikeSharingFacility facility, Scenario scenario ) {
		facilities.put( facility.getId() , facility );
		Id<BikeSharingFacility> idN = Id.create(facility.getId().toString(), BikeSharingFacility.class);
		BikeSharingFacilities bsFac = (BikeSharingFacilities) 
				scenario.getScenarioElement( BikeSharingFacilities.ELEMENT_NAME);
		BikeSharingFacility unmod = new BikeSharingFacilityImpl(facility);
		unmodifiableFacilities.put( idN , unmod);
		quadTreeBuilder.put( facility.getCoord() , facility );
	}
	
	public Map<Id<Person>,WaitingData> totalWaitingListTake;
	public Map<Id<Person>, WaitingData> totalWaitingListReturn;
	public List<BikeSharingFacility> station_in_relocation = null;
	public List<BikeSharingFacility> station_in_need_of_relocation = null; //TODO:

	public Map<Id<BikeSharingFacility>, BikeSharingFacility> getFacilities() 
	{
		return facilities;
	}
	


	/** Put in c, c_pt, e, e_pt or ff, which defines the type you want to have
	 * c = conventional bike
	 * c_pt = a conventional bike, near to a pt station
	 * e = electric bike
	 * e_pt = electric bike near to a pt station
	 * ff = free floating bike **/
	public Map<Id<BikeSharingFacility>, BikeSharingFacility> getFacilityForRelocation (String whichType)
	{
		Map<Id<BikeSharingFacility>, BikeSharingFacility> stationsOfType = new LinkedHashMap<Id<BikeSharingFacility>, BikeSharingFacility>();
		
		for(Entry<Id<BikeSharingFacility>, BikeSharingFacility> tmp : facilities.entrySet())
		{
			if ((station_in_relocation == null) || (!(station_in_relocation.contains(tmp.getValue()))))
			{
				if (whichType.equals("c"))
				{
					if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().contains("c"))
					{
							stationsOfType.put(tmp.getKey(), tmp.getValue());
					}
				}
				
				if (whichType.equals("c_pt"))
				{
					if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().contains("c"))
					{
						if (tmp.getValue().getId().toString().endsWith("_pt"))
						{
							stationsOfType.put(tmp.getKey(), tmp.getValue());
						}
					}
				}
				
				if (whichType.equals("e"))
				{
					if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().contains("e"))
					{
						stationsOfType.put(tmp.getKey(), tmp.getValue());
					}
				}
				
				else if (whichType.equals("e_pt"))
				{
					if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().contains("e"))
					{
						if (tmp.getValue().getId().toString().endsWith("_pt"))
						{
							stationsOfType.put(tmp.getKey(), tmp.getValue());
						}
					}
				}
			}
		}
	return stationsOfType;
	}
	
	public synchronized BikeSharingFacilities getSpecialFacilities (String whichType, Scenario scenario)
	{
		BikeSharingFacilities stationsOfType = new BikeSharingFacilities();
		
		for(Entry<Id<BikeSharingFacility>, BikeSharingFacility> tmp : facilities.entrySet())
		{
			if (whichType.equals("c"))
			{
				if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().contains("c"))
				{
						stationsOfType.addFacility(tmp.getValue(), scenario);
				}
			}
			
			if (whichType.equals("c_pt"))
			{
				if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().contains("c"))
				{
					if (tmp.getValue().getId().toString().endsWith("_pt"))
					{
						stationsOfType.addFacility(tmp.getValue(), scenario);
//						log.warn("added " + tmp.getValue().getId().toString() ) ;
					}
				}
			}
			
			if (whichType.equals("e"))
			{
				if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().contains("e"))
				{
					stationsOfType.addFacility(tmp.getValue(), scenario);
				}
			}
			
			else if (whichType.equals("e_pt"))
			{
				if(tmp.getValue().getId().toString() != null && tmp.getValue().getStationType().contains("e"))
				{
					if (tmp.getValue().getId().toString().endsWith("_pt"))
					{
						stationsOfType.addFacility(tmp.getValue(), scenario);
					}
				}
			}
		}
	return stationsOfType;
	}
	
	/**
	 * may not always return the same instance!
	 */
	public QuadTree<BikeSharingFacility> getCurrentQuadTree() 
	{
		return quadTreeBuilder.getQuadTree();
	}

	@Override
	public BikeSharingFacilitiesFactory getFactory() {
		return new BikeSharingFacilitiesFactory() {
			@Override
			public BikeSharingFacility createBikeSharingFacility(
					final Id<BikeSharingFacility> id,
					final Coord coord,
					int numberOfAvailableBikes,
					int numberOfAvailableBikes2,
					final int totalNumberOfBikes,
					int freeParkingSlots,
					String type,
					String[] cyclesInStation,
					String[] cyclesInStation2,
					Id<Link> linkId,
					List<WaitingData> waitingToDepart,
					List<WaitingData>  waitingToReturn) {
				return new BikeSharingFacilityImpl(
							id,
							coord,
							numberOfAvailableBikes,
							numberOfAvailableBikes2,
							totalNumberOfBikes,
							freeParkingSlots,
							type,
							cyclesInStation,
							cyclesInStation2,
							linkId,
							waitingToDepart,
							waitingToReturn);
			}
		};
	}

	public ObjectAttributes getFacilitiesAttributes() {
		return facilitiesAttributes;
	}

	/**
	 * retrieve the metadata
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	/**
	 * add metadata. Metadata associates attribute names to values,
	 * and can be used to store any information useful to organize data:
	 * date of generation, source, author, etc.
	 */
	public void addMetadata(final String attribute, final String value) {
		final String old = metadata.put( attribute , value );
		if ( old != null ) log.warn( "replacing metadata \""+attribute+"\" from \""+old+"\" to \""+value+"\"" );
	}
}

