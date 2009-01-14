package com.partner.pathfinder.internal;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GraphDAO {

  public List<String> listLocations() {
    return Arrays.asList(
      "CNHKG", "AUMEL", "SESTO", "FIHEL", "USCHI", "JNTKO", "DEHAM",
      "CNSHA", "NLRTM", "SEGOT", "CNHGH", "USNYC", "USDAL"
    );
  }

  public String getVoyageNumber(String from, String to) {
    // TODO return only those that are in the database
    final String random = UUID.randomUUID().toString().toUpperCase();
    final String cmId =  random.substring(0, 4);
    //dao.storeCarrierMovementId(cmId, from, to);
    return cmId;
  }
}
