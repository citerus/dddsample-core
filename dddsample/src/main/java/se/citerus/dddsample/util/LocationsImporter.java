package se.citerus.dddsample.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.UnLocode;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Imports about 55 000 locations from an official UN Locode CSV export.
 *
 */
public class LocationsImporter implements ServletContextListener {
  private static final String ZIP_FILE_NAME = "unlocodes.zip";
  private static final String ZIP_ENTRY_NAME = "2006-2 UNLOCODE CodeList.txt";
  private static final int BATCH_SIZE = 1000;
  private static final Log logger = LogFactory.getLog(LocationsImporter.class);

  protected int importLocations(Session session) throws IOException {
    ZipFile zipFile = new ZipFile(new ClassPathResource(ZIP_FILE_NAME).getFile());
    ZipEntry zipEntry = zipFile.getEntry(ZIP_ENTRY_NAME);
    InputStream inputStream = zipFile.getInputStream(zipEntry);
    LineIterator iterator = IOUtils.lineIterator(inputStream, "ISO-8859-1");
    session.setCacheMode(CacheMode.IGNORE);
    session.setFlushMode(FlushMode.MANUAL);

    int insertCount = 1;
    while (iterator.hasNext()) {
      String line = iterator.nextLine();
      Location location = parseLocation(line);
      if (location != null) {
          session.save(location);
          if (insertCount % BATCH_SIZE == 0) {
            session.flush();
            session.clear();
          }
          insertCount++;
      }
    }
    session.flush();

    return insertCount;
  }

  private Location parseLocation(String line) {
    String countryCode = line.substring(3, 5);
    String locationCode = line.substring(6, 9);
    if (locationCode.trim().length() == 3) {
      String name = line.substring(10, 46).trim();
      UnLocode unlocode = new UnLocode(countryCode, locationCode);
      return new Location(unlocode, name);
    } else {
      return null;
    }
  }

  public void contextInitialized(ServletContextEvent event) {
    final WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
    final PlatformTransactionManager ptm = (PlatformTransactionManager) BeanFactoryUtils.beanOfType(context, PlatformTransactionManager.class);
    final SessionFactory sf = (SessionFactory) BeanFactoryUtils.beanOfType(context, SessionFactory.class);
    final TransactionTemplate tt = new TransactionTemplate(ptm);

    long t = System.currentTimeMillis();
    Integer count = (Integer) tt.execute(new TransactionCallback() {
      public Object doInTransaction(TransactionStatus status) {
        try {
          return importLocations(sf.getCurrentSession());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
    logger.info("Imported " + count + " locations in " + (System.currentTimeMillis() - t)/1000.0 + " seconds.");
  }

  public void contextDestroyed(ServletContextEvent event) {}
}
