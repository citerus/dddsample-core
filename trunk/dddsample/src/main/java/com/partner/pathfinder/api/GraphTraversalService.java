package com.partner.pathfinder.api;

import java.util.List;

/**
 * Part of the external graph traversal API exposed by the routing team
 * and used by us (booking and tracking team).
 * 
 */
public interface GraphTraversalService {

  List<TransitPath> findShortestPath(String originUnLocode, String destinationUnLocode);

}