package com.pathfinder.internal;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.api.TransitEdge;
import com.pathfinder.api.TransitPath;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class GraphTraversalServiceImpl implements GraphTraversalService {

  private GraphDAO dao;
  private Random random;
  private static final long ONE_MIN_MS = 1000 * 60;
  private static final long ONE_DAY_MS = ONE_MIN_MS * 60 * 24;

  public GraphTraversalServiceImpl(GraphDAO dao) {
    this.dao = dao;
    this.random = new Random();
  }

  public List<TransitPath> findShortestPath(
      final String originNode, final String destinationNode, final Properties limitations) {
    List<String> allVertices = dao.listAllNodes();
    allVertices.remove(originNode);
    allVertices.remove(destinationNode);

    int candidateCount = getRandomNumberOfCandidates();
    List<TransitPath> candidates = new ArrayList<>(candidateCount);

    for (int i = 0; i < candidateCount; i++) {
      allVertices = getRandomChunkOfNodes(allVertices);
      List<TransitEdge> transitEdges = new ArrayList<>(allVertices.size() - 1);
      String fromNode = originNode;
      Instant date = Instant.now();

      for (int j = 0; j <= allVertices.size(); ++j) {
        Instant fromDate = nextDate(date);
        Instant toDate = nextDate(fromDate);
        String toNode = (j >= allVertices.size() ? destinationNode : allVertices.get(j));
        transitEdges.add(
            new TransitEdge(
                dao.getTransitEdge(fromNode, toNode), fromNode, toNode, fromDate, toDate));
        fromNode = toNode;
        date = nextDate(toDate);
      }
      candidates.add(new TransitPath(transitEdges));
    }
    return candidates;
  }

  private Instant nextDate(Instant date) {
    return date.plus(1, ChronoUnit.DAYS).plus((random.nextInt(1000) - 500), ChronoUnit.MINUTES);
  }

  private int getRandomNumberOfCandidates() {
    return 3 + random.nextInt(3);
  }

  private List<String> getRandomChunkOfNodes(List<String> allNodes) {
    Collections.shuffle(allNodes);
    final int total = allNodes.size();
    final int chunk = total > 4 ? 1 + random.nextInt(5) : total;
    return allNodes.subList(0, chunk);
  }
}
