package eu.eunoiaproject.bikesharing.framework.eventsHandler.handler;

import org.matsim.core.events.handler.EventHandler;

import eu.eunoiaproject.bikesharing.framework.events.StartRentalEvent;

public interface StartRentalEventHandler extends EventHandler {
	
	public void handleEvent (StartRentalEvent event);

}
