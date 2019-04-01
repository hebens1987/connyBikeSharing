package eu.eunoiaproject.bikesharing.framework.eventsHandler.handler;


import org.matsim.core.events.handler.EventHandler;

import eu.eunoiaproject.bikesharing.framework.events.EndRentalEvent;

public interface EndRentalEventHandler extends EventHandler{
	
	public void handleEvent (EndRentalEvent event);
	
}
