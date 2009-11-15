package com.reporting.reports;

import se.citerus.dddsample.reporting.api.CargoDetails;
import se.citerus.dddsample.reporting.api.Handling;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class CargoReport {

  CargoDetails cargoDetails;
  List<Handling> handlings;

  public CargoDetails getCargo() {
    return cargoDetails;
  }

  public void setCargo(CargoDetails cargoDetails) {
    this.cargoDetails = cargoDetails;
  }

  public List<Handling> getHandlings() {
    return handlings;
  }

  public void setHandlings(List<Handling> handlings) {
    this.handlings = handlings;
  }

}
