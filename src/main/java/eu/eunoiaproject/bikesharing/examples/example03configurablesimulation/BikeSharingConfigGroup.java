package eu.eunoiaproject.bikesharing.examples.example03configurablesimulation;

import org.matsim.core.config.ReflectiveConfigGroup;

public final class BikeSharingConfigGroup extends ReflectiveConfigGroup {
	public static final String NAME="bikeSharing" ;

	public BikeSharingConfigGroup(){
		super( NAME );
	}
	// ---
	public enum RunType { standard, debug }
	private RunType runType = RunType.standard ;
	public RunType getRunType(){
		return runType;
	}
	public void setRunType( RunType runType ){
		this.runType = runType;
	}


}
