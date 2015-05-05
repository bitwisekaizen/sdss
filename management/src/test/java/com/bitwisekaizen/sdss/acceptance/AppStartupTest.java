package com.bitwisekaizen.sdss.acceptance;

import com.bitwisekaizen.sdss.config.AppServerProperties;
import com.bitwisekaizen.sdss.config.ApplicationConfig;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebAppConfiguration
@IntegrationTest
// See http://stackoverflow.com/questions/25537436/acceptance-testing-a-spring-boot-web-app-with-testng
@TestExecutionListeners(inheritListeners = false, listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@Test
@TestPropertySource(locations = {"classpath:test.properties"})
public class AppStartupTest extends AbstractTestNGSpringContextTests {

    @Value("${local.server.port}")
    private int serverPort;

    @Autowired
    private AppServerProperties appServerProperties;

    @Test
    public void appCanStartUp() {
        assertThat(appServerProperties, notNullValue());
        assertThat(serverPort, is(Matchers.greaterThan(0)));
    }
}