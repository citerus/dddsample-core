package se.citerus.dddsample.domain.model.carrier;

import se.citerus.dddsample.domain.model.location.Location;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Sample carrier movements, for test purposes.
 *
 */
public class SampleVoyages {

  public static final Voyage CM001 = createVoyage("CM001", STOCKHOLM, HAMBURG);
  public static final Voyage CM002 = createVoyage("CM002", HAMBURG, HONGKONG);
  public static final Voyage CM003 = createVoyage("CM003", HONGKONG, NEWYORK);
  public static final Voyage CM004 = createVoyage("CM004", NEWYORK, CHICAGO);
  public static final Voyage CM005 = createVoyage("CM005", CHICAGO, HAMBURG);
  public static final Voyage CM006 = createVoyage("CM006", HAMBURG, HANGZOU);

  private static Voyage createVoyage(String id, Location from, Location to) {
    return new Voyage(new VoyageNumber(id), new Schedule(Arrays.asList(
      new CarrierMovement(from, to, new Date(), new Date())
    )));
  }

  public static final Map<VoyageNumber, Voyage> ALL = new HashMap();

  static {
    for (Field field : SampleVoyages.class.getDeclaredFields()) {
      if (field.getType().equals(Voyage.class)) {
        try {
          Voyage voyage = (Voyage) field.get(null);
          ALL.put(voyage.voyageNumber(), voyage);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public static List<Voyage> getAll() {
    return new ArrayList(ALL.values());
  }

  public static Voyage lookup(VoyageNumber voyageNumber) {
    return ALL.get(voyageNumber);
  }
  
}
