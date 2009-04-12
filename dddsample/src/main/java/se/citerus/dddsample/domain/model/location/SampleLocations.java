package se.citerus.dddsample.domain.model.location;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Sample locations, for test purposes.
 * 
 */
public class SampleLocations {
  private static final TimeZone CHINA =  TimeZone.getTimeZone("Asia/Shanghai");
  private static final TimeZone CENTRAL_EUROPE =  TimeZone.getTimeZone("Europe/Stockholm");
  private static final TimeZone JAPAN =  TimeZone.getTimeZone("Asia/Tokyo");
  private static final TimeZone EASTERN =  TimeZone.getTimeZone("America/New_York");
  private static final TimeZone CENTRAL =  TimeZone.getTimeZone("America/Chicago");
  private static final TimeZone EASTERN_AUSTRALIA =  TimeZone.getTimeZone("Australia/Melbourne");
  

  public static final Location HONGKONG = new Location(new UnLocode("CNHKG"), "Hongkong",  CHINA);
  public static final Location MELBOURNE = new Location(new UnLocode("AUMEL"), "Melbourne", EASTERN_AUSTRALIA);
  public static final Location STOCKHOLM = new Location(new UnLocode("SESTO"), "Stockholm",  CENTRAL_EUROPE);
  public static final Location HELSINKI = new Location(new UnLocode("FIHEL"), "Helsinki",  CENTRAL_EUROPE);
  public static final Location CHICAGO = new Location(new UnLocode("USCHI"), "Chicago", CENTRAL);
  public static final Location TOKYO = new Location(new UnLocode("JNTKO"), "Tokyo", JAPAN);
  public static final Location HAMBURG = new Location(new UnLocode("DEHAM"), "Hamburg", CENTRAL_EUROPE);
  public static final Location SHANGHAI = new Location(new UnLocode("CNSHA"), "Shanghai", CHINA);
  public static final Location ROTTERDAM = new Location(new UnLocode("NLRTM"), "Rotterdam", CENTRAL_EUROPE);
  public static final Location GOTHENBURG = new Location(new UnLocode("SEGOT"), "Göteborg", CENTRAL_EUROPE);
  public static final Location HANGZOU = new Location(new UnLocode("CNHGH"), "Hangzhou", CHINA);
  public static final Location NEWYORK = new Location(new UnLocode("USNYC"), "New York", EASTERN);
  public static final Location DALLAS = new Location(new UnLocode("USDAL"), "Dallas", CENTRAL);

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
