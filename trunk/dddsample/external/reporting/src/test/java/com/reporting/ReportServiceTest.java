package com.reporting;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/context.xml", "/context-cxf.xml", "/context-test-setup.xml"})
public class ReportServiceTest {

  @Test
  public void deliveryReport() throws Exception {
    JSONObject json = readJSON("/report/cargo/ABC");
    JSONObject cargo = json.getJSONObject("cargo");
    
    assertEquals("ABC", cargo.get("trackingId"));
    assertEquals("Hongkong", cargo.get("receivedIn"));
    assertEquals("Stockholm", cargo.get("finalDestination"));
    assertEquals("6/15/09 12:00 PM", cargo.get("arrivalDeadline"));
    assertEquals("6/12/09 6:30 PM", cargo.get("eta"));
    assertEquals("Onboard voyage", cargo.get("currentStatus"));
    assertEquals("V0100", cargo.get("currentVoyage"));
    assertEquals("Tokyo", cargo.get("currentLocation"));
    assertEquals("6/8/09 2:23 PM", cargo.get("lastUpdatedOn"));

    JSONArray handlings = cargo.getJSONArray("handlings");
    assertEquals(4, handlings.length());

    verifyHandling(handlings.getJSONObject(0), "Receive", "Hongkong", null);
    verifyHandling(handlings.getJSONObject(1), "Load", "Hongkong", "V0100");
    verifyHandling(handlings.getJSONObject(2), "Unload", "Long Beach", "V0100");
    verifyHandling(handlings.getJSONObject(3), "Load", "Long Beach", "V0200");
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

  @Test
  public void voyageReport() throws Exception {
    JSONObject json = readJSON("/report/voyage/V0200");
    JSONObject voyage = json.getJSONObject("voyage");

    assertEquals("V0200", voyage.get("voyageNumber"));
  }

  private JSONObject readJSON(String path) throws IOException, JSONException {
    URLConnection urlConnection = new URL("http://localhost:14000" + path).openConnection();
    String jsonString = IOUtils.toString(urlConnection.getInputStream());
    return new JSONObject(jsonString);
  }

}
