package com.reporting.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.reporting.reports.VoyageReport;
import se.citerus.dddsample.reporting.api.OnboardCargo;
import se.citerus.dddsample.reporting.api.VoyageDetails;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import static com.lowagie.text.FontFactory.HELVETICA_BOLD;
import static com.lowagie.text.FontFactory.getFont;

@Produces("application/pdf")
@Provider
public class PDFVoyageReportProvider extends PDFMessageBodyWriter<VoyageReport> {

  @Override
  protected Class<VoyageReport> getSupportedClass() {
    return VoyageReport.class;
  }

  @Override
  protected void buildDocument(VoyageReport voyageReport, Document document) {
    try {
      VoyageDetails voyage = voyageReport.getVoyage();
      document.add(new Paragraph("Voyage " + voyage.getVoyageNumber(), getFont(HELVETICA_BOLD, 18.0f)));
      document.add(new Paragraph(" "));
      document.add(new Paragraph("Status: " + voyage.getCurrentStatus()));
      document.add(new Paragraph("Next stop: " + voyage.getNextStop() + " at " + voyage.getEtaNextStopAsString()));
      document.add(new Paragraph("Delay: " + voyage.getDelayedByMinutes() + " minutes"));
      document.add(new Paragraph("Last updated: " + voyage.getLastUpdatedOnAsString()));
      document.add(new Paragraph(" "));
      document.add(new Paragraph("Onboard cargos", getFont(HELVETICA_BOLD)));
      document.add(new Paragraph(" "));

      PdfPTable table = new PdfPTable(2);
      for (OnboardCargo cargo : voyageReport.getOnboardCargos()) {
        table.addCell(cargo.getTrackingId());
        table.addCell(cargo.getFinalDestination());
      }
      document.add(table);
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
  }

}