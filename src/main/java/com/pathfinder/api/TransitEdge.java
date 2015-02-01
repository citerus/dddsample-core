package com.pathfinder.api;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an edge in a path through a graph,
 * describing the route of a cargo.
 *  
 */
public final class TransitEdge implements Serializable {

  private final String voyageNumber;
  private final String fromUnLocode;
  private final String toUnLocode;
  private final Date fromDate;
  private final Date toDate;

  /**
   * Constructor.
   *
   * @param voyageNumber
   * @param fromUnLocode
   * @param toUnLocode
   * @param fromDate
   * @param toDate
   */
  public TransitEdge(final String voyageNumber,
                     final String fromUnLocode,
                     final String toUnLocode,
                     final Date fromDate,
                     final Date toDate) {
    this.voyageNumber = voyageNumber;
    this.fromUnLocode = fromUnLocode;
    this.toUnLocode = toUnLocode;
    this.fromDate = fromDate;
    this.toDate = toDate;
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

  public Date getFromDate() {
    return fromDate;
  }

  public Date getToDate() {
    return toDate;
  }
}