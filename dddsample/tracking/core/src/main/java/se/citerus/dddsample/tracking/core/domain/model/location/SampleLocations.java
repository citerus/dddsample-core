package se.citerus.dddsample.tracking.core.domain.model.location;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Sample locations, for test purposes.
 */
public class SampleLocations {
  private static final TimeZone CHINA = TimeZone.getTimeZone("Asia/Shanghai");
  private static final TimeZone CENTRAL_EUROPE = TimeZone.getTimeZone("Europe/Stockholm");
  private static final TimeZone JAPAN = TimeZone.getTimeZone("Asia/Tokyo");
  private static final TimeZone EASTERN = TimeZone.getTimeZone("America/New_York");
  private static final TimeZone CENTRAL = TimeZone.getTimeZone("America/Chicago");
  private static final TimeZone PACIFIC = TimeZone.getTimeZone("America/Los_Angeles");
  private static final TimeZone EASTERN_AUSTRALIA = TimeZone.getTimeZone("Australia/Melbourne");

  public static final CustomsZone US = new CustomsZone("US", "United States");
  public static final CustomsZone EU = new CustomsZone("EU", "European Union");
  public static final CustomsZone CN = new CustomsZone("CN", "United States");
  public static final CustomsZone AU = new CustomsZone("AU", "Australia");
  public static final CustomsZone JN = new CustomsZone("JN", "Japan");

  public static final Location HONGKONG = new Location(new UnLocode("CNHKG"), "Hongkong", CHINA, CN);
  public static final Location MELBOURNE = new Location(new UnLocode("AUMEL"), "Melbourne", EASTERN_AUSTRALIA, AU);
  public static final Location STOCKHOLM = new Location(new UnLocode("SESTO"), "Stockholm", CENTRAL_EUROPE, EU);
  public static final Location HELSINKI = new Location(new UnLocode("FIHEL"), "Helsinki", CENTRAL_EUROPE, EU);
  public static final Location CHICAGO = new Location(new UnLocode("USCHI"), "Chicago", CENTRAL, US);
  public static final Location LONGBEACH = new Location(new UnLocode("USLBG"), "Long Beach", PACIFIC, US);
  public static final Location OAKLAND = new Location(new UnLocode("USOAK"), "Oakland", PACIFIC, US);
  public static final Location SEATTLE = new Location(new UnLocode("USSEA"), "Seattle", PACIFIC, US);
  public static final Location TOKYO = new Location(new UnLocode("JNTKO"), "Tokyo", JAPAN, JN);
  public static final Location HAMBURG = new Location(new UnLocode("DEHAM"), "Hamburg", CENTRAL_EUROPE, EU);
  public static final Location SHANGHAI = new Location(new UnLocode("CNSHA"), "Shanghai", CHINA, CN);
  public static final Location ROTTERDAM = new Location(new UnLocode("NLRTM"), "Rotterdam", CENTRAL_EUROPE, EU);
  public static final Location GOTHENBURG = new Location(new UnLocode("SEGOT"), "Göteborg", CENTRAL_EUROPE, EU);
  public static final Location HANGZOU = new Location(new UnLocode("CNHGH"), "Hangzhou", CHINA, CN);
  public static final Location NEWYORK = new Location(new UnLocode("USNYC"), "New York", EASTERN, US);
  public static final Location DALLAS = new Location(new UnLocode("USDAL"), "Dallas", CENTRAL, US);

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
