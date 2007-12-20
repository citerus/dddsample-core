package se.citerus.dddsample.service;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.repository.CargoRepositoryInMem;


public class CargoServiceTest extends AbstractDependencyInjectionSpringContextTests {

  // TODO:
  // find  nice way to test context configuration and
  // transaction existence (and attributes),
  // with repository layer stubbed out. Sketchy atm.

  CargoService cargoService;
  CargoRepositoryInMem cargoRepository;

  public CargoServiceTest() {
    setDependencyCheck(false);
  }

  public void setCargoService(CargoService cargoService) {
    AspectJProxyFactory factory = new AspectJProxyFactory(cargoRepository);
    factory.addAdvice(new TransactionVerifier());
    this.cargoRepository = factory.getProxy();
    this.cargoService = cargoService;
  }

  protected String[] getConfigLocations() {
    return new String[] { "context-service.xml" };
  }

  public void testCargoServiceFindByTrackingIdScenario() throws Exception {
    Cargo cargo = cargoService.find("XYZ");

    assertEquals(new TrackingId("XYZ"), cargo.trackingId());
  }

  private static class TransactionVerifier implements Advice {
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
      if (TransactionAspectSupport.currentTransactionStatus() == null) {
        fail("Transaction is manadatory");
      }
      return methodInvocation.proceed();
    }
  }

}
