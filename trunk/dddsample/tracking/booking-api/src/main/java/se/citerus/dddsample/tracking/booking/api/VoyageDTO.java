package se.citerus.dddsample.tracking.booking.api;

import java.io.Serializable;
import java.util.List;

public class VoyageDTO implements Serializable {
  private final String voyageNumber;
  private final List<CarrierMovementDTO> movements;

  public VoyageDTO(String voyageNumber, List<CarrierMovementDTO> movements) {
    this.voyageNumber = voyageNumber;
    this.movements = movements;
  }

  public String getVoyageNumber() {
    return voyageNumber;
  }

  public List<CarrierMovementDTO> getMovements() {
    return movements;
  }
}
