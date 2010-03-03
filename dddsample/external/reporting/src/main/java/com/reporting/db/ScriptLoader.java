package com.reporting.db;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;

public class ScriptLoader implements InitializingBean {

  private SimpleJdbcTemplate jdbc;
  private Resource script;

  public ScriptLoader(DataSource dataSource, Resource script) {
    this.jdbc = new SimpleJdbcTemplate(dataSource);
    this.script = script;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    jdbc.update(IOUtils.toString(script.getInputStream()));
  }

}
