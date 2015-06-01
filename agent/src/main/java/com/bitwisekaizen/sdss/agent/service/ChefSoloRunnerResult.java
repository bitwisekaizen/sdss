package com.bitwisekaizen.sdss.agent.service;


/**
 * Chef solo run result.
 */
public class ChefSoloRunnerResult {
    private boolean success;
    private String output;

    private ChefSoloRunnerResult(boolean success, String output) {
        this.success = success;
        this.output = output;
    }

    /**
     * Create a successful result.
     *
     * @param output output from the chef run
     * @return successful result.
     */
    public static ChefSoloRunnerResult aSuccessfulResultWithOutput(String output) {
        return new ChefSoloRunnerResult(true, output);
    }

    /**
     * Create a failed result.
     *
     * @param output output from the chef run
     * @return failed result.
     */
    public static ChefSoloRunnerResult aFailedResultWithOutput(String output) {
        return new ChefSoloRunnerResult(false, output);
    }

    /**
     * @return true if the result is successful; else, false.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return output of the result.
     */
    public String getOutput() {
        return output;
    }
}
