/* *********************************************************************** *
 * project: org.matsim.													   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,     *
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

/**
 * @author hebens & nagel
 */
package eu.eunoiaproject.bikesharing.examples.example03configurablesimulation;

import org.matsim.core.config.ReflectiveConfigGroup;

public final class BikeSharingConfigGroup extends ReflectiveConfigGroup {
	public static final String NAME="bikeSharing" ;

	public BikeSharingConfigGroup(){
		super( NAME );
	}
	// ---
	public enum RunType { standard, debug }
	private RunType runType = RunType.standard ;
	public RunType getRunType(){
		return runType;
	}
	public void setRunType( RunType runType ){
		this.runType = runType;
	}


}
