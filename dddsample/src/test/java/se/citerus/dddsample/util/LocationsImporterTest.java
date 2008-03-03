package se.citerus.dddsample.util;

import se.citerus.dddsample.repository.AbstractRepositoryTest;

public class LocationsImporterTest extends AbstractRepositoryTest {

  public void testImportLocations() throws Exception {
    LocationsImporter importer = new LocationsImporter();
    long t = System.currentTimeMillis();

    //importer.importLocations(getSessionFactory().getCurrentSession());

    System.out.println("\n* * * Time to import: " + (System.currentTimeMillis() - t)/1000.0 + " seconds.\n");
    //setComplete();
  }

}
