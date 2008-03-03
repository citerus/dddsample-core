package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.domain.UnLocode;

import java.util.Map;

public class CargoRepositoryTest extends AbstractRepositoryTest {

  CargoRepository cargoRepository;
  private final Location stockholm = new Location(new UnLocode("SE","STO"), "Stockholm");
  private final Location melbourne = new Location(new UnLocode("AU","MEL"), "Melbourne");

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void testFindByCargoId() {
    final TrackingId trackingId = new TrackingId("XYZ");

    Cargo cargo = cargoRepository.find(trackingId);

    assertEquals(trackingId, cargo.trackingId());
    assertEquals(stockholm, cargo.origin());
    assertEquals(melbourne, cargo.finalDestination());
    // TODO: verify delivery history
  }

  public void testSave() {
    sessionFactory.getCurrentSession().saveOrUpdate(stockholm);
    sessionFactory.getCurrentSession().saveOrUpdate(melbourne);


    Cargo cargo = new Cargo(new TrackingId("AAA"), stockholm, melbourne);
    cargoRepository.save(cargo);

    flush();

    Map<String, Object> map = sjt.queryForMap("select * from Cargo where tracking_id = 'AAA'");

    assertEquals("AAA", map.get("TRACKING_ID"));
    // TODO: check origin/finalDestination ids
  }

}