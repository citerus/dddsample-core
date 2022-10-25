package se.citerus.dddsample.infrastructure.persistence.jdbi;

import com.google.common.collect.ImmutableMap;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.statement.Update;
import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

@Repository
public class CargoRepositoryJdbi implements CargoRepository {
    private final Jdbi jdbi;

    public CargoRepositoryJdbi(Jdbi jdbi) {
        this.jdbi = jdbi;
        jdbi.registerRowMapper(ConstructorMapper.factory(TrackingId.class));
        jdbi.registerArgument(new AbstractArgumentFactory<TrackingId>(Types.VARCHAR) {
            @Override
            protected Argument build(TrackingId value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, value.idString());
            }
        });
        //noinspection Convert2Lambda
        jdbi.registerRowMapper(new RowMapper<Leg>() {
            @Override
            public Leg map(ResultSet rs, StatementContext ctx) throws SQLException {
                return new Leg(
                        new VoyageNumber(rs.getString("voyageNumber")),
                        new Location(new UnLocode(rs.getString("ll_unloCode")), rs.getString("ll_name")),
                        new Location(new UnLocode(rs.getString("ul_unloCode")), rs.getString("ul_name")),
                        new Date(rs.getTimestamp("loadTime").getTime()),
                        new Date(rs.getTimestamp("unloadTime").getTime())
                    );
            }
        });
        //noinspection Convert2Lambda
        jdbi.registerRowMapper(new RowMapper<Cargo>() {
            @Override
            public Cargo map(ResultSet rs, StatementContext ctx) throws SQLException {
                TrackingId trackingId = new TrackingId(rs.getString("trackingId"));
                Location origin = new Location(new UnLocode(rs.getString("originUnloCode")), rs.getString("originName"));
                Location destination = new Location(new UnLocode(rs.getString("destinationUnloCode")), rs.getString("destinationName"));
                Date arrivalDeadline = new Date(rs.getTimestamp("arrivalDeadline").getTime());
                RouteSpecification routeSpecification = new RouteSpecification(origin, destination, arrivalDeadline);
                Cargo cargo = new Cargo(trackingId, routeSpecification);
                List<Leg> legs = findLegsForCargo(trackingId, jdbi);
                if (!legs.isEmpty()) {
                    cargo.assignToRoute(new Itinerary(legs));
                }
                return cargo;
            }
        });
    }

    @Override
    public Cargo find(TrackingId trackingId) {
        return jdbi.withHandle(h -> h.createQuery("SELECT c.trackingId, " +
                                "ol.unLocode AS originUnloCode, " +
                                "ol.name AS originName, " +
                                "dl.unLocode AS destinationUnloCode, " +
                                "dl.name AS destinationName, " +
                                "rs.arrivalDeadline " +
                                "FROM Cargo c " +
                                "JOIN RouteSpecification rs ON c.routeSpecification = rs.id " +
                                "JOIN Location ol ON ol.id = rs.origin " +
                                "JOIN Location dl ON dl.id = rs.destination " +
                                "WHERE trackingId = :trackingId")
                .bind("trackingId", trackingId.idString())
                .mapTo(Cargo.class)
                .findFirst())
                .orElse(null);
    }

    @Override
    public List<Cargo> findAll() {
        return jdbi.withHandle(h -> h.createQuery("SELECT c.trackingId, " +
                        "ol.unLocode AS originUnloCode, " +
                        "ol.name AS originName, " +
                        "dl.unLocode AS destinationUnloCode, " +
                        "dl.name AS destinationName, " +
                        "rs.arrivalDeadline " +
                        "FROM Cargo c " +
                        "JOIN RouteSpecification rs ON c.routeSpecification = rs.id " +
                        "JOIN Location ol ON ol.id = rs.origin " +
                        "JOIN Location dl ON dl.id = rs.destination ")
                .mapTo(Cargo.class)
                .list());
    }

    @Override
    public void store(Cargo cargo) {
        jdbi.useTransaction(h -> {
            boolean exists = h.select("SELECT COUNT(id) FROM Cargo WHERE trackingId = :trackingId")
                    .bind("trackingId", cargo.trackingId().idString())
                    .mapTo(Integer.class)
                    .findOnly() > 0;
            if (exists) {
                update(cargo, h);
            } else {
                create(cargo, h);
            }
        });
    }

    public void update(Cargo cargo) {
        jdbi.useTransaction(h -> update(cargo, h));
    }

    private static void update(Cargo cargo, Handle h) {
        int cargoId = h.select("SELECT id FROM Cargo WHERE trackingId = :trackingId")
                .bind("trackingId", cargo.trackingId())
                .mapTo(Integer.class)
                .findOnly();

        h.createUpdate("DELETE FROM Leg WHERE cargo = :cargoId")
                .bind("cargoId", cargoId)
                .execute();

        h.createUpdate("UPDATE RouteSpecification " +
                        "SET origin=(SELECT id FROM Location WHERE unLocode = :origin),destination=(SELECT id FROM Location WHERE unLocode = :destination),arrivalDeadline=:arrivalDeadline " +
                        "WHERE id = (SELECT routeSpecification FROM Cargo WHERE id = :cargoId)")
                .bindMap(ImmutableMap.of(
                        "cargoId", cargoId,
                        "origin", cargo.routeSpecification().origin().unLocode().idString(),
                        "destination", cargo.routeSpecification().destination().unLocode().idString(),
                        "arrivalDeadline", cargo.routeSpecification().arrivalDeadline()
                ))
                .execute();

        // TODO update cargo (hibernate migration)

        insertItinerary(h, cargoId, cargo.itinerary());

        // TODO update lastEvent? (hibernate migration)
    }

    private static void create(Cargo cargo, Handle h) {
        h.createUpdate("INSERT INTO RouteSpecification(origin, destination, arrivalDeadline) " +
                        "VALUES(" +
                        "(SELECT id FROM Location WHERE unLocode = :origin), " +
                        "(SELECT id FROM Location WHERE unLocode = :destination), " +
                        ":arrivalDeadline)")
                .bindMap(ImmutableMap.of(
                        "origin", cargo.routeSpecification().origin().unLocode().idString(),
                        "destination", cargo.routeSpecification().destination().unLocode().idString(),
                        "arrivalDeadline", cargo.routeSpecification().arrivalDeadline()
                ))
                .execute();
        int routeSpecId = h.createQuery("CALL IDENTITY()").mapTo(Integer.class).findOnly();

        Update statement = h.createUpdate("INSERT INTO Cargo(" +
                        "origin, " +
                        "trackingId, " +
                        "misdirected, " +
                        (cargo.delivery().estimatedTimeOfArrival() != null ? "eta, " : "") +
                        "calculatedAt, " +
                        "isUnloadedAtDestination, " +
                        "routingStatus, " +
                        "transportStatus, " +
                        "currentVoyage, " +
                        "lastKnownLocation, " +
                        "routeSpecification " +
                        ") VALUES(" +
                        "(SELECT id FROM Location WHERE unloCode = :origin)," +
                        ":trackingId," +
                        ":misdirected," +
                        (cargo.delivery().estimatedTimeOfArrival() != null ? ":eta, " : "") +
                        ":calculatedAt," +
                        ":isUnloadedAtDestination," +
                        ":routingStatus," +
                        ":transportStatus," +
                        "(SELECT id FROM Voyage WHERE voyageNumber = :currentVoyage)," +
                        "(SELECT id FROM Location WHERE unloCode = :lastKnownLocation)," +
                        ":routeSpecification" +
                        ")")
                .bindMap(ImmutableMap.of(
                        "origin", cargo.origin().unLocode().idString(),
                        "trackingId", cargo.trackingId().idString(),
                        "misdirected", cargo.delivery().isMisdirected(),
                        "calculatedAt", cargo.delivery().calculatedAt(),
                        "isUnloadedAtDestination", cargo.delivery().isUnloadedAtDestination(),
                        "routingStatus", cargo.delivery().routingStatus().name(),
                        "transportStatus", cargo.delivery().transportStatus().name(),
                        "currentVoyage", cargo.delivery().currentVoyage().idString(),
                        "lastKnownLocation", cargo.delivery().lastKnownLocation().unLocode().idString(),
                        "routeSpecification", routeSpecId));
        if (cargo.delivery().estimatedTimeOfArrival() != null) {
            statement.bind("eta", cargo.delivery().estimatedTimeOfArrival());
        }
        statement.execute();
        int cargoId = h.createQuery("CALL IDENTITY()").mapTo(Integer.class).findOnly();

        insertItinerary(h, cargoId, cargo.itinerary());

        // TODO check for lastEvent and find and insert id if found? (hibernate migration)
    }

    @Override
    public TrackingId nextTrackingId() {
        return jdbi.withHandle(h -> h.createQuery("SELECT UPPER(SUBSTR(CAST(UUID() AS VARCHAR(38)), 0, 9)) AS id FROM (VALUES(0))")
                .mapTo(TrackingId.class)
                .findOnly());
    }

    private List<Leg> findLegsForCargo(TrackingId trackingId, Jdbi jdbi) {
        return jdbi.withHandle(h -> h.createQuery("SELECT v.voyageNumber, ll.unLocode as ll_unloCode, ll.name as ll_name, ul.unLocode as ul_unloCode, ul.name as ul_name, leg.loadTime, leg.unloadTime " +
                        "FROM Leg leg " +
                        "JOIN Voyage v ON leg.voyage = v.id " +
                        "JOIN Location ll ON leg.loadLocation = ll.id " +
                        "JOIN Location ul ON leg.unloadLocation = ul.id " +
                        "WHERE leg.cargo = (SELECT id FROM Cargo WHERE trackingId = :trackingId)")
                .bind("trackingId", trackingId.idString())
                .mapTo(Leg.class)
                .list());
    }

    private static void insertItinerary(Handle h, int cargoId, Itinerary itinerary) {
        if (itinerary != null) {
            itinerary.legs().forEach(leg -> {
                h.createUpdate("INSERT INTO Leg(voyage, cargo, loadLocation, loadTime, unloadLocation, unloadTime) " +
                                "VALUES((SELECT id FROM Voyage WHERE voyageNumber = :voyage), :cargo, (SELECT id FROM Location WHERE unLocode = :loadLocation), :loadTime, (SELECT id FROM Location WHERE unLocode = :unloadLocation), :unloadTime)")
                        .bindMap(ImmutableMap.of(
                                "voyage", leg.voyage().idString(),
                                "cargo", cargoId,
                                "loadLocation", leg.loadLocation().unLocode().idString(),
                                "unloadLocation", leg.unloadLocation().unLocode().idString(),
                                "loadTime", leg.loadTime(),
                                "unloadTime", leg.unloadTime()))
                        .execute();
            });
        }
    }
}
