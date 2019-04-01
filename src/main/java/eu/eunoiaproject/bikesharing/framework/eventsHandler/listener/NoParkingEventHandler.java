package eu.eunoiaproject.bikesharing.framework.eventsHandler.listener;

import java.util.ArrayList;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

import eu.eunoiaproject.bikesharing.framework.events.NoParkingSpaceEvent;
import eu.eunoiaproject.bikesharing.framework.eventsHandler.handler.NoParkingSpotEventHandler;

public class NoParkingEventHandler implements NoParkingSpotEventHandler {

	ArrayList<NoParkingInfo> noParking = new ArrayList<NoParkingInfo>();
	
	@Override
	public void reset(int iteration) {
		// TODO Auto-generated method stub
		noParking = new ArrayList<NoParkingInfo>();
		
	}
	
	public ArrayList<NoParkingInfo> info() {
		
		return this.noParking;
	}
	
	public class NoParkingInfo {
		
		Id<Link> linkId = null;
		String type = null;
		String person = null;
		
		public String toString() {
			
			return linkId.toString() + " " + type + "Person: " + person;
		}		
		
	}

	@Override
	public void handleEvent(NoParkingSpaceEvent event) {
		// TODO Auto-generated method stub
		NoParkingInfo info = new NoParkingInfo();
		info.linkId = event.getLinkId();
		info.type = event.getBikesharingType();
		info.person = event.getPerson().toString();
		noParking.add(info);
		
	}

}
