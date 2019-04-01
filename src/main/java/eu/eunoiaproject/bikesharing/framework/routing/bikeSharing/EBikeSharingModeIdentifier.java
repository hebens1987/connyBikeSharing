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

package eu.eunoiaproject.bikesharing.framework.routing.bikeSharing;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.MainModeIdentifier;

import eu.eunoiaproject.bikesharing.framework.EBConstants;

import java.util.List;

public class EBikeSharingModeIdentifier implements MainModeIdentifier {
	private final MainModeIdentifier delegate;

	public EBikeSharingModeIdentifier(
			MainModeIdentifier delegate) {
		this.delegate = delegate;
	}

	@Override
	public String identifyMainMode(
			final List<? extends PlanElement> tripElements) {
		boolean hadEBikeSharing = false;
		boolean hadEBikeSharingPT = false;
		boolean hadEBikeSharingFF = false;
		for ( PlanElement pe : tripElements ) {
			if ( pe instanceof Leg ) {
				final Leg l = (Leg) pe;
				if ( l.getMode().equals( TransportMode.transit_walk )) {return TransportMode.transit_walk;}
				if (l.getMode().equals( TransportMode.pt ) ) {	return TransportMode.pt;}
				
			}
		if (!( pe instanceof Leg )) {
				// identify bike sharing using interactions, as they are used
				// to tag "direct walk" BS trips
				final Activity act = (Activity) pe;
				if ( act.getType().contains( EBConstants.INTERACTION_TYPE_BS ) ) {
					hadEBikeSharing = true;
				}
				
				if ( act.getType().contains( EBConstants.INTERACTION_TYPE_FF ) ) {
					hadEBikeSharingFF = true;
				}

			}
		}

		if ( hadEBikeSharing ) {
			// there were bike sharing legs but no transit walk
			return EBConstants.MODE;
		}
		
		if ( hadEBikeSharingPT ) {
			// there were bike sharing legs but no transit walk
			return EBConstants.MODE_PT;
		}
		
		if ( hadEBikeSharingFF ) {
			// there were bike sharing legs but no transit walk
			return EBConstants.MODE_FF;
		}

		return delegate.identifyMainMode( tripElements );
	}
}
