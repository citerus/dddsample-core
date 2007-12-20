package se.citerus.dddsample.service;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.repository.CargoRepository;

public class CargoServiceImpl implements CargoService {
  private CargoRepository cargoRepository;

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  /**
   * Finds a Cargo based on given trackingId
   * 
   * @return the unique cargo with matching tracking id, or null if not found.
   * 
   */
  @Transactional(readOnly = true)
  public Cargo find(String trackingId) {
    return cargoRepository.find(new TrackingId(trackingId));
  }

}
