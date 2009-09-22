package com.reporting;

public interface ReportingService {

  DeliveryReport getDeliveryReport(int start, int offset);

  VoyageReport getVoyageReport();

}
