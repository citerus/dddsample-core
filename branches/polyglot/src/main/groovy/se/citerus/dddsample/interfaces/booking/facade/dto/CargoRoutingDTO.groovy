package se.citerus.dddsample.interfaces.booking.facade.dto

/**
 * DTO for registering and routing a cargo.
 */
//@Immutable
final class CargoRoutingDTO implements Serializable {

  final String trackingId
  final String origin
  final String finalDestination
  final Date arrivalDeadline
  final boolean misrouted
  final List<LegDTO> legs



  CargoRoutingDTO(String trackingId,
                  String origin,
                  String finalDestination,
                  Date arrivalDeadline,
                  boolean misrouted,
                  List<LegDTO> legs) {
    this.trackingId = trackingId
    this.origin = origin
    this.finalDestination = finalDestination
    this.arrivalDeadline = arrivalDeadline
    this.misrouted = misrouted
    this.legs = legs
  }

  boolean isRouted() {
    !legs.empty
  }

}
