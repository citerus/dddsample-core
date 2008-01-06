package se.citerus.dddsample.domain;

import javax.persistence.*;

@Entity
@Table(name = "carrier_movement")
public class CarrierMovement {

  @Id
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "from_location_fk")
  private final Location from;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "to_location_fk")
  private final Location to;

  public CarrierMovement(Location from, Location to) {
    this.from = from;
    this.to = to;
  }

  public Location from() {
    return from;
  }

  public Location to() {
    return to;
  }

}
