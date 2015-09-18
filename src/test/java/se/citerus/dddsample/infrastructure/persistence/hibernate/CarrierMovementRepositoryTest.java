package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.hibernate.SessionFactory;
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
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"/context-infrastructure-persistence.xml", "/context-domain.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public class CarrierMovementRepositoryTest {

    @Autowired
    VoyageRepository voyageRepository;

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
    public void testFind() throws Exception {
        Voyage voyage = voyageRepository.find(new VoyageNumber("0101"));
        assertNotNull(voyage);
        assertEquals("0101", voyage.voyageNumber().idString());
    /* TODO adapt
    assertEquals(STOCKHOLM, carrierMovement.departureLocation());
    assertEquals(HELSINKI, carrierMovement.arrivalLocation());
    assertEquals(DateTestUtil.toDate("2007-09-23", "02:00"), carrierMovement.departureTime());
    assertEquals(DateTestUtil.toDate("2007-09-23", "03:00"), carrierMovement.arrivalTime());
    */
    }

}
