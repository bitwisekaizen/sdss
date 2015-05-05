package com.bitwisekaizen.sdss.config;

import org.flywaydb.core.Flyway;
import org.h2.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "com.bitwisekaizen")
@EnableConfigurationProperties({AppServerProperties.class, PersistenceProperties.class})
public class ApplicationConfig {

    @Autowired
    private AppServerProperties appServerProperties;

    @Autowired
    private PersistenceProperties persistenceProperties;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        return new TomcatEmbeddedServletContainerFactory(appServerProperties.getPort());
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriverClass(Driver.class);
        ds.setUrl(persistenceProperties.getJdbcUrl());
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @DependsOn("flyway")
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(jpaVendorAdapter);
        lef.setPackagesToScan("com.bitwisekaizen.sdss.entities");
        lef.setDataSource(dataSource());
        lef.setJpaProperties(jpaProperties());
        lef.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        return lef;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(dataSource());
        return flyway;
    }

    private Properties jpaProperties() {
        Properties props = new Properties();
        props.put("hibernate.query.substitutions", "true 'Y', false 'N'");
        props.put("hibernate.hbm2ddl.auto", persistenceProperties.getHbm2ddlAuto());
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "true");
        return props;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(false);
        adapter.setDatabase(Database.H2);
        return adapter;
    }
}
