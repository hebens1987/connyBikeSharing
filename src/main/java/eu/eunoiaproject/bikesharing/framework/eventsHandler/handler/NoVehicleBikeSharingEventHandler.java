package eu.eunoiaproject.bikesharing.framework.eventsHandler.handler;


import org.matsim.core.events.handler.EventHandler;

import eu.eunoiaproject.bikesharing.framework.events.NoVehicleBikeSharingEvent;

public interface NoVehicleBikeSharingEventHandler extends EventHandler {
	
	public void handleEvent (NoVehicleBikeSharingEvent event);
	
}