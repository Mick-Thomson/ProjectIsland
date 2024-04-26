package com.thomson.entities.animals.herbivores;

import com.thomson.entities.Entity;
import com.thomson.entities.animals.Animal;

/** Абстрактный класс для травоядных */
public abstract class Herbivores extends Animal {

    protected Herbivores(double weight, int maxOnCage, int speed, double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }
}
