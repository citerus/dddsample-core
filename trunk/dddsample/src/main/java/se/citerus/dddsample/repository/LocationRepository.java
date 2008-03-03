package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.UnLocode;

public interface LocationRepository {

  Location find(UnLocode unLocode);

}
