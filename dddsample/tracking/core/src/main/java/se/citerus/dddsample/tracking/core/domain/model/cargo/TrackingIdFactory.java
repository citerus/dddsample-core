package se.citerus.dddsample.tracking.core.domain.model.cargo;

/**
 * Generates tracking ids for cargos. This is a domain service.
 */
public interface TrackingIdFactory {

  TrackingId nextTrackingId();

}
