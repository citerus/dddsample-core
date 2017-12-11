package se.citerus.dddsample.infrastructure.persistence.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
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

@RunWith(SpringRunner.class)
@ContextConfiguration(value = {"/context-infrastructure-persistence.xml"})
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

        assertThat(result.get("CARGO_ID")).isEqualTo(1L);
        Date completionDate = new Date(((Timestamp) result.get("COMPLETIONTIME")).getTime()); // equals call is not symmetric between java.sql.Timestamp and java.util.Date, so we should convert Timestamp Date
        assertThat(completionDate).isEqualTo(new Date(10));
        Date registrationDate = new Date(((Timestamp) result.get("REGISTRATIONTIME")).getTime()); // equals call is not symmetric between java.sql.Timestamp and java.util.Date, so we should convert Timestamp Date
        assertThat(registrationDate).isEqualTo(new Date(20));
        assertThat(result.get("TYPE")).isEqualTo("CLAIM");
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
    public void testFindEventsForCargo() {
        TrackingId trackingId = new TrackingId("XYZ");
        List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).distinctEventsByCompletionTime();
        assertThat(handlingEvents).hasSize(12);
    }

}