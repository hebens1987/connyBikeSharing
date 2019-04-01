/* *********************************************************************** *
 * project: org.matsim.*
 * BikeSharingConstants.java
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
package eu.eunoiaproject.bikesharing.framework;

/**
 * Defines some constants used thoughout the code.
 * @author thibautd
 */
public class EBConstants {
	/**
	 * The mode of bike sharing legs/trips
	 */
	public static final String MODE = "eBikeSharing";
	public static final String MODE_PT = "eBikeSharing_pt";
	public static final String MODE_FF = "ffBikeSharing";

	/**
	 * The activity type of a bike pick-up or drop-off
	 */
	public static final String INTERACTION_TYPE_BS = "eb_interaction";
	public static final String INTERACTION_TYPE_FF = "ff_interaction";
	
	public static final String WAIT = "wait";
	
	public static final String BS_BIKE = "bs";
	public static final String BS_E_BIKE = "e_bs";
	public static final String BS_WALK = "bs_walk";
	public static final String BS_BIKE_FF = "bs_ff";
	public static final String BS_WALK_FF = "bs_walk_ff";
	
	public static final int TIME_TAKE = 120;
	public static final int TIME_RETURN = 30;
}

