package se.citerus.dddsample.domain;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class CargoRepositoryTest extends TestCase {

  private static final String CTX = "applicationContext.xml";

  public void testFindByCargoId() {

    CargoRepository repository = getCargoRepository();
    final TrackingId trackingId = new TrackingId("XYZ");
    Cargo cargo = repository.find(trackingId);

    assertEquals(trackingId, cargo.trackingId());

  }

  private CargoRepository getCargoRepository() {
    return (CargoRepository) getBean("cargoRepository");
  }

  private Object getBean(String bean) {
    ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { CTX });

    return context.getBean(bean);
  }

}