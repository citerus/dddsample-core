package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.CarrierId;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.Location;

import java.util.HashMap;

public class CarrierRepositoryInMem implements CarrierRepository {
  private HashMap<String, CarrierMovement> carriers;

  public CarrierRepositoryInMem() {
    carriers = new HashMap<String, CarrierMovement>();
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
    
    carriers.put("SESTO_DEHAM", stockholmToHamburg);
    carriers.put("DEHAM_CNHKG", hamburgToHongKong);
    carriers.put("AUMEL_JPTOK", melbourneToTokyo);
    carriers.put("JPTOK_USLA", tokyoToLosAngeles);
    carriers.put("SESTO_FIHEL", stockholmToHelsinki);
  }

  public CarrierMovement find(String carrierId) {
    return carriers.get(carrierId);
  }
  
  

}
