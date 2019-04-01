package eu.eunoiaproject.bikesharing.framework.processingBikeSharing;

import eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing.BikeSharingFacility;

/**
 * 
 *hier noch miteinbeziehen 
 *ob Station A zum Abreisezeitpunkt verfügbare Räder
 *ob Station B zum Abreisezeitpunkt verfügbare Stellplätze
 *
 * 
 *
 * @author Hebenstreit
 */
public class StationAndType
{
	
	public BikeSharingFacility station;
	public boolean type;
	public boolean usedAsPtChange = false;
	public double tripDur = 0;

	public StationAndType()
	{}

}
	