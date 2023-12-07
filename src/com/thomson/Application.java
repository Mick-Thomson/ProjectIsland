package com.thomson;

import com.thomson.simulation.SimulationStarter;

public class Application {
    public static void main(String[] args) {
//        System.out.println("TimeCode 1:28:00");
        SimulationStarter simulationStarter = new SimulationStarter();
        simulationStarter.start();
    }
}