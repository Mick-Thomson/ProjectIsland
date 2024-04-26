package com.thomson.entities.animals.predators;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "bear")
public class Bear extends Predator {
    public Bear(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Bear(500d, 5, 2, 80d, "\uD83D\uDC3B");
    }
}
