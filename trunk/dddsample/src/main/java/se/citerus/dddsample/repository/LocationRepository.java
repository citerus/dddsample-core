package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Location;

public interface LocationRepository {

  Location find(String string);

}
