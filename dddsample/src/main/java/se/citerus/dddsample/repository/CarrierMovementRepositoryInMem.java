package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.CarrierId;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.Location;

import java.util.HashMap;
import java.util.Map;

public class CarrierMovementRepositoryInMem implements CarrierMovementRepository {
  private Map<CarrierId, CarrierMovement> carriers;

  public CarrierMovementRepositoryInMem() {
    carriers = new HashMap<CarrierId, CarrierMovement>();
    setup();
  }

  private void setup() {
    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));
    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierId("CAR_002"), new Location("DEHAM"), new Location("CNHKG"));
    final CarrierMovement melbourneToTokyo = new CarrierMovement(
            new CarrierId("CAR_003"), new Location("AUMEL"), new Location("JPTOK"));
    final CarrierMovement tokyoToLosAngeles = new CarrierMovement(
            new CarrierId("CAR_004"), new Location("JPTOK"), new Location("USLA"));
    final CarrierMovement stockholmToHelsinki = new CarrierMovement(
            new CarrierId("CAR_005"), new Location("SESTO"), new Location("FIHEL"));
    
    carriers.put(new CarrierId("SESTO_DEHAM"), stockholmToHamburg);
    carriers.put(new CarrierId("DEHAM_CNHKG"), hamburgToHongKong);
    carriers.put(new CarrierId("AUMEL_JPTOK"), melbourneToTokyo);
    carriers.put(new CarrierId("JPTOK_USLA"), tokyoToLosAngeles);
    carriers.put(new CarrierId("SESTO_FIHEL"), stockholmToHelsinki);
  }

  public CarrierMovement find(CarrierId carrierId) {
    return carriers.get(carrierId);
  }

}
