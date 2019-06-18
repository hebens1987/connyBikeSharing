package eu.eunoiaproject.bikesharing.resultsAnalysis;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartPanel;
import org.matsim.core.utils.io.UncheckedIOException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class Graphics
{
	
	static List<DataType> dataGlobal = null;
	
	 public static List<ModiArray> getModiArray(final List<String> arr, double categoryStep, double categoryMax, boolean notGlocke)
	 {
		 	double orig = categoryStep;
			List<DataType> data = new ArrayList<DataType>();
			List<ModiArray> cat = new ArrayList<ModiArray>();
			for (int i = 0; i < arr.size()-1; i++)
			{
				if (!(arr.get(i).contains("----")))
				{
					DataType temp = new DataType();
					String[] interim = arr.get(i).split(";");
					temp.toAnalyze = Double.parseDouble(interim[0]);
					String modi = interim[1];
					int k = 2;
					if (modi.equals("trans") || modi.equals("bsWal") || modi.equals("walking"))
					{
						while (k < interim.length)
						{
								modi += interim[k];
								k++;
						}
						
						if ((modi.contains("bs\""))&& (modi.contains("pt")))
						{
							modi = "bs_pt";
						}
						
						else if (modi.contains("bs\""))
						{
							modi = "bs";
						}
						
						else if (modi.contains("pt"))
						{
							modi = "pt";
						}
						else if (modi.contains("walki"))
						{
							modi = "walking";
						}
						else if (modi.contains("walk"))
						{
							modi = "walk";
						}
						else if (modi.contains("bsWal"))
						{
							modi = "walk";
						}
					}
					else if (modi.contains("car"))
					{
						modi = "car";
					}
					else if (modi.contains("bike"))
					{
						modi = "bike";
					}

					else if (modi.contains("walki"))
					{
						modi = "walking";
					}
					else if (modi.contains("walk"))
					{
						modi = "walk";
					}
					else {modi = "x";}
					
					if (!(modi.equals("x")))
					{
						temp.mode=modi;		
						data.add(temp);
					}
				}
			}
	 while (categoryStep <= categoryMax)
	 {
		 ModiArray modeArr = new ModiArray();
		 modeArr.category = categoryStep;
		 modeArr.bike = 0;
		 modeArr.bs = 0;
		 modeArr.car = 0;
		 modeArr.pt = 0;
		 modeArr.walking = 0;
		 modeArr.walk = 0;
		 modeArr.bs_pt = 0;
		 categoryStep += orig;
		 cat.add(modeArr);
	 }
	 ModiArray modeArr = new ModiArray();
	 modeArr.category = orig;
	 modeArr.bike = 0;
	 modeArr.bs = 0;
	 modeArr.car = 0;
	 modeArr.pt = 0;
	 modeArr.walk = 0;
	 modeArr.walking = 0;
	 modeArr.bs_pt = 0;
	 cat.add(modeArr);
	 dataGlobal = data;
	 for (int i = 0; i < data.size(); i++)
	 {
		double tmp = data.get(i).toAnalyze/orig;
		int k = new Double(tmp).intValue(); //Aufrunden
		
		if (k <= cat.size()-1)
		{
			if (data.get(i).mode.equals("bike"))
			{
				cat.get(k).bike++;
			}
			
			else if (data.get(i).mode.equals("bs"))
			{
				cat.get(k).bs++;
			}
			
			else if (data.get(i).mode.equals("bs_pt"))
			{
				cat.get(k).bs_pt++;
			}
			
			else if (data.get(i).mode.equals("pt"))
			{
				cat.get(k).pt++;
			}
			else if ((data.get(i).mode.equals("walking")))
			{
				cat.get(k).walking++;
			}
			else if ((data.get(i).mode.equals("walk"))||
					(data.get(i).mode.equals("t_walk"))||
					(data.get(i).mode.equals("walG")))
			{
				cat.get(k).walk++;
			}
			
			else if (data.get(i).mode.equals("car"))
			{
				cat.get(k).car++;
			}
		}
		if (k > cat.size()-1)
		{
			if (data.get(i).mode.equals("bike"))
			{
				cat.get(cat.size()-1).bike++;
			}
			
			else if (data.get(i).mode.equals("bs"))
			{
				cat.get(cat.size()-1).bs++;
			}
			
			else if (data.get(i).mode.equals("bs_pt"))
			{
				cat.get(cat.size()-1).bs_pt++;
			}
			
			else if (data.get(i).mode.equals("tran"))
			{
				cat.get(cat.size()-1).pt++;
			}
			else if ((data.get(i).mode.equals("walking")))
			{
				cat.get(cat.size()-1).walking++;
			}
			
			else if ((data.get(i).mode.equals("walk"))||
					(data.get(i).mode.equals("t_walk"))||
					(data.get(i).mode.equals("walG")))
			{
				cat.get(cat.size()-1).walk++;
			}
			
			else if (data.get(cat.size()-1).mode.equals("car"))
			{
				cat.get(cat.size()-1).car++;
			}
		}
	 }
	 double totalBike;
	 double totalWalk;
	 double totalWalking;
	 double totalPt;
	 double totalCar;
	 double totalBs;
	 double totalBs_pt;

	// List<ModiArray> cat2 = new ArrayList<ModiArray>(cat);
	for (int j = 1; j < cat.size(); j++)
	{
	 cat.get(j).bike += cat.get(j-1).bike;
	 cat.get(j).walk += cat.get(j-1).walk;
	 cat.get(j).walking += cat.get(j-1).walking;
	 cat.get(j).pt += cat.get(j-1).pt;
	 cat.get(j).car += cat.get(j-1).car;
	 cat.get(j).bs += cat.get(j-1).bs;
	 cat.get(j).bs_pt += cat.get(j-1).bs_pt;
	}
	 
	 totalBike = cat.get(cat.size()-1).bike;
	 totalWalk = cat.get(cat.size()-1).walk;
	 totalWalking = cat.get(cat.size()-1).walking;
	 totalPt = cat.get(cat.size()-1).pt;
	 totalCar = cat.get(cat.size()-1).car;
	 totalBs = cat.get(cat.size()-1).bs;
	 totalBs_pt = cat.get(cat.size()-1).bs_pt;

		 for (int j = 0; j < cat.size(); j++)
		 {
			 cat.get(j).bike = cat.get(j).bike/totalBike;
			 cat.get(j).walk = cat.get(j).walk/totalWalk;
			 cat.get(j).pt = cat.get(j).pt/totalPt;
			 cat.get(j).car = cat.get(j).car/totalCar;
			 cat.get(j).walking = cat.get(j).walking/totalWalking;
			 cat.get(j).bs = cat.get(j).bs/totalBs;
			 cat.get(j).bs_pt = cat.get(j).bs_pt/totalBs_pt;
		 }
		 if (notGlocke)
		 {
		 return cat;
		 }
		List<ModiArray> cat2 = new ArrayList<ModiArray>(cat);
		for (int j = 0; j < cat2.size()-2; j++)
		{
			if (j == 0)
			{
				cat2.get(j).bike = cat.get(j).bike*10;
				cat2.get(j).car= cat.get(j).car*10;
				cat2.get(j).pt = cat.get(j).pt*10;
				cat2.get(j).walk =  cat.get(j).walk*10;
				cat2.get(j).walking = cat.get(j).walking*10;
				cat2.get(j).bs = cat.get(j).bs*10;
				cat2.get(j).bs_pt =  cat.get(j).bs_pt*10;
			}
			else
			{
			cat2.get(j).bike = (cat.get(j+1).bike- cat.get(j).bike)*10;
			cat2.get(j).car= (cat.get(j+1).car- cat.get(j).car) *10;
			cat2.get(j).pt = (cat.get(j+1).pt - cat.get(j).pt) *10;
			cat2.get(j).walk = (cat.get(j+1).walk - cat.get(j).walk) *10;
			cat2.get(j).walking = (cat.get(j+1).walking - cat.get(j).walking)*10;
			cat2.get(j).bs = (cat.get(j+1).bs - cat.get(j).bs)*10;
			cat2.get(j).bs_pt = (cat.get(j+1).bs_pt- cat.get(j).bs_pt)*10;
			}
		}
		cat2.get(cat2.size()-1).bike = 0;
		cat2.get(cat2.size()-1).car= 0 ;
		cat2.get(cat2.size()-1).pt = 0 ;
		cat2.get(cat2.size()-1).walk = 0 ;
		cat2.get(cat2.size()-1).walking = 0;
		cat2.get(cat2.size()-1).bs = 0 ;
		cat2.get(cat2.size()-1).bs_pt = 0 ;
		return cat2;
	 }
	 
	 static JFreeChart getGraphic(List<ModiArray> data, String dataType, String einheiten) 
	 {
			final XYSeriesCollection xyData = new XYSeriesCollection();
			final XYSeries carSerie = new XYSeries("car", false, true);
			final XYSeries ptSerie = new XYSeries("pt", false, true);
			final XYSeries bikeSerie = new XYSeries("bike", false, true);
			final XYSeries walkSerie = new XYSeries("walk", false, true);
			final XYSeries walkingSerie = new XYSeries("walking", false, true);
			final XYSeries bsSerie = new XYSeries("bs", false, true);
			final XYSeries bsptSerie = new XYSeries("bs_pt", false, true);
			
			for (int i = 0; i < data.size(); i++ )
			{
				double x = data.get(i).category;
				carSerie.add(x, data.get(i).car);
				ptSerie.add(x, data.get(i).pt);
				walkSerie.add(x,data.get(i).walk);
				bikeSerie.add(x,data.get(i).bike);
				walkingSerie.add(x,data.get(i).walking);
				bsSerie.add(x,data.get(i).bs);
				bsptSerie.add(x,data.get(i).bs_pt);
				
			}
			
			xyData.addSeries(carSerie);
			xyData.addSeries(ptSerie);
			xyData.addSeries(walkingSerie);
			xyData.addSeries(bikeSerie);
			xyData.addSeries(bsSerie);
			xyData.addSeries(walkSerie);
			xyData.addSeries(bsptSerie);



	        final JFreeChart chart = ChartFactory.createXYStepChart(
	                "Sum Curve, " + dataType, einheiten, "percentual share | " + dataType, xyData,
	                PlotOrientation.VERTICAL,
	                true,   // legend
	                false,   // tooltips
	                false   // urls
	        );

			XYPlot plot = chart.getXYPlot();

			final CategoryAxis axis1 = new CategoryAxis(einheiten);
			axis1.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 7));
			plot.setDomainAxis(new NumberAxis(einheiten));

			plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
			plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
			plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
			plot.getRenderer().setSeriesStroke(3, new BasicStroke(2.0f));
			plot.getRenderer().setSeriesStroke(4, new BasicStroke(2.0f));
			plot.getRenderer().setSeriesStroke(5, new BasicStroke(2.0f));
			plot.getRenderer().setSeriesStroke(6, new BasicStroke(2.0f));
			plot.setBackgroundPaint(Color.white);
			plot.setRangeGridlinePaint(Color.gray);
			plot.setDomainGridlinePaint(Color.gray);

			return chart;
		}

	    /**
		 * Writes a graphic showing the number of departures, arrivals and vehicles
		 * en route of all legs/trips to the specified file.
		 *
		 * @param legHistogram
	     * @param filename
		 *
		 */
		public static void writeGraphic(final List<String> arr, 
				double categoryStep, double categoryMax, String dataType, 
				String einheiten, String fileName) {
			
			List<ModiArray> data = getModiArray(arr, categoryStep, categoryMax, true);
			
			try {
	            ChartUtilities.saveChartAsPNG(new File(fileName), getGraphic(data, dataType, einheiten), 1024, 768);
			} catch (IOException e) {
	            throw new UncheckedIOException(e);
			}
		}
		
		public static void writeGraphicGlocke(final List<String> arr, 
				double categoryStep, double categoryMax, String dataType, 
				String einheiten, String fileName) {
			
			List<ModiArray> data = getModiArray(arr, categoryStep, categoryMax, false);
			
			try {
	            ChartUtilities.saveChartAsPNG(new File(fileName), getGraphic(data, dataType, einheiten), 1024, 768);
			} catch (IOException e) {
	            throw new UncheckedIOException(e);
			}
		}
}

	
	