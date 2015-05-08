package com.bitwisekaizen.sdss.management.acceptance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Helper to invoke multiple concurrent tasks.
 */
public class TaskHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskHelper.class);

    /**
     * Run the specified tasks to be run concurrently.
     *
     * @param callables tasks to call
     * @param <T> return type of each callable
     * @return List of results from the callables.
     */
    public static <T> List<T> submitAllTasksToExecutor(List<Callable<T>> callables) {
        List<Future<T>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            for (Callable<T> callable : callables) {
                futures.add(executorService.submit(callable));
            }
            List<T>  results = waitForAllFutures(futures);
            return results;
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * Wait for all futures to finished.
     *
     * @param futures futures to wait for
     * @param <T> return type of each future
     * @return List of results from the futures.
     */
    private static <T> List<T> waitForAllFutures(List<Future<T>> futures) {
        List<ExecutionException> executionExceptions = new ArrayList<>();

        List<T> results = new ArrayList<>();

        for (Future<T> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                LOGGER.error("Error in executing task.", e);
                executionExceptions.add(e);
            }
        }

        LOGGER.info("Tasks error count: " + executionExceptions.size());
        if (!executionExceptions.isEmpty()) {
            throw new TestException("Error occurred in executing tasks.");
        }
        return results;
    }

}