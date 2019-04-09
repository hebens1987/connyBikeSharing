/* *********************************************************************** *
 * project: org.matsim.*
 * BikeSharingConfigGroup.java
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

import org.matsim.core.config.ConfigGroup;


import java.util.Map;
import java.util.TreeMap;

/**
 * Stores the parameters from the config file for a bike sharing simulation.
 * @author thibautd
 */
public class EBikeSharingConfigGroup extends ConfigGroup {
	
	public static final String USE_PROB ="useProbability";
	private static final String PROB_TAKE ="probabilityTake";
	private static final String PROB_RETURN ="probabilityReturn";
	private static final String FACILITIES = "facilitiesFile";
	private static final String BS_BIKES = "bikeSharingBikes";
	private static final String  RELOC_INTERVAL = "relocationInterval";
	
	public static final String GROUP_NAME = "bikeSharingFacilities";

	private Integer statsWriterFrequency = null;
	private String bikeSharingBikes = null;
	private String facAttr = null;
	private String facilitiesFile = null;
	int freeParkingSlots = 0;
	int availableBikes = 0;
	double soc = 0;
	double ohmicResistance = 0; 
	double voltage = 0; 
	double batteryCapacity = 0;
	double kmFullToEmpty = 0;
	int totalBikeSlots = 0;
	
	String  useProbability;
	String  probabilityTake;
	String relocInterval;
	String probabilityReturn;
	String cyclesInStation = null;
	
	
	public EBikeSharingConfigGroup() 
	{
		super( GROUP_NAME );
	}

	/***************************************************************************/
	@Override
	public final void addParam(final String key, final String value) 
	/***************************************************************************/
	{
		
		System.out.println(key + " " + value);
		
		// emulate previous behavior of reader (ignore null values at reading). td Apr'15
		if ( "null".equalsIgnoreCase( value ) ) return;

		if (USE_PROB.equals(key)) 
		{
			setUseProbability(value);
		} 
		else if (PROB_TAKE.equals(key)) 
		{
			setProbabilityTake(value);
		} 
		
		else if (RELOC_INTERVAL.equals(key)) 
		{
			setRelocInterval(value);
		} 
		else if (PROB_RETURN.equals(key)) 
		{
			setProbabilityReturn(value);
		} 
		else if (FACILITIES.equals(key)) 
		{
			setFacilitiesFile(value);
		} 
		else if (BS_BIKES.equals(key)) 
		{
			setBikeSharingBikes(value);
		} 
		else {
			throw new IllegalArgumentException(key);
		}
	}

	/***************************************************************************/
	@Override
	public final TreeMap<String, String> getParams() 
	/***************************************************************************/
	{
		TreeMap<String, String> map = new TreeMap<>();
		map.put(USE_PROB, getValue(USE_PROB));
		map.put(PROB_TAKE, getValue(PROB_TAKE));
		map.put(PROB_RETURN, getValue(PROB_RETURN));
		return map;
	}

	/***************************************************************************/
	@Override
	public final String getValue(final String key) 
	/***************************************************************************/
	{
		if (USE_PROB.equals(key)) 
		{
			return getUseProbability();
		} 
		else if (PROB_TAKE.equals(key)) 
		{
			return getProbabilityTake();
		} 
		else if (RELOC_INTERVAL.equals(key)) 
		{
			return getRelocInterval();
		} 
		else if (PROB_RETURN.equals(key)) 
		{
			return getProbabilityReturn();
		} 
		else if (FACILITIES.equals(key)) 
		{
			return getFacilitiesFile();
		} 
		else if (BS_BIKES.equals(key)) 
		{
			return getBikeSharingBikes();
		} 
		else {
			throw new IllegalArgumentException(key);
		}
	}

	@Override
	public Map<String, String> getComments() {
		final Map<String, String> comments = super.getComments();

		comments.put( "Facilities: " + getFacilitiesFile(), FACILITIES );
		comments.put( "Bike Sharing Bikes: " + getBikeSharingBikes(), BS_BIKES );
		comments.put( "Probability gets used:" + getUseProbability(), USE_PROB );

		return comments;
	}
	

	public String getFacilitiesAttributesFile() {
		return this.facAttr;
	}

	public void setFacilitiesAttributesFile(final String facAttr) {
		this.facAttr = facAttr;
	}
	
	public String getBikeSharingBikes() {
		return this.bikeSharingBikes;
	}

	public void setBikeSharingBikes(final String bikeSharingBikes) {
		this.bikeSharingBikes = bikeSharingBikes;
	}
	
	
	public String getFacilitiesFile() {
		return this.facilitiesFile;
	}

	public void setFacilitiesFile(final String facilitiesFile) {
		this.facilitiesFile = facilitiesFile;
	}
	
	public String getUseProbability() {
		return this.useProbability;
	}

	public void setUseProbability(final  String useProbability) {
		this.useProbability = useProbability;
	}
	
	public String getProbabilityTake() {
		return this.probabilityTake;
	}

	public void setProbabilityTake(final  String  probabilityTake) {
		this.probabilityTake = probabilityTake;
	}
	
	public String getRelocInterval() {
		return this.relocInterval;
	}

	public void setRelocInterval(final  String  relocInterval) {
		this.relocInterval = relocInterval;
	}
	
	public String getProbabilityReturn() {
		return this.probabilityReturn;
	}

	public void setProbabilityReturn(final  String probabilityReturn) {
		this.probabilityReturn = probabilityReturn;
	}

	public Integer getStatsWriterFrequency() {
		return this.statsWriterFrequency;
	}

	public void setStatsWriterFrequency(final String statsWriterFrequency) {
		this.statsWriterFrequency = Integer.parseInt( statsWriterFrequency );
	}
	

}


