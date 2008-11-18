package se.citerus.routingteam.internal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GraphDAO {

  private final JdbcTemplate jt;

  public GraphDAO(DataSource dataSource) {
    jt = new JdbcTemplate(dataSource);
  }

  public List<String> listLocations() {
    final List<String> result = new ArrayList();

    jt.query("select unlocode from location", new RowCallbackHandler() {
      public void processRow(ResultSet resultSet) throws SQLException {
        result.add(resultSet.getString("unlocode"));
      }
    });

    return result;
  }

  // TODO adapt to Voyage
  public void storeCarrierMovementId(String cmId, String from, String to) {
    final String locationSql = "select id from location where unlocode = ?";

    final Long fromId = jt.queryForLong(locationSql, new Object[]{ from });
    final Long toId = jt.queryForLong(locationSql, new Object[]{ to });

    final Object[] params = {cmId, fromId, toId};
    jt.update(
      "insert into CarrierMovement (carrier_movement_id,from_id,to_id) " +
      "values (?,?,?)", params);
  }
}
