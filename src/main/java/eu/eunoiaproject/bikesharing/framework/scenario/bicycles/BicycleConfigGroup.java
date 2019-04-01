package eu.eunoiaproject.bikesharing.framework.scenario.bicycles;


import org.matsim.core.config.ConfigGroup;
import java.util.Map;
import java.util.TreeMap;

/**
* IKK_BikeConfigGroup defines the Input-File Parameters
* Autors: hebenstreit, aspäck
*/

public class BicycleConfigGroup extends ConfigGroup {

	private static final String INPUT_NETWORK_ATTRIBUTE_FILE = "bicyclelinkAttributesInput";
	private static final String INPUT_USERGROUPS = "usergroupsInput";
	private static final String INPUT_PERSON_ATTRIBUTES = "personCycleAttributesInput";
		
	public static final String GROUP_NAME = "bicycleAttributes";
					
//Persons					
	// Attribute Bike-Type Low Safety
		private static final String DU_BIKETYPEGROUP ="bikeTypeGroup_Input";
		private static final String DU_Amount_BIKETYPE = "duAmountBiketype_Input";
		private static final String DU_Slope_BIKETYPE = "duSlopeBiketype_Input";
		private static final String DU_Surrounding_BIKETYPE = "duSurroundingBiketype_Input";
		private static final String DU_Safety_BIKETYPE = "duSafetyBiketype_Input";
		private static final String DU_Comfort_BIKETYPE = "duComfortBiketype_Input";
		private static final String DU_Speed_BIKETYPE = "duSpeedBiketype_Input";
		private static final String SEARCH_RADIUS = "searchRadius_forBS";
		private static final String M_SEARCH_RADIUS = "maxSearchRadius_forBS";
		private static final String M_TRIP_LEN = "maxTripLength_forBS";
		private static final String PT_SEARCH_RADIUS = "ptSearchRadius";
	
	
	private String networkAttFile = null;
	private String usergroupsFile = null;
	private String personFile = null;
	
	private String duAmountBiketype_Input = null;
	private String duSlopeBiketype_Input = null;
	private String duSurroundingBiketype_Input = null;
	private String duSafetyBiketype_Input = null;
	private String duComfortBiketype_Input = null;
	private String duSpeedBiketype_Input = null;
	private String bikeTypeGroup_Input = null;
	private String searchRadius = null;
	private String mSearchRadius = null;
	private String ptSearchRadius = null;
	private String mTripLen = null;
		
	/***************************************************************************/
	public BicycleConfigGroup() 
	/***************************************************************************/
	{
		super(GROUP_NAME);
	}
	
	/***************************************************************************/
	@Override
	public final void addParam(final String key, final String value) 
	/***************************************************************************/
	{
		
		System.out.println(key + " " + value);
		
		// emulate previous behavior of reader (ignore null values at reading). td Apr'15
		if ( "null".equalsIgnoreCase( value ) ) return;

		if (INPUT_NETWORK_ATTRIBUTE_FILE.equals(key)) {
			setNetworkAttFile(value);
		} else if (INPUT_USERGROUPS.equals(key)) {
			setUsergroupFile(value);
		} else if (INPUT_PERSON_ATTRIBUTES.equals(key)) {
			setPersonFile(value);
			}
		else if (DU_BIKETYPEGROUP.equals(key)){
				bikeTypeGroup_Input = value;
			}
		else if ( DU_Amount_BIKETYPE.equals(key)) {
			duAmountBiketype_Input = value;
		} else if ( DU_Slope_BIKETYPE.equals(key)) {
			duSlopeBiketype_Input = value;
		} else if ( DU_Surrounding_BIKETYPE.equals(key)) {
			duSurroundingBiketype_Input = value;	
		} else if ( DU_Safety_BIKETYPE.equals(key)) {
			duSafetyBiketype_Input = value;
		} else if ( DU_Comfort_BIKETYPE.equals(key)) {
			duComfortBiketype_Input = value;	
		} else if ( DU_Speed_BIKETYPE.equals(key)) {
			duSpeedBiketype_Input = value;		
		} else if ( SEARCH_RADIUS.equals(key)) {
			searchRadius = value;	
		} else if ( M_SEARCH_RADIUS.equals(key)) {
			mSearchRadius = value;	
		} else if ( M_TRIP_LEN.equals(key)) {
			mTripLen = value;
		} else if ( PT_SEARCH_RADIUS.equals(key)) {
		ptSearchRadius = value;	
		} else {
			throw new IllegalArgumentException(key);
		}
	}



	/***************************************************************************/
	@Override
	public final String getValue(final String key) 
	/***************************************************************************/
	{
		if (INPUT_NETWORK_ATTRIBUTE_FILE.equals(key)) {
			return getNetworkAttFile();
		} else if (INPUT_USERGROUPS.equals(key)) {
			return getUsergroupFile();
		} else if (INPUT_PERSON_ATTRIBUTES.equals(key)) {
			return getPersonFile();	
		} 
		else if (DU_BIKETYPEGROUP.equals(key)){
			return bikeTypeGroup_Input;
		}else if ( DU_Amount_BIKETYPE.equals(key)) {
			return duAmountBiketype_Input;
		} else if ( DU_Slope_BIKETYPE.equals(key)) {
			return duSlopeBiketype_Input;
		} else if ( DU_Surrounding_BIKETYPE.equals(key)) {
			return duSurroundingBiketype_Input;	
		} else if ( DU_Safety_BIKETYPE.equals(key)) {
			return duSafetyBiketype_Input;
		} else if ( DU_Comfort_BIKETYPE.equals(key)) {
			return duComfortBiketype_Input;	
		} else if ( DU_Speed_BIKETYPE.equals(key)) {
			return duSpeedBiketype_Input;	
		} else if ( SEARCH_RADIUS.equals(key)) {
			return searchRadius;	
		} else if ( M_SEARCH_RADIUS.equals(key)) {
			return mSearchRadius;	
		} else if ( M_TRIP_LEN.equals(key)) {
			return mTripLen;
		} else if ( PT_SEARCH_RADIUS.equals(key)) {
			return ptSearchRadius;
		} else {
			throw new IllegalArgumentException(key);
		}
	}


	/***************************************************************************/
	@Override
	public final TreeMap<String, String> getParams() 
	/***************************************************************************/
	{
		TreeMap<String, String> map = new TreeMap<>();
		map.put(INPUT_NETWORK_ATTRIBUTE_FILE, getValue(INPUT_NETWORK_ATTRIBUTE_FILE));
		map.put(INPUT_USERGROUPS, getValue(INPUT_USERGROUPS));
		map.put(INPUT_PERSON_ATTRIBUTES, getValue(INPUT_PERSON_ATTRIBUTES));
		return map;
	}

	/***************************************************************************/
	@Override
	public final Map<String, String> getComments() 
	/***************************************************************************/
	{
		Map<String,String> map = super.getComments();
		map.put(INPUT_NETWORK_ATTRIBUTE_FILE, "Path to a file containing information "
				+ "for the network's links (required file format: ObjectAttributes).");
		map.put(INPUT_USERGROUPS, "Path to a file containing information for the "
				+ "usergroup Infos (required file format: ObjectAttributes).");
		map.put(INPUT_PERSON_ATTRIBUTES, "Path to a file containing information for "
				+ "the person Infos (required file format: ObjectAttributes).");
		return map;
	}
	
	/***************************************************************************/
	void setNetworkAttFile(String file) 
	/***************************************************************************/
	{
		this.networkAttFile = file;
	}

	/***************************************************************************/
	public String getNetworkAttFile() 
	/***************************************************************************/
	{
		return this.networkAttFile;
	}
	
	/***************************************************************************/
	private void setPersonFile(String value) 
	/***************************************************************************/
	{
		this.personFile = value;
	}

	/***************************************************************************/
	public String getPersonFile() 
	/***************************************************************************/
	{
		return this.personFile;
	}
	
	/***************************************************************************/
	private void setUsergroupFile(String value) 
	/***************************************************************************/
	{
		this.usergroupsFile = value;
	}
	
	/***************************************************************************/
	public String getUsergroupFile() 
	/***************************************************************************/
	{
		return this.usergroupsFile;
	}
}