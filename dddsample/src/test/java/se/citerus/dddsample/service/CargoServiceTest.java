package se.citerus.dddsample.service;

import static org.easymock.EasyMock.*;

import org.apache.commons.lang.ArrayUtils;
import org.easymock.IAnswer;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;
import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class CargoServiceTest extends AbstractDependencyInjectionSpringContextTests {

  CargoService cargoService;
  CargoRepository cargoRepository;
  HandlingEventRepository handlingEventRepository;
  SessionFactory sessionFactory;

  public CargoServiceTest() {
    setAutowireMode(AUTOWIRE_BY_NAME);
    setDependencyCheck(false);    
  }

  public void setCargoService(CargoService cargoService) {
    this.cargoService = cargoService;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  protected void onSetUp() {
    Session session = createMock(Session.class);
    Connection connection = createMock(Connection.class);
    Transaction transaction = createMock(Transaction.class);

    expect(sessionFactory.openSession()).andReturn(session);
    expect(session.connection()).andReturn(connection);
    session.setFlushMode(FlushMode.NEVER);
    expect(session.beginTransaction()).andReturn(transaction);
    expect(session.isConnected()).andReturn(true);
    expect(session.connection()).andReturn(connection);
    transaction.commit();
    expect(session.close()).andReturn(connection);

    replay(session, sessionFactory, transaction, connection);
  }

  protected String[] getConfigLocations() {
    return new String[] { "context-service.xml", "mock-context-persistence.xml" };
  }

  /**
   * Tests that service call is executed within a transaction,
   * and that the returning DTO is correctly assembled given the stubbed
   * Cargo returned.
   */
  public void testCargoServiceFindByTrackingIdScenario() {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("ORIG"), new Location("DEST"));
    HandlingEvent claimed = new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.CLAIM, new Location("SESTO"));
    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("MUGER"));
    HandlingEvent loaded = new HandlingEvent(cargo, new Date(12), new Date(25), HandlingEvent.Type.LOAD, new Location("SESTO"), carrierMovement);
    HandlingEvent unloaded = new HandlingEvent(cargo, new Date(100), new Date(110), HandlingEvent.Type.UNLOAD, new Location("MUGER"), carrierMovement);
    // Add out of order to verify ordering in DTO
    cargo.deliveryHistory().addAllEvents(Arrays.asList(loaded, unloaded, claimed));

    final IAnswer<Cargo> cargoAnswer = new TransactionVerifyingAnswer<Cargo>() {
      public Cargo answerWithinTransaction() throws Throwable {
        return cargo;
      }
    };
    expect(cargoRepository.find(new TrackingId("XYZ"))).andAnswer(cargoAnswer);

    replay(cargoRepository);


    // Tested call
    CargoWithHistoryDTO cargoDTO = cargoService.find("XYZ");


    assertEquals("XYZ", cargoDTO.getTrackingId());
    assertEquals("ORIG", cargoDTO.getOrigin());
    assertEquals("DEST", cargoDTO.getFinalDestination());
    assertEquals("MUGER", cargoDTO.getCurrentLocation());

    List<HandlingEventDTO> events = cargoDTO.getEvents();
    assertEquals(3, events.size());

    // Claim happened first
    HandlingEventDTO eventDTO = events.get(0);
    assertEquals("SESTO", eventDTO.getLocation());
    assertEquals("CLAIM", eventDTO.getType());
    assertEquals("", eventDTO.getCarrier());
    assertEquals(new Date(10), eventDTO.getTime());

    // Then load
    eventDTO = events.get(1);
    assertEquals("SESTO", eventDTO.getLocation());
    assertEquals("LOAD", eventDTO.getType());
    assertEquals("CAR_001", eventDTO.getCarrier());
    assertEquals(new Date(12), eventDTO.getTime());

    // Finally unload
    eventDTO = events.get(2);
    assertEquals("MUGER", eventDTO.getLocation());
    assertEquals("UNLOAD", eventDTO.getType());
    assertEquals("CAR_001", eventDTO.getCarrier());
    assertEquals(new Date(100), eventDTO.getTime());
  }

  public void testCargoServiceFindByTrackingIdNullResult() {
    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(null);
    replay(cargoRepository);

    // Tested call
    CargoWithHistoryDTO cargoDTO = cargoService.find("XYZ");
    
    assertNull(cargoDTO);
  }

  protected void onTearDown() throws Exception {
    verify(cargoRepository, sessionFactory);
    reset(cargoRepository, sessionFactory);
  }

  /**
   * EasyMock answer that verifies an ongoing transaction.
   */
  public static abstract class TransactionVerifyingAnswer<T> implements IAnswer<T> {

    protected abstract T answerWithinTransaction() throws Throwable;

    private void verifyTransaction() {
      TransactionStatus ts = TransactionAspectSupport.currentTransactionStatus();
      assertNotNull("Transaction should be active", ts);
    }

    public final T answer() throws Throwable {
      verifyTransaction();
      return answerWithinTransaction();
    }
  }

}
