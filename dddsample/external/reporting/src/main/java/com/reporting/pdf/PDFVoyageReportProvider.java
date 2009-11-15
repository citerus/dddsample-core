package com.reporting.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.reporting.reports.VoyageReport;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

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
      document.add(new Paragraph("Voyage " + voyageReport.getVoyage().getVoyageNumber()));
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
  }

}