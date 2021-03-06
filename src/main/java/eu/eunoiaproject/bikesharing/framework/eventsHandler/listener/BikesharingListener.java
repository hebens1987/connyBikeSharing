/* *********************************************************************** *
 * project: org.matsim.*
 * AgentStartsWaitingForBikeEvent.java
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
package eu.eunoiaproject.bikesharing.framework.eventsHandler.listener;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.matsim.core.controler.MatsimServices;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.events.IterationStartsEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.listener.IterationStartsListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.io.IOUtils;

import eu.eunoiaproject.bikesharing.framework.eventsHandler.listener.NoParkingEventHandler.NoParkingInfo;
import eu.eunoiaproject.bikesharing.framework.eventsHandler.listener.NoVehicleEventHandler.NoVehicleInfo;
import eu.eunoiaproject.bikesharing.framework.eventsHandler.listener.TwoWayEventsHandler.RentalInfo;


public class BikesharingListener implements StartupListener, IterationEndsListener, IterationStartsListener{
	TwoWayEventsHandler cshandler;

	NoVehicleEventHandler noVehicleHandler;
	NoParkingEventHandler noParkingHandler;
	MatsimServices controler;
	int frequency = 0;
	
	public BikesharingListener(MatsimServices controler, int frequency) {
				
		this.controler = controler;
		this.frequency = frequency;
		
	}
	
	@Override
	public void notifyIterationEnds(IterationEndsEvent event) {
		// TODO Auto-generated method stub
		
		if (event.getIteration() % this.frequency == 0) {
		
		ArrayList<RentalInfo> info = cshandler.rentals();
		
		final BufferedWriter outLink = IOUtils.getBufferedWriter(this.controler.getControlerIO().getIterationFilename(event.getIteration(), "RT_CS"));
		try {
			outLink.write("personID   startTime   endTIme   startLink   distance   accessTime   egressTime	vehicleID");
			outLink.newLine();
		for(RentalInfo i: info) {
			
			
				outLink.write(i.toString());
				outLink.newLine();
			
		}
		outLink.flush();
		outLink.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<NoVehicleInfo> infoNoVehicles = noVehicleHandler.info();
		
		final BufferedWriter outNoVehicle = IOUtils.getBufferedWriter(this.controler.getControlerIO().getIterationFilename(event.getIteration(), "No_Vehicle_Stats.txt"));
		try {
			outNoVehicle.write("linkID	CSType");
			outNoVehicle.newLine();
		for(NoVehicleInfo i: infoNoVehicles) {
			
			
			outNoVehicle.write(i.toString());
			outNoVehicle.newLine();
			
		}
		outNoVehicle.flush();
		outNoVehicle.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<NoParkingInfo> infoNoParking = noParkingHandler.info();
		
		final BufferedWriter outNoParking = IOUtils.getBufferedWriter(this.controler.getControlerIO().getIterationFilename(event.getIteration(), "No_Parking_Stats.txt"));
		try {
			outNoParking.write("linkID	CSType");
			outNoParking.newLine();
		for(NoParkingInfo i: infoNoParking) {
			
			
			outNoParking.write(i.toString());
			outNoParking.newLine();
			
		}
		outNoParking.flush();
		outNoParking.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		event.getServices().getEvents().removeHandler(this.cshandler);
		event.getServices().getEvents().removeHandler(this.noVehicleHandler);
		event.getServices().getEvents().removeHandler(this.noParkingHandler);
		}
		
	}

	@Override
	public void notifyStartup(StartupEvent event) {
		// TODO Auto-generated method stub

        this.cshandler = new TwoWayEventsHandler(event.getServices().getScenario().getNetwork());

		
		this.noVehicleHandler = new NoVehicleEventHandler();	
		
		this.noParkingHandler = new NoParkingEventHandler();
		
	}

	@Override
	public void notifyIterationStarts(IterationStartsEvent event) {
		// TODO Auto-generated method stub
		if (event.getIteration() % this.frequency == 0) {
			event.getServices().getEvents().addHandler(this.cshandler);
			event.getServices().getEvents().addHandler(this.noVehicleHandler);
			event.getServices().getEvents().addHandler(this.noParkingHandler);
		}
		
	}
	
		

}
