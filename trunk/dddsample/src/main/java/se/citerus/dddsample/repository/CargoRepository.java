package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.TrackingId;

import java.util.List;

public interface CargoRepository {

  Cargo find(TrackingId trackingId);

  List<Cargo> findAll();

  void save(Cargo cargo);

  TrackingId nextTrackingId();

}
