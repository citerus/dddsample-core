package com.reporting;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import static com.lowagie.text.FontFactory.*;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.Produces;

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
      document.add(new Paragraph("Cargo " + cargoReport.getTrackingId(), getFont(HELVETICA_BOLD, 18.0f)));
      document.add(new Paragraph("Status: " + cargoReport.getCurrentStatus()));
      document.add(new Paragraph("Location: " + cargoReport.getCurrentLocation()));
      document.add(new Paragraph("Voyage: " + cargoReport.getCurrentVoyage()));
      document.add(new Paragraph("Destination: " + cargoReport.getFinalDestination()));
      document.add(new Paragraph("Arrival deadline: " + cargoReport.getArrivalDeadline()));
      document.add(new Paragraph("ETA: " + cargoReport.getEta()));

      document.add(new Paragraph("Handling history", getFont(HELVETICA_BOLD)));
      PdfPTable table = new PdfPTable(4);
      for (CargoReport.Handling handling : cargoReport.getHandlings()) {
        table.addCell(handling.getType());
        table.addCell(handling.getLocation());
        table.addCell(handling.getVoyage());
        table.addCell(handling.getCompletedOn());
      }
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
  }

}