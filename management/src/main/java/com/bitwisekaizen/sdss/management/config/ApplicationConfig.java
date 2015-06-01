package com.bitwisekaizen.sdss.management.config;

import com.bitwisekaizen.sdss.agentclient.StorageAgentClient;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.h2.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.MINUTES;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "com.bitwisekaizen.sdss.management")
@EnableConfigurationProperties({AppServerProperties.class, PersistenceProperties.class})
@EnableJpaRepositories(basePackages = "com.bitwisekaizen.sdss.management.repository")
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
        lef.setPackagesToScan("com.bitwisekaizen.sdss.management.entity");
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

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages("com.bitwisekaizen.sdss.management.web");
        resourceConfig.register(JacksonFeature.class);
        //resourceConfig.register(LoggingFilter.class);
        ServletContainer servletContainer = new org.glassfish.jersey.servlet.ServletContainer(resourceConfig);
        return new ServletRegistrationBean(servletContainer, "/api/*");
    }

    @Bean
    public StorageAgentClient createStorageAgentClient(
            @Value("${app.running.integration.test}") boolean runningIntegrationTest,
            @Value("${app.storage.agent.url}") String serverUrl) {
        if (runningIntegrationTest) {
            return new InMemoryStorageAgentClient(createClient(serverUrl));
        } else {
            return new StorageAgentClient(createClient(serverUrl));
        }
    }

    public WebTarget createClient( String serverUrl) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        // https://java.net/jira/browse/JERSEY-2373
        clientConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, (int) MINUTES.toMillis(5));
        clientConfig.property(ClientProperties.READ_TIMEOUT, (int) MINUTES.toMillis(30));
        Client client = ClientBuilder.newBuilder().newClient(clientConfig).register(JacksonFeature.class);

        return client.target(serverUrl);
    }
}
