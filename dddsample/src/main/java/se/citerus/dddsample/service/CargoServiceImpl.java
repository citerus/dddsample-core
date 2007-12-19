package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.CargoRepository;
import se.citerus.dddsample.domain.TrackingId;

public class CargoServiceImpl implements CargoService {
  private CargoRepository repository;
  
  /**
   * Finds a Cargo based on given trackingId
   * 
   * @return null if no Cargo is found
   * 
   */
  public Cargo find(String trackingId) {
    return getCargoRepository().find(new TrackingId(trackingId));
  }

  

  public CargoRepository getCargoRepository() {
    return repository;
  }


  public void setCargoRepository(CargoRepository repository) {
    this.repository = repository;
  }

  
}
