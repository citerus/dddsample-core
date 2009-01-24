package com.partner.pathfinder.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

/**
 * Part of the external graph traversal API exposed by the routing team
 * and used by us (booking and tracking team).
 * 
 */
public interface GraphTraversalService extends Remote {

  List<TransitPath> findShortestPath(String originUnLocode, String destinationUnLocode, Properties limitations) throws RemoteException;

}