package se.citerus.dddsample.domain;

/**
 * A few locations for easy testing.
 *
 */
public class SampleLocations {

  public static final Location HONGKONG = new Location(new UnLocode("CN", "HKG"), "Hongkong");
  public static final Location MELBOURNE = new Location(new UnLocode("AU","MEL"), "Melbourne");
  public static final Location STOCKHOLM = new Location(new UnLocode("SE","STO"), "Stockholm");
  public static final Location HELSINKI = new Location(new UnLocode("Fi","HEL"), "Helsinki");
  public static final Location USCHI = new Location(new UnLocode("US", "CHI"), "Chicago");
  public static final Location JPTKO = new Location(new UnLocode("JN","TKO"), "Tokyo");
  public static final Location DEHAM = new Location(new UnLocode("DE", "HAM"), "Hamburg");

}
