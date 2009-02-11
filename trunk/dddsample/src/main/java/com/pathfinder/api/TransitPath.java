package com.pathfinder.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class TransitPath implements Serializable {

  private final List<TransitEdge> transitEdges;

  /**
   * Constructor.
   *
   * @param transitEdges The legs for this itinerary.
   */
  public TransitPath(final List<TransitEdge> transitEdges) {
    this.transitEdges = transitEdges;
  }

  /**
   * @return An unmodifiable list DTOs.
   */
  public List<TransitEdge> getTransitEdges() {
    return Collections.unmodifiableList(transitEdges);
  }
}
