package com.thomson;

import com.thomson.multithreading.ActionAnimal;
import com.thomson.multithreading.MultithreadingService;
import com.thomson.simulation.SimulationStarter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Application {
    public static void main(String[] args) throws InterruptedException {
//        System.out.println("TimeCode 1:28:00");

        SimulationStarter simulation = new SimulationStarter();
        simulation.start();
    }
}