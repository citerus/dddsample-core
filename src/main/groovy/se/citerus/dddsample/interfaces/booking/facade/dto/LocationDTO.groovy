package se.citerus.dddsample.interfaces.booking.facade.dto

/**
 * Location DTO.
 */
//@Immutable
final class LocationDTO implements Serializable {

  final String unLocode
  final String name

  LocationDTO(String unLocode, String name) {
    this.unLocode = unLocode
    this.name = name
  }
  
}
