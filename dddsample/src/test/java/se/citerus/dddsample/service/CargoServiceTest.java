package se.citerus.dddsample.service;

import static org.easymock.EasyMock.*;
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
import se.citerus.dddsample.service.dto.CargoWithHistoryDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;

import java.sql.Connection;
import java.util.Date;
import java.util.List;


public class CargoServiceTest extends AbstractDependencyInjectionSpringContextTests {

  CargoService cargoService;
  CargoRepository cargoRepository;
  SessionFactory sessionFactory;

  public void setCargoService(CargoService cargoService) {
    this.cargoService = cargoService;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
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
    IAnswer<Cargo> answerOnFind = new TransactionVerifyingAnswer<Cargo>() {

      public Cargo answerWithinTransaction() throws Throwable {
        Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("ORIG"), new Location("DEST"));
        CarrierMovement cm = new CarrierMovement(new CarrierId("CAR_001"), new Location("FROM"), new Location("TO"));
        cargo.handle(
                new HandlingEvent(new Date(10L), new Date(20L), HandlingEvent.Type.CLAIM, new Location("TO"), cm)
        );
        return cargo;
      }

    };
    expect(cargoRepository.find(new TrackingId("XYZ"))).andAnswer(answerOnFind);
    replay(cargoRepository);


    // Tested call
    CargoWithHistoryDTO cargoDTO = cargoService.find("XYZ");


    assertEquals("XYZ", cargoDTO.getTrackingId());
    assertEquals("ORIG", cargoDTO.getOrigin());
    assertEquals("DEST", cargoDTO.getFinalDestination());
    assertEquals("TO", cargoDTO.getCurrentLocation());

    List<HandlingEventDTO> events = cargoDTO.getEvents();
    assertEquals(1, events.size());
    HandlingEventDTO eventDTO = events.get(0);
    assertEquals("TO", eventDTO.getLocation());
    assertEquals("CLAIM", eventDTO.getType());
    assertEquals("CAR_001", eventDTO.getCarrier());    
    assertEquals(new Date(10), eventDTO.getTime());
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
