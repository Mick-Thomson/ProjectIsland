package com.thomson.entities.animals.herbivores;

import com.thomson.annotations.Entity;
import com.thomson.entities.animals.Animal;

@Entity(className = "boar")
public class Boar extends Herbivores {
    public Boar(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }

    @Override
    public Animal reproduce() {
        return new Boar(400d, 50, 2, 50d, "\uD83D\uDC17");
    }
}
