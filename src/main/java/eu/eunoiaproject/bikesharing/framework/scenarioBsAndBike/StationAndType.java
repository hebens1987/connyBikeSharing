package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;

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
	