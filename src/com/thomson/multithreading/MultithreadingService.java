package com.thomson.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultithreadingService {
    public void startMultithreading() {
        ExecutorService executorService = Executors.newFixedThreadPool(7);


        executorService.shutdown();
    }
}
