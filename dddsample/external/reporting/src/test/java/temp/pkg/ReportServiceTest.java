package temp.pkg;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/context.xml", "/context-cxf.xml", "/context-test-setup.xml"})
public class ReportServiceTest {

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
    URL url = new URL("http://localhost:14000" + path);
    URLConnection urlConnection = url.openConnection();
    String jsonString = IOUtils.toString(urlConnection.getInputStream());
    return new JSONObject(jsonString);
  }

  private String readPDF(String path) throws IOException {
    URL url = new URL("http://localhost:14000" + path);
    URLConnection urlConnection = url.openConnection();
    return IOUtils.toString(urlConnection.getInputStream());
  }

}
