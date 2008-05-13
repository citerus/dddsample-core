package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.TrackingId;

public interface CargoRepository {

  Cargo find(TrackingId trackingId);

  void save(Cargo cargo);

  TrackingId nextTrackingId();

}
