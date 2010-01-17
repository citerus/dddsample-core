package se.citerus.dddsample.reporting.api;

public interface ReportSubmission {

  void reportCargo(CargoDetails cargoDetails);

  void reportHandling(String trackingId, Handling handling);

}
