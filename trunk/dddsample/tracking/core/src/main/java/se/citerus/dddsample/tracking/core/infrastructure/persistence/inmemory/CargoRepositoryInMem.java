package se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory;

import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CargoRepositoryInMem implement the CargoRepository interface but is a test
 * class not intended for usage in real application.
 * <p/>
 * It setup a simple local hash with a number of Cargo's with TrackingId as key
 * defined at compile time.
 * <p/>
 */
public class CargoRepositoryInMem implements CargoRepository {

  private final Map<TrackingId, Cargo> cargoDb = new HashMap<TrackingId, Cargo>();

  @Override
  public Cargo find(final TrackingId trackingId) {
    return cargoDb.get(trackingId);
  }

  @Override
  public List<Cargo> findCargosOnVoyage(Voyage voyage) {
    final List<Cargo> onVoyage = new ArrayList<Cargo>();
    for (Cargo cargo : cargoDb.values()) {
      if (voyage.sameAs(cargo.currentVoyage())) {
        onVoyage.add(cargo);
      }
    }

    return onVoyage;
  }

  @Override
  public void store(Cargo cargo) {
    cargoDb.put(cargo.trackingId(), cargo);
  }

  @Override
  public List<Cargo> findAll() {
    return new ArrayList<Cargo>(cargoDb.values());
  }


}
