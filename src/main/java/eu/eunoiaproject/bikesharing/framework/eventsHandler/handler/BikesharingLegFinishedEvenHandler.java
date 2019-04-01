package eu.eunoiaproject.bikesharing.framework.eventsHandler.handler;


import org.matsim.core.events.handler.EventHandler;

import eu.eunoiaproject.bikesharing.framework.events.BikesharingLegFinishedEvent;

public interface BikesharingLegFinishedEvenHandler extends EventHandler{

	public void handleEvent (BikesharingLegFinishedEvent event);


}
