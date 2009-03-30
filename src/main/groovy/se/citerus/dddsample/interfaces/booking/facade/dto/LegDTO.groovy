package se.citerus.dddsample.interfaces.booking.facade.dto

/**
 * DTO for a leg in an itinerary.
 */
//@Immutable
final class LegDTO implements Serializable {

  final String voyageNumber
  final String from
  final String to
  final Date loadTime
  final Date unloadTime

  LegDTO(String voyageNumber, String from, String to, Date loadTime, Date unloadTime) {
    this.voyageNumber = voyageNumber
    this.from = from
    this.to = to
    this.loadTime = loadTime
    this.unloadTime = unloadTime
  }

}
