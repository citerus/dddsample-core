package com.reporting;

import com.reporting.db.ReportDAO;
import com.reporting.reports.CargoReport;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.citerus.dddsample.reporting.api.Handling;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/context.xml", "/context-cxf.xml", "/context-test-setup.xml"})
public class ReportServiceTest {

  @Autowired
  ReportDAO reportDAO;
  private XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

  @Test
  public void cargoReport() throws Exception {
    JSONObject json = readJSON("/cargo/ABC.json");
    JSONObject cargoReport = json.getJSONObject("cargoReport");

    JSONObject cargo = cargoReport.getJSONObject("cargo");
    
    assertEquals("ABC", cargo.get("trackingId"));
    assertEquals("Hongkong", cargo.get("receivedIn"));
    assertEquals("Stockholm", cargo.get("finalDestination"));
    assertEquals("6/15/09 12:00 PM", cargo.get("arrivalDeadline"));
    assertEquals("6/12/09 6:30 PM", cargo.get("eta"));
    assertEquals("Onboard voyage", cargo.get("currentStatus"));
    assertEquals("V0100", cargo.get("currentVoyage"));
    //assertEquals("Tokyo", cargo.get("currentLocation"));
    assertEquals("6/8/09 2:23 PM", cargo.get("lastUpdatedOn"));

    JSONArray handlings = cargoReport.getJSONArray("handlings");
    assertEquals(4, handlings.length());

    verifyHandling(handlings.getJSONObject(0), "Receive", "Hongkong", null);
    verifyHandling(handlings.getJSONObject(1), "Load", "Hongkong", "V0100");
    verifyHandling(handlings.getJSONObject(2), "Unload", "Long Beach", "V0100");
    verifyHandling(handlings.getJSONObject(3), "Load", "Long Beach", "V0200");
  }

  @Test
  public void cargoPDFReport() throws Exception {
    String pdf = readPDF("/cargo/ABC.pdf");
    assertTrue(pdf.length() > 0);
  }

  @Test(expected = FileNotFoundException.class)
  public void cargoNotFound() throws Exception {
    readJSON("/cargo/NOSUCH.json");
  }

  @Test
  public void voyageReportWithCargos() throws Exception {
    JSONObject json = readJSON("/voyage/V0100.json");
    JSONObject voyageReport = json.getJSONObject("voyageReport");

    JSONObject voyage = voyageReport.getJSONObject("voyage");

    assertEquals("V0100", voyage.get("voyageNumber"));
    assertEquals("Honolulu", voyage.get("nextStop"));
    assertEquals("6/10/09 4:25 AM", voyage.get("etaNextStop"));
    assertEquals("In port", voyage.get("currentStatus"));
    assertEquals(1400, voyage.get("delayedByMinutes"));
    assertEquals("6/6/09 2:01 PM", voyage.get("lastUpdatedOn"));

    JSONObject cargo = voyageReport.getJSONObject("onboardCargos");
    assertEquals("ABC", cargo.get("trackingId"));
    assertEquals("Stockholm", cargo.get("finalDestination"));
  }

  @Test
  public void voyageReport() throws Exception {
    JSONObject json = readJSON("/voyage/V0200.json");
    JSONObject voyageReport = json.getJSONObject("voyageReport");

    JSONObject voyage = voyageReport.getJSONObject("voyage");

    assertEquals("V0200", voyage.get("voyageNumber"));
    assertEquals("Seattle", voyage.get("nextStop"));
    assertEquals("6/7/09 12:45 PM", voyage.get("etaNextStop"));
    assertEquals("In transit", voyage.get("currentStatus"));
    assertEquals(0, voyage.get("delayedByMinutes"));
    assertEquals("6/6/09 2:01 PM", voyage.get("lastUpdatedOn"));
    assertFalse(voyageReport.has("onboardCargos"));
  }

  @Test(expected = FileNotFoundException.class)
  public void voyageNotFound() throws Exception {
    readJSON("/voyage/NOSUCH.json");
  }

  @Test
  public void voyagePDFReport() throws Exception {
    String pdf = readPDF("/voyage/V0200.pdf");
    assertTrue(pdf.length() > 0);
  }

  @Test
  public void reportCargo() throws Exception {
    HttpURLConnection con = openXMLPutConnection("/cargo");
    XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(con.getOutputStream());
    writer.writeStartDocument();
    writer.writeStartElement("cargoDetails");

    addElement(writer, "trackingId", "FGH456");
    addElement(writer, "receivedIn", "HONGKONG");
    addElement(writer, "finalDestination", "HELSINKI");
    addElement(writer, "arrivalDeadline", "2010-05-15");
    addElement(writer, "eta", "2010-05-04 14:30");
    addElement(writer, "currentStatus", "ONBOARD_CARRIER");
    addElement(writer, "currentVoyage", "S0134");
    addElement(writer, "currentLocation", "");
    addElement(writer, "lastUpdatedOn", "2010-05-01 12:20");

    writer.writeEndElement();
    writer.writeEndDocument();

    writer.flush();
    writer.close();
    
    assertEquals(HTTP_NO_CONTENT, con.getResponseCode());

    CargoReport cargoReport = reportDAO.loadCargoReport("FGH456");
    assertNotNull(cargoReport);
  }

  @Test
  public void reportHandling() throws Exception {
    HttpURLConnection con = openXMLPostConnection("/cargo/ABC/handled");
    XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(con.getOutputStream());
    writer.writeStartDocument();
    writer.writeStartElement("handling");

    addElement(writer, "type", "Unload");
    addElement(writer, "location", "New York");
    addElement(writer, "voyage", "V0200");
    addElement(writer, "completedOn", "2009-06-09 12:10");

    writer.writeEndElement();
    writer.writeEndDocument();

    writer.flush();
    writer.close();

    assertEquals(HTTP_NO_CONTENT, con.getResponseCode());

    CargoReport cargoReport = reportDAO.loadCargoReport("ABC");
    List<Handling> handlings = cargoReport.getHandlings();
    assertEquals(5, handlings.size());
    assertEquals("Unload", handlings.get(4).getType());
  }

  private HttpURLConnection openXMLPostConnection(String path) throws IOException {
    return openWithMethod(path, "POST", "application/xml");
  }

  private HttpURLConnection openXMLPutConnection(String path) throws IOException {
    return openWithMethod(path, "PUT", "application/xml");
  }

  private HttpURLConnection openWithMethod(String path, String method, String contentType) throws IOException {
    HttpURLConnection con = open(path);
    con.setDoOutput(true);
    con.setRequestMethod(method);
    con.setRequestProperty("Content-type", contentType);
    return con;
  }

  private void addElement(XMLStreamWriter writer, String elementName, String content) throws XMLStreamException {
    writer.writeStartElement(elementName);
    writer.writeCharacters(content);
    writer.writeEndElement();
  }

  private void verifyHandling(JSONObject handling, String type, String location, String voyage) throws JSONException {
    assertEquals(type, handling.get("type"));
    assertEquals(location, handling.get("location"));
    if (voyage == null) {
      assertFalse(handling.has("voyage"));
    } else {
      assertEquals(voyage, handling.get("voyage"));
    }
  }

  private JSONObject readJSON(String path) throws IOException, JSONException {
    URLConnection urlConnection = open(path);
    String jsonString = IOUtils.toString(urlConnection.getInputStream());
    return new JSONObject(jsonString);
  }

  private String readPDF(String path) throws IOException {
    URLConnection urlConnection = open(path);
    return IOUtils.toString(urlConnection.getInputStream());
  }

  private HttpURLConnection open(String path) throws IOException {
    URL url = new URL("http://localhost:14000" + path);
    return (HttpURLConnection) url.openConnection();
  }

}
