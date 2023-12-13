package com.thomson.entities.animals.herbivores;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "caterpillar")
public class Caterpillar extends Herbivores {
    public Caterpillar(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Caterpillar(0.01, 1000, 0, 0d, "\uD83D\uDC1B");
    }
}
