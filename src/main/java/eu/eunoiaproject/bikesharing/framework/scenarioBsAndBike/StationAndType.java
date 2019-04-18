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

/**
 * 
 *hier noch miteinbeziehen 
 *ob Station A zum Abreisezeitpunkt verfügbare Räder
 *ob Station B zum Abreisezeitpunkt verfügbare Stellplätze
 *
 * 
 *
 * @author Hebenstreit
 */
public class StationAndType
{
	
	public BikeSharingFacility station;
	public boolean type;
	public boolean usedAsPtChange = false;
	public double tripDur = 0;

	public StationAndType()
	{}

}
	