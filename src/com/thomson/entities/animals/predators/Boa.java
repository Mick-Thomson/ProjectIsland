package com.thomson.entities.animals.predators;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "boa")
public class Boa extends Predator {
    public Boa(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Boa(50d, 30, 3, 8d, "\uD83D\uDC0D");
    }
}
