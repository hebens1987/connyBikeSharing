package eu.eunoiaproject.bikesharing.framework.scenario.bikeSharing;

import org.apache.log4j.Logger;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.io.MatsimXmlWriter;
import org.matsim.core.utils.misc.Counter;

import eu.eunoiaproject.freeFloatingBS.BikesFF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Writes a {@link BikeSharingFacilities} container to a file.
 * @author thibautd
 */
public class BikeSharingBikesWriter extends MatsimXmlWriter {
	private static final Logger log =
		Logger.getLogger(BikeSharingBikesWriter.class);

	private final BikeSharingBikes facilities;

	public BikeSharingBikesWriter( final BikeSharingBikes facilities ) {
		this.facilities = facilities;
	}

	public void write(final String fileName) {
		log.info( "writing eBike sharing bike in file "+fileName );
		openFile( fileName );
		writeXmlHead();
		writeDoctype( "bikeSharingBikes" , "bikesharingbikes_v1.dtd" );//TODO:
		writeStartTag( "bikeSharingBikes" , Collections.<Tuple<String, String>>emptyList() );

		if ( !facilities.getMetadata().isEmpty() ) {
			writeStartTag( "metadata" , Collections.<Tuple<String, String>>emptyList() );
			for ( Map.Entry<String, String> meta : facilities.getMetadata().entrySet() ) {
				writeStartTag(
						"attribute",
						Arrays.asList(
							createTuple( "name" , meta.getKey() ),
							createTuple( "value" , meta.getValue() ) ),
						true);
			}
			writeEndTag( "metadata" );
		}

		// this has the side effect of jumping a line, making the output more readable
		writeContent( "" , true );

		final Counter counter = new Counter( "writing bike sharing facility # " );
		for ( Bikes f : facilities.getFacilities().values() ) {
			counter.incCounter();
			writeBikes( f );
		}
		for ( BikesE f : facilities.getEFacilities().values() ) {
			counter.incCounter();
			writeEBikes( f );
		}
		
		for ( BikesFF f : facilities.getFFFacilities().values() ) {
			counter.incCounter();
			writeFFBikes( f );
		}
		counter.printCounter();

		writeEndTag( "bikeSharingFacilities" );
		close();
	}
	
	private void writeFFBikes (final BikesFF f)
	{
		final List< Tuple<String, String> > atts = new ArrayList< Tuple<String, String> >();
		atts.add (createTuple("bikeId", f.getBikeId().toString()));
		atts.add(createTuple("linkId",f.getLinkId().toString()));
		atts.add(createTuple("x", f.getCoordinate().getX()));
		atts.add(createTuple("y", f.getCoordinate().getY()));
		
		writeStartTag(
				"ffbike",
				atts,
				true );
	}

	private void writeEBikes(final BikesE f) {
		final List< Tuple<String, String> > atts = new ArrayList< Tuple<String, String> >();

		atts.add( createTuple( "bikeId" , f.getBikeId().toString() ) );
		atts.add( createTuple( "timeOfLastAct" , f.getTime()));
		atts.add( createTuple( "stateOfCharge" , f.getStateOfCharge()));
		atts.add( createTuple( "bikeInStation" , f.getInfoIfBikeInStation()));
		atts.add( createTuple( "ohmicResistance" , f.getOhmicResistance()));
		atts.add( createTuple( "voltage" , f.getVoltage()));
		atts.add( createTuple( "batteryChargeCapacity" , f.getBatteryChargeCapacity()));
		atts.add( createTuple( "kmFullToEmpty" , f.getKmFullToEmpty()));
		atts.add( createTuple( "isEBike" , f.getIsEBike()));

		writeStartTag(
				"ebike",
				atts,
				true );
	}
	
	private void writeBikes(final Bikes f) {
		final List< Tuple<String, String> > atts = new ArrayList< Tuple<String, String> >();

		atts.add( createTuple( "bikeId" , f.getBikeId().toString() ) );
		atts.add( createTuple( "timeOfLastAct" , f.getTime()));
		atts.add( createTuple( "bikeInStation" , f.getInfoIfBikeInStation()));
		atts.add( createTuple( "isEBike" , f.getIsEBike()));

		writeStartTag(
				"bike",
				atts,
				true );
	}
}

