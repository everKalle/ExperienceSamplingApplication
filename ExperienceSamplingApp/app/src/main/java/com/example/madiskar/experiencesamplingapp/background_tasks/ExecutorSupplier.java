package com.example.madiskar.experiencesamplingapp.background_tasks;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ExecutorSupplier {

    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private final ThreadPoolExecutor forBackgroundTasks;
    private static ExecutorSupplier sInstance;

    public static synchronized ExecutorSupplier getInstance() {
        if (sInstance == null)
            sInstance = new ExecutorSupplier();
        return sInstance;
    }

    private ExecutorSupplier() {

        forBackgroundTasks = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );

    }

    public ThreadPoolExecutor forBackgroundTasks() {
        return forBackgroundTasks;
    }

}