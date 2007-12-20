package se.citerus.dddsample.service;

import static org.easymock.EasyMock.*;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.repository.CargoRepository;


public class CargoServiceTest extends AbstractDependencyInjectionSpringContextTests {

  CargoService cargoService;
  CargoRepository cargoRepository;
  PlatformTransactionManager transactionManager;

  public CargoServiceTest() {
    setDependencyCheck(false);
  }

  public void setCargoService(CargoService cargoService) {
    this.cargoService = cargoService;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @Override
  protected void prepareTestInstance() throws Exception {
    // TODO: verify an existing transaction inside the repository mock instead, this smells like we're testing framework code...
    super.prepareTestInstance();
    expect(cargoRepository.find(new TrackingId("XYZ"))).
            andReturn(new Cargo(new TrackingId("XYZ"), new Location("ORIG"), new Location("DEST")));
    replay(cargoRepository);

    TransactionStatus ts = createMock(TransactionStatus.class);
    replay(ts);
    expect(transactionManager.getTransaction(isA(TransactionDefinition.class))).
            andReturn(ts);
    transactionManager.commit(isA(TransactionStatus.class));
    replay(transactionManager);
  }

  protected String[] getConfigLocations() {
    return new String[] { "context-service.xml", "mock-context-persistence.xml" };
  }

  public void testCargoServiceFindByTrackingIdScenario() throws Exception {
    Cargo cargo = cargoService.find("XYZ");

    assertEquals(new TrackingId("XYZ"), cargo.trackingId());
  }

  protected void onTearDown() throws Exception {
    verify(cargoRepository, transactionManager);
  }
}
