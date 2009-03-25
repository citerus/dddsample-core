package se.citerus.dddsample.interfaces.booking.facade.dto;

import java.io.Serializable;

/**
 * Location DTO.
 */
public class LocationDTO implements Serializable {

  private final String unLocode;
  private final String name;

  public LocationDTO(String unLocode, String name) {
    this.unLocode = unLocode;
    this.name = name;
  }

  public String getUnLocode() {
    return unLocode;
  }

  public String getName() {
    return name;
  }
  
}
