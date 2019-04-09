package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.relocation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.utils.geometry.CoordUtils;

import eu.eunoiaproject.bikesharing.framework.eBikeHandling.TakeABike;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingBikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.Bikes;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BikesE;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.EBikeSharingConfigGroup;

public class RelocationHandler {
	

	
	/***************************************************************************/
	private RelocationHandler(BikeSharingFacilities facilities, BikeSharingBikes b)
	/***************************************************************************/
	{

	}

	/***************************************************************************/
	public static void actStation (BikeSharingFacility considered, String whichType, 
			boolean stationIsEmpty, int n, double time, 
			BikeSharingFacilities facilities, BikeSharingBikes b
			)
	/***************************************************************************/
	{
		BikeSharingFacility[] relocStat = 
				RelocateBikes.getRelocationStations(considered, whichType, stationIsEmpty, facilities);
		
		if (stationIsEmpty)
		{
			if (relocStat[1].getFreeParkingSlots() > n/2 && relocStat[2].getFreeParkingSlots() > n/2 && relocStat[0].getFreeParkingSlots() > n)
			{
				actStationType1(relocStat, time, n, facilities, b);
			}
			
		}
		else
		{
			if (relocStat[1].getNumberOfAvailableBikes() > n/2 && relocStat[2].getNumberOfAvailableBikes() > n/2 && relocStat[0].getFreeParkingSlots() > n)
			{
				actStationType2(relocStat, time, n, facilities, b);
			}
		}
	}
	
	/***************************************************************************/
	public static void actStationType2(
			BikeSharingFacility[] relocStat, double time, int n,
			BikeSharingFacilities facilities, BikeSharingBikes b)
	/***************************************************************************/
	{
		if (facilities.station_in_relocation == null)
		{
			facilities.station_in_relocation = new ArrayList <BikeSharingFacility>();
		}
		
		//considered station gets bikes
		String[] bikes0 = relocStat[0].getCycles_in_station(); //werden (später) erweitert
		String statB = time + " actEmptyStation: (" + relocStat[0].getStationId().toString() + ") TakeBike from: " + relocStat[1].getId().toString() + ":;";
		String statC = time + " actEmptyStation: (" + relocStat[0].getStationId().toString() + ") TakeBike from: " + relocStat[2].getId().toString() + ":;";
		
		Coord A = relocStat[0].getCoord();
		Coord B = relocStat[1].getCoord();
		Coord C = relocStat[2].getCoord();
		double distAB = CoordUtils.calcEuclideanDistance(A,B);
		double distAC = CoordUtils.calcEuclideanDistance(A,C);
		double dist = Math.max(distAB, distAC);
		double distTime = dist/8.5; //8.5 m/s = 30.6 km/h
		
		String [] bikesStatA = new String [n/2];
		String [] bikesStatB = new String [n/2];
		
		String [] extend;
		if (bikes0 == null)
		{
			extend = new String[n];
		}
		else
		{
			extend = new String[n+bikes0.length];
		}
		
		if (relocStat[0].getStationType().contains("e"))
		{
			for (int i = 0; i < n/2 ; i++ )
			{
				BikesE eone = TakeABike.takeEBike(relocStat[1], b.ebikes, time, null, facilities, null);
				if (eone == null)
				{
					System.out.println("ToCheck - Hebenstreit");
				}
				statB = statB + eone.toString() + ";";
				extend[i] = eone.getId().toString();
				
				BikesE etwo = TakeABike.takeEBike(relocStat[2], b.ebikes, time, null, facilities, null);
				if (etwo == null)
				{
					System.out.println("ToCheck - Hebenstreit");
				}
				statC = statC + etwo.toString() + ";";
				extend[n-1-i] = etwo.getId().toString();
			}
		}
		
		else
		{
			for (int i = 0; i < n/2 ; i++ )
			{
				Bikes cone = TakeABike.takeCBike(relocStat[1], b.bikes, time, facilities);
				if (cone == null)
				{
					System.out.println("ToCheck - Hebenstreit");
				}
				cone.setTime(time+distTime-1);
				statB = statB + cone.getId().toString() + ";";
				extend[i] = cone.getId().toString();
				
				Bikes ctwo = TakeABike.takeCBike(relocStat[2], b.bikes, time, facilities);
				statC = statC + ctwo.getId().toString() + ";";
				ctwo.setTime(time+distTime-1);
				extend[n-i-1] = ctwo.getId().toString();
			}
		}
		
		if (bikes0 != null)
		{
			for (int i = 0; i < bikes0.length; i++)
			{
				extend[n+i] = bikes0[i];
			}
		}
		statB = statB + "\n";
		statC = statC + "\n";
		writeStringOnFile(statB);
		writeStringOnFile(statC);
		relocStat[0].setBikesInRelocation(extend);
		relocStat[0].setOngoingRelocation(true);
		relocStat[0].setTimeOfRelocation(time + distTime);
		facilities.station_in_relocation.add(relocStat[0]);
	}
	
	
	/***************************************************************************/
	public static void actStationType1(
			BikeSharingFacility[] relocStat, double time, int n, 
			BikeSharingFacilities facilities, BikeSharingBikes b)
	/***************************************************************************/
	{
		
		if (facilities.station_in_relocation == null)
		{
			facilities.station_in_relocation = new ArrayList <BikeSharingFacility>();
		}
		
		//considered station looses bikes (gets parkings)
		Coord A = relocStat[0].getCoord();
		Coord B = relocStat[1].getCoord();
		Coord C = relocStat[2].getCoord();
		double distAB = CoordUtils.calcEuclideanDistance(A,B);
		double distAC = CoordUtils.calcEuclideanDistance(A,C);
		double dist = Math.max(distAB, distAC);
		double distTime = dist/8.5; //8.5 m/s = 30.6 km/h
		
		String [] extendB = new String[n/2];
		String [] extendC = new String[n/2];
		String statA = time + " actFullStation (" + relocStat[0].getId().toString() + "): TakeBike from" + relocStat[0].getId().toString() + ":;";
		
		if (relocStat[0].getStationType().contains("e"))
		{
			for (int i = 0; i < n ; i++ )
			{
				BikesE eone = TakeABike.takeEBike(relocStat[0], b.ebikes, time, null, facilities, null);
				statA = statA + eone.getId().toString() + ";";

				if (i < n/2 )
				{
					extendB[i] = eone.getId().toString();
					eone.setTime(time+(distAB/8.5)-1);
				}
				else
				{
					extendC[i-n/2] = eone.getId().toString();
					eone.setTime(time+(distAC/8.5)-1);
				}
			}
		}

		else
		{
			for (int i = 0; i < n ; i++ )
			{
				Bikes eone = TakeABike.takeCBike(relocStat[0], b.bikes, time, facilities);
				statA = statA + eone.getId().toString() + ";";
				
				if (i < n/2 )
				{
					extendB[i] = eone.getId().toString();
					eone.setTime(time+(distAB/8.5)-1);
				} 
				else
				{
					extendC[i-n/2] = eone.getId().toString();
					eone.setTime(time+(distAC/8.5)-1);
				}
			}
		}
		statA = statA + "\n";
		writeStringOnFile(statA);
				
		relocStat[1].setBikesInRelocation(extendB);
		relocStat[1].setOngoingRelocation(true);
		relocStat[1].setTimeOfRelocation(time + distTime);
		
		relocStat[2].setBikesInRelocation(extendC);
		relocStat[2].setOngoingRelocation(true);
		relocStat[2].setTimeOfRelocation(time + distTime);
		
		facilities.station_in_relocation.add(relocStat[1]);
		facilities.station_in_relocation.add(relocStat[2]);
	}
	
	 public static void writeStringOnFile(String message) {
		 	String fileName = "F:/BikeRouting/Wien_Gesamt/Wien_Output/relocation.txt";
		 	try 
		    {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		    out.println(message);
		    out.close();
		     } catch (IOException e) 
		 	{
		    }
	    }
}