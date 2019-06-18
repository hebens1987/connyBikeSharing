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

import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.agents.BikesharingPersonDriverAgentImpl;
import org.matsim.core.mobsim.qsim.agents.PersonDriverAgentImpl;
import org.matsim.core.mobsim.qsim.agents.TransitAgent;
import org.matsim.core.mobsim.qsim.agents.TransitAgentImpl;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.rental.TakingReturningMethodology;

public class BikesharingAgentFactory implements AgentFactory{
	private final BikeSharingContext bikeSharingContext;
	private final static Logger log = Logger.getLogger(BikesharingAgentFactory.class);

	public BikesharingAgentFactory( BikeSharingContext bikeSharingContext) 
	{this.bikeSharingContext = bikeSharingContext;}

	@Override
	public MobsimDriverAgent createMobsimAgentFromPerson(final Person p) 
	{
		p.getSelectedPlan().setType("other");
		for (int i = 0; i < p.getSelectedPlan().getPlanElements().size(); i ++)
		{
			if (p.getSelectedPlan().getPlanElements().get(i) instanceof Leg)
			{
				Leg leg = (Leg)p.getSelectedPlan().getPlanElements().get(i);
				if ((leg.getMode().equals(EBConstants.BS_BIKE)))
				{
					p.getSelectedPlan().setType(EBConstants.MODE + "_bs");
					//log.warn("BS for Person: " + p.getId().toString());
					break;
				}
				else if ((leg.getMode().equals(EBConstants.BS_E_BIKE)))
				{
					p.getSelectedPlan().setType(EBConstants.MODE + "_ebs");
					//log.warn("BS for Person: " + p.getId().toString());
					break;
				}
				else if (leg.getMode().equals(EBConstants.BS_BIKE_FF))
				{
					p.getSelectedPlan().setType(EBConstants.BS_BIKE_FF);
					log.warn("e-BS for Person: " + p.getId().toString());
					break;
				}
			}
		}
		return new BikesharingPersonDriverAgentImpl(p.getSelectedPlan(), null, bikeSharingContext );
	}
}
