package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.UnLocode;

import java.util.List;

public interface LocationRepository {

  Location find(UnLocode unLocode);

  List<Location> findAll();

}
