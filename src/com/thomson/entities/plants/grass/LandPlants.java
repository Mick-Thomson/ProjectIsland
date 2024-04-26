package com.thomson.entities.plants.grass;

import com.thomson.entities.plants.Plant;

/**
 * Абстрактный класс для растений растущих на земле
 */
public abstract class LandPlants extends Plant {
    /**
     * Конструктор класса
     * @param weight
     * @param maxOnCage
     * @param speed
     * @param enoughAmountOfFood
     * @param unicode
     */
    public LandPlants(Double weight, Integer maxOnCage, Integer speed, Double enoughAmountOfFood, String unicode) {
        super(weight, maxOnCage, speed, enoughAmountOfFood, unicode);
    }
}
