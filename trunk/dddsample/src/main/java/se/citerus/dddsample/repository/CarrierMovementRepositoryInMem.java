package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.CarrierMovementId;
import se.citerus.dddsample.domain.Location;

import java.util.HashMap;
import java.util.Map;

public class CarrierMovementRepositoryInMem implements CarrierMovementRepository {
  private Map<CarrierMovementId, CarrierMovement> carriers;

  public CarrierMovementRepositoryInMem() {
    carriers = new HashMap<CarrierMovementId, CarrierMovement>();
    setup();
  }

  private void setup() {
    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));
    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_002"), new Location("DEHAM"), new Location("CNHKG"));
    final CarrierMovement melbourneToTokyo = new CarrierMovement(
            new CarrierMovementId("CAR_003"), new Location("AUMEL"), new Location("JPTOK"));
    final CarrierMovement tokyoToLosAngeles = new CarrierMovement(
            new CarrierMovementId("CAR_004"), new Location("JPTOK"), new Location("USLA"));
    final CarrierMovement stockholmToHelsinki = new CarrierMovement(
            new CarrierMovementId("CAR_005"), new Location("SESTO"), new Location("FIHEL"));
    
    carriers.put(new CarrierMovementId("SESTO_DEHAM"), stockholmToHamburg);
    carriers.put(new CarrierMovementId("DEHAM_CNHKG"), hamburgToHongKong);
    carriers.put(new CarrierMovementId("AUMEL_JPTOK"), melbourneToTokyo);
    carriers.put(new CarrierMovementId("JPTOK_USLA"), tokyoToLosAngeles);
    carriers.put(new CarrierMovementId("SESTO_FIHEL"), stockholmToHelsinki);
  }

  public CarrierMovement find(CarrierMovementId carrierMovementId) {
    return carriers.get(carrierMovementId);
  }

}
