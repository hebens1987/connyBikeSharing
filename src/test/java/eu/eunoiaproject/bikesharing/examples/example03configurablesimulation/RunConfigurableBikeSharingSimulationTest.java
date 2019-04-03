package eu.eunoiaproject.bikesharing.examples.example03configurablesimulation;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.junit.Assert;
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

import javax.inject.Singleton;

import static eu.eunoiaproject.bikesharing.examples.example03configurablesimulation.RunConfigurableBikeSharingSimulation.*;

public class RunConfigurableBikeSharingSimulationTest{
	private static final Logger log = Logger.getLogger( RunConfigurableBikeSharingSimulationTest.class ) ;

	@Test
	public void testOne() {

		final Config config = prepareConfig( null, InputCase.raster );

		config.controler().setLastIteration(10);

		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class );;
		bikeSharingConfig.setRunType( BikeSharingConfigGroup.RunType.debug );

		Scenario scenario = prepareScenario( config ) ;

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

	}
	@Test
	public void testTwo() {

		final Config config = prepareConfig( null, InputCase.kaiInputDiss );

		config.controler().setLastIteration(10);

		BikeSharingConfigGroup bikeSharingConfig = ConfigUtils.addOrGetModule( config, BikeSharingConfigGroup.NAME, BikeSharingConfigGroup.class );;
		bikeSharingConfig.setRunType( BikeSharingConfigGroup.RunType.debug );

		Scenario scenario = prepareScenario( config ) ;

		Controler controler = prepareControler( scenario ) ;

		controler.addOverridingModule( new AbstractModule(){
			@Override
			public void install(){
//				this.bind( Checker.class ).in( Singleton.class ) ;
//				this.addEventHandlerBinding().to( Checker.class ).in( Singleton.class ) ;
//				this.addControlerListenerBinding().to( Checker.class ).in( Singleton.class ) ;

				this.addEventHandlerBinding().to( EventsPrinter.class ) ;
			}
		} );

		controler.run() ;

	}

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
