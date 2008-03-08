package se.citerus.dddsample.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 */
@Entity
public class Itinerary {

  @Id
  @GeneratedValue
  private Long id;

}
