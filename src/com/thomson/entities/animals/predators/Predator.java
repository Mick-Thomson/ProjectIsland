package com.thomson.entities.animals.predators;

import com.thomson.entities.animals.Animal;

/** Абстрактный класс для хищников */
public abstract class Predator extends Animal {

    protected Predator(double weight, int maxOnCage, int speed, double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }
}
