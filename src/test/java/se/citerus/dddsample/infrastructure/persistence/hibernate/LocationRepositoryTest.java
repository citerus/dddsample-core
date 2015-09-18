package se.citerus.dddsample.infrastructure.persistence.hibernate;

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
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"/context-infrastructure-persistence.xml", "/context-domain.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public class LocationRepositoryTest {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HibernateTransactionManager transactionManager;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SampleDataGenerator.loadSampleData(jdbcTemplate, new TransactionTemplate(transactionManager));
    }

    @Test
    public void testFind() throws Exception {
        final UnLocode melbourne = new UnLocode("AUMEL");
        Location location = locationRepository.find(melbourne);
        assertNotNull(location);
        assertEquals(melbourne, location.unLocode());

        assertNull(locationRepository.find(new UnLocode("NOLOC")));
    }

    @Test
    public void testFindAll() throws Exception {
        List<Location> allLocations = locationRepository.findAll();

        assertNotNull(allLocations);
        assertEquals(7, allLocations.size());
    }

}
