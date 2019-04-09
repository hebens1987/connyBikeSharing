package eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike;

import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.utils.objectattributes.ObjectAttributesXmlReader;

/**
* Autors: IKK - Aspaeck, Luger
*/
public class IKK_ObjectAttributesSingleton {

	private static IKK_ObjectAttributesSingleton instance = null;
	
	
	private ObjectAttributes bikeLinkAttributes;
	private ObjectAttributes usergroupAttributes;
	private ObjectAttributes personAttributes;
	private ObjectAttributes intermodalSpeedValues;
	private ObjectAttributes intermodalSpeedBoundary;
	private ObjectAttributes intermodalSpeedGroupNumber;
	
	/***************************************************************************/
	private IKK_ObjectAttributesSingleton()
	/***************************************************************************/
	{
		
	}

	/***************************************************************************/
	public IKK_ObjectAttributesSingleton(BicycleConfigGroup bikeConfigGroup)
	/***************************************************************************/
	{
		bikeLinkAttributes = new ObjectAttributes();
		new ObjectAttributesXmlReader(bikeLinkAttributes).parse(bikeConfigGroup.getNetworkAttFile());
		
		personAttributes = new ObjectAttributes();
		new ObjectAttributesXmlReader(personAttributes).parse(bikeConfigGroup.getPersonFile());

	}
	
	/***************************************************************************/
	public static IKK_ObjectAttributesSingleton getInstance(
			BicycleConfigGroup bikeConfigGroup, boolean newCheckout)
	/***************************************************************************/
	{

		if (newCheckout)
			instance = new IKK_ObjectAttributesSingleton(bikeConfigGroup);
		if (instance == null)
			instance = new IKK_ObjectAttributesSingleton(bikeConfigGroup);
		
		
		return instance;
	}

	/***************************************************************************/
	public ObjectAttributes getBikeLinkAttributes() 
	/***************************************************************************/
	{
		return bikeLinkAttributes;
	}

	/***************************************************************************/
	public void setBikeLinkAttributes(ObjectAttributes bikeLinkAttributes) 
	/***************************************************************************/{
		this.bikeLinkAttributes = bikeLinkAttributes;
	}

	/***************************************************************************/
	public ObjectAttributes getUsergroupAttributes() 
	/***************************************************************************/{
		return usergroupAttributes;
	}

	/***************************************************************************/
	public void setUsergroupAttributes(ObjectAttributes usergroupAttributes) 
	/***************************************************************************/{
		this.usergroupAttributes = usergroupAttributes;
	}

	/***************************************************************************/
	public ObjectAttributes getPersonAttributes() 
	/***************************************************************************/{
		return personAttributes;
	}

	/***************************************************************************/
	public void setPersonAttributes(ObjectAttributes personAttributes) 
	/***************************************************************************/{
		this.personAttributes = personAttributes;
	}
	
	/***************************************************************************/
	public ObjectAttributes getSpeedValue() 
	/***************************************************************************/{
		return intermodalSpeedValues;
	}

	/***************************************************************************/
	public void setSpeedValue(ObjectAttributes intermodalSpeedValues) 
	/***************************************************************************/
	{
		this.intermodalSpeedValues = intermodalSpeedValues;
	}
	
	/***************************************************************************/
	public ObjectAttributes getSpeedGroupNumber() 
	/***************************************************************************/
	{
		return intermodalSpeedGroupNumber;
	}

	/***************************************************************************/
	public void setSpeedGroupNumber(ObjectAttributes intermodalSpeedGroupNumber) 
	/***************************************************************************/
	{
		this.intermodalSpeedGroupNumber = intermodalSpeedGroupNumber;
	}
	
	/***************************************************************************/
	public ObjectAttributes getSpeedBoundary() 
	/***************************************************************************/
	{
		return intermodalSpeedBoundary;
	}

	/***************************************************************************/
	public void setSpeedBoundary(ObjectAttributes intermodalSpeedBoundary) 
	/***************************************************************************/
	{
		this.intermodalSpeedBoundary = intermodalSpeedBoundary;
	}

}

