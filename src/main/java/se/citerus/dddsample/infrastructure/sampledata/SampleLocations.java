package se.citerus.dddsample.infrastructure.sampledata;

import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.LocationDTO;

import java.lang.reflect.Field;
import java.util.*;

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

  public static final LocationDTO HONGKONG_DTO = new LocationDTO("CNHKG", "Hongkong");
  public static final LocationDTO MELBOURNE_DTO = new LocationDTO("AUMEL", "Melbourne");
  public static final LocationDTO STOCKHOLM_DTO = new LocationDTO("SESTO", "Stockholm");
  public static final LocationDTO HELSINKI_DTO = new LocationDTO("FIHEL", "Helsinki");
  public static final LocationDTO CHICAGO_DTO = new LocationDTO("USCHI", "Chicago");
  public static final LocationDTO TOKYO_DTO = new LocationDTO("JNTKO", "Tokyo");
  public static final LocationDTO HAMBURG_DTO = new LocationDTO("DEHAM", "Hamburg");
  public static final LocationDTO SHANGHAI_DTO = new LocationDTO("CNSHA", "Shanghai");
  public static final LocationDTO ROTTERDAM_DTO = new LocationDTO("NLRTM", "Rotterdam");
  public static final LocationDTO GOTHENBURG_DTO = new LocationDTO("SEGOT", "Göteborg");
  public static final LocationDTO HANGZOU_DTO = new LocationDTO("CNHGH", "Hangzhou");
  public static final LocationDTO NEWYORK_DTO = new LocationDTO("USNYC", "New York");
  public static final LocationDTO DALLAS_DTO = new LocationDTO("USDAL", "Dallas");

  public static final Map<UnLocode, Location> ALL = new HashMap<>();
  public static final Map<String, LocationDTO> ALL_DTOS = new HashMap<>();

  static {
    for (Field field : SampleLocations.class.getDeclaredFields()) {
      if (field.getType().equals(Location.class)) {
        try {
          Location location = (Location) field.get(null);
          ALL.put(location.unLocode(), location);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      } else if (field.getType().equals(LocationDTO.class)) {
        try {
          LocationDTO location = (LocationDTO) field.get(null);
          ALL_DTOS.put(location.unlocode, location);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public static List<Location> getAll() {
    return new ArrayList<>(ALL.values());
  }

  public static Collection<LocationDTO> getAllDtos() {
    return ALL_DTOS.values();
  }

  public static Location lookup(UnLocode unLocode) {
    return ALL.get(unLocode);
  }

}
