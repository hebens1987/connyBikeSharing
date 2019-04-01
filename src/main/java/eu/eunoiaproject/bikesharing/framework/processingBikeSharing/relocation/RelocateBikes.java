package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.relocation;

import java.util.ArrayList;
import java.util.List;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacilities;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacility;
import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacilityImpl;

public class RelocateBikes {
	

	
	/***************************************************************************/
	RelocateBikes(BikeSharingFacilities facilities)
	/***************************************************************************/
	{
	}

	
	/***************************************************************************/
	public static BikeSharingFacility[] getRelocationStations (BikeSharingFacility considered, 
			String whichType, boolean stationIsEmpty, BikeSharingFacilities facilities)
	/***************************************************************************/
	{
		//whichType = c; c_pt; e; e_pt
		List <BikeSharingFacility> fac = new ArrayList<BikeSharingFacility>(facilities.getFacilityForRelocation(whichType).values());
		
		BikeSharingFacility stat1;
		BikeSharingFacility stat2;
		List<BikeSharingFacility> facUse = bubbleSort(fac, whichType);
		//station is full, this means bikes need to be removed to other stations
		if (!(stationIsEmpty)) 
		{
			int len = facUse.size();
			stat1 = facUse.get(len-1);
			stat2 = facUse.get(len-2);
		}
		
		
		//station is empty, this means bikes need to be taken to it from other stations
		else
		{
			stat1 = facUse.get(0);
			stat2 = facUse.get(1);
		}
		
		BikeSharingFacility[] bsArray = new BikeSharingFacility[3];
		// 0 = orig station
		// 1 = 1 station
		// 2 = 2 station
		bsArray [0] = considered;
		bsArray [1] = stat1;
		bsArray [2] = stat2;
		
		return bsArray;
		
		
	}
	
	public static List<BikeSharingFacility> bubbleSort(List <BikeSharingFacility> arr, String bikeType)
	{
		//sortiert von klein nach groß (zuerst die leeren, dann die vollen stationen)
	   
	   
		List<BikeSharingFacility> arrUse = new ArrayList <BikeSharingFacility> ();
		for (int i = 0; i < arr.size(); i++)
		{
			if (arr.get(i).getStationType().equals(bikeType))
			{
				arrUse.add(arr.get(i));
			}
		}
		
	   BikeSharingFacility wdTemp = new BikeSharingFacilityImpl();
	   
	   for (int i = 0; i < arrUse.size(); i++)
	   {
	       for (int j = 0; j < arrUse.size()-1; j++)
	       {
	    	   double bikes1 = arrUse.get(i).getNumberOfAvailableBikes();
	    	   double bikesTotal1 = arrUse.get(i).getTotalBikeNumber();
	    	   double bikes2 = arrUse.get(j).getNumberOfAvailableBikes();
	    	   double bikesTotal2 = arrUse.get(j).getTotalBikeNumber();
	    	   double one = bikes1/bikesTotal1;
	    	   double two = bikes2/bikesTotal2;
	    	   
	           if ((one) <= (two))
	           {
	        	wdTemp = arrUse.get(i);
	        	arrUse.set(i, arrUse.get(j));
	        	arrUse.set(j, wdTemp);
	           }
	       }
	   }
	   
	   /*for (int i = 0; i < arrUse.size(); i++)
	   {
		   double a = arrUse.get(i).getNumberOfAvailableBikes();
		   double b = arrUse.get(i).getTotalBikeNumber();
		   double test = a/b;
		   System.out.println(arrUse.get(i).getNumberOfAvailableBikes() + "...." + arrUse.get(i).getTotalBikeNumber() + " ... " + test);
	   }*/
	   
	   return arrUse;
	}

}

	