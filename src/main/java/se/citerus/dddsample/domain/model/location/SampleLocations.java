package se.citerus.dddsample.domain.model.location;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sample locations, for test purposes.
 * 
 */
public class SampleLocations {

  public static final Location HONGKONG = new Location(new UnLocode("CNHKG"), "Hongkong");
  public static final Location MELBOURNE = new Location(new UnLocode("AUMEL"), "Melbourne");
  public static final Location STOCKHOLM = new Location(new UnLocode("SESTO"), "Stockholm");
  public static final Location HELSINKI = new Location(new UnLocode("FIHEL"), "Helsinki");
  public static final Location CHICAGO = new Location(new UnLocode("USCHI"), "Chicago");
  public static final Location TOKYO = new Location(new UnLocode("JNTKO"), "Tokyo");
  public static final Location HAMBURG = new Location(new UnLocode("DEHAM"), "Hamburg");
  public static final Location SHANGHAI = new Location(new UnLocode("CNSHA"), "Shanghai");
  public static final Location ROTTERDAM = new Location(new UnLocode("NLRTM"), "Rotterdam");
  public static final Location GOTHENBURG = new Location(new UnLocode("SEGOT"), "Göteborg");
  public static final Location HANGZOU = new Location(new UnLocode("CNHGH"), "Hangzhou");
  public static final Location NEWYORK = new Location(new UnLocode("USNYC"), "New York");
  public static final Location DALLAS = new Location(new UnLocode("USDAL"), "Dallas");

  public static final Map<UnLocode, Location> ALL = new HashMap<UnLocode, Location>();

  static {
    for (Field field : SampleLocations.class.getDeclaredFields()) {
      if (field.getType().equals(Location.class)) {
        try {
          Location location = (Location) field.get(null);
          ALL.put(location.unLocode(), location);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public static List<Location> getAll() {
    return new ArrayList<Location>(ALL.values());
  }

  public static Location lookup(UnLocode unLocode) {
    return ALL.get(unLocode);
  }

}
