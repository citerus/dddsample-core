package se.citerus.dddsample.service;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.TrackingId;


public class CargoServiceTest extends TestCase {

  public void testCargoServiceFindByTrackingIdScenario() throws Exception {
    CargoService service = getCargoService();
    
    Cargo cargo = service.find("XYZ");
    
    assertEquals(new TrackingId("XYZ"), cargo.trackingId());
  }
  

  private CargoService getCargoService() {
    return (CargoService) getBean("cargoService");
  }

  private Object getBean(String bean) {
    ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });

    return context.getBean(bean);
  }
}
