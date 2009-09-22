package com.pathfinder.internal;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.api.TransitEdge;
import com.pathfinder.api.TransitPath;

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

  public List<TransitPath> findShortestPath(final String originUnLocode,
                                            final String destinationUnLocode,
                                            final Properties limitations) {
    Date date = nextDate(new Date());

    List<String> allVertices = dao.listLocations();
    allVertices.remove(originUnLocode);
    allVertices.remove(destinationUnLocode);

    final int candidateCount = getRandomNumberOfCandidates();
    final List<TransitPath> candidates = new ArrayList<TransitPath>(candidateCount);

    for (int i = 0; i < candidateCount; i++) {
      allVertices = getRandomChunkOfLocations(allVertices);
      final List<TransitEdge> transitEdges = new ArrayList<TransitEdge>(allVertices.size() - 1);
      final String firstLegTo = allVertices.get(0);

      Date fromDate = nextDate(date);
      Date toDate = nextDate(fromDate);
      date = nextDate(toDate);

      transitEdges.add(new TransitEdge(
        dao.getVoyageNumber(originUnLocode, firstLegTo),
        originUnLocode, firstLegTo, fromDate, toDate));

      for (int j = 0; j < allVertices.size() - 1; j++) {
        final String curr = allVertices.get(j);
        final String next = allVertices.get(j + 1);
        fromDate = nextDate(date);
        toDate = nextDate(fromDate);
        date = nextDate(toDate);
        transitEdges.add(new TransitEdge(dao.getVoyageNumber(curr, next), curr, next, fromDate, toDate));
      }

      final String lastLegFrom = allVertices.get(allVertices.size() - 1);
      fromDate = nextDate(date);
      toDate = nextDate(fromDate);
      transitEdges.add(new TransitEdge(
        dao.getVoyageNumber(lastLegFrom, destinationUnLocode),
        lastLegFrom, destinationUnLocode, fromDate, toDate));

      candidates.add(new TransitPath(transitEdges));
    }

    return candidates;
  }

  private Date nextDate(Date date) {
    return new Date(date.getTime() + ONE_DAY_MS + (random.nextInt(1000) - 500) * ONE_MIN_MS);
  }

  private int getRandomNumberOfCandidates() {
    return 3 + random.nextInt(3);
  }

  private List<String> getRandomChunkOfLocations(List<String> allLocations) {
    Collections.shuffle(allLocations);
    final int total = allLocations.size();
    final int chunk = total > 4 ? 1 + new Random().nextInt(5) : total;
    return allLocations.subList(0, chunk);
  }

}
