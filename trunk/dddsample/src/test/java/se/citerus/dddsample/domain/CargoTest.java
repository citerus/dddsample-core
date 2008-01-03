package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CargoTest extends TestCase{

  public void testCurrentLocationAtOrigin() throws Exception {
    Location destination = new Location("AUMEL");
    Location origin = new Location("SESTO");
    Cargo cargo = new Cargo(new TrackingId("XYZ"), origin, destination);

		assertEquals(origin, cargo.getCurrentLocation());
	}


  public void testCurrentLocationUnloaded() throws Exception {
		Cargo cargo = populateCargoOffHongKong();
		
		assertEquals(new Location("CNHGK"), cargo.getCurrentLocation());
	}
	
	public void testCurrentLocationloaded() throws Exception {
		Cargo cargo = populateCargoOnHamburg();
		
		assertEquals(new Location("DEHAM"), cargo.getCurrentLocation());
	}
	
	public void testAtFinalLocation() throws Exception {
		Cargo cargo = populateCargoOffMelbourne();
		
		assertTrue(cargo.atFinalDestiation());
	}
	
	public void testNotAtFinalLocationWhenNotUnloaded() throws Exception {
		Cargo cargo = populateCargoOnHongKong();
		
		assertFalse(cargo.atFinalDestiation());
	}
	
	// TODO: Generate test data some better way
	private Cargo populateCargoOffHongKong() throws Exception {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

		final CarrierMovement stockholmToHamburg = new CarrierMovement(new Location("SESTO"), new Location("DEHAM"));

	    cargo.handle(new HandlingEvent(getDate("2007-12-01"), HandlingEvent.Type.LOAD, stockholmToHamburg));
	    cargo.handle(new HandlingEvent(getDate("2007-12-02"), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

	    final CarrierMovement hamburgToHongKong =
	       new CarrierMovement(new Location("DEHAM"), new Location("CNHGK"));

	    cargo.handle(new HandlingEvent(getDate("2007-12-03"), HandlingEvent.Type.LOAD, hamburgToHongKong));
	    cargo.handle(new HandlingEvent(getDate("2007-12-04"), HandlingEvent.Type.UNLOAD, hamburgToHongKong));
	    
		return cargo;
	}
	
	private Cargo populateCargoOnHamburg() throws Exception {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

		final CarrierMovement stockholmToHamburg = new CarrierMovement(new Location("SESTO"), new Location("DEHAM"));

	    cargo.handle(new HandlingEvent(getDate("2007-12-01"), HandlingEvent.Type.LOAD, stockholmToHamburg));
	    cargo.handle(new HandlingEvent(getDate("2007-12-02"), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

	    final CarrierMovement hamburgToHongKong =
	       new CarrierMovement(new Location("DEHAM"), new Location("CNHGK"));

	    cargo.handle(new HandlingEvent(getDate("2007-12-03"), HandlingEvent.Type.LOAD, hamburgToHongKong));

		return cargo;
	}
	private Cargo populateCargoOffMelbourne() throws Exception {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

		final CarrierMovement stockholmToHamburg = new CarrierMovement(new Location("SESTO"), new Location("DEHAM"));

	    cargo.handle(new HandlingEvent(getDate("2007-12-01"), HandlingEvent.Type.LOAD, stockholmToHamburg));
	    cargo.handle(new HandlingEvent(getDate("2007-12-02"), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

	    final CarrierMovement hamburgToHongKong =
	       new CarrierMovement(new Location("DEHAM"), new Location("CNHGK"));

	    cargo.handle(new HandlingEvent(getDate("2007-12-03"), HandlingEvent.Type.LOAD, hamburgToHongKong));
	    cargo.handle(new HandlingEvent(getDate("2007-12-04"), HandlingEvent.Type.UNLOAD, hamburgToHongKong));

	    final CarrierMovement hongKongToMelbourne =
		       new CarrierMovement(new Location("CNHGK"), new Location("AUMEL"));
	    
	    cargo.handle(new HandlingEvent(getDate("2007-12-05"), HandlingEvent.Type.LOAD, hongKongToMelbourne));
	    cargo.handle(new HandlingEvent(getDate("2007-12-07"), HandlingEvent.Type.UNLOAD, hongKongToMelbourne));
	    
		return cargo;
	}
	
	private Cargo populateCargoOnHongKong() throws Exception {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

		final CarrierMovement stockholmToHamburg = new CarrierMovement(new Location("SESTO"), new Location("DEHAM"));

	    cargo.handle(new HandlingEvent(getDate("2007-12-01"), HandlingEvent.Type.LOAD, stockholmToHamburg));
	    cargo.handle(new HandlingEvent(getDate("2007-12-02"), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

	    final CarrierMovement hamburgToHongKong =
	       new CarrierMovement(new Location("DEHAM"), new Location("CNHGK"));

	    cargo.handle(new HandlingEvent(getDate("2007-12-03"), HandlingEvent.Type.LOAD, hamburgToHongKong));
	    cargo.handle(new HandlingEvent(getDate("2007-12-04"), HandlingEvent.Type.UNLOAD, hamburgToHongKong));

	    final CarrierMovement hongKongToMelbourne =
		       new CarrierMovement(new Location("CNHGK"), new Location("AUMEL"));
	    
	    cargo.handle(new HandlingEvent(getDate("2007-12-05"), HandlingEvent.Type.LOAD, hongKongToMelbourne));
	    
		return cargo;
	}
	
	/**
   * Parse an ISO 8601 (YYYY-MM-DD) String to Date
   *
   * @param isoFormat String to parse.
   * @return Created date instance.
   * @throws ParseException Thrown if parsing fails.
   */
  private Date getDate(String isoFormat) throws ParseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.parse(isoFormat);
  }
}
