package se.citerus.dddsample.domain.model.carrier;

import se.citerus.dddsample.domain.model.location.Location;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Sample carrier movements, for test purposes.
 *
 */
public class SampleCarrierMovements {

  public static final CarrierMovement CM001 = createCarrierMovement("CM001", STOCKHOLM, HAMBURG);
  public static final CarrierMovement CM002 = createCarrierMovement("CM002", HAMBURG, HONGKONG);
  public static final CarrierMovement CM003 = createCarrierMovement("CM003", HONGKONG, NEWYORK);
  public static final CarrierMovement CM004 = createCarrierMovement("CM004", NEWYORK, CHICAGO);
  public static final CarrierMovement CM005 = createCarrierMovement("CM005", CHICAGO, HAMBURG);
  public static final CarrierMovement CM006 = createCarrierMovement("CM006", HAMBURG, HANGZOU);

  private static CarrierMovement createCarrierMovement(String id, Location from, Location to) {
    return new CarrierMovement(
      new CarrierMovementId(id), from, to,  new Date(), new Date());
  }

  public static final Map<CarrierMovementId, CarrierMovement> ALL = new HashMap();

  static {
    for (Field field : SampleCarrierMovements.class.getDeclaredFields()) {
      if (field.getType().equals(CarrierMovement.class)) {
        try {
          CarrierMovement carrierMovement = (CarrierMovement) field.get(null);
          ALL.put(carrierMovement.carrierMovementId(), carrierMovement);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public static List<CarrierMovement> getAll() {
    return new ArrayList(ALL.values());
  }

  public static CarrierMovement lookup(CarrierMovementId carrierMovementId) {
    return ALL.get(carrierMovementId);
  }
  
}
