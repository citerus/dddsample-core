package se.citerus.dddsample.application.persistence;

import se.citerus.dddsample.domain.model.CarrierMovement;
import se.citerus.dddsample.domain.model.CarrierMovementId;
import static se.citerus.dddsample.domain.model.SampleLocations.*;
import se.citerus.dddsample.domain.repository.CarrierMovementRepository;

import java.util.HashMap;
import java.util.Map;

public final class CarrierMovementRepositoryInMem implements CarrierMovementRepository {

  private Map<CarrierMovementId, CarrierMovement> carriers;

  public CarrierMovementRepositoryInMem() {
    carriers = new HashMap<CarrierMovementId, CarrierMovement>();
  }

  private void setup() {
    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), STOCKHOLM, HAMBURG);
    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_002"), HAMBURG, HONGKONG);
    final CarrierMovement melbourneToTokyo = new CarrierMovement(
            new CarrierMovementId("CAR_003"), MELBOURNE, TOKYO);
    final CarrierMovement tokyoToChicago = new CarrierMovement(
            new CarrierMovementId("CAR_004"), TOKYO, CHICAGO);
    final CarrierMovement stockholmToHelsinki = new CarrierMovement(
            new CarrierMovementId("CAR_005"), STOCKHOLM, HELSINKI);
    
    carriers.put(new CarrierMovementId("SESTO_DEHAM"), stockholmToHamburg);
    carriers.put(new CarrierMovementId("DEHAM_CNHKG"), hamburgToHongKong);
    carriers.put(new CarrierMovementId("AUMEL_JPTOK"), melbourneToTokyo);
    carriers.put(new CarrierMovementId("JPTOK_USCHI"), tokyoToChicago);
    carriers.put(new CarrierMovementId("SESTO_FIHEL"), stockholmToHelsinki);
  }

  public CarrierMovement find(final CarrierMovementId carrierMovementId) {
    return carriers.get(carrierMovementId);
  }

}
