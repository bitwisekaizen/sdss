package com.bitwisekaizen.sdss.agent.service;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Instance can run a chef-solo command given the specified node file.
 */
@Component
public class ChefSoloRunner {

    /**
     * Run chef solo command using the specified node file.
     *
     * @param nodeFile node file to use.
     */
    public void runUsingNodeFile(File nodeFile) {
    }
}
