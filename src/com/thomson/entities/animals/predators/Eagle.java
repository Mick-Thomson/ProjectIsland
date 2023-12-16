package com.thomson.entities.animals.predators;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "eagle")
public class Eagle extends Predator {
    public Eagle(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Eagle(6d, 20, 3, 1d, "\uD83E\uDD85");
    }
}
