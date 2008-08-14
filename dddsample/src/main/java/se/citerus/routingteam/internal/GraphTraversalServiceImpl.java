package se.citerus.routingteam.internal;

import se.citerus.routingteam.GraphTraversalService;
import se.citerus.routingteam.TransitEdge;
import se.citerus.routingteam.TransitPath;

import java.util.*;

public class GraphTraversalServiceImpl implements GraphTraversalService {

  private GraphDAO dao;
  private Random random;

  public GraphTraversalServiceImpl(GraphDAO dao) {
    this.dao = dao;
    this.random = new Random();
  }

  public List<TransitPath> performHeavyCalculations(String originUnLocode, String destinationUnLocode) {
    List<String> allVertices = dao.listLocations();
    allVertices.remove(originUnLocode);
    allVertices.remove(destinationUnLocode);

    final int candidateCount = getRandomNumberOfCandidates();
    final List<TransitPath> candidates = new ArrayList<TransitPath>(candidateCount);

    for (int i = 0; i < candidateCount; i++) {
      allVertices = getRandomChunkOfLocations(allVertices);
      final List<TransitEdge> transitEdges = new ArrayList<TransitEdge>(allVertices.size() - 1);
      final String firstLegTo = allVertices.get(0);

      transitEdges.add(new TransitEdge(
        getRandomCarrierMovementId(originUnLocode, firstLegTo),
        originUnLocode, firstLegTo));

      for (int j = 0; j < allVertices.size() - 1; j++) {
        final String curr = allVertices.get(j);
        final String next = allVertices.get(j + 1);
        transitEdges.add(new TransitEdge(getRandomCarrierMovementId(curr, next), curr, next));
      }

      final String lastLegFrom = allVertices.get(allVertices.size() - 1);
      transitEdges.add(new TransitEdge(
        getRandomCarrierMovementId(lastLegFrom, destinationUnLocode),
        lastLegFrom, destinationUnLocode));

      candidates.add(new TransitPath(transitEdges));
    }

    return candidates;
  }

  private String getRandomCarrierMovementId(String from, String to) {
    final String random = UUID.randomUUID().toString().toUpperCase();
    final String cmId =  random.substring(0, 4);
    dao.storeCarrierMovementId(cmId, from, to);
    return cmId;
  }

  private int getRandomNumberOfCandidates() {
    return 1 + random.nextInt(4);
  }

  private List<String> getRandomChunkOfLocations(List<String> allLocations) {
    Collections.shuffle(allLocations);
    final int total = allLocations.size();
    final int chunk = total > 4 ? (total - 4) + random.nextInt(5) : total;
    return allLocations.subList(0, chunk);
  }

}
