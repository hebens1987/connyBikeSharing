/* *********************************************************************** *
 * project: org.matsim.*
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


import eu.eunoiaproject.freeFloatingBS.BikeFFImpl;
import eu.eunoiaproject.freeFloatingBS.BikesFF;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.internal.MatsimToplevelContainer;
import org.matsim.pt.router.TransitRouterConfig;
import org.matsim.pt.router.TransitRouterImpl;
import org.matsim.vehicles.Vehicle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The scenario element containing information about bike sharing facilities.
 * @author thibautd
 */
public class BikeSharingBikes implements MatsimToplevelContainer {
	private static final Logger log =
		Logger.getLogger(BikeSharingBikes.class);
	public static final String ELEMENT_NAME = "bikeSharingBikes";
	public  Map<Id<Vehicle>, BikesE> ebikes =
		new LinkedHashMap< >();
	public  Map<Id<Vehicle>, Bikes> bikes =
			new LinkedHashMap< >();
	
	public  Map<Id<Vehicle>, Bikes> bikesOrig =
			new LinkedHashMap< >();
	public  Map<Id<Vehicle>, BikesFF> bikesFF =
			new LinkedHashMap< >();
	
	public  Map<Id<Vehicle>, BikesFF> bikesFFOrig =
			new LinkedHashMap< >();
	
	public  Map<Id<Vehicle>, BikesE> ebikeOrig =
			new LinkedHashMap< >();
	
//	public  Map<Id<Vehicle>, BikesE> ptRouter =
//			new LinkedHashMap< >();
	
	public TransitRouterImpl trImpl = null;

//	private ObjectAttributes EAttributes = new ObjectAttributes();
//	private  ObjectAttributes Attributes = new ObjectAttributes();
//	private  ObjectAttributes AttributesFF = new ObjectAttributes();
//
//	private QuadTreeRebuilder<BikesE> quadTreeBuilderE = new QuadTreeRebuilder< >();
//	private QuadTreeRebuilder<Bikes> quadTreeBuilderC = new QuadTreeRebuilder< >();
//	private QuadTreeRebuilder<BikesFF> quadTreeBuilderFF = new QuadTreeRebuilder< >();

	private Map<String, String> metadata = new LinkedHashMap< >();
	
	void addFacility( final BikesE ebike ) {
		ebikes.put(ebike.getBikeId() , ebike);
		ebikeOrig.put(ebike.getBikeId(), ebike);
	}
	
	void addFacility( final Bikes bike ) {
		bikes.put(bike.getBikeId() , bike);
		bikesOrig.put(bike.getBikeId() , bike);
	}
	
	void addFacility( final BikesFF ffbike ) {
		bikesFF.put(ffbike.getBikeId() , ffbike);
		bikesFFOrig.put(ffbike.getBikeId() , ffbike);
	}
	
	public TransitRouterImpl generatePTRouterForBS (Scenario sc)
	{
		if (this.trImpl == null)
		{
			TransitRouterConfig ctr = new TransitRouterConfig(sc.getConfig());
			this.trImpl = new TransitRouterImpl(ctr, sc.getTransitSchedule());
		}
		
		return this.trImpl;
	}

//	public Map<Id<Vehicle>, BikesE> getOrigEFacilities() {
//		return ebikeOrig;
//	}
//
//	public Map<Id<Vehicle>, Bikes> getOrigFacilities() {
//		return bikesOrig;
//	}
//
//	public Map<Id<Vehicle>, BikesFF> getOrigFFFacilities() {
//		return bikesFFOrig;
//	}
	
	public Map<Id<Vehicle>, BikesE> getEFacilities() {
		return ebikes;
	}
	
	public Map<Id<Vehicle>, Bikes> getFacilities() {
		return bikes;
	}
	
	Map<Id<Vehicle>, BikesFF> getFFFacilities() {
		return bikesFF;
	}
	
//	/**
//	 * may not always return the same instance!
//	 */
//	public QuadTree<BikesE> getCurrentQuadTreeE() {
//		return quadTreeBuilderE.getQuadTree();
//	}
//
//	public QuadTree<Bikes> getCurrentQuadTreeC() {
//		return quadTreeBuilderC.getQuadTree();
//	}
//
//	public QuadTree<BikesFF> getCurrentQuadTreeFF() {
//		return quadTreeBuilderFF.getQuadTree();
//	}

	@Override
	public BikeSharingBikesFactory getFactory() {
		return new BikeSharingBikesFactory() 
		{
			@Override
			public BikesE createBikesE(BikesE ebike)
			{
				return new BikeImplE(ebike);
			}
			@Override
			public Bikes createBikesC(Bikes bike) 
			{
				return new BikeImpl(bike);
			}
			@Override
			public Bikes createBikesC(Id bikesId, int timeOfLastAct, boolean infoIfBikeInStation, boolean isEType,
					Id station, Id origStation) {
				
				return new BikeImpl(bikesId, timeOfLastAct, infoIfBikeInStation, isEType, station, origStation);
			}
			
			@Override
			public BikesFF createBikesFF(BikesFF bike)
			{
				return new BikeFFImpl(bike);
			}
			
			@Override
			public BikesFF createBikesFF(Id bikesId, double x, double y, Id linkId) {
				
				return new BikeFFImpl(bikesId, x,y, linkId);

			}
			@Override
			public BikesFF createBikesFF(Id id, Coord coord, Id linkId) {
				return new BikeFFImpl(id, coord, linkId);
			}
			
			@Override
			public BikesE createBikesE(Id id, int time, double stateOfCharge, boolean infoIfBikeInStation,
					double ohmicResistance, double voltage, double batteryChargeCapacity, Id station,
					double kmFullToEmpty, boolean isEType, Id origStation, double origStateOfCharge) {

				return new BikeImplE(id, time, stateOfCharge, infoIfBikeInStation,
						ohmicResistance, voltage, batteryChargeCapacity, station,
						kmFullToEmpty, isEType, origStation, origStateOfCharge);
			}
		};
	}

//	public ObjectAttributes getEFacilitiesAttributes() {
//		return EAttributes;
//	}
//
//	public ObjectAttributes getFacilitiesAttributes() {
//		return Attributes;
//	}
//
//	public ObjectAttributes getFFFacilitiesAttributes() {
//		return AttributesFF;
//	}

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

