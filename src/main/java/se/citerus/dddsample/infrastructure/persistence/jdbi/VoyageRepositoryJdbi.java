package se.citerus.dddsample.infrastructure.persistence.jdbi;

import com.google.common.collect.ImmutableMap;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class VoyageRepositoryJdbi implements VoyageRepository {
    private final Jdbi jdbi;

    public VoyageRepositoryJdbi(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Voyage find(VoyageNumber voyageNumber) {
        return jdbi.withHandle(h -> {
            h.registerRowMapper(new RowMapper<Voyage>() {
                @Override
                public Voyage map(ResultSet rs, StatementContext ctx) throws SQLException {
                    List<CarrierMovement> carrierMovements = new ArrayList<>();
                    carrierMovements.add(new CarrierMovement(
                            new Location(new UnLocode(rs.getString("dl_unloCode")), rs.getString("dl_name")),
                            new Location(new UnLocode(rs.getString("al_unloCode")), rs.getString("al_name")),
                            new Date(rs.getTimestamp("departureTime").getTime()),
                            new Date(rs.getTimestamp("arrivalTime").getTime())));
                    return new Voyage(new VoyageNumber(rs.getString("voyageNumber")), new Schedule(carrierMovements));
                }
            });
            List<Voyage> voyages = h.createQuery("SELECT v.voyageNumber, cm.departureTime, cm.arrivalTime, al.unLocode AS al_unloCode, al.name AS al_name, dl.unLocode AS dl_unloCode, dl.name AS dl_name " +
                            "FROM Voyage v " +
                            "JOIN CarrierMovement cm ON cm.voyage = v.id " +
                            "JOIN Location dl ON cm.departureLocation = dl.id " +
                            "JOIN Location al ON cm.arrivalLocation = al.id " +
                            "WHERE voyageNumber = :voyageNumber")
                    .bind("voyageNumber", voyageNumber.idString())
                    .mapTo(Voyage.class)
                    .list();
            if (voyages.isEmpty()) {
                return Optional.<Voyage>empty();
            }
            return Optional.of(new Voyage(voyageNumber, new Schedule(voyages.stream()
                    .flatMap(v -> v.schedule().carrierMovements().stream())
                    .sorted(Comparator.comparing(CarrierMovement::departureTime))
                    .collect(Collectors.toList()))));
        }).orElse(null);
    }

    public void store(Voyage voyage) {
        jdbi.useHandle(h -> {
            h.createUpdate("INSERT INTO Voyage(voyageNumber) VALUES(:voyageNumber)")
                    .bind("voyageNumber", voyage.voyageNumber().idString())
                    .execute();
            int voyageId = h.createQuery("CALL IDENTITY()").mapTo(Integer.class).findOnly();
            voyage.schedule().carrierMovements().forEach(cm ->
                    h.createUpdate("INSERT INTO CarrierMovement(arrivalLocation, arrivalTime, departureLocation, departureTime, voyage) " +
                                    "VALUES(" +
                                    "(SELECT id FROM Location WHERE unLocode = :arrivalLocation), " +
                                    ":arrivalTime, " +
                                    "(SELECT id FROM Location WHERE unLocode = :departureLocation), " +
                                    ":departureTime, " +
                                    ":voyage)")
                            .bindMap(ImmutableMap.of(
                                    "arrivalLocation", cm.arrivalLocation().unLocode().idString(),
                                    "arrivalTime", cm.arrivalTime(),
                                    "departureLocation", cm.departureLocation().unLocode().idString(),
                                    "departureTime", cm.departureTime(),
                                    "voyage", voyageId))
                            .execute());
        });
    }
}
