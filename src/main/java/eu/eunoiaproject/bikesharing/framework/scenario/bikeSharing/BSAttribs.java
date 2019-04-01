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

package eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing;

import java.util.ArrayList;
import java.util.List;
//import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import eu.eunoiaproject.bikesharing.framework.scenario.bicycles.BicycleConfigGroup;



public class BSAttribs
{
	int userGroupType = -1;
	double searchRadius = 0;
	double maxSearchRadius = 0;
	double maxBSTripLength = 0;
	double ptSearchRadius = 0;
	public List<BSAtt> bsAttribs;
	
	//private static final Logger log = Logger.getLogger(BSAttribs.class);

	public List<BSAtt> getBSAttribs(
			Scenario scenario) 
	{
		BicycleConfigGroup configGr = (BicycleConfigGroup) scenario.getConfig().getModule("bicycleAttributes");
		String ug_type = configGr.getValue("bikeTypeGroup_Input");
		String searchRadius_forBS = configGr.getValue("searchRadius_forBS");
        String maxSearchRadius_forBS = configGr.getValue("maxSearchRadius_forBS");
        String maxTripLength_forBS = configGr.getValue("maxTripLength_forBS");
        
        List<BSAtt> list;
        if(bsAttribs == null)
        {
        	list = new ArrayList <BSAtt>();
        }
        else
        {
        	list = bsAttribs;
        }
                
        String[] ug = ug_type.split(",");
        String[] sR = searchRadius_forBS.split(",");
        String[] mSR = maxSearchRadius_forBS.split(",");
        String[] mTL = maxTripLength_forBS.split(",");
        String[] ptSR = maxTripLength_forBS.split(",");
        
        for (int i = 0; i < ug.length; i++)
        {
        	BSAtt bs = new BSAtt();
        	bs.userGroupType = Integer.parseInt(ug[i]);
        	bs.maxBSTripLength = Double.parseDouble(mTL[i]);
        	bs.maxSearchRadius = Double.parseDouble(mSR[i]);
        	bs.searchRadius = Double.parseDouble(sR[i]);
        	bs.ptSearchRadius = Double.parseDouble(ptSR[i]); //1.41 = square(1² +1²)
        	list.add(bs);
        }
        
        bsAttribs = list;
        return list;
	}
}
