package com.pathfinder.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GraphDAO {

  private static final Random random = new Random();

  public List<String> listLocations() {
    return new ArrayList<String>(Arrays.asList(
      "CNHKG", "AUMEL", "SESTO", "FIHEL", "USCHI", "JPTOK", "DEHAM"
    ));
  }

  public String getVoyageNumber(String from, String to) {
    final int i = random.nextInt(3);
    if (i == 0) return "0101";
    if (i == 1) return "0202";
    return "0303";
  }
  
}
