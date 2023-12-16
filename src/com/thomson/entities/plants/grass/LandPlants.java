package com.thomson.entities.plants.grass;

import com.thomson.entities.plants.Plant;

public abstract class LandPlants extends Plant {

    public LandPlants(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }
}
