package com.bitwisekaizen.sdss.config;

import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Server properties such as port number.
 */
@ConfigurationProperties(value = "app.server")
public class AppServerProperties {

    @Range(min = 0, max = 65535)
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}