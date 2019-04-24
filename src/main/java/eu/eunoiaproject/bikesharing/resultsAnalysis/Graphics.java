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


public class Graphics
{
	
	 public static List<ModiArray> getModiArray(final List<String> arr, double categoryStep, double categoryMax)
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
					if (modi.equals("tran") || modi.equals("bs_w"))
					{
						while (k < interim.length)
						{
								modi += interim[k];
								k++;
						}
						if ((modi.contains("bs\""))&& (modi.contains("pt")))
						{
							modi = "pt_bs";
						}
						
						else if (modi.contains("bs\""))
						{
							modi = "bs";
						}
						
						else if (modi.contains("pt"))
						{
							modi = "tran";
						}
						else
						{
							modi = "walk";
						}
					}
					temp.mode=modi;		
					data.add(temp);
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
		 modeArr.walk = 0;
		 categoryStep += orig;
		 cat.add(modeArr);
	 }
	 ModiArray modeArr = new ModiArray();
	 modeArr.category = categoryStep-1;
	 modeArr.bike = 0;
	 modeArr.bs = 0;
	 modeArr.car = 0;
	 modeArr.pt = 0;
	 modeArr.walk = 0;
	 cat.add(modeArr);
	 
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
			
			else if (data.get(i).mode.equals("tran"))
			{
				cat.get(k).pt++;
			}
			
			else if ((data.get(i).mode.equals("walk"))||
					(data.get(i).mode.equals("t_walk"))||
					(data.get(i).mode.equals("walG")))
			{
				cat.get(k).walk++;
			}
			
			else if (data.get(i).mode.equals("car\""))
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
			
			if (data.get(i).mode.equals("bs"))
			{
				cat.get(cat.size()-1).bs++;
			}
			
			else if (data.get(i).mode.equals("tran"))
			{
				cat.get(cat.size()-1).pt++;
			}
			
			else if ((data.get(i).mode.equals("walk"))||
					(data.get(i).mode.equals("t_walk"))||
					(data.get(i).mode.equals("walG")))
			{
				cat.get(cat.size()-1).walk++;
			}
			
			else if (data.get(cat.size()-1).mode.equals("car\""))
			{
				cat.get(cat.size()-1).car++;
			}
		}
	 }
	 
	 
	 for (int j = 1; j < cat.size(); j++)
	 {
		 cat.get(j).bike += cat.get(j-1).bike;
		 cat.get(j).walk += cat.get(j-1).walk;
		 cat.get(j).pt += cat.get(j-1).pt;
		 cat.get(j).car += cat.get(j-1).car;
		 cat.get(j).bs += cat.get(j-1).bs;
	 }
	 
	 double totalBike = cat.get(cat.size()-1).bike;
	 double totalWalk = cat.get(cat.size()-1).walk;
	 double totalPt = cat.get(cat.size()-1).pt;
	 double totalCar = cat.get(cat.size()-1).car;
	 double totalBs = cat.get(cat.size()-1).bs;
	 
	 for (int j = 0; j < cat.size(); j++)
	 {
		 cat.get(j).bike = cat.get(j).bike/totalBike;
		 cat.get(j).walk = cat.get(j).walk/totalWalk;
		 cat.get(j).pt = cat.get(j).pt/totalPt;
		 cat.get(j).car = cat.get(j).car/totalCar;
		 cat.get(j).bs = cat.get(j).bs/totalBs;
	 }

	 return cat;
	 }
	 
	 static JFreeChart getGraphic(List<ModiArray> data, String dataType, String einheiten) 
	 {
			final XYSeriesCollection xyData = new XYSeriesCollection();
			final XYSeries carSerie = new XYSeries("car", false, true);
			final XYSeries ptSerie = new XYSeries("pt", false, true);
			final XYSeries bikeSerie = new XYSeries("bike", false, true);
			final XYSeries walkSerie = new XYSeries("walk", false, true);
			final XYSeries bsSerie = new XYSeries("bs", false, true);
			
			for (int i = 0; i < data.size(); i++ )
			{
				double x = data.get(i).category;
				carSerie.add(x, data.get(i).car);
				ptSerie.add(x, data.get(i).pt);
				walkSerie.add(x,data.get(i).walk);
				bikeSerie.add(x,data.get(i).bike);
				bsSerie.add(x,data.get(i).bs);
				
			}
			
			xyData.addSeries(carSerie);
			xyData.addSeries(ptSerie);
			xyData.addSeries(walkSerie);
			xyData.addSeries(bikeSerie);
			xyData.addSeries(bsSerie);



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
			
			List<ModiArray> data = getModiArray(arr, categoryStep, categoryMax);
			
			try {
	            ChartUtilities.saveChartAsPNG(new File(fileName), getGraphic(data, dataType, einheiten), 1024, 768);
			} catch (IOException e) {
	            throw new UncheckedIOException(e);
			}
		}

}

	
	