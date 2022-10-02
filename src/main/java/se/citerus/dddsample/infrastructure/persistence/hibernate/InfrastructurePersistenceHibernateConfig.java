package se.citerus.dddsample.infrastructure.persistence.hibernate;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hsqldb.jdbc.JDBCDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class InfrastructurePersistenceHibernateConfig {

    @Value("${dataSource.db_name}")
    public String databaseName;

    @Value("${dataSource.url}")
    public String databaseUrl;

    @Value("${dataSource.username}")
    public String databaseUsername;

    @Value("${dataSource.password}")
    public String databasePassword;

    @Value("${dataSource.driver_class}")
    public String databaseDriver;

    @Bean
    public DataSource dataSource() {
        JDBCDataSource jdbcDataSource = new JDBCDataSource();
        jdbcDataSource.setDatabase(databaseName);
        jdbcDataSource.setUrl(databaseUrl);
        jdbcDataSource.setUser(databaseUsername);
        jdbcDataSource.setPassword(databasePassword);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(jdbcDataSource);
        hikariDataSource.setDriverClassName(databaseDriver);
        hikariDataSource.setMinimumIdle(4);
        hikariDataSource.setAutoCommit(false);
        return hikariDataSource;
    }

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        return new LocalSessionFactoryBuilder(dataSource)
                .configure() // expects a file named hibernate.cfg.xml on the classpath
                .buildSessionFactory();
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    public HandlingEventRepositoryHibernate handlingEventRepository(SessionFactory sessionFactory) {
        return new HandlingEventRepositoryHibernate(sessionFactory);
    }

    @Bean
    public CargoRepositoryHibernate cargoRepository(SessionFactory sessionFactory) {
        return new CargoRepositoryHibernate(sessionFactory);
    }

    @Bean
    public VoyageRepositoryHibernate voyageRepository(SessionFactory sessionFactory) {
        return new VoyageRepositoryHibernate(sessionFactory);
    }

    @Bean
    public LocationRepositoryHibernate locationRepository(SessionFactory sessionFactory) {
        return new LocationRepositoryHibernate(sessionFactory);
    }
}
