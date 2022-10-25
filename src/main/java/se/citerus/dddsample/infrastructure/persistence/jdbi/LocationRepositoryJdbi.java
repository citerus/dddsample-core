package se.citerus.dddsample.infrastructure.persistence.jdbi;

import com.google.common.collect.ImmutableMap;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.ColumnMapperFactory;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
public class LocationRepositoryJdbi implements LocationRepository {
    private final Jdbi jdbi;

    public LocationRepositoryJdbi(Jdbi jdbi) {
        this.jdbi = jdbi;
        jdbi.registerArgument(new AbstractArgumentFactory<Location>(Types.VARCHAR) {
            @Override
            protected Argument build(Location value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, value.unLocode().idString());
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<UnLocode>(Types.VARCHAR) {
            @Override
            protected Argument build(UnLocode value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, value.idString());
            }
        });
        jdbi.registerColumnMapper(ColumnMapperFactory.of(UnLocode.class, new ColumnMapper<UnLocode>() {
            @Override
            public UnLocode map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
                return new UnLocode(r.getString(columnNumber));
            }
        }));
        jdbi.registerRowMapper(ConstructorMapper.factory(Location.class));
    }

    public void store(Location location) {
        jdbi.useHandle(h -> h.createUpdate("INSERT INTO Location(unLocode, name) VALUES(:unloCode, :name)")
                .bindMap(ImmutableMap.of("unloCode", location.unLocode().idString(), "name", location.name()))
                .execute());
    }

    @Override
    public Location find(UnLocode unLocode) {
        return jdbi.withHandle(h -> h.createQuery("SELECT * FROM Location where unLocode = :unloCode")
                        .bind("unloCode", unLocode.idString())
                        .mapTo(Location.class)
                        .findFirst())
                .orElse(null);
    }

    @Override
    public List<Location> findAll() {
        return jdbi.withHandle(h -> h.createQuery("SELECT * FROM Location")
                .mapTo(Location.class)
                .list());
    }
}
