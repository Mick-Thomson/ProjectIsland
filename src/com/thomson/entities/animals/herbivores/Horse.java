package com.thomson.entities.animals.herbivores;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "horse")
public class Horse extends Herbivores {
    public Horse(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Horse(400d, 20, 4, 60d, "\uD83D\uDC0E");
    }
}
