package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"/context-infrastructure-persistence.xml", "/context-domain.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public class HandlingEventRepositoryTest {

    @Autowired
    HandlingEventRepository handlingEventRepository;

    @Autowired
    CargoRepository cargoRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HibernateTransactionManager transactionManager;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        SampleDataGenerator.loadSampleData(jdbcTemplate, new TransactionTemplate(transactionManager));
    }

    @Test
    public void testSave() {
        Location location = locationRepository.find(new UnLocode("SESTO"));

        Cargo cargo = cargoRepository.find(new TrackingId("XYZ"));
        Date completionTime = new Date(10);
        Date registrationTime = new Date(20);
        HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, HandlingEvent.Type.CLAIM, location);

        handlingEventRepository.store(event);

        flush();

        Map<String, Object> result = jdbcTemplate.queryForMap("select * from HandlingEvent where id = ?", getLongId(event));
        assertEquals(1L, result.get("CARGO_ID"));
        assertEquals(new Date(10), result.get("COMPLETIONTIME"));
        assertEquals(new Date(20), result.get("REGISTRATIONTIME"));
        assertEquals("CLAIM", result.get("TYPE"));
        // TODO: the rest of the columns
    }

    private void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    private Long getLongId(Object o) {
        final Session session = sessionFactory.getCurrentSession();
        if (session.contains(o)) {
            return (Long) session.getIdentifier(o);
        } else {
            try {
                Field id = o.getClass().getDeclaredField("id");
                id.setAccessible(true);
                return (Long) id.get(o);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

    @Test
    public void testFindEventsForCargo() throws Exception {
        TrackingId trackingId = new TrackingId("XYZ");
        List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).distinctEventsByCompletionTime();
        assertEquals(12, handlingEvents.size());
    }

}