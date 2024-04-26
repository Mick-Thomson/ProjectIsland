package com.thomson;

import com.thomson.simulation.SimulationStarter;

public class Application {
    public static void main(String[] args) throws InterruptedException {
//        System.out.println("TimeCode 1:28:00");

        SimulationStarter simulation = new SimulationStarter();
        simulation.start();

        System.out.println("Конец");
    }
}