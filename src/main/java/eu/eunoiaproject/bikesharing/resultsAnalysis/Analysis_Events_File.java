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
	
	public static void main(final String... args) throws IOException 
	/***************************************************************************/
	{
		boolean isInitialAnalysis = true;
	
		List<Geometry> vienna = readShapeFile("C:/Users/hebens/Documents/Output/BEZIRKSGRENZEOGDPolygon.shp");
		String fileLocation2 = "C:/Users/hebens/Documents/Output/April2019_noBs_vAttrib_all39000/";
		String fileName = "output_plans.xml";
		
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
			
			analysePlansAndTripModeChoice (actPlans, vienna);
			
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
					25, 50000, "length", 
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
	
	public static void analysePlansAndTripModeChoice (List<String> zeilen, List<Geometry> geoList)
	{
		
		String planMode = "";
		double counterBike_V = 0;
		double counterWalk_V = 0;
		double counterPt_V = 0;
		double counterCar_V = 0;
		double counterBike_nV = 0;
		double counterWalk_nV = 0;
		double counterPt_nV = 0;
		double counterCar_nV = 0;
		
		double counterPlanBike = 0;
		double counterPlanWalk = 0;
		double counterPlanPt = 0;
		double counterPlanCar = 0;

		
		for (int i = 0; i < zeilen.size(); i++)
		{
			if (zeilen.get(i).contains("<plan"))
			{
				boolean inVienna = false;
				double x = -1;
				double y;
				while (!(zeilen.get(i).contains("</plan")))
				{
					if (zeilen.get(i).contains("home"))
					{
						if (x < 0)
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
									break;
								}
							}
						}
					}
					if (zeilen.get(i).contains("\"bike\""))
					{	
						if (inVienna)
						{
							counterBike_V = counterBike_V + 1;
						}
						else counterBike_nV = counterBike_nV + 1;
						planMode = "bike";
					}
					else if (zeilen.get(i).contains("\"walk\""))
					{
						if (inVienna)
						{
							counterWalk_V = counterWalk_V+1;
						}
						else counterWalk_nV = counterWalk_nV + 1;;
						planMode = "walk";
					}
					else if (zeilen.get(i).contains("\"pt\""))
					{
						while (!
								(zeilen.get(i).contains("work"))&&
								(zeilen.get(i).contains("home"))&&
								(zeilen.get(i).contains("shopping")))
						{
							i++;
						}
						planMode = "pt";
						if (inVienna)
						{
							counterPt_V = counterPt_V+1;
						}
						else counterPt_nV = counterPt_nV + 1;
					}
					else if (zeilen.get(i).contains("\"car\""))
					{
						if (inVienna)
						{
							counterCar_V = counterCar_V+1;
						}
						else counterCar_nV = counterCar_nV + 1;
						planMode="car";
					}
					i++;
				}
			}
			if (planMode.equals("car")) {counterPlanCar = counterPlanCar + 1;}
			else if (planMode.equals("bike")) {counterPlanBike = counterPlanBike + 1;}
			else if (planMode.equals("walk")) {counterPlanWalk = counterPlanWalk + 1;}
			else if (planMode.equals("pt")) {counterPlanPt = counterPlanPt + 1;}
		}
		double totalPlans = counterPlanCar + counterPlanBike + counterPlanWalk + counterPlanPt;
		double totalTrips_V = counterCar_V + counterBike_V + counterWalk_V + counterPt_V;
		double totalTrips_nV = counterCar_nV + counterBike_nV + counterWalk_nV + counterPt_nV;
		
		System.out.println("Plans (n=" + totalPlans + ") : walk: " + counterPlanWalk/totalPlans + " bike: " + counterPlanBike/totalPlans +
				                 " car: "  + counterPlanCar/totalPlans  + " pt: " + counterPlanPt/totalPlans);
		System.out.println("Vienna Trips (n=" + totalTrips_V + ") : walk: " + counterWalk_V/totalTrips_V + " bike: " + counterBike_V/totalTrips_V +
                " car: "  + counterCar_V/totalTrips_V  + " pt: " + counterPt_V/totalTrips_V);
		System.out.println("Not Vienna Trips (n=" + totalTrips_nV + ") : walk: " + counterWalk_nV/totalTrips_nV + " bike: " + counterBike_nV/totalTrips_nV +
                " car: "  + counterCar_nV/totalTrips_nV  + " pt: " + counterPt_nV/totalTrips_nV);
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
					
					if ((!(duration < 0)) && (!(modes.equals(""))))
					{
						if (modes.equals("tran;"))
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
					if ((!(length < 0)) && (!(modes.equals(""))))
					{
						if (modes.equals("tran;"))
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