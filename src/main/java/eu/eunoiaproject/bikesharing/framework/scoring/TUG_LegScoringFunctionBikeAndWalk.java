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



import javax.inject.Inject;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.population.routes.GenericRouteImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.CharyparNagelScoringParameters;

import eu.eunoiaproject.bikesharing.framework.EBConstants;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;


class TUG_LegScoringFunctionBikeAndWalk extends CharyparNagelLegScoring
{	
	@Inject
	BicycleConfigGroup bikeConfigGroup;
	
	//private Config config;
	private Person person;
	private PlanCalcScoreConfigGroup cn;

	//TODO: VerknÃ¼pfen von LegScoringFunctionBikeAndWalk mit IKK_WalkTravelDisutility und IKK_BikeTravelDisutility
	//oder Verbessern von LegScoringFunctionBikeAndWalk --> einarbeiten von IKK_WalkTravelDisutility und IKK_BikeTravelDisutility
	
	/***************************************************************************/
	public TUG_LegScoringFunctionBikeAndWalk(
		  CharyparNagelScoringParameters params,
		  Config config,
		  Network network,
		  Person person,
		  BicycleConfigGroup bikeConfigGroup )
	/***************************************************************************/
	{
		super(params, network);
		this.person = person;
		this.network = network;
		this.bikeConfigGroup = bikeConfigGroup;
		this.cn = (PlanCalcScoreConfigGroup) config.getModule("planCalcScore");

	}
	
	/***************************************************************************/
	private double [] getLegDistAndTime( Leg newLegX )
	/***************************************************************************/
	{
		double[] timeDist = {0,0};
    	TUG_BikeFeltTravelTime feltTime = new TUG_BikeFeltTravelTime(bikeConfigGroup);
		double feltTravelTime = 0;
		double distance = 0;
		if (newLegX.getRoute() instanceof GenericRouteImpl)
		{
			if (newLegX.getRoute().getEndLinkId().equals(newLegX.getRoute().getStartLinkId()))
			{
        		Link link= network.getLinks().get(newLegX.getRoute().getEndLinkId());
				timeDist = feltTime.getLinkTravelDisutility(link, 0, person, null, newLegX.getMode(), link.getLength());
				return timeDist;
			}
			else
			{
				System.out.println("Das sollte nicht passieren!");
			}
			
		}
		LinkNetworkRouteImpl nr = (LinkNetworkRouteImpl)newLegX.getRoute();
		String routeD = newLegX.getRoute().getRouteDescription();
		if (routeD == null) routeD = nr.getRouteDescription();
		

	    if (routeD!=null)
	    {
	    	String[] linksOfRoute = new String[50000];
	    	if (routeD.contains(" "))
	    	{
	    		linksOfRoute = routeD.split("\\s+");
	    	}
	        	
	    	else
	    	{
	    		linksOfRoute[0] = routeD;
	    	}
	    	for (int j = 0; j < linksOfRoute.length; j++)
	    	{

	        	//link array
	        		Id<Link> act = null;
	        		String linkIdToCompare = linksOfRoute[j];
	        		act = Id.createLinkId(linkIdToCompare);
	        		Link link= network.getLinks().get(act);
	        		//System.out.println(link.getId());
	        		timeDist = feltTime.getLinkTravelDisutility(link, 0, person, null, newLegX.getMode(), link.getLength());

	        		if ( (j == 0) )//||(j == linksOfRoute.length-1))
	        		{
	        			timeDist[0] = 0;//timeDist[0]; //Warum wird erstes hier nicht mitgerechnet (Distanz)
	        			timeDist[1] = 0 ; //timeDist[1];
	        		}
	        		else if (j == linksOfRoute.length-1)
	        		{
	        			timeDist[0] = 0;
	        			//timeDist[1] = 0;
	        		}
	        		else if (j == 1)
	        		{
	        			timeDist[0] = 0;
	        		}
	        		feltTravelTime += timeDist[0];
	        		distance += timeDist[1];
	    	}
	    }
	    
	    double mProS = distance/feltTravelTime;

	    if (mProS < 1)
	    {
	    	feltTravelTime = distance/2;
	    }
	    if (mProS > 10)
	    {
	    	feltTravelTime = distance/10;
	    }
	    timeDist[0] = feltTravelTime;
	    timeDist[1] = distance;
	    return timeDist;

	}
	
	/***************************************************************************/
	@Override
	public void handleLeg(Leg leg) 
	/***************************************************************************/
	{
    	this.score = 0;
    	double dist = 0;
    	double feltTravelTime = 0;
    	
		if (leg.getMode().equals(EBConstants.MODE_FF) 
				|| leg.getMode().equals(EBConstants.BS_BIKE )
								|| leg.getMode().equals(EBConstants.BS_E_BIKE )
												|| leg.getMode().equals(TransportMode.bike)) //only pt,transit_walk and car shall not run into
		{
			double[] arr = getLegDistAndTime(leg );
			
	    	if (arr != null)
	    	{
	    		dist = arr[0];
	    		feltTravelTime = arr[1];
	    		leg.getRoute().setDistance(dist);
	    	}
		}
		
		else
		{
			feltTravelTime = leg.getTravelTime();
			dist = leg.getRoute().getDistance();
		}
		
    	double legScore = 0;
    	if (feltTravelTime == 0)
    	{
    		legScore = calcLegScore(leg.getDepartureTime(), leg.getDepartureTime()+leg.getTravelTime(), leg);
    	}
    	else
    	{
    		legScore = calcLegScore(leg.getDepartureTime(), leg.getDepartureTime() + feltTravelTime, leg);
    	}
    	if (legScore == 0 || legScore == Double.POSITIVE_INFINITY)
    	{
    		System.out.println("warum? Hebenstreit");
    	}
		this.score += legScore;
	}

    //TODO: EBike-Nutzen ergaenzen (Hebenstreit)  
	///***************************************************************************/
	@Override
	protected double calcLegScore(
			double departureTime, double arrivalTime, Leg legX)
	/***************************************************************************/
	{	
    	double tmpScore = 0.0D;
    	double travelTime = arrivalTime-departureTime;
    	double dist = legX.getRoute().getDistance();
		
    	if ((legX.getMode().equals(TransportMode.egress_walk))||(legX.getMode().equals(TransportMode.access_walk)))
    	{   
    		tmpScore += getWalkScore(dist, travelTime);
    		//System.out.println("#####################################  WalkScore = " + tmpScore);
    		//System.out.println("Pause - press Key to continue!");
        	//new java.util.Scanner(System.in).nextLine();
    	}    
		
    	if (legX.getMode().contains(TransportMode.walk))
    	{   
    		tmpScore += getWalkScore(dist, travelTime);
    		//System.out.println("#####################################  WalkScore = " + tmpScore);
    		//System.out.println("Pause - press Key to continue!");
        	//new java.util.Scanner(System.in).nextLine();
    	}           		         		
    	else if (legX.getMode().equals(TransportMode.bike))
    	{   
    		tmpScore += getBikeScoreExp(dist, legX.getTravelTime(), legX);
    		//if (dist / travelTime > 8)
    		//{
    		//	System.out.println("Warum nur? Hebenstreit");
    		//}
    	}
    	
    	else if ((EBConstants.BS_BIKE).equals(legX.getMode())) //Hebenstreit
    	{				           			
    		tmpScore += getBSScore(dist, travelTime);
     
    	}
    	
    	else if ((EBConstants.BS_E_BIKE).equals(legX.getMode())) //Hebenstreit
    	{				           			
    		tmpScore += getEBSScore(dist, travelTime);
     
    	}
    	
    	else if ((TransportMode.transit_walk).equals(legX.getMode())) //Hebenstreit
    	{	
    		tmpScore += getWalkScorePt(travelTime);
    	}
    	
    	else if ((TransportMode.pt).equals(legX.getMode())) //Hebenstreit
    	{	
      	
    		tmpScore += getPTScore(dist, travelTime);
    	}
    	
    	else if ((TransportMode.car).equals(legX.getMode())) //Hebenstreit
    	{				           			
    		tmpScore += getCarScore(dist, travelTime);
    	}
    	return tmpScore;
	}
	
	/***************************************************************************/
	private double getPTScore(double distance, double travelTime)
	/***************************************************************************/
	{
		double score = 0.0D;
		ModeParams modeParams = cn.getOrCreateModeParams(TransportMode.pt);
		double utilTrav = modeParams.getMarginalUtilityOfTraveling()/(3600);
		double utilDist = modeParams.getMarginalUtilityOfDistance();
		double constant = modeParams.getConstant();
		
		score += travelTime * utilTrav + distance * utilDist + constant;
		return score;
	}
	
	/***************************************************************************/
	private double getCarScore(double distance, double travelTime)
	/***************************************************************************/
	{
		double score = 0.0D;
		ModeParams modeParams = cn.getOrCreateModeParams(TransportMode.car);
		double utilTrav = modeParams.getMarginalUtilityOfTraveling()/(3600);
		double utilDist = modeParams.getMonetaryDistanceRate();
		double constant = modeParams.getConstant();
		
		score += (travelTime * utilTrav)*2/(1+Math.exp(-distance/12+2)) + distance * utilDist + constant;
		return score;
	}
	


	/***************************************************************************/
	private double getWalkScore(double distance, double travelTime)
	/***************************************************************************/
	{
		double score = 0.0D;
		ModeParams modeParams = cn.getOrCreateModeParams(TransportMode.walk);
		double utilTrav = modeParams.getMarginalUtilityOfTraveling()/(3600);
		double utilDist = modeParams.getMonetaryDistanceRate();
		double constant = modeParams.getConstant();
		double factor = 0;
		double weighting = ((travelTime/3600) *(travelTime/3600))*12.5; //Hebenstreit Parameter
		if (distance < 0.001)
		{
			distance = travelTime /(0.9/1.41) ; //4 km/h
		}
		
		score += (travelTime * utilTrav) * weighting + (distance * utilDist) + constant;
		
		if (distance > 3500)
		{
			factor = (distance - 3500)/3500*5;
			if (factor < 0) {factor = 0;}
		}
		if (score < constant)
		{
			score = constant;
		}
		return score * (1+factor);
	}
	
	/***************************************************************************/
	private double getWalkScorePt(double travelTime)
	/***************************************************************************/
	{
		double score = 0.0D;
		ModeParams modeParams = cn.getOrCreateModeParams(TransportMode.transit_walk);
		double utilTrav = modeParams.getMarginalUtilityOfTraveling()/(3600);
		double constant = modeParams.getConstant();
		double factor = 0;
		double weighting = ((travelTime/3600) *(travelTime/3600))*12.5; //Hebenstreit Parameter
		
		score += (travelTime * utilTrav) * weighting + constant;
		
		if (travelTime > 900)
		{
			factor = (travelTime - 900)/900;
		}
		return score * (1+factor);
	}

	
	/***************************************************************************/            	
	private double getEBSScore(double distance, double travelTime)
	/***************************************************************************/
	{
		double score = 0.0D;
		double scorePercentage = 1;
		ModeParams modeParams = cn.getOrCreateModeParams(EBConstants.BS_BIKE);
		double utilTrav = modeParams.getMarginalUtilityOfTraveling()/(3600)*0.8;
		double utilDist = modeParams.getMarginalUtilityOfDistance()*0.8;
		double constant = modeParams.getConstant();

		double time = 3600 - travelTime;

		if (time >= 0)
		{
			scorePercentage = 1; //unter 1h Mietzeit
		}
		
		if (time < -1800)
		{
			scorePercentage = 4; //Ãœber 1h30 Mietzeit
		}
		
		else
		{
			scorePercentage = 8; //>1h und <1h30 Mietzeit
		}
    	
    	score += (travelTime * utilTrav + distance * utilDist)*scorePercentage + constant;

    	return score;
	}

	/***************************************************************************/            	
	private double getBSScore(double distance, double travelTime)
	/***************************************************************************/
	{
		double score = 0.0D;
		double scorePercentage = 1;
		ModeParams modeParams = cn.getOrCreateModeParams(EBConstants.BS_BIKE);
		double utilTrav = modeParams.getMarginalUtilityOfTraveling()/(3600);
		double utilDist = modeParams.getMarginalUtilityOfDistance();
		double constant = modeParams.getConstant();

		double time = 3600 - travelTime;

		if (time >= 0)
		{
			scorePercentage = 1; //unter 1h Mietzeit
		}
		
		if (time < -1800)
		{
			scorePercentage = 10; //Ãœber 1h30 Mietzeit
		}
		
		else
		{
			scorePercentage = 5; //>1h und <1h30 Mietzeit
		}
    	
    	score += (travelTime * utilTrav + distance * utilDist)*scorePercentage + constant;

    	return score;
	}
	
	/***************************************************************************/            	
	private double getBikeScoreExp(double distance, double travelTime, Leg leg)
	/***************************************************************************/
	{
		double score = 0.0D;
		ModeParams modeParams = cn.getOrCreateModeParams(TransportMode.bike);
		double utilTrav = modeParams.getMarginalUtilityOfTraveling()/(3600);
		double utilDist = modeParams.getMarginalUtilityOfDistance();
		double constant = modeParams.getConstant();
		double factor = 0;
		
		double weighting = (travelTime/3600) *(travelTime/3600)*2.5; //Hebenstreit: Parameter
		
		score += (travelTime * utilTrav)  + ((distance * utilDist)*weighting) + constant;
		
		if (distance > 10000)
		{
			factor = (distance - 10000)/1000/2;
		}
		
		score = score * (1+factor);
		return score;
	}
	 	
}

