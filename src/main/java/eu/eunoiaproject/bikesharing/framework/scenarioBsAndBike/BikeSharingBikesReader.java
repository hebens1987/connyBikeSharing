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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.io.MatsimXmlParser;
import org.matsim.core.utils.misc.Counter;
import org.xml.sax.Attributes;

import eu.eunoiaproject.freeFloatingBS.BikesFF;

import java.util.Stack;

/**
 * Fills a {@link  bikeSharingBikes} container, and puts it in a Scenario,
 * under the {@link bikeSharingBikes#ELEMENT_NAME} name.
 * The container can be retrieved with
 * <tt>
 * EBikeSharingFacilities fs = (EBikeSharingFacilities) scenario.getScenarioElement( EBikeSharingFacilities.ELEMENT_NAME );
 * </tt>
 * @author thibautd & hebenstreit
 */
public class BikeSharingBikesReader extends MatsimXmlParser {

	private final Scenario scenario;

	final public BikeSharingBikes facilities = new BikeSharingBikes();
	private Counter counter;
	private Counter counter2;
	private Counter counter3;

	public BikeSharingBikesReader(final Scenario scenario) 
	{
		this.scenario = scenario;
	}

	@Override
	public void startTag(
			final String name,
			final Attributes atts,
			final Stack<String> context) 
	{
		if ( name.equals( "bikeSharingBikes" ) ) 
		{
			counter = new Counter( "reading bike sharing Bike # " );
			counter2 = new Counter( "reading ebike sharing Bike # " );
			counter3 = new Counter( "reading ffbike sharing Bike # " );
		}
		if ( name.equals( "ebike" ) ) 
		{
			counter2.incCounter();

			facilities.addFacility
			(	facilities.getFactory().createBikesE(
						Id.create(atts.getValue("bikeId"), BikesE.class),
						0, //Start of Simulation -> time = 0 seconds
						Double.parseDouble(atts.getValue("stateOfCharge")),
						true, //at start of Simulation - all bikes in a station
						Double.parseDouble(atts.getValue("ohmicResistance")),
						Double.parseDouble(atts.getValue("voltage")),
						Double.parseDouble(atts.getValue("batteryChargeCapacity")),
						Id.create(atts.getValue("stationId"), BikeSharingFacility.class),
						Double.parseDouble(atts.getValue("kmFullToEmpty")),
						true,							
						Id.create(atts.getValue("stationId"), BikeSharingFacility.class),
						Double.parseDouble(atts.getValue("stateOfCharge"))));		
		}
		else if ( name.equals("bike"))
		{
			counter.incCounter();
			
			facilities.addFacility
			(		facilities.getFactory().createBikesC(
							Id.create(atts.getValue("bikeId"), Bikes.class),
							0,//Start of Simulation -> time = 0 seconds
							true, //at start of Simulation - all bikes in a station
							false,
							Id.create(atts.getValue("stationId"), BikeSharingFacility.class),
							Id.create(atts.getValue("stationId"), BikeSharingFacility.class)));

		}
		
		else if (name.equals("ffbike"))
		{
			counter3.incCounter();
			
			facilities.addFacility
			(	facilities.getFactory().createBikesFF(
						Id.create(atts.getValue("bikeId"), BikesFF.class),
						Double.parseDouble(atts.getValue("x")),
						Double.parseDouble(atts.getValue("y")),
						Id.create(atts.getValue("linkId"), Link.class)));		
		}


		if ( name.equals( "attribute" ) ) 
		{
			facilities.addMetadata( atts.getValue( "name" ) , atts.getValue( "value" ) );
		}
	}

	@Override
	public void endTag(
			final String name,
			final String content,
			final Stack<String> context) {
		if ( name.equals( "bikeSharingBikes" ) ) {
			counter.printCounter();
			counter2.printCounter();
			counter3.printCounter();
			scenario.addScenarioElement( BikeSharingBikes.ELEMENT_NAME, facilities );
		}
	}

}

