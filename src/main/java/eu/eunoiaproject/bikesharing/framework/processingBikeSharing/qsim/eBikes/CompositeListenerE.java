/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes;

import java.util.ArrayList;
import java.util.List;

import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacility;


public class CompositeListenerE implements EBikeSharingManagerListener {
	private final List<EBikeSharingManagerListener> listeners = new ArrayList<EBikeSharingManagerListener>();

	public void addListener( final EBikeSharingManagerListener l ) {
		listeners.add(l);
	}
	
	@Override
	public void handleChange(BikeSharingFacility f, boolean isTaking) {
		for ( EBikeSharingManagerListener l : listeners )
		{
			l.handleChange( f, isTaking );
		}
	}
	
	


}
