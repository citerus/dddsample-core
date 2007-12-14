package se.citerus.dddsample.domain;


public interface CargoRepository {
  Cargo find(TrackingId trackingId);
}
