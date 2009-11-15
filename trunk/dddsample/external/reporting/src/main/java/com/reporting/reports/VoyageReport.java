package com.reporting.reports;

import se.citerus.dddsample.reporting.api.OnboardCargo;
import se.citerus.dddsample.reporting.api.VoyageDetails;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class VoyageReport {

  private VoyageDetails voyage;
  private List<OnboardCargo> onboardOnboardCargos = new ArrayList<OnboardCargo>();

  public List<OnboardCargo> getOnboardCargos() {
    return onboardOnboardCargos;
  }

  public void setOnboardCargos(List<OnboardCargo> onboardOnboardCargos) {
    this.onboardOnboardCargos = onboardOnboardCargos;
  }

  public VoyageDetails getVoyage() {
    return voyage;
  }

  public void setVoyage(VoyageDetails voyage) {
    this.voyage = voyage;
  }

}
