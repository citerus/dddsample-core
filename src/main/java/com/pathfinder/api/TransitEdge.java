package com.pathfinder.api;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an edge in a path through a graph,
 * describing the route of a cargo.
 *  
 */
public final class TransitEdge implements Serializable {

  private final String edge;
  private final String fromNode;
  private final String toNode;
  private final Date fromDate;
  private final Date toDate;

  /**
   * Constructor.
   *
   * @param edge
   * @param fromNode
   * @param toNode
   * @param fromDate
   * @param toDate
   */
  public TransitEdge(final String edge,
                     final String fromNode,
                     final String toNode,
                     final Date fromDate,
                     final Date toDate) {
    this.edge = edge;
    this.fromNode = fromNode;
    this.toNode = toNode;
    this.fromDate = fromDate;
    this.toDate = toDate;
  }

  public String getEdge() {
    return edge;
  }

  public String getFromNode() {
    return fromNode;
  }

  public String getToNode() {
    return toNode;
  }

  public Date getFromDate() {
    return fromDate;
  }

  public Date getToDate() {
    return toDate;
  }
}