package se.citerus.dddsample.service;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;

public class CargoServiceImplTest extends TestCase {

  public void testCargoStatusFromLastHandlingEvent() {

    CargoServiceImpl cargoServiceImpl = new CargoServiceImpl();

    HandlingEvent lastEvent = null;
    assertEquals(CargoWithHistoryDTO.StatusCode.notReceived, cargoServiceImpl.statusForLastEvent(lastEvent));

    Location ham = new Location(new UnLocode("DE", "HAM"), "Hamburg");
    lastEvent = new HandlingEvent(null, null, null, HandlingEvent.Type.RECEIVE, ham);
    assertEquals(CargoWithHistoryDTO.StatusCode.inPort, cargoServiceImpl.statusForLastEvent(lastEvent));

    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("ABC"), ham, ham);
    lastEvent = new HandlingEvent(null, null, null, HandlingEvent.Type.LOAD, ham, carrierMovement);
    assertEquals(CargoWithHistoryDTO.StatusCode.onBoardCarrier, cargoServiceImpl.statusForLastEvent(lastEvent));

    lastEvent = new HandlingEvent(null, null, null, HandlingEvent.Type.UNLOAD, ham, carrierMovement);
    assertEquals(CargoWithHistoryDTO.StatusCode.inPort, cargoServiceImpl.statusForLastEvent(lastEvent));

    lastEvent = new HandlingEvent(null, null, null, HandlingEvent.Type.CLAIM, ham);
    assertEquals(CargoWithHistoryDTO.StatusCode.claimed, cargoServiceImpl.statusForLastEvent(lastEvent));

  }

}