package com.thomson.entities.animals.predators;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "fox")
public class Fox extends Predator {
    public Fox(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Fox(8d, 30, 2, 2d, "\uD83E\uDD8A");
    }
}
