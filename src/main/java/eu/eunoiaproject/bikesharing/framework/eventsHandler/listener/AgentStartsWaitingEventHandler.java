package eu.eunoiaproject.bikesharing.framework.eventsHandler.listener;

import java.util.ArrayList;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

import eu.eunoiaproject.bikesharing.framework.events.AgentStartsWaiting;
import eu.eunoiaproject.bikesharing.framework.eventsHandler.handler.AgentStartsWaitingHandler;

public class AgentStartsWaitingEventHandler implements AgentStartsWaitingHandler {

	ArrayList<NoVehicleInfo> noVehicle = new ArrayList<NoVehicleInfo>();
	
	@Override
	public void reset(int iteration) {
		// TODO Auto-generated method stub
		noVehicle = new ArrayList<NoVehicleInfo>();
		
	}

	@Override
	public void handleEvent(AgentStartsWaiting event) {
		// TODO Auto-generated method stub
		NoVehicleInfo info = new NoVehicleInfo();
		info.person = event.getPerson().toString();
		noVehicle.add(info);
		
	}
	
	public ArrayList<NoVehicleInfo> info() {
		
		return this.noVehicle;
	}
	
	public class NoVehicleInfo {
		
		Id<Link> linkId = null;
		String type = null;
		String person = null;
		
		public String toString() {
			
			return linkId.toString() + " " + type + "Person: " + person;
		}
		
		
	}

}