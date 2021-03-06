package eu.eunoiaproject.bikesharing.examples.example03configurablesimulation;

import com.google.inject.Inject;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.BicycleConfigGroup;
import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.events.handler.BasicEventHandler;
//import org.matsim.testcases.MatsimTestUtils;

import javax.inject.Singleton;

import static eu.eunoiaproject.bikesharing.examples.example03configurablesimulation.RunConfigurableBikeSharingSimulation.*;

public class RunConfigurableBikeSharingSimulationTest{
	private static final Logger log = Logger.getLogger( RunConfigurableBikeSharingSimulationTest.class ) ;

	@Rule public MatsimTestUtils utils = new MatsimTestUtils() ;

	/*@Test
	public void testOne() {

		final Config config = prepareConfig( null, InputCase.raster );

		config.controler().setLastIteration(60);
		config.controler().setOutputDirectory( utils.getOutputDirectory() );

		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class );;
		bikeSharingConfig.setRunType( BikeSharingConfigGroup.RunType.debug );

		Scenario scenario = prepareScenario( config ) ;

		BicycleConfigGroup bconf =(BicycleConfigGroup)scenario.getConfig().getModule(BicycleConfigGroup.GROUP_NAME);
		IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bconf,true);//Important otherwise wrong bike objects loaded
		// yyyyyy what is this?  why is this?  why is this in the test but not in the upstream code?  kai, apr'19
		//		Das liest die Object Attributes files ein und dami sich das nicht unzählich wiederholt macht es das nur wenns nicht null ist...
		//		Damit es Test cases mit unterschiedlichem inpun läuft hab ich es verändert... ist nicht hübsch aber tit was es soll (zuvor hat testcase 2 auf attribute von testcase 1 zugegriffen)

		Controler controler = prepareControler( scenario ) ;

		controler.addOverridingModule( new AbstractModule(){
			@Override
			public void install(){
				this.bind( Checker.class ).in( Singleton.class ) ;
				this.addEventHandlerBinding().to( Checker.class ).in( Singleton.class ) ;
				this.addControlerListenerBinding().to( Checker.class ).in( Singleton.class ) ;

				this.addEventHandlerBinding().to( EventsPrinter.class ) ;
			}
		} );

		controler.run() ;

	}*/
	@Test
	public void testTwo() {

		//		final Config config = prepareConfig( null, InputCase.connyInputDiss );
		final Config config = prepareConfig( null, InputCase.connyInputDiss );

		config.controler().setLastIteration(60);
		config.controler().setOutputDirectory( utils.getOutputDirectory() );

		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class );;
		bikeSharingConfig.setRunType( BikeSharingConfigGroup.RunType.debug );

		Scenario scenario = prepareScenario( config ) ;

		BicycleConfigGroup bconf =(BicycleConfigGroup)scenario.getConfig().getModule(BicycleConfigGroup.GROUP_NAME);
		IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bconf,true);//Important otherwise wrong bike objects loaded
		// yyyyyy what is this?  why is this?  why is this in the test but not in the upstream code?  kai, apr'19
//		Das liest die Object Attributes files ein und dami sich das nicht unzählich wiederholt macht es das nur wenns nicht null ist...
//		Damit es Test cases mit unterschiedlichem inpun läuft hab ich es verändert... ist nicht hübsch aber tit was es soll (zuvor hat testcase 2 auf attribute von testcase 1 zugegriffen)

		Controler controler = prepareControler( scenario ) ;

		controler.addOverridingModule( new AbstractModule(){
			@Override
			public void install(){
				this.addEventHandlerBinding().to( EventsPrinter.class ) ;
			}
		} );

		controler.run() ;

	}

	/*@Test
	public void testThree() {

		final Config config = prepareConfig( null, InputCase.raster );

		config.controler().setLastIteration(10);
		config.controler().setOutputDirectory( utils.getOutputDirectory() );

		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class );;
		bikeSharingConfig.setRunType( BikeSharingConfigGroup.RunType.standard );

		Scenario scenario = prepareScenario( config ) ;

		BicycleConfigGroup bconf =(BicycleConfigGroup)scenario.getConfig().getModule(BicycleConfigGroup.GROUP_NAME);
		IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bconf,true);//Important otherwise wrong bike objects loaded
		// yyyyyy what is this?  why is this?  why is this in the test but not in the upstream code?  kai, apr'19
		//		Das liest die Object Attributes files ein und dami sich das nicht unzählich wiederholt macht es das nur wenns nicht null ist...
		//		Damit es Test cases mit unterschiedlichem inpun läuft hab ich es verändert... ist nicht hübsch aber tit was es soll (zuvor hat testcase 2 auf attribute von testcase 1 zugegriffen)

		Controler controler = prepareControler( scenario ) ;

		controler.addOverridingModule( new AbstractModule(){
			@Override
			public void install(){
				this.addEventHandlerBinding().to( EventsPrinter.class ) ;
			}
		} );

		controler.run() ;

	}

	@Test
	public void testFour() {

		final Config config = prepareConfig( null, InputCase.inputDiss );

		config.controler().setLastIteration(10);
		config.controler().setOutputDirectory( utils.getOutputDirectory() );

		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class );;
		bikeSharingConfig.setRunType( BikeSharingConfigGroup.RunType.standard );

		Scenario scenario = prepareScenario( config ) ;

		BicycleConfigGroup bconf =(BicycleConfigGroup)scenario.getConfig().getModule(BicycleConfigGroup.GROUP_NAME);
		IKK_ObjectAttributesSingleton bts = IKK_ObjectAttributesSingleton.getInstance(bconf,true);//Important otherwise wrong bike objects loaded
		// yyyyyy what is this?  why is this?  why is this in the test but not in the upstream code?  kai, apr'19
		//		Das liest die Object Attributes files ein und dami sich das nicht unzählich wiederholt macht es das nur wenns nicht null ist...
		//		Damit es Test cases mit unterschiedlichem inpun läuft hab ich es verändert... ist nicht hübsch aber tit was es soll (zuvor hat testcase 2 auf attribute von testcase 1 zugegriffen)

		Controler controler = prepareControler( scenario ) ;

		controler.addOverridingModule( new AbstractModule(){
			@Override
			public void install(){
				this.addEventHandlerBinding().to( EventsPrinter.class ) ;
			}
		} );

		controler.run() ;

	}*/

	private static class EventsPrinter implements BasicEventHandler {
		private int iteration ;
		@Inject private Config config ;
		@Override public void handleEvent( Event event ){
			if ( iteration == config.controler().getLastIteration() ) {
				if( event instanceof  PersonDepartureEvent ) {
					System.err.println( event.toString() );
				}
			}
		}
		@Override
		public void reset( int iteration ){
			this.iteration = iteration ;
		}
	}

	private static class Checker implements BasicEventHandler, ShutdownListener {
		int iteration = 0 ;
		int cnt = 0 ;
		@Inject Config config ;
		@Override public void handleEvent( Event event ){
			if ( iteration == 1 ) {
//				if( !(event instanceof LinkEnterEvent || event instanceof LinkLeaveEvent) ){
				if( event instanceof  PersonDepartureEvent ) {
					System.err.println( event.toString() );
				}
				if ( event instanceof PersonDepartureEvent && ((PersonDepartureEvent) event).getLegMode().equals( "e_bs" ) ) {
					switch ( cnt ) {
						case 0:
							Assert.assertEquals( 27515., event.getTime(), 0.1 );
							Assert.assertEquals( "p72", ((PersonDepartureEvent) event).getPersonId().toString() );
							break ;
						case 1:
							Assert.assertEquals( 29287, event.getTime(), 0.1 );
							Assert.assertEquals( "p84", ((PersonDepartureEvent) event).getPersonId().toString() );
							break ;
					}
					cnt ++ ;
					log.warn("cnt=" + cnt ) ;
				}
			}
		}

		@Override public void reset( int iteration ){
			this.iteration = iteration ;
		}

		@Override
		public void notifyShutdown( ShutdownEvent event ){
			Assert.assertEquals(8, cnt );
		}
	}
}
