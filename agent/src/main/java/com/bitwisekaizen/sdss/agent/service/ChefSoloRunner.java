package com.bitwisekaizen.sdss.agent.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Instance can run a chef-solo command given the specified node file.
 */
@Component
public class ChefSoloRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChefSoloRunner.class);
    private boolean useSudo;
    private String chefExecutable;
    private String defaultArgs;
    private String chefSoloTarArchiveUrl;
    private File emptyConfig;

    @Autowired
    public ChefSoloRunner(@Value("${app.chef.executable.use.sudo}") boolean useSudo,
            @Value("${app.chef.executable}") String chefExecutable,
            @Value("${app.chef.default.args}") String defaultArgs,
            @Value("${app.chef.tar.archive.url}") String chefSoloTarArchiveUrl) {
        this.useSudo = useSudo;
        this.chefExecutable = chefExecutable;
        this.defaultArgs = defaultArgs;
        this.chefSoloTarArchiveUrl = chefSoloTarArchiveUrl;

        try {
            emptyConfig = File.createTempFile("chef-solo-runner-empty-solo-config", ".json");
            emptyConfig.deleteOnExit();
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Run chef solo command using the specified node file.
     *
     * @param nodeFile node file to use.
     */
    public synchronized ChefSoloRunnerResult runUsingNodeFile(File nodeFile) {
        return runUsingChefArguments(String.format(defaultArgs,
                nodeFile.getAbsolutePath(), emptyConfig.getAbsolutePath(), chefSoloTarArchiveUrl));
    }

    /**
     * Run chef solo using the specified arguments
     * An example is: -c ~/solo.rb -j ~/node.json -r http://www.example.com/chef-solo.tar.gz
     *
     * @param argumentsString chef executable arguments.
     * @return return the chef solo run result.
     */
    private ChefSoloRunnerResult runUsingChefArguments(String argumentsString) {
        List<String> arguments = new ArrayList<>();
        ProcessBuilder processBuilder;
        if (useSudo) {
            arguments.add("sudo");
        }

        arguments.add(chefExecutable);
        for (String argument : argumentsString.split(" ")) {
            arguments.add(argument);
        }

        processBuilder = new ProcessBuilder(arguments);

        LOGGER.info("Running chef command: " + arguments);

        processBuilder.directory(new File(System.getProperty("user.home")));
        try {
            File output = File.createTempFile("chef-solo-runner-output", ".txt");
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(output));
            Process process = processBuilder.start();


            boolean success = false;
            if (process.waitFor() == 0) {
                success = true;
            }

            String outputContent = FileUtils.readFileToString(output);

            if (success) {
                return ChefSoloRunnerResult.aSuccessfulResultWithOutput(outputContent);
            } else {
                return ChefSoloRunnerResult.aFailedResultWithOutput(outputContent);
            }
        } catch (IOException e) {
            throw new UnhandledException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
