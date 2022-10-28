package se.citerus.dddsample.interfaces.handling.ws;

import org.springframework.http.ResponseEntity;

public interface HandlingReportService {

    ResponseEntity<?> submitReport(HandlingReport handlingReport);

}
