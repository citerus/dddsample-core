package se.citerus.dddsample.service;

import se.citerus.dddsample.domain.Cargo;

public interface CargoService {

  Cargo find(String trackingId);

}
