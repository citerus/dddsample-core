package se.citerus.dddsample.infrastructure.persistence.jdbi;

import com.google.common.collect.ImmutableMap;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class HandlingEventRepositoryJdbi implements HandlingEventRepository {
    private final Jdbi jdbi;

    public HandlingEventRepositoryJdbi(Jdbi jdbi) {
        this.jdbi = jdbi;
        jdbi.registerRowMapper(new RowMapper<HandlingEvent>() {
            @Override
            public HandlingEvent map(ResultSet rs, StatementContext ctx) throws SQLException {
                String voyageNumber = rs.getString("voyageNumber");
                if (voyageNumber == null) {
                    return new HandlingEvent(
                            new TrackingId(rs.getString("trackingId")),
                            new Date(rs.getTimestamp("completionTime").getTime()),
                            new Date(rs.getTimestamp("registrationTime").getTime()),
                            HandlingEvent.Type.valueOf(rs.getString("type")),
                            new Location(new UnLocode(rs.getString("unLocode")), rs.getString("name"))
                    );
                }
                return new HandlingEvent(
                        new TrackingId(rs.getString("trackingId")),
                        new Date(rs.getTimestamp("completionTime").getTime()),
                        new Date(rs.getTimestamp("registrationTime").getTime()),
                        HandlingEvent.Type.valueOf(rs.getString("type")),
                        new Location(new UnLocode(rs.getString("unLocode")), rs.getString("name")),
                        new VoyageNumber(voyageNumber)
                );
            }
        });
    }

    @Override
    public void store(HandlingEvent event) {
        jdbi.useHandle(h -> h.createUpdate("INSERT INTO HandlingEvent(type, voyage, location, completionTime, registrationTime, cargo) VALUES(" +
                        ":type, " +
                        "(SELECT id FROM Voyage v WHERE v.voyageNumber = :voyage), " +
                        "(SELECT id FROM Location l WHERE l.unloCode = :location), " +
                        ":completionTime, " +
                        ":registrationTime, " +
                        "(SELECT id FROM Cargo c WHERE c.trackingId = :cargo))")
                .bindMap(ImmutableMap.of("type", event.type().name(),
                        "voyage", event.voyage().idString(),
                        "location", event.location().unLocode().idString(),
                        "completionTime", event.completionTime(),
                        "registrationTime", event.registrationTime(),
                        "cargo", event.cargo().idString()))
                .execute());
    }

    @Override
    public HandlingHistory lookupHandlingHistoryOfCargo(TrackingId trackingId) {
        List<HandlingEvent> handlingEvents = jdbi.withHandle(h ->
                h.createQuery("SELECT he.completionTime, he.registrationTime, he.type, c.trackingId, v.voyageNumber, l.unLocode, l.name " +
                                "FROM HandlingEvent he " +
                                "JOIN Cargo c ON he.cargo = c.id " +
                                "JOIN Location l ON he.location = l.id " +
                                "LEFT JOIN Voyage v ON he.voyage = v.id " +
                                "WHERE c.trackingId = :trackingId")
                .bind("trackingId", trackingId.idString())
                .mapTo(HandlingEvent.class)
                .list());
        return new HandlingHistory(handlingEvents);
    }
}
