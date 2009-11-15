package com.reporting;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;

public class Fixture implements InitializingBean {

  private SimpleJdbcTemplate jdbc;
  private Resource schema;
  private Resource testdata;

  public Fixture(DataSource dataSource, Resource schema, Resource testdata) {
    this.jdbc = new SimpleJdbcTemplate(dataSource);
    this.testdata = testdata;
    this.schema = schema;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    jdbc.update(IOUtils.toString(schema.getInputStream()));
    jdbc.update(IOUtils.toString(testdata.getInputStream()));
  }

}
