package eu.eunoiaproject.bikesharing.framework.eventsHandler.handler;


import org.matsim.core.events.handler.EventHandler;

import eu.eunoiaproject.bikesharing.framework.events.NoParkingSpaceEvent;

public interface NoParkingSpotEventHandler extends EventHandler {
	
	public void handleEvent (NoParkingSpaceEvent event);
	
}