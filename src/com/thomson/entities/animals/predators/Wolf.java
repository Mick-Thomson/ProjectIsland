package com.thomson.entities.animals.predators;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "wolf")
public class Wolf extends Predator {
    public Wolf(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Wolf(50d, 30, 3, 8d, "\uD83D\uDC3A");
    }
}
