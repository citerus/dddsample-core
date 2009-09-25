package se.citerus.dddsample.tracking.core.domain.service;

import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;

/**
 * Generates tracking ids for cargos. This is a domain service.
 */
public interface TrackingIdGenerator {

  TrackingId nextTrackingId();

}
