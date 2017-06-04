package com.pathfinder.internal;

import java.util.List;

public interface GraphDAO {
	List<String> listAllNodes();
	String getTransitEdge(String from, String to);
}
