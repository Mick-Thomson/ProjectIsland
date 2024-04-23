package com.thomson.multithreading;

import com.thomson.simulation.SimulationStarter;

import java.util.concurrent.Callable;

public class ActionAnimal implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        SimulationStarter simulationStarter = new SimulationStarter();
        simulationStarter.start();
        return 7;
    }


}
