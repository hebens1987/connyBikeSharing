package eu.eunoiaproject.bikesharing.framework.eventsHandler.handler;


import org.matsim.core.events.handler.EventHandler;

import eu.eunoiaproject.bikesharing.framework.events.EBikeBatteryGoneEmpty;

public interface EBikeBatteryGoneEmptyEventsHandler extends EventHandler {
	
	public void handleEvent (EBikeBatteryGoneEmpty event);
	
}