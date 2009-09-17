package se.citerus.dddsample.domain.model.cargo;

/**
 * Generates tracking ids for cargos. This is a domain service.
 */
public interface TrackingIdGenerator {

  TrackingId nextTrackingId();

}
