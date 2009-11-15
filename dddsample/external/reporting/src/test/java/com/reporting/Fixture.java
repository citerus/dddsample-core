/**
 * Purpose
 * @author peter
 * @created 2009-okt-03
 * $Id$
 */
package com.reporting;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;

public class Fixture implements ApplicationListener {

  private SimpleJdbcTemplate jdbc;
  private Resource schema;
  private Resource testdata;

  public Fixture(DataSource dataSource, Resource schema, Resource testdata) {
    this.jdbc = new SimpleJdbcTemplate(dataSource);
    this.testdata = testdata;
    this.schema = schema;
  }

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    if (event instanceof ContextRefreshedEvent) {
      try {
        jdbc.update(IOUtils.toString(schema.getInputStream()));
        jdbc.update(IOUtils.toString(testdata.getInputStream()));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
