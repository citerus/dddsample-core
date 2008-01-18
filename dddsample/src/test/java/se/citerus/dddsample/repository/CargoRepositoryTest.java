package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;

import java.util.Map;

public class CargoRepositoryTest extends AbstractRepositoryTest {

  CargoRepository cargoRepository;

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void testFindByCargoId() {
    final TrackingId trackingId = new TrackingId("XYZ");
    final Location origin = new Location("SESTO");
    final Location finalDestination = new Location("AUMEL");

    Cargo cargo = cargoRepository.find(trackingId);

    assertEquals(trackingId, cargo.trackingId());
    assertEquals(origin, cargo.origin());
    assertEquals(finalDestination, cargo.finalDestination());
    // TODO: verify delivery history
  }

  public void testSave() {
    // TODO: introduce Location repository
    Location finalDestination = new Location("TO");
    Location origin = new Location("FROM");
    sessionFactory.getCurrentSession().saveOrUpdate(origin);
    sessionFactory.getCurrentSession().saveOrUpdate(finalDestination);


    Cargo cargo = new Cargo(new TrackingId("AAA"), origin, finalDestination);
    cargoRepository.save(cargo);

    flush();

    Map<String, Object> map = sjt.queryForMap("select * from Cargo where tracking_id = 'AAA'");

    assertEquals("AAA", map.get("TRACKING_ID"));
    // TODO: check origin/finalDestination ids
  }

}