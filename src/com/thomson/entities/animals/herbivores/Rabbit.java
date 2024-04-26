package com.thomson.entities.animals.herbivores;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "rabbit")
public class Rabbit extends Herbivores {
    public Rabbit(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Rabbit(2d, 150, 2, 0.45, "\uD83D\uDC07");
    }
}
