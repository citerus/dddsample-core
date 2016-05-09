package com.pathfinder.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GraphDAOStub implements GraphDAO{

  private static final Random random = new Random();

  public List<String> listAllNodes() {
    return new ArrayList<String>(Arrays.asList(
      "CNHKG", "AUMEL", "SESTO", "FIHEL", "USCHI", "JNTKO", "DEHAM", "CNSHA", "NLRTM", "SEGOT", "CNHGH", "USNYC", "USDAL"
    ));
  }

  public String getTransitEdge(String from, String to) {
    final int i = random.nextInt(5);
    if (i == 0) return "0100S";
    if (i == 1) return "0200T";
    if (i == 2) return "0300A";
    if (i == 3) return "0301S";
    return "0400S";
  }
  
}
