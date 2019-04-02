package eu.eunoiaproject.bikesharing.examples.example03configurablesimulation;

import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;

import static org.junit.Assert.*;

public class RunConfigurableBikeSharingSimulationTest{

	@Test
	public void testOne() {
		String [] args = null ;
//		RunConfigurableBikeSharingSimulation.runType = RunConfigurableBikeSharingSimulation.RunType.debug ;
//		RunConfigurableBikeSharingSimulation.user = RunConfigurableBikeSharingSimulation.User.raster ;
//		Config config = RunConfigurableBikeSharingSimulation.prepareConfig( args );;
//		Scenario scenario = RunConfigurableBikeSharingSimulation.prepareScenario( config ) ;
//		Controler controler = RunConfigurableBikeSharingSimulation.prepareControler( scenario ) ;
//		controler.run() ;
		RunConfigurableBikeSharingSimulation.main(args) ;
	}

}
