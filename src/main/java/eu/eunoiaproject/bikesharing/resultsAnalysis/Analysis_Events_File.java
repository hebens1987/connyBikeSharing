package eu.eunoiaproject.bikesharing.resultsAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

	public static void main(final String... args) throws IOException 
	/***************************************************************************/
	{
		boolean isInitialAnalysis = true;
		//TODO
		List<Geometry> vienna = readShapeFile("C:/Users/hebens/Documents/Output/BEZIRKSGRENZEOGDPolygon.shp");
		String fileLocation2 = "C:/Users/hebens/Documents/Output/v_13_Mai_2019_fullBS_short/ITERS/it.0/";
		String fileName = "0.events.xml";

		List<String> zeilen = readFile(fileLocation2, fileName);
		List<List<String>> personList = getPersonElements(zeilen);
		PrintWriter pw = new PrintWriter (fileLocation2 + "/EVENTS_Analysis.txt");
		analyzePersonElements(personList, pw);
	}
	
	public static void analyzePersonElements (List<List<String>> pList, PrintWriter pw)
	{
		for (int i = 0; i < pList.size(); i++)
		{
			pw.println("next");

			for (int j = 0; j < pList.get(i).size(); j++)
			{
				int zeitActstart = 0;
				int reisezeitAB = 0;
				int zeitDeparture = 0;
				int zeitArrival = 0;
				//int zeitImFahrzeugAB = 0;
				String actTypeModiAndDuration = "";
				String singleMode ="";
				int zeitActend = 0;
				String actType = "";
				boolean runWhile = true;
				while (runWhile)
				{
					if (pList.get(i).get(j).contains("actend"))
					{
						int index1= pList.get(i).get(j).indexOf("time=")+6;
						int index2 = pList.get(i).get(j).indexOf("actend=")-2;
						String sub = pList.get(i).get(j).substring(index1, index2);
						zeitActstart = Integer.parseInt(sub);
						int indexA= pList.get(i).get(j).indexOf("act type=")+9;
						actType = pList.get(i).get(j).substring(indexA, indexA + 4);
						actTypeModiAndDuration += actType + ",";
					}
					else if (pList.get(i).get(j).contains("departure"))
					{
						int index1= pList.get(i).get(j).indexOf("time=")+6;
						int index2 = pList.get(i).get(j).indexOf("departure")-2;
						String time = pList.get(i).get(j).substring(index1, index2);
						zeitDeparture = Integer.parseInt(time);
					}
					
					else if (pList.get(i).get(j).contains("arrival"))
					{
						int index1= pList.get(i).get(j).indexOf("time=")+6;
						int index2 = pList.get(i).get(j).indexOf("arrival")-2;
						String time = pList.get(i).get(j).substring(index1, index2);
						zeitArrival = Integer.parseInt(time);
						int indexA= pList.get(i).get(j).indexOf("legMode=")+7;
						singleMode = pList.get(i).get(j).substring(indexA, indexA+4);
						int durationLeg = zeitArrival - zeitDeparture;
						actTypeModiAndDuration += singleMode + "," + durationLeg + ",";
					}

					else if (pList.get(i).get(j).contains("actstart"))
					{
						if(!(pList.get(i).get(j).contains("interaction")))
						{
							runWhile = false;
							int index1= pList.get(i).get(j).indexOf("time=")+6;
							int index2 = pList.get(i).get(j).indexOf("actstart")-2;
							String time = pList.get(i).get(j).substring(index1, index2);
							zeitActend = Integer.parseInt(time);
						}
					}
					if (!runWhile)
					{
						reisezeitAB = zeitActend - zeitActstart;
						pw.println("," + reisezeitAB +","+ actTypeModiAndDuration);
					}
					j++;
				}
			}
		}
		System.out.println("<----- fertig ----->");
	}
	
	public static List<List<String>> getPersonElements (List<String> zeilen)
	{
		List<List<String>> fullList = new ArrayList<List<String>>();
		List<String> singlePersonList = new ArrayList<String>();
		
		HashMap<String, String> map = new HashMap<String,String>();
		for (int i = 0; i < zeilen.size(); i++)
		{
			if ((zeilen.get(i).contains("actend")) && (zeilen.get(i).contains("home")))
			{
					int indexStart = zeilen.get(i).indexOf("person=")+8;
					int indexEnd = zeilen.get(i).indexOf("link=")-2;
					String id = zeilen.get(i).substring(indexStart, indexEnd);
					if (map.get(id)== null)
					{
						map.put(id, zeilen.get(i));
						for (int k = i; k < zeilen.size(); k++)
						{
							if ((!(zeilen.get(k).contains("person")))
								&& (!(zeilen.get(k).contains("agent"))))
							{
								zeilen.remove(k);
								k--;
							}
							else if (zeilen.get(k).contains(id))
							{
								singlePersonList.add(zeilen.get(k));
								zeilen.remove(k);
								k--;
							}
						}
						fullList.add(singlePersonList);
						singlePersonList = new ArrayList<String>();
					}
			}
			
		}
		return fullList;
	}


	public static List<String> readFile(String fileLocation, String fileName) throws IOException
	{
		List<String> zeilen = new ArrayList<String>();
		File f = new File (fileLocation + fileName);
		
		try {
			FileInputStream inputStream = new FileInputStream(f);
			Scanner sc = new Scanner (inputStream, "UTF-8");
			
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains("left link"))
				{ //do nothing 
				}
				else if (line.contains("entered link"))
				{ //do nothing 
				}
				else if (line.contains("TransitDriverStarts"))
				{ //do nothing 	
				}
				else if (line.contains("interaction"))
				{ //do nothing 	
				}
				else
				{
					zeilen.add(line);
				}
			}
		}

		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return zeilen;
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