package com.reporting.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.reporting.reports.CargoReport;
import se.citerus.dddsample.reporting.api.CargoDetails;
import se.citerus.dddsample.reporting.api.Handling;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import static com.lowagie.text.FontFactory.HELVETICA_BOLD;
import static com.lowagie.text.FontFactory.getFont;

@Produces("application/pdf")
@Provider
public class PDFCargoReportProvider extends PDFMessageBodyWriter<CargoReport> {

  @Override
  protected Class<CargoReport> getSupportedClass() {
    return CargoReport.class;
  }

  @Override
  protected void buildDocument(CargoReport cargoReport, Document document) {
    try {
      CargoDetails cargoDetails = cargoReport.getCargo();
      document.add(new Paragraph("Cargo " + cargoDetails.getTrackingId(), getFont(HELVETICA_BOLD, 18.0f)));
      document.add(new Paragraph(" "));
      document.add(new Paragraph("Status: " + cargoDetails.getCurrentStatus()));
      document.add(new Paragraph("Location: " + cargoDetails.getCurrentLocation()));
      document.add(new Paragraph("Voyage: " + cargoDetails.getCurrentVoyage()));
      document.add(new Paragraph("Destination: " + cargoDetails.getFinalDestination()));
      document.add(new Paragraph("Arrival deadline: " + cargoDetails.getArrivalDeadline()));
      document.add(new Paragraph("ETA: " + cargoDetails.getEta()));
      document.add(new Paragraph(" "));
      document.add(new Paragraph("Handling history", getFont(HELVETICA_BOLD)));
      document.add(new Paragraph(" "));

      PdfPTable table = new PdfPTable(4);
      for (Handling handling : cargoReport.getHandlings()) {
        table.addCell(handling.getType());
        table.addCell(handling.getLocation());
        table.addCell(handling.getVoyage());
        table.addCell(handling.getCompletedOnAsString());
      }
      document.add(table);
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
  }

}