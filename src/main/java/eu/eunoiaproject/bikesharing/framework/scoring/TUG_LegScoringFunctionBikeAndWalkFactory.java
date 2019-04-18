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
package eu.eunoiaproject.bikesharing.framework.scoring;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.network.NetworkImpl;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.CharyparNagelActivityScoring;
import org.matsim.core.scoring.functions.CharyparNagelAgentStuckScoring;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.CharyparNagelScoringParametersForPerson;
import org.matsim.core.scoring.functions.SubpopulationCharyparNagelScoringParameters;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;

import javax.inject.Inject;

public class TUG_LegScoringFunctionBikeAndWalkFactory implements ScoringFunctionFactory {
	
	private final Scenario scenario;
	private final CharyparNagelScoringParametersForPerson params;
	//private final IKK_BikeConfigGroup bikeConfigGroup = null;
	//private Person person = null;
	
	/***************************************************************************/
	@Inject
	public
	TUG_LegScoringFunctionBikeAndWalkFactory( final Scenario sc ) 
	/***************************************************************************/
	{
		this.scenario = sc;
		this.params = new SubpopulationCharyparNagelScoringParameters( sc );
		sc.getConfig();
	}
	
	@Inject
	BicycleConfigGroup bikeConfigGroup;
	

	/***************************************************************************/
	@Override
	public ScoringFunction createNewScoringFunction(Person person) 
	/***************************************************************************/
	{
		SumScoringFunction scoringFunctionSum = new SumScoringFunction();
	    //this is the main difference, since we need a special scoring for b-sharing legs

		scoringFunctionSum.addScoringFunction(
	    new TUG_LegScoringFunctionBikeAndWalk( params.getScoringParameters(person),
	    								 this.scenario.getConfig(),
	    								 (NetworkImpl)this.scenario.getNetwork(), person, bikeConfigGroup ) );
		scoringFunctionSum.addScoringFunction(
				new CharyparNagelLegScoring(
						params.getScoringParameters( person ),
						this.scenario.getNetwork())
			    );
		//the remaining scoring functions can be changed and adapted to the needs of the user
		scoringFunctionSum.addScoringFunction(
				new CharyparNagelActivityScoring(
						params.getScoringParameters(
								person ) ) );
		scoringFunctionSum.addScoringFunction(
				new CharyparNagelAgentStuckScoring(
						params.getScoringParameters(
								person ) ) );
	    return scoringFunctionSum;
	  }
}
