package com.pathfinder.api;

import java.io.Serializable;

/**
 * Represents an edge in a path through a graph,
 * describing the route of a cargo.
 */
public final class TransitEdge implements Serializable {

  private final String voyageNumber;
  private final String fromUnLocode;
  private final String toUnLocode;

  /**
   * Constructor.
   *
   * @param voyageNumber    voyage number
   * @param fromUnLocode    UN Locode of start location
   * @param toUnLocode      UN Locode of end location    
   */
  public TransitEdge(final String voyageNumber,
                     final String fromUnLocode,
                     final String toUnLocode) {
    this.voyageNumber = voyageNumber;
    this.fromUnLocode = fromUnLocode;
    this.toUnLocode = toUnLocode;
  }

  public String getVoyageNumber() {
    return voyageNumber;
  }

  public String getFromUnLocode() {
    return fromUnLocode;
  }

  public String getToUnLocode() {
    return toUnLocode;
  }

}