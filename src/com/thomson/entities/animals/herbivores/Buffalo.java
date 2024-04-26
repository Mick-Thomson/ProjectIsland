package com.thomson.entities.animals.herbivores;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "buffalo")
public class    Buffalo extends Herbivores {
    public Buffalo(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Buffalo(700d, 10, 3, 100d, "\uD83D\uDC03");
    }
}
