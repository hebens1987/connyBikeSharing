package eu.eunoiaproject.bikesharing.framework.eventsHandler.handler;


import org.matsim.core.events.handler.EventHandler;

import eu.eunoiaproject.bikesharing.framework.events.AgentStartsWaiting;

public interface AgentStartsWaitingHandler extends EventHandler {
	
	public void handleEvent (AgentStartsWaiting event);
	
}