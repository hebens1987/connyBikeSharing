package eu.eunoiaproject.bikesharing.resultsAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class Analysis_Events_File
{
	static double counterBike_V = 0;
	static double counterWalk_V = 0;
	static double counterPt_V = 0;
	static double counterCar_V = 0;
	static double counterPtBs_V = 0;
	static double counterBs_V = 0;
	
	static double counterBike_nV = 0;
	static double counterWalk_nV = 0;
	static double counterPt_nV = 0;
	static double counterCar_nV = 0;
	static double counterBs_nV = 0;
	static double counterPtBs_nV=0;
	
	static double counterBs_V0 = 0;
	static double counterPtBs_V0 = 0;
	static double counterBs_V1 = 0;
	static double counterPtBs_V1 = 0;
	static double counterBs_V2 = 0;
	static double counterPtBs_V2 = 0;
	static double counterBs_V3 = 0;
	static double counterPtBs_V3 = 0;
	static double counterBs_V4 = 0;
	static double counterPtBs_V4 = 0;
	static double counterBs_V5 = 0;
	static double counterPtBs_V5 = 0;
	static double counterBs_V6 = 0;
	static double counterPtBs_V6 = 0;
	static double counterBs_V7 = 0;
	static double counterPtBs_V7 = 0;
	static double counterBs_V8 = 0;
	static double counterPtBs_V8 = 0;
	static double counterBs_V9 = 0;
	static double counterPtBs_V9 = 0;
	static double counterBs_V10 = 0;
	static double counterPtBs_V10 = 0;
	static double counterBs_V11 = 0;
	static double counterPtBs_V11 = 0;
	static double counterBs_V12 = 0;
	static double counterPtBs_V12 = 0;
	static double counterBs_V13 = 0;
	static double counterPtBs_V13 = 0;
	static double counterBs_V14 = 0;
	static double counterPtBs_V14 = 0;
	static double counterBs_V15 = 0;
	static double counterPtBs_V15 = 0;
	static double counterBs_V16 = 0;
	static double counterPtBs_V16 = 0;
	static double counterBs_V17 = 0;
	static double counterPtBs_V17 = 0;
	static double counterBs_V18 = 0;
	static double counterPtBs_V18 = 0;
	static double counterBs_V19 = 0;
	static double counterPtBs_V19 = 0;
	static double counterBs_V20 = 0;
	static double counterPtBs_V20 = 0;
	static double counterBs_V21 = 0;
	static double counterPtBs_V21 = 0;
	static double counterBs_V22 = 0;
	static double counterPtBs_V22 = 0;
	
	static double counterBike_V0 = 0;
	static double counterWalk_V0 = 0;
	static double counterPt_V0 = 0;
	static double counterCar_V0 = 0;
	static double counterBike_V1 = 0;
	static double counterWalk_V1 = 0;
	static double counterPt_V1 = 0;
	static double counterCar_V1 = 0;
	static double counterBike_V2 = 0;
	static double counterWalk_V2 = 0;
	static double counterPt_V2 = 0;
	static double counterCar_V2 = 0;
	static double counterBike_V3 = 0;
	static double counterWalk_V3 = 0;
	static double counterPt_V3 = 0;
	static double counterCar_V3 = 0;
	static double counterBike_V4 = 0;
	static double counterWalk_V4 = 0;
	static double counterPt_V4 = 0;
	static double counterCar_V4 = 0;
	static double counterBike_V5 = 0;
	static double counterWalk_V5 = 0;
	static double counterPt_V5 = 0;
	static double counterCar_V5 = 0;
	static double counterBike_V6 = 0;
	static double counterWalk_V6 = 0;
	static double counterPt_V6 = 0;
	static double counterCar_V6 = 0;
	static double counterBike_V7 = 0;
	static double counterWalk_V7 = 0;
	static double counterPt_V7 = 0;
	static double counterCar_V7 = 0;
	static double counterBike_V8 = 0;
	static double counterWalk_V8 = 0;
	static double counterPt_V8 = 0;
	static double counterCar_V8 = 0;
	static double counterBike_V9 = 0;
	static double counterWalk_V9 = 0;
	static double counterPt_V9 = 0;
	static double counterCar_V9 = 0;
	static double counterBike_V10 = 0;
	static double counterWalk_V10 = 0;
	static double counterPt_V10 = 0;
	static double counterCar_V10 = 0;
	static double counterBike_V11 = 0;
	static double counterWalk_V11 = 0;
	static double counterPt_V11 = 0;
	static double counterCar_V11 = 0;
	static double counterBike_V12 = 0;
	static double counterWalk_V12 = 0;
	static double counterPt_V12 = 0;
	static double counterCar_V12 = 0;
	static double counterBike_V13 = 0;
	static double counterWalk_V13 = 0;
	static double counterPt_V13 = 0;
	static double counterCar_V13 = 0;
	static double counterBike_V14 = 0;
	static double counterWalk_V14 = 0;
	static double counterPt_V14 = 0;
	static double counterCar_V14 = 0;
	static double counterBike_V15 = 0;
	static double counterWalk_V15 = 0;
	static double counterPt_V15 = 0;
	static double counterCar_V15 = 0;
	static double counterBike_V16 = 0;
	static double counterWalk_V16 = 0;
	static double counterPt_V16 = 0;
	static double counterCar_V16 = 0;
	static double counterBike_V17 = 0;
	static double counterWalk_V17 = 0;
	static double counterPt_V17 = 0;
	static double counterCar_V17 = 0;
	static double counterBike_V18 = 0;
	static double counterWalk_V18 = 0;
	static double counterPt_V18 = 0;
	static double counterCar_V18 = 0;
	static double counterBike_V19 = 0;
	static double counterWalk_V19 = 0;
	static double counterPt_V19 = 0;
	static double counterCar_V19 = 0;
	static double counterBike_V20 = 0;
	static double counterWalk_V20 = 0;
	static double counterPt_V20 = 0;
	static double counterCar_V20 = 0;
	static double counterBike_V21 = 0;
	static double counterWalk_V21 = 0;
	static double counterPt_V21 = 0;
	static double counterCar_V21 = 0;
	static double counterBike_V22 = 0;
	static double counterWalk_V22 = 0;
	static double counterPt_V22 = 0;
	static double counterCar_V22 = 0;
	
	public static void main(final String... args) throws IOException 
	/***************************************************************************/
	{
		boolean isInitialAnalysis = true;
		//TODO
		List<Geometry> vienna = readShapeFile("C:/Users/hebens/Documents/Output/BEZIRKSGRENZEOGDPolygon.shp");
		String fileLocation2 = "C:/Users/hebens/Documents/Output/v2_Mai2019_noBS(all3)zehntel_x2/ITERS/it.15/";
		String fileName = "15.plans.xml";
		
		//String fileLocation2 = "H:/otherValues/";
		//String fileName = "output_plans.xml";
		
		//String fileLocationNetwork = "F:/BikeRouting/Wien_Gesamt/Material/";
		//String fileNetwork = "network.xml"; 
		
		//--------------------------------------------------------------------------
		String fileLocationNetworkToCompare = "G:/DIS_Hebenstreit/Output/v18Nov2018_SUED_135/"; 
		String fileNetworkToCompare = "networkCount_bike.xml";
		
		String fileLocationNetworkCompareWith = "G:/DIS_Hebenstreit/Output/v18Nov2018_BC_135/"; //orig file (base case)
		String fileNetworkCompareWith = "networkCount_bike.xml";

		if (isInitialAnalysis)
		{
			List<String> zeilen = readFile(fileLocation2, fileName);
			//List<String> zeilenNetwork = readFile (fileLocationNetwork, fileNetwork);
			List<String> actPlans = new ArrayList<String>();
			Map<String,LinkId_Count_Mode> arr = new HashMap<String,LinkId_Count_Mode>();
			
			PrintWriter writerLen = new PrintWriter (fileLocation2 + "/analysisFile_Length.txt");
			List<String> arrLen =length(writerLen,zeilen);
			writerLen.close();
			
			PrintWriter writerDur = new PrintWriter (fileLocation2 + "/analysisFile_Duration.txt");
			List<String> arrDur = duration(writerDur,zeilen);
			writerDur.close();
			
			PrintWriter writerPerson = new PrintWriter (fileLocation2 + "/Persons.txt");
			person(writerPerson, zeilen);
			writerPerson.close();
			
			PrintWriter writerRoute = new PrintWriter (fileLocation2 + "/analysisFile_actRoutes.txt");
			routes(writerRoute, zeilen);
			writerRoute.close();
			
			PrintWriter writerMODES = new PrintWriter (fileLocation2 + "/analysisFile_MODES.txt");
			bs(writerMODES, zeilen);
			writerRoute.close();
			
			PrintWriter writerActPlansOnly = new PrintWriter (fileLocation2 + "/analysisFile_actPlansOnly.txt");
			actPlans = onlyActPlans(writerActPlansOnly, zeilen);
			writerActPlansOnly.close();
			
			livingPopulation(actPlans,vienna);
			PrintWriter modalSplit = new PrintWriter(fileLocation2 + "modalSplitPerDistricts.txt");
			analysePlansAndTripModeChoice (actPlans, vienna, modalSplit);
			modalSplit.close();
			
			PrintWriter actSequence = new PrintWriter(fileLocation2 + "activityLegSequence.txt");
			actLegSequence(actSequence, actPlans);
			actSequence.close();
			
			
			PrintWriter plansOfTypeWriter = new PrintWriter (fileLocation2 + "/analysisFile_plansOfType.xml");
			plansOfTypeWriter.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			plansOfTypeWriter.println("<!DOCTYPE population SYSTEM \"http://www.matsim.org/files/dtd/population_v5.dtd\">");
			plansOfTypeWriter.println("<population>");
			plansOfType(plansOfTypeWriter,actPlans,"eBikeSharing");
			plansOfTypeWriter.println("</population>");
			plansOfTypeWriter.close();
			
			PrintWriter writePlansInRow = new PrintWriter(fileLocation2 + "analysisFile_plansInRow.txt");
			plansInRow (writePlansInRow, actPlans);
			writePlansInRow.close();
			
			PrintWriter coordWriter = new PrintWriter (fileLocation2 + "/analysisFile_CoordType.txt");
			homeCoord(coordWriter, actPlans);
			coordWriter.close();
			
			//PrintWriter writerNetworkCountB = new PrintWriter (fileLocation2 + "/networkCount_"+"bike"+".xml");
			//abc (arr, actPlans, zeilenNetwork, "bike", writerNetworkCountB);
			//writerNetworkCountB.close();
			
			//PrintWriter writerNetworkCountC = new PrintWriter (fileLocation2 + "/networkCount_"+"car"+".xml");
			//abc (arr, actPlans, zeilenNetwork, "car", writerNetworkCountC);
			//writerNetworkCountC.close();
			
			//PrintWriter writerNetworkCountW = new PrintWriter (fileLocation2 + "/networkCount_"+"walk"+".xml");
			//abc (arr, actPlans, zeilenNetwork, "walk", writerNetworkCountW);
			//writerNetworkCountW.close();
			
			PrintWriter writerLinkStats = new PrintWriter (fileLocation2 + "/analysisFile_linkStats.txt");
			List <LinkId_Count_Mode> arrI = new ArrayList<LinkId_Count_Mode>(arr.values());
			for (int i = 0; i < arrI.size(); i++)
			{
				writerLinkStats.println (arrI.get(i).linkId +  ";" + arrI.get(i).count);
			}
			writerLinkStats.close();
			
			
			Graphics.writeGraphic(arrDur, 
					25, 10800, "duration", 
					"seconds", fileLocation2 + "/duration.png");
			
			Graphics.writeGraphic(arrLen, 
					25, 50000, "distance", 
					 "meters", fileLocation2 + "/length.png");
				
		}
		else
		{
			List<String> zeilenNetworkCompareWith = readFile (fileLocationNetworkCompareWith, fileNetworkCompareWith);
			List<String> zeilenNetworkToCompare = readFile (fileLocationNetworkToCompare, fileNetworkToCompare);
			
			PrintWriter writerSpider = new PrintWriter (fileLocationNetworkToCompare + "/networkSpider.xml");
			differenceSpider(writerSpider,zeilenNetworkToCompare, zeilenNetworkCompareWith);
			writerSpider.close();
		}
		
		System.out.println("<-----finished------>");
	}
	
	public static void livingPopulation (List<String> zeilenOrig, List<Geometry> geoList)
	{
		List<String> zeilen = new ArrayList<String>();
		int[] content = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		int outsideVienna = 0;
		for (int i = 0; i < zeilenOrig.size(); i++)
		{
			if (zeilenOrig.get(i).contains("<plan"))
			{
				zeilen.add(zeilenOrig.get(i+1));
			}
		}
	
			int counter = 0;
			for (int i = 0; i < zeilen.size(); i++)
			{
				if (zeilen.get(i).contains("home"))
				{
					int indexX = zeilen.get(i).indexOf("x=\"")+3;
					int indexY = zeilen.get(i).indexOf("y=\"")+3;
					
					double x = Double.parseDouble(zeilen.get(i).substring(indexX, indexX+12));				
					double y = Double.parseDouble(zeilen.get(i).substring(indexY, indexY+12));
					Coord xy = new Coord(x,y);
					// Koordinaten Transformation 
					String epsgTo = "epsg:4326"; //WGS84 
					String epsgFrom= "epsg:32632";//<-- utm 33N, epsg: 32632 = utm 32N
					CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(epsgFrom, epsgTo);					        
						       
					Coord coordNewR = ct.transform(xy);
					Coordinate coord = new Coordinate(coordNewR.getX(), coordNewR.getY());
							
					PrecisionModel pm = new PrecisionModel();
					Geometry point = new Point(coord,pm,1);
					boolean notInVienna = true;
					for (int j = 0; j < geoList.size();  j++)
					{
						if (point.within(geoList.get(j)))
						{
							content[j] = content[j]+1;
							notInVienna = false;
						}
					}
					if (notInVienna) {outsideVienna++;}
				}	
		}
		for (int i = 0; i < content.length; i++)
		{
			System.out.println(i + ";" + content[i]);
		}
		System.out.println("nV;" + outsideVienna);
	}

	public static void districtTest (int district, String mode, boolean inVienna)
	{
		if (mode.equals("pt"))
		{
			if (inVienna) {counterPt_V += 1;}
			else {counterPt_nV +=1;}
			
			if (district == 0) {counterPt_V0 += 1;}
			else if (district == 1) {counterPt_V1 += 1;}
			else if (district == 2) {counterPt_V2 += 1;}
			else if (district == 3) {counterPt_V3 += 1;}
			else if (district == 4) {counterPt_V4 += 1;}
			else if (district == 5) {counterPt_V5 += 1;}
			else if (district == 6) {counterPt_V6 += 1;}
			else if (district == 7) {counterPt_V7 += 1;}
			else if (district == 8) {counterPt_V8 += 1;}
			else if (district == 9) {counterPt_V9 += 1;}
			else if (district == 10) {counterPt_V10 += 1;}
			else if (district == 11) {counterPt_V11 += 1;}
			else if (district == 12) {counterPt_V12 += 1;}
			else if (district == 13) {counterPt_V13 += 1;}
			else if (district == 14) {counterPt_V14 += 1;}
			else if (district == 15) {counterPt_V15 += 1;}
			else if (district == 16) {counterPt_V16 += 1;}
			else if (district == 17) {counterPt_V17 += 1;}
			else if (district == 18) {counterPt_V18 += 1;}
			else if (district == 19) {counterPt_V19 += 1;}
			else if (district == 20) {counterPt_V20 += 1;}
			else if (district == 21) {counterPt_V21 += 1;}
			else if (district == 22) {counterPt_V22 += 1;}
		}
		else if (mode.equals("car"))
		{
			if (inVienna) {counterCar_V += 1;}
			else {counterCar_nV +=1;}
			
			if (district == 0) {counterCar_V0 += 1;}
			else if (district == 1) {counterCar_V1 += 1;}
			else if (district == 2) {counterCar_V2 += 1;}
			else if (district == 3) {counterCar_V3 += 1;}
			else if (district == 4) {counterCar_V4 += 1;}
			else if (district == 5) {counterCar_V5 += 1;}
			else if (district == 6) {counterCar_V6 += 1;}
			else if (district == 7) {counterCar_V7 += 1;}
			else if (district == 8) {counterCar_V8 += 1;}
			else if (district == 9) {counterCar_V9 += 1;}
			else if (district == 10) {counterCar_V10 += 1;}
			else if (district == 11) {counterCar_V11 += 1;}
			else if (district == 12) {counterCar_V12 += 1;}
			else if (district == 13) {counterCar_V13 += 1;}
			else if (district == 14) {counterCar_V14 += 1;}
			else if (district == 15) {counterCar_V15 += 1;}
			else if (district == 16) {counterCar_V16 += 1;}
			else if (district == 17) {counterCar_V17 += 1;}
			else if (district == 18) {counterCar_V18 += 1;}
			else if (district == 19) {counterCar_V19 += 1;}
			else if (district == 20) {counterCar_V20 += 1;}
			else if (district == 21) {counterCar_V21 += 1;}
			else if (district == 22) {counterCar_V22 += 1;}
		}
		
		else if (mode.equals("bs"))
		{
			if (inVienna) {counterBs_V += 1;}
			else {counterBs_nV +=1;}
			
			if (district == 0) {counterBs_V0 += 1;}
			else if (district == 1) {counterBs_V1 += 1;}
			else if (district == 2) {counterBs_V2 += 1;}
			else if (district == 3) {counterBs_V3 += 1;}
			else if (district == 4) {counterBs_V4 += 1;}
			else if (district == 5) {counterBs_V5 += 1;}
			else if (district == 6) {counterBs_V6 += 1;}
			else if (district == 7) {counterBs_V7 += 1;}
			else if (district == 8) {counterBs_V8 += 1;}
			else if (district == 9) {counterBs_V9 += 1;}
			else if (district == 10) {counterBs_V10 += 1;}
			else if (district == 11) {counterBs_V11 += 1;}
			else if (district == 12) {counterBs_V12 += 1;}
			else if (district == 13) {counterBs_V13 += 1;}
			else if (district == 14) {counterBs_V14 += 1;}
			else if (district == 15) {counterBs_V15 += 1;}
			else if (district == 16) {counterBs_V16 += 1;}
			else if (district == 17) {counterBs_V17 += 1;}
			else if (district == 18) {counterBs_V18 += 1;}
			else if (district == 19) {counterBs_V19 += 1;}
			else if (district == 20) {counterBs_V20 += 1;}
			else if (district == 21) {counterBs_V21 += 1;}
			else if (district == 22) {counterBs_V22 += 1;}
		}
		else if (mode.equals("bike"))
		{ 
			if (inVienna) {counterBike_V += 1;}
			else {counterBike_nV +=1;}
				
			if (district == 0) {counterBike_V0 += 1;}
			else if (district == 1) {counterBike_V1 += 1;}
			else if (district == 2) {counterBike_V2 += 1;}
			else if (district == 3) {counterBike_V3 += 1;}
			else if (district == 4) {counterBike_V4 += 1;}
			else if (district == 5) {counterBike_V5 += 1;}
			else if (district == 6) {counterBike_V6 += 1;}
			else if (district == 7) {counterBike_V7 += 1;}
			else if (district == 8) {counterBike_V8 += 1;}
			else if (district == 9) {counterBike_V9 += 1;}
			else if (district == 10) {counterBike_V10 += 1;}
			else if (district == 11) {counterBike_V11 += 1;}
			else if (district == 12) {counterBike_V12 += 1;}
			else if (district == 13) {counterBike_V13 += 1;}
			else if (district == 14) {counterBike_V14 += 1;}
			else if (district == 15) {counterBike_V15 += 1;}
			else if (district == 16) {counterBike_V16 += 1;}
			else if (district == 17) {counterBike_V17 += 1;}
			else if (district == 18) {counterBike_V18 += 1;}
			else if (district == 19) {counterBike_V19 += 1;}
			else if (district == 20) {counterBike_V20 += 1;}
			else if (district == 21) {counterBike_V21 += 1;}
			else if (district == 22) {counterBike_V22 += 1;}
		}
		
		else if (mode.contains("walk"))
		{
			if (inVienna) {counterWalk_V += 1;}
			else {counterWalk_nV +=1;}
			
			if (district == 0) {counterWalk_V0 += 1;}
			else if (district == 1) {counterWalk_V1 += 1;}
			else if (district == 2) {counterWalk_V2 += 1;}
			else if (district == 3) {counterWalk_V3 += 1;}
			else if (district == 4) {counterWalk_V4 += 1;}
			else if (district == 5) {counterWalk_V5 += 1;}
			else if (district == 6) {counterWalk_V6 += 1;}
			else if (district == 7) {counterWalk_V7 += 1;}
			else if (district == 8) {counterWalk_V8 += 1;}
			else if (district == 9) {counterWalk_V9 += 1;}
			else if (district == 10) {counterWalk_V10 += 1;}
			else if (district == 11) {counterWalk_V11 += 1;}
			else if (district == 12) {counterWalk_V12 += 1;}
			else if (district == 13) {counterWalk_V13 += 1;}
			else if (district == 14) {counterWalk_V14 += 1;}
			else if (district == 15) {counterWalk_V15 += 1;}
			else if (district == 16) {counterWalk_V16 += 1;}
			else if (district == 17) {counterWalk_V17 += 1;}
			else if (district == 18) {counterWalk_V18 += 1;}
			else if (district == 19) {counterWalk_V19 += 1;}
			else if (district == 20) {counterWalk_V20 += 1;}
			else if (district == 21) {counterWalk_V21 += 1;}
			else if (district == 22) {counterWalk_V22 += 1;}
		}
		
		else if (mode.equals("pt_bs"))
		{
			if (inVienna) {counterPtBs_V += 1;}
			else {counterPtBs_nV +=1;}
			
			if (district == 0) {counterPtBs_V0 += 1;}
			else if (district == 1) {counterPtBs_V1 += 1;}
			else if (district == 2) {counterPtBs_V2 += 1;}
			else if (district == 3) {counterPtBs_V3 += 1;}
			else if (district == 4) {counterPtBs_V4 += 1;}
			else if (district == 5) {counterPtBs_V5 += 1;}
			else if (district == 6) {counterPtBs_V6 += 1;}
			else if (district == 7) {counterPtBs_V7 += 1;}
			else if (district == 8) {counterPtBs_V8 += 1;}
			else if (district == 9) {counterPtBs_V9 += 1;}
			else if (district == 10) {counterPtBs_V10 += 1;}
			else if (district == 11) {counterPtBs_V11 += 1;}
			else if (district == 12) {counterPtBs_V12 += 1;}
			else if (district == 13) {counterPtBs_V13 += 1;}
			else if (district == 14) {counterPtBs_V14 += 1;}
			else if (district == 15) {counterPtBs_V15 += 1;}
			else if (district == 16) {counterPtBs_V16 += 1;}
			else if (district == 17) {counterPtBs_V17 += 1;}
			else if (district == 18) {counterPtBs_V18 += 1;}
			else if (district == 19) {counterPtBs_V19 += 1;}
			else if (district == 20) {counterPtBs_V20 += 1;}
			else if (district == 21) {counterPtBs_V21 += 1;}
			else if (district == 22) {counterPtBs_V22 += 1;}
		}
		
	}

	public static void analysePlansAndTripModeChoice (List<String> zeilen, List<Geometry> geoList, PrintWriter pw)
	{
		String planMode = "";
		for (int i = 0; i < zeilen.size(); i++)
		{
			if (zeilen.get(i).contains("<plan"))
			{
				boolean inVienna = false;
				int wien = -1;
				double x = -1;
				double y;
				String modesWithinAct = "";
				i++;
				if (zeilen.get(i).contains("home"))
				{
					int indexX = zeilen.get(i).indexOf("x=\"")+3;
					int indexY = zeilen.get(i).indexOf("y=\"")+3;
					
					x = Double.parseDouble(zeilen.get(i).substring(indexX, indexX+12));
					y = Double.parseDouble(zeilen.get(i).substring(indexY, indexY+12));
					Coord xy = new Coord(x,y);

					// Koordinaten Transformation 
					String epsgTo = "epsg:4326"; //WGS84 
				    String epsgFrom= "epsg:32632";//<-- utm 33N, epsg: 32632 = utm 32N
				    CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(epsgFrom, epsgTo);					        
				       
				    Coord coordNewR = ct.transform(xy);
				    Coordinate coord = new Coordinate(coordNewR.getX(), coordNewR.getY());
					
					PrecisionModel pm = new PrecisionModel();
					Geometry point = new Point(coord,pm,1);
					for (int j = 0; j < geoList.size(); j++)
					{
						if (point.within(geoList.get(j)))
						{
							inVienna = true;
							wien = j;
							
							break;
						}
					}
				}
				while (! zeilen.get(i).contains("</plan"))
				{
					modesWithinAct = "";
					while((!(zeilen.get(i).contains("work"))) &&
							(!(zeilen.get(i).contains("home"))) &&
									(!(zeilen.get(i).contains("shopping"))))
					{
						if (zeilen.get(i).contains("\"bike\"")) {modesWithinAct += ",bike";}
						else if (zeilen.get(i).contains("\"walk\"")) {modesWithinAct += ",walk";}
						else if (zeilen.get(i).contains("\"walking\"")) {modesWithinAct += ",walking";}
						else if (zeilen.get(i).contains("\"bsWalk\"")) {modesWithinAct += ",bsWalk";}
						else if (zeilen.get(i).contains("\"bs\"")) {modesWithinAct += ",bsX";}
						else if (zeilen.get(i).contains("\"car\"")) {modesWithinAct += ",car";}
						else if (zeilen.get(i).contains("\"transit_walk\"")) {modesWithinAct += ",walk";}
						else if (zeilen.get(i).contains("\"pt\"")) {modesWithinAct += ",pt";}
						i++;
					}
				String mode = "";
				if (modesWithinAct.contains("bike")) {mode = "bike";}
				else if (modesWithinAct.contains("pt")) {
					if (modesWithinAct.contains("bsX"))
					{
						mode = "pt_bs";
					}
					else mode = "pt";
				}
				else if (modesWithinAct.contains("bsX")) {mode = "bs";}
				else if (modesWithinAct.contains("car")) {mode = "car";}
				else if (modesWithinAct.contains("walk")) {mode = "walk";}
				districtTest (wien, mode, inVienna);
				mode = "";
				i++;
				}
			}
		}
		double totalTrips_V = counterCar_V + counterBike_V + counterPtBs_V + counterBs_V + counterWalk_V + counterPt_V;
		double totalTrips_nV = counterCar_nV + counterBike_nV + counterPtBs_nV + counterBs_nV + counterWalk_nV + counterPt_nV;
		double totalTrips_V0 = counterCar_V0 + counterBike_V0 + counterPtBs_V0 + counterBs_V0 + counterWalk_V0 + counterPt_V0;
		double totalTrips_V1 = counterCar_V1 + counterBike_V1 + counterPtBs_V1 + counterBs_V1 + counterWalk_V1 + counterPt_V1;
		double totalTrips_V2 = counterCar_V2 + counterBike_V2 + counterPtBs_V2 + counterBs_V2 + counterWalk_V2 + counterPt_V2;
		double totalTrips_V3 = counterCar_V3 + counterBike_V3 + counterPtBs_V3 + counterBs_V3 + counterWalk_V3 + counterPt_V3;
		double totalTrips_V4 = counterCar_V4 + counterBike_V4 + counterPtBs_V4 + counterBs_V4 + counterWalk_V4 + counterPt_V4;
		double totalTrips_V5 = counterCar_V5 + counterBike_V5 + counterPtBs_V5 + counterBs_V5 + counterWalk_V5 + counterPt_V5;
		double totalTrips_V6 = counterCar_V6 + counterBike_V6 + counterPtBs_V6 + counterBs_V6 + counterWalk_V6 + counterPt_V6;
		double totalTrips_V7 = counterCar_V7 + counterBike_V7 + counterPtBs_V7 + counterBs_V7 + counterWalk_V7 + counterPt_V7;
		double totalTrips_V8 = counterCar_V8 + counterBike_V8 + counterPtBs_V8 + counterBs_V8 + counterWalk_V8 + counterPt_V8;
		double totalTrips_V9 = counterCar_V9 + counterBike_V9 + counterPtBs_V9 + counterBs_V9 + counterWalk_V9 + counterPt_V9;
		double totalTrips_V10 = counterCar_V10 + counterBike_V10 + counterPtBs_V10 + counterBs_V10 + counterWalk_V10 + counterPt_V10;
		double totalTrips_V11 = counterCar_V11 + counterBike_V11 + counterPtBs_V11 + counterBs_V11 + counterWalk_V11 + counterPt_V11;
		double totalTrips_V12 = counterCar_V12 + counterBike_V12 + counterPtBs_V12 + counterBs_V12 + counterWalk_V12 + counterPt_V12;
		double totalTrips_V13 = counterCar_V13 + counterBike_V13 + counterPtBs_V13 + counterBs_V13 + counterWalk_V13 + counterPt_V13;
		double totalTrips_V14 = counterCar_V14 + counterBike_V14 + counterPtBs_V14 + counterBs_V14 + counterWalk_V14 + counterPt_V14;
		double totalTrips_V15 = counterCar_V15 + counterBike_V15 + counterPtBs_V15 + counterBs_V15 + counterWalk_V15 + counterPt_V15;
		double totalTrips_V16 = counterCar_V16 + counterBike_V16 + counterPtBs_V16 + counterBs_V16 + counterWalk_V16 + counterPt_V16;
		double totalTrips_V17 = counterCar_V17 + counterBike_V17 + counterPtBs_V17 + counterBs_V17 + counterWalk_V17 + counterPt_V17;
		double totalTrips_V18 = counterCar_V18 + counterBike_V18 + counterPtBs_V18 + counterBs_V18 + counterWalk_V18 + counterPt_V18;
		double totalTrips_V19 = counterCar_V19 + counterBike_V19 + counterPtBs_V19 + counterBs_V19 + counterWalk_V19 + counterPt_V19;
		double totalTrips_V20 = counterCar_V20 + counterBike_V20 + counterPtBs_V20 + counterBs_V20 + counterWalk_V20 + counterPt_V20;
		double totalTrips_V21 = counterCar_V21 + counterBike_V21 + counterPtBs_V21 + counterBs_V21 + counterWalk_V21 + counterPt_V21;
		double totalTrips_V22 = counterCar_V22 + counterBike_V22 + counterPtBs_V22 + counterBs_V22 + counterWalk_V22 + counterPt_V22;
		
		pw.println("District/Area;WALK;BIKE;CAR;PT;PT_BS;BS");
		pw.println("Vienna Trips (n=" + totalTrips_V + ");" + counterWalk_V/totalTrips_V + ";" + counterBike_V/totalTrips_V +
                ";"  + counterCar_V/totalTrips_V  + ";" + counterPt_V/totalTrips_V
                + ";" + counterPtBs_V/totalTrips_V + ";" + counterBs_V/totalTrips_V);
		
		pw.println("Not Vienna Trips (n=" + totalTrips_nV + ") ;" + counterWalk_nV/totalTrips_nV + ";" + counterBike_nV/totalTrips_nV +
                ";"  + counterCar_nV/totalTrips_nV  + ";" + counterPt_nV/totalTrips_nV
                + ";" + counterPtBs_nV/totalTrips_nV + ";" + counterBs_nV/totalTrips_nV);
		

		pw.println("7.Neubau (n=" + totalTrips_V0 + ") ;" + counterWalk_V0/totalTrips_V0 + ";" + counterBike_V0/totalTrips_V0 +
                ";"  + counterCar_V0/totalTrips_V0  + ";" + counterPt_V0/totalTrips_V0 
                + ";" + counterPtBs_V0/totalTrips_V0 + ";" + counterBs_V0/totalTrips_V0);
		
		pw.println("3. Landstraße (n=" + totalTrips_V1 + ") ;" + counterWalk_V1/totalTrips_V1 + ";" + counterBike_V1/totalTrips_V1 +
                ";"  + counterCar_V1/totalTrips_V1  + ";" + counterPt_V1/totalTrips_V1
                + ";" + counterPtBs_V1/totalTrips_V1 + ";" + counterBs_V1/totalTrips_V1);
		
		pw.println("8. Josefstadt (n=" + totalTrips_V2 + ") ;" + counterWalk_V2/totalTrips_V2 + ";" + counterBike_V2/totalTrips_V2 +
                ";"  + counterCar_V2/totalTrips_V2  + ";" + counterPt_V2/totalTrips_V2
                + ";" + counterPtBs_V2/totalTrips_V2 + ";" + counterBs_V2/totalTrips_V2);
		
		pw.println("1. Innere Stadt (n=" + totalTrips_V3 + ") ;" + counterWalk_V3/totalTrips_V3 + ";" + counterBike_V3/totalTrips_V3 +
                ";"  + counterCar_V3/totalTrips_V3  + ";" + counterPt_V3/totalTrips_V3
                + ";" + counterPtBs_V3/totalTrips_V3 + ";" + counterBs_V3/totalTrips_V3);
		
		pw.println("16. Ottakring (n=" + totalTrips_V4 + ") ;" + counterWalk_V4/totalTrips_V4 + ";" + counterBike_V4/totalTrips_V4 +
                ";"  + counterCar_V4/totalTrips_V4  + ";" + counterPt_V4/totalTrips_V4
                + ";" + counterPtBs_V4/totalTrips_V4 + ";" + counterBs_V4/totalTrips_V4);
		
		pw.println("9. Alsergrund (n=" + totalTrips_V5 + ") ;" + counterWalk_V5/totalTrips_V5 + ";" + counterBike_V5/totalTrips_V5 +
                ";"  + counterCar_V5/totalTrips_V5  + ";" + counterPt_V5/totalTrips_V5 +
                ";" + counterPtBs_V5/totalTrips_V5 + ";" + counterBs_V5/totalTrips_V5);
		
		pw.println("2. Leopoldstadt (n=" + totalTrips_V6 + ") ;" + counterWalk_V6/totalTrips_V6 + ";" + counterBike_V6/totalTrips_V6 +
                ";"  + counterCar_V6/totalTrips_V6  + ";" + counterPt_V6/totalTrips_V6
                + ";" + counterPtBs_V6/totalTrips_V6 + ";" + counterBs_V6/totalTrips_V6);
		
		pw.println("18. Währing (n=" + totalTrips_V7 + ") ;" + counterWalk_V7/totalTrips_V7 + ";" + counterBike_V7/totalTrips_V7 +
                ";"  + counterCar_V7/totalTrips_V7  + ";" + counterPt_V7/totalTrips_V7
                + ";" + counterPtBs_V7/totalTrips_V7 + ";" + counterBs_V7/totalTrips_V7);
		
		pw.println("17. Hernals (n=" + totalTrips_V8 + ") ;" + counterWalk_V8/totalTrips_V8 + ";" + counterBike_V8/totalTrips_V8 +
                ";"  + counterCar_V8/totalTrips_V8  + ";" + counterPt_V8/totalTrips_V8
                + ";" + counterPtBs_V8/totalTrips_V8 + ";" + counterBs_V8/totalTrips_V8);
		
		pw.println("20. Brigittenau (n=" + totalTrips_V9 + ") ;" + counterWalk_V9/totalTrips_V9 + ";" + counterBike_V9/totalTrips_V9 +
                ";"  + counterCar_V9/totalTrips_V9  + ";" + counterPt_V9/totalTrips_V9
                + ";" + counterPtBs_V9/totalTrips_V9 + ";" + counterBs_V9/totalTrips_V9);
		
		pw.println("14. Penzing (n=" + totalTrips_V10 + ") ;" + counterWalk_V10/totalTrips_V10 + ";" + counterBike_V10/totalTrips_V10 +
                ";"  + counterCar_V10/totalTrips_V10  + ";" + counterPt_V10/totalTrips_V10
                + ";" + counterPtBs_V10/totalTrips_V10 + ";" + counterBs_V10/totalTrips_V10);
		
		pw.println("19. Döbling (n=" + totalTrips_V11 + ") ;" + counterWalk_V11/totalTrips_V11 + ";" + counterBike_V11/totalTrips_V11 +
                ";"  + counterCar_V11/totalTrips_V11  + ";" + counterPt_V11/totalTrips_V11
                + ";" + counterPtBs_V11/totalTrips_V11 + ";" + counterBs_V11/totalTrips_V11);
		
		pw.println("22. Donaustadt (n=" + totalTrips_V12 + ") ;" + counterWalk_V12/totalTrips_V12 + ";" + counterBike_V12/totalTrips_V12 +
                ";"  + counterCar_V12/totalTrips_V12  + ";" + counterPt_V12/totalTrips_V12
                + ";" + counterPtBs_V12/totalTrips_V12 + ";" + counterBs_V12/totalTrips_V12);
		
		pw.println("21. Floridsdorf (n=" + totalTrips_V13 + ") ;" + counterWalk_V13/totalTrips_V13 + ";" + counterBike_V13/totalTrips_V13 +
                ";"  + counterCar_V13/totalTrips_V13  + ";" + counterPt_V13/totalTrips_V13
                + ";" + counterPtBs_V13/totalTrips_V13 + ";" + counterBs_V13/totalTrips_V13);
		
		pw.println("23. Liesing (n=" + totalTrips_V14 + ") ;" + counterWalk_V14/totalTrips_V14 + ";" + counterBike_V14/totalTrips_V14 +
                ";"  + counterCar_V14/totalTrips_V14  + ";" + counterPt_V14/totalTrips_V14
                + ";" + counterPtBs_V14/totalTrips_V14 + ";" + counterBs_V14/totalTrips_V14);
		
		pw.println("11. Simmering (n=" + totalTrips_V15 + ") ;" + counterWalk_V15/totalTrips_V15 + ";" + counterBike_V15/totalTrips_V15 +
                ";"  + counterCar_V15/totalTrips_V15  + ";" + counterPt_V15/totalTrips_V15
                + ";" + counterPtBs_V15/totalTrips_V15 + ";" + counterBs_V15/totalTrips_V15);
		
		pw.println("10. Favoriten (n=" + totalTrips_V16 + ") ;" + counterWalk_V16/totalTrips_V16 + ";" + counterBike_V16/totalTrips_V16 +
                ";"  + counterCar_V16/totalTrips_V16  + ";" + counterPt_V16/totalTrips_V16
                + ";" + counterPtBs_V16/totalTrips_V16 + ";" + counterBs_V16/totalTrips_V16);
		
		pw.println("12. Meidling (n=" + totalTrips_V17 + ") ;" + counterWalk_V17/totalTrips_V17 + ";" + counterBike_V17/totalTrips_V17 +
                ";"  + counterCar_V17/totalTrips_V17  + ";" + counterPt_V17/totalTrips_V17
                + ";" + counterPtBs_V17/totalTrips_V17 + ";" + counterBs_V17/totalTrips_V17);
		
		
		pw.println("5. Margareten (n=" + totalTrips_V18 + ") ;" + counterWalk_V18/totalTrips_V18 + ";" + counterBike_V18/totalTrips_V18 +
                ";"  + counterCar_V18/totalTrips_V18  + ";" + counterPt_V18/totalTrips_V18
                + ";" + counterPtBs_V18/totalTrips_V18 + ";" + counterBs_V18/totalTrips_V18);
		
		pw.println("4. Wieden (n=" + totalTrips_V19 + ") ;" + counterWalk_V19/totalTrips_V19 + ";" + counterBike_V19/totalTrips_V19 +
                ";"  + counterCar_V19/totalTrips_V19  + ";" + counterPt_V19/totalTrips_V19
                + ";" + counterPtBs_V19/totalTrips_V19 + ";" + counterBs_V19/totalTrips_V19);
		
		pw.println("6. Mariahilf (n=" + totalTrips_V20 + ") ;" + counterWalk_V20/totalTrips_V20 + ";" + counterBike_V20/totalTrips_V20 +
                ";"  + counterCar_V20/totalTrips_V20  + ";" + counterPt_V20/totalTrips_V20
                + ";" + counterPtBs_V20/totalTrips_V20 + ";" + counterBs_V20/totalTrips_V20);
		
		pw.println("13. Hietzing (n=" + totalTrips_V21 + ") ;" + counterWalk_V21/totalTrips_V21 + ";" + counterBike_V21/totalTrips_V21 +
                ";"  + counterCar_V21/totalTrips_V21  + ";" + counterPt_V21/totalTrips_V21
                + ";" + counterPtBs_V21/totalTrips_V21 + ";" + counterBs_V21/totalTrips_V21);
		
		pw.println("15. Rudolfsheim-F. (n=" + totalTrips_V22 + ") ;" + counterWalk_V22/totalTrips_V22 + ";" + counterBike_V22/totalTrips_V22 +
                ";"  + counterCar_V22/totalTrips_V22  + ";" + counterPt_V22/totalTrips_V22
                + ";" + counterPtBs_V22/totalTrips_V22 + ";" + counterBs_V22/totalTrips_V22);
		
	}
	
	public static void ActCoordList(PrintWriter writerRoute, List<String> zeilen, String mode)
	{
		for (int i = 0; i < zeilen.size()-2; i++)
		{
			if ((zeilen.get(i).contains("<plan ")) && (zeilen.get(i).contains("selected=\"yes\" type=\""+mode)))
			{
				int temp = 0;
				while (!(zeilen.get(i).contains("</plan>")))
				{
					if (zeilen.get(i).contains("<act type"))
					{
						if (temp == 0)
						{
							temp = i;
						}
						String neueZeile = zeilen.get(i);
						int indexA = neueZeile.indexOf("x=");
						int indexB = neueZeile.indexOf("y=")+16;
						String t = neueZeile.substring(indexA, indexB);
						writerRoute.println(temp+"_"+i + "\""+ t);
					}
					i++;

				}

			}
		}
	}
	
	public static void actLegSequence(PrintWriter writerRoute, List<String> zeilen)
	{
		for (int i = 0; i < zeilen.size()-2; i++)
		{
			if ((zeilen.get(i).contains("<plan ")) && (zeilen.get(i).contains("selected=\"yes\" type=\"")))
			{
				while (!(zeilen.get(i).contains("</plan>")))
				{
					if (zeilen.get(i).contains("<act type"))
					{
						String neueZeile = zeilen.get(i);
						int indexA = neueZeile.indexOf("<act type");
						String t = neueZeile.substring(indexA+10, indexA + 20);
						writerRoute.print(t + ";");
					}
					
					if (zeilen.get(i).contains("<leg mode"))
					{
						String neueZeile = zeilen.get(i);
						int indexA = neueZeile.indexOf("<leg mode");
						String t = neueZeile.substring(indexA+10, indexA + 15);
						writerRoute.print(t + ";");
					}

					i++;
				}
				writerRoute.println(";");
			}
		}
	}
	
	
	public static void plansOfType(PrintWriter writerRoute, List<String> zeilen, String mode)
	{
		for (int i = 0; i < zeilen.size()-2; i++)
		{
			if ((zeilen.get(i).contains("<plan ")) && (zeilen.get(i).contains("selected=\"yes\" type=\""+mode)))
			{
				writerRoute.println("<person id=\""+i+"\" employed=\"yes\">");

				while (!(zeilen.get(i).contains("</plan>")))
				{
					writerRoute.println(zeilen.get(i));
					i++;
				}
				writerRoute.println("</plan> </person>");
			}
		}
	}
	

	public static void routes(PrintWriter writerRoute, List<String> zeilen)
	{
		for (int i = 0; i < zeilen.size()-2; i++)
		{
			if ((zeilen.get(i).contains("<plan ")) && (zeilen.get(i).contains("selected=\"yes\"")))
			{
				while (!(zeilen.get(i).contains("</plan>")))
				{
					if (zeilen.get(i).contains("<route"))
					{
						writerRoute.println(zeilen.get(i-1));
						writerRoute.println(zeilen.get(i));
					}
					i++;
				}
			}
		}
	}
	
	public static void differenceSpider(PrintWriter writerRoute, List<String> zeilen, List<String> zeilen2)
	{
		for (int i = 0; i < zeilen.size()-1; i++)
		{
			String neueZeile = zeilen.get(i);
			String neueZeile2 = zeilen2.get(i);
			if (zeilen.get(i).contains("<link id"))
			{
					int indexA = neueZeile.indexOf("freespeed=")+11;
					int indexB = neueZeile.indexOf("capacity=")-2;
					String t = neueZeile.substring(indexA, indexB);
					String sub1 = neueZeile.substring(0,indexA);
					String sub2 = neueZeile.substring(indexB, neueZeile.length());
					int t_int = Integer.parseInt(t);
					
					int indexA_v2 = neueZeile2.indexOf("freespeed=")+11;
					int indexB_v2 = neueZeile2.indexOf("capacity=")-2;
					String t2 = neueZeile2.substring(indexA_v2, indexB_v2);
					int t2_int = Integer.parseInt(t2);
					
					int newValue = (t_int - t2_int);

					String replacement = Integer.toString(newValue);
					neueZeile = sub1 + replacement + sub2;
			}
			writerRoute.println(neueZeile);
		}			
	}
	
	
	public static List<String> readFile(String fileLocation, String fileName) throws IOException
	{
		List<String> zeilen = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(fileLocation + fileName));
		try 
		{
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
	
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		        zeilen.add(line);
		    }
		} 
		finally 
		{
		    br.close();
		}
		
		return zeilen;
	}
	
	public static void person(PrintWriter writerPerson, List<String> zeilen)
	{
		for (int i = 0; i < zeilen.size()-1; i++)
		{
			if ((zeilen.get(i).contains("<person id=")))
			{
				writerPerson.println(zeilen.get(i) + zeilen.get(i+1) + zeilen.get(i+2));
			}
		}
	}
	
	public static void actLinkId_Count_Mode(int i, String str, Map<String,LinkId_Count_Mode> arr, List<String> zeilen)
	{
		int indexA = zeilen.get(i).lastIndexOf("\">")+2;
		int indexB = zeilen.get(i).lastIndexOf("</route>");
		String temp = zeilen.get(i).substring(indexA, indexB);
		String[] links = temp.split(" ");
		
		for (int j = 0; j < links.length; j++)
		{
			if (arr.get(links[j] + ";" + str)== null)
			{
				LinkId_Count_Mode lcm = new LinkId_Count_Mode();
				lcm.linkId =  (links[j] + ";" + str);
				lcm.count = 1;
				arr.put(links[j] + ";" + str, lcm);
			}
			else
			{
				LinkId_Count_Mode lcm = arr.get(links[j] + ";" + str);
				lcm.count = lcm.count+1;
				arr.replace(links[j] + ";" + str, lcm);
			}
		}
	}
	
	public static void abc (Map <String, LinkId_Count_Mode> arr, 
			List <String> actPlans, List<String> zeilenNetwork, String mode,
			PrintWriter networkWriter)
	{
		for (int i = 0; i < actPlans.size(); i++)
		{
			if ((actPlans.get(i).contains("<route type=\"links\"")) && (actPlans.get(i-1).contains(mode)))
			{
				actLinkId_Count_Mode(i, mode, arr, actPlans);
			}
		}
		
		for (int i = 0; i < zeilenNetwork.size()-1; i++)
		{
			String neueZeile = zeilenNetwork.get(i);
			if (neueZeile.contains("<link id="))
			{
				
				int index = neueZeile.indexOf("<link id=")+10;
				int index2 = neueZeile.indexOf("from=")-2;
				int indexA = neueZeile.indexOf("freespeed=");
				int indexB = neueZeile.indexOf("capacity=")-2;
				String toReplace = neueZeile.substring(indexA, indexB);
				String linkId = neueZeile.substring(index, index2);
				String sub1 = neueZeile.substring(0,indexA);
				String sub2 = neueZeile.substring(indexB, neueZeile.length());

				if (arr.get(linkId + ";" + mode) != null)
				{
					int rep = arr.get(linkId + ";" + mode).count;
					String replacement = "freespeed=\"" + Integer.toString(rep);
					neueZeile = sub1 + replacement + sub2;
					
				}
				
				else
				{
					String replacement = "freespeed=\"0";
					neueZeile = sub1 + replacement + sub2;
				}
				
			}
			networkWriter.println(neueZeile);
		}
		
		
	}

	
	public static List<String> onlyActPlans(PrintWriter writer, List<String> zeilen)
	{
		List<String> arr = new ArrayList<String>();
		for (int i = 0; i < zeilen.size()-1; i++)
		{
			if ((zeilen.get(i).contains("<plan ")) && (zeilen.get(i).contains("selected=\"no\"")))
			{
				while (!(zeilen.get(i).contains("</plan>")))
				{
					i++;
				}
				i++;
			}
			if (zeilen.get(i).contains("<plan "))
			{
				int index = zeilen.get(i).indexOf("selected=");
				String str = zeilen.get(i).substring(index, zeilen.get(i).length());
				writer.println("<plan " + str);
				arr.add("<plan " + str);
				i++;
			}
			writer.println(zeilen.get(i));
			arr.add(zeilen.get(i));
			
		}
		return arr;
	}
	
	public static void plansInRow(PrintWriter writer, List<String> zeilen)
	{
		for (int i = 0; i < zeilen.size()-1; i++)
		{
			if ((zeilen.get(i).contains("<plan ")))
			{
				String plan = "";
				while (!(zeilen.get(i).contains("</plan>")))
				{
					plan += zeilen.get(i);
					i++;
				}
				writer.println(plan);
			}
		}

	}
	public static List<String> bs(PrintWriter writerDur, List<String> zeilen)
	{
		String modes = "";
		double duration = -1;
		List<String> arr = new ArrayList<String>();
		boolean ranIntoNextWhile = false;
		String header = "non";
		for (int i = 0; i < zeilen.size()-1; i++)
		{
			if ((zeilen.get(i).contains("<plan ")) && (zeilen.get(i).contains("selected=\"yes\"")))
			{
				header = zeilen.get(i+1);
				i = i + 2;
				while (!(zeilen.get(i).contains("</plan>")))
				{
					ranIntoNextWhile = false;
					while (!(zeilen.get(i).contains("<act type")))
					{
						//analyzing the length
						if (zeilen.get(i).contains("<route type"))
						{
							int indexA = zeilen.get(i).lastIndexOf("trav_time=\"")+11;
							int indexB = zeilen.get(i).lastIndexOf("distance")-2;
							String temp = zeilen.get(i).substring(indexA, indexB);
							String[] times = temp.split(":");
							double h = Double.parseDouble(times[0]);
							double min = Double.parseDouble(times[1]);
							double sec = Double.parseDouble(times[2]);
								
							double seconds = h * 3600 + min*60 + sec;
	
							if (duration < 0)
							{
								duration = seconds;
							}
							else
							duration += seconds;
						
							int indexY = zeilen.get(i-1).lastIndexOf("<leg mode=")+1;

							String modesTemp = zeilen.get(i-1).substring(indexY+10, indexY+14)+";";
							if (zeilen.get(i).contains("generic"))
							{
								if (modesTemp.equals("walk;"))
								{
									modesTemp = "walG;";
								}
							}
							modes += modesTemp;
						}

						ranIntoNextWhile = true;
						i++;
						if (zeilen.get(i).contains("<act type"))
						{
							if (zeilen.get(i).contains("pt interaction"))
							{
								i++;
							}
							
							if (zeilen.get(i).contains("eb_interaction"))
							{
								i++;
							}
							
						}
					}
					if (!(ranIntoNextWhile))
					{
						i++;
					}
					
					if (!(modes.equals("")))
					{
						writerDur.println(header + ";" + modes);
						arr.add(header + ";" + modes);
						duration = -1;
						modes = "";
					}
				}
				
			}
		}
		return arr;
	}
	
	public static List<String> duration(PrintWriter writerDur, List<String> zeilen)
	{
		String modes = "";
		double duration = -1;
		List<String> arr = new ArrayList<String>();
		boolean ranIntoNextWhile = false;
		for (int i = 0; i < zeilen.size()-1; i++)
		{
			if ((zeilen.get(i).contains("<plan ")) && (zeilen.get(i).contains("selected=\"yes\"")))
			{
				i = i + 2;
				writerDur.println("<-------" + i + "------->");
				while (!(zeilen.get(i).contains("</plan>")))
				{
					ranIntoNextWhile = false;
					while (!(zeilen.get(i).contains("<act type")))
					{
						//analyzing the length
						if (zeilen.get(i).contains("<route type"))
						{
							int indexA = zeilen.get(i).lastIndexOf("trav_time=\"")+11;
							int indexB = zeilen.get(i).lastIndexOf("distance")-2;
							String temp = zeilen.get(i).substring(indexA, indexB);
							String[] times = temp.split(":");
							double h = Double.parseDouble(times[0]);
							double min = Double.parseDouble(times[1]);
							double sec = Double.parseDouble(times[2]);
								
							double seconds = h * 3600 + min*60 + sec;
	
							if (duration < 0)
							{
								duration = seconds;
							}
							else
							duration += seconds;
						
							int indexY = zeilen.get(i-1).lastIndexOf("<leg mode=")+1;
							String modesTemp = zeilen.get(i-1).substring(indexY+10, indexY+15)+";";
							
							if (modesTemp.contains("acces")||modesTemp.contains("egres"))
							{
								//Do nothing
							}
							else modes += modesTemp;
						}

						ranIntoNextWhile = true;
						i++;
						if (zeilen.get(i).contains("<act type"))
						{
							if (zeilen.get(i).contains("interaction"))
							{
								i++;
							}
							else if (zeilen.get(i).contains("wait"))
							{
								i++;
							}
						}
					}
					if (!(ranIntoNextWhile))
					{
						i++;
					}
					
					if ((!(duration < 0)) && (!(modes.equals(""))))
					{
						if (modes.equals("trans;"))
						{
							modes = "t_walk;";
						}
						writerDur.println(duration + ";" + modes);
						arr.add(duration + ";" + modes);
						duration = -1;
						modes = "";
					}
				}
				
			}
		}
		return arr;
	}
	
	public static void homeCoord(PrintWriter writerCoord, List<String> zeilen)
	{
		for (int i = 0; i < zeilen.size()-2; i++) 
		{
			int counter = 0;
			int k = i;
			boolean hadPt = false;

			if (zeilen.get(k).contains("plan selected"))
			{
				writerCoord.print(zeilen.get(k) + zeilen.get(k+1));
				k = k+1;
				while (!(zeilen.get(k).contains("</person>")))
				{
				
					if(zeilen.get(k).contains("</leg>"))
					{
						counter ++;
					}
					
					if (zeilen.get(k).contains("experimentalPt1"))
					{
						hadPt = true;
					}
					
					if (zeilen.get(k).contains("interaction"))
					{
						counter--;
					}
					k++;
				}
				i = k;
				writerCoord.println("\"" + counter + "\"" + hadPt );
			}
		}
	}
	
	public static List<String> length(PrintWriter writerLen, List<String> zeilen)
	{
		String modes = "";
		double length = -1;
		boolean ranIntoNextWhile = false;
		List<String> arr = new ArrayList<String>();
		for (int i = 0; i < zeilen.size()-1; i++)
		{
			if ((zeilen.get(i).contains("<plan ")) && (zeilen.get(i).contains("selected=\"yes\"")))
			{ 
				modes="";
				i = i + 2;
				writerLen.println("<-------" + i + "------->");
				while (!(zeilen.get(i).contains("</plan>")))
				{
					ranIntoNextWhile = false;
					while (!(zeilen.get(i).contains("<act type")))
					{
						//analyzing the length
						if (zeilen.get(i).contains("<route type"))
						{
								int indexA = zeilen.get(i).lastIndexOf("distance=\"");
								String len = zeilen.get(i).substring(indexA+10, indexA + 22 );
								int indexKomma = len.lastIndexOf(".");
								len = len.substring(0, indexKomma+2);
							
								double lenNew = Double.parseDouble(len);
								if (length < 0)
								{
									length = lenNew;
								}
								else
								length += lenNew;
						
								int indexY = zeilen.get(i-1).lastIndexOf("<leg mode=")+1;

								String modesTemp = zeilen.get(i-1).substring(indexY+10, indexY+15)+";";
								if (modesTemp.contains("acces")||modesTemp.contains("egres"))
								{
									//Do nothing
								}
								else modes += modesTemp;
						}

						ranIntoNextWhile = true;
						i++;
						if (zeilen.get(i).contains("<act type"))
						{
							if (zeilen.get(i).contains("interaction"))
							{
								i++;
							}
							else if (zeilen.get(i).contains("wait"))
							{
								i++;
							}
						}
					}
					if (!(ranIntoNextWhile))
					{
						i++;
					}
					if ((!(length < 0)) && (!(modes.equals(""))))
					{
						if (modes.equals("trans;"))
						{
							modes = "t_walk;";
						}
						writerLen.println(length + ";" + modes);
						arr.add(length + ";" + modes);
						length = -1;
						modes = "";
					}
				}
			}
		}
	return arr;
	}
	
	public static List<Geometry> readShapeFile(String filename)
    {
           //attrString: Für Brandenburg: Nr
           //für OSM: osm_id
           
           List<Geometry> shapeMap = new ArrayList<Geometry>();
           
           for (SimpleFeature ft : ShapeFileReader.getAllFeatures(filename)) {

                        GeometryFactory geometryFactory= new GeometryFactory();
                        WKTReader wktReader = new WKTReader(geometryFactory);
                        Geometry geometry;

                        try {
                               geometry = wktReader.read((ft.getAttribute("the_geom")).toString());
                               //geometry = transformGeom(geometry);
                               shapeMap.add(geometry);
                               //System.out.println(shapeMap);
                        } catch (ParseException e) {
                               // TODO Auto-generated catch block
                               e.printStackTrace();
                        }

                  } 
           return shapeMap;
    }



}