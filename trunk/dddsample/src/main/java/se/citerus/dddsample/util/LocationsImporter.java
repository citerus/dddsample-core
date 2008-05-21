package se.citerus.dddsample.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Imports about 55 000 locations from an official UN Locode CSV export.
 *
 * NOTE: not yet used
 *
 */
public class LocationsImporter implements ServletContextListener {
  private static final String ZIP_FILE_NAME = "unlocodes.zip";
  private static final String ZIP_ENTRY_NAME = "2006-2 UNLOCODE CodeList.txt";
  private static final int BATCH_SIZE = 1000;
  private static final Log logger = LogFactory.getLog(LocationsImporter.class);

  protected int importLocations(JdbcTemplate jt) throws IOException {
    ZipFile zipFile = new ZipFile(new ClassPathResource(ZIP_FILE_NAME).getFile());
    ZipEntry zipEntry = zipFile.getEntry(ZIP_ENTRY_NAME);
    InputStream inputStream = zipFile.getInputStream(zipEntry);
    LineIterator iterator = IOUtils.lineIterator(inputStream, "ISO-8859-1");

    int count = 0;
    String sql = "INSERT INTO Location (unlocode,name) VALUES (?,?)";

    final String[][] batchArgs = new String[BATCH_SIZE][2];
    while (iterator.hasNext()) {
      String line = iterator.nextLine();
      String[] args = parseLocation(line);
      if (args != null) {
        int pos = count % BATCH_SIZE;
        batchArgs[pos][0] = args[0];
        batchArgs[pos][1] = args[1];
        count++;
        if (count % BATCH_SIZE == 0) {
          jt.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
              ps.setString(1, batchArgs[i][0]);
              ps.setString(2, batchArgs[i][1]);
            }
            public int getBatchSize() {
              return BATCH_SIZE;
            }
          });
        }
        //jt.update(sql, new Object[] {pos, args[0], args[1]});
      }
    }
    // TODO: batch insert the tail of the line list

    return count;
  }

  private String[] parseLocation(String line) {
    String countryCode = line.substring(3, 5);
    String locationCode = line.substring(6, 9);
    if (locationCode.trim().length() == 3) {
      String name = line.substring(10, 46).trim();
      return new String[] {countryCode + locationCode, name};
    } else {
      return null;
    }
  }

  public void contextInitialized(ServletContextEvent event) {
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
    PlatformTransactionManager ptm = (PlatformTransactionManager) BeanFactoryUtils.beanOfType(context, PlatformTransactionManager.class);
    TransactionTemplate tt = new TransactionTemplate(ptm);
    DataSource dataSource = (DataSource) BeanFactoryUtils.beanOfType(context, DataSource.class);
    final JdbcTemplate jt = new JdbcTemplate(dataSource);


    long t = System.currentTimeMillis();
    Integer count = (Integer) tt.execute(new TransactionCallback() {
      public Object doInTransaction(TransactionStatus status) {
        try {
          return importLocations(jt);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
    logger.info("Imported " + count + " locations in " + (System.currentTimeMillis() - t)/1000.0 + " seconds.");
  }

  public void contextDestroyed(ServletContextEvent event) {}
}
