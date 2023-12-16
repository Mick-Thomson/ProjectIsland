package com.thomson.entities.plants.grass;

import com.thomson.annotations.Entity;
import com.thomson.entities.plants.Plant;

@Entity(className = "grass")
public class Grass extends LandPlants {
    public Grass(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Grass germination() {
        return new Grass(1d, 200, 0, 0d, "\\uD83C\\uDF3F");
    }

}
