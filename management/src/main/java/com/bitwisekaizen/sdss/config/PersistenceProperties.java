package com.bitwisekaizen.sdss.config;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties containing the persistence information.
 */
@ConfigurationProperties(value = "app.persistence")
public class PersistenceProperties {

    @NotBlank
    private String jdbcUrl;

    private String hbm2ddlAuto;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getHbm2ddlAuto() {
        return hbm2ddlAuto;
    }

    public void setHbm2ddlAuto(String hbm2ddlAuto) {
        this.hbm2ddlAuto = hbm2ddlAuto;
    }
}