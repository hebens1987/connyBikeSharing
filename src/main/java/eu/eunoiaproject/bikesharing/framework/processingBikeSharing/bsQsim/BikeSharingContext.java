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
package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.util.LeastCostPathCalculator;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;

public class BikeSharingContext{
	public QSim getqSim(){
		return qSim;
	}

	public LeastCostPathCalculator getWalkPathCalculator(){
		return walkPathCalculator ;
	}

	public IKK_ObjectAttributesSingleton getInstance(){
		return instance;
	}

	static class Builder {
		private LeastCostPathCalculator standardBikePathCalculator;
		private LeastCostPathCalculator sharedBikePathCalculator;
		private LeastCostPathCalculator sharedEBikePathCalculator;
		private QSim qSim;
		private LeastCostPathCalculator walkPathCalculator;
		private LeastCostPathCalculator direktBikePathCalculator;
		private IKK_ObjectAttributesSingleton instance;

		Builder setStandardBikePathCalculator( LeastCostPathCalculator calc ) {
			standardBikePathCalculator = calc ;
			return this ;
		}
		Builder setDirektBikePathCalculator( LeastCostPathCalculator calc ) {
			direktBikePathCalculator = calc ;
			return this ;
		}
		Builder setSharedBikePathCalculator( LeastCostPathCalculator calc ) {
			sharedBikePathCalculator = calc ;
			return this ;
		}
		Builder setSharedEBikePathCalculator( LeastCostPathCalculator calc ) {
			sharedEBikePathCalculator = calc ;
			return this ;
		}
		BikeSharingContext build() {
			return new BikeSharingContext( standardBikePathCalculator, sharedBikePathCalculator, sharedEBikePathCalculator, directBikePathCalculator,
					qSim, walkPathCalculator, instance) ;
		}

		Builder setQSim( QSim qSim ){
			this.qSim = qSim;
			return this ;
		}
		
		Builder setWalkPathCalculator( LeastCostPathCalculator routeAlgo ){
			this.walkPathCalculator = routeAlgo;
			return this ;
		}

		Builder setDirectBikePathCalculator( LeastCostPathCalculator routeAlgo ){
			this.direktBikePathCalculator = routeAlgo;
			return this ;
		}
		Builder setObjectAttributesSingleton( IKK_ObjectAttributesSingleton instance ){
			this.instance = instance;
			return this ;
		}
	}

	private LeastCostPathCalculator standardBikePathCalculator ;
	private LeastCostPathCalculator sharedBikePathCalculator ;
	private LeastCostPathCalculator sharedEBikePathCalculator;
	private static LeastCostPathCalculator directBikePathCalculator;
	private final QSim qSim;
	private final LeastCostPathCalculator walkPathCalculator;
	private final IKK_ObjectAttributesSingleton instance;

	private BikeSharingContext( LeastCostPathCalculator standardBikePathCalculator, LeastCostPathCalculator sharedBikePathCalculator,
			LeastCostPathCalculator sharedEBikePathCalculator, LeastCostPathCalculator directBikePathCalculator, QSim qSim, LeastCostPathCalculator walkPathCalculator, IKK_ObjectAttributesSingleton instance){
		this.standardBikePathCalculator = standardBikePathCalculator;
		this.sharedBikePathCalculator = sharedBikePathCalculator;
		this.sharedEBikePathCalculator = sharedEBikePathCalculator;
		BikeSharingContext.directBikePathCalculator = directBikePathCalculator;
		this.qSim = qSim;
		this.walkPathCalculator = walkPathCalculator;
		this.instance = instance;
	}

	public LeastCostPathCalculator getStandardBikePathCalculator(){
		return standardBikePathCalculator;
	}
	public LeastCostPathCalculator getDirectBikePathCalculator(){
		return directBikePathCalculator;
	}
	public LeastCostPathCalculator getSharedEBikePathCalculator(){
		return sharedEBikePathCalculator;
	}
	public LeastCostPathCalculator getSharedBikePathCalculator(){
		return sharedBikePathCalculator;
	}
}
