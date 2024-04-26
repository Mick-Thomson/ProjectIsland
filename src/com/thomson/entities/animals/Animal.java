package com.thomson.entities.animals;

import com.thomson.annotations.Property;
import com.thomson.entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

/** Абстрактный класс для животных */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Animal extends Entity {
    private static final int MAX_CHANCE = 100;
    @Property(propertyName = "weight", priority = 1)
    private double weight;
    @Property(propertyName = "maxOnCage", priority = 2)
    private int maxOnCage;
    @Property(propertyName = "speed", priority = 3)
    private int speed;
    @Property(propertyName = "enoughAmountOfFood", priority = 4)
    private double enoughAmountOfFood;
    @Property(propertyName = "unicode", priority = 5)
    private String unicode;
    private double healthScale;

    protected Animal(double weight, int maxOnCage, int speed, double enoughAmountOfFood) {
        this.weight = weight;
        this.maxOnCage = maxOnCage;
        this.speed = speed;
        this.enoughAmountOfFood = enoughAmountOfFood;
        this.healthScale = enoughAmountOfFood;
    }

    protected Animal(double weight, int maxOnCage, int speed, double enoughAmountOfFood, String unicode) {
        this.weight = weight;
        this.maxOnCage = maxOnCage;
        this.speed = speed;
        this.enoughAmountOfFood = enoughAmountOfFood;
        this.unicode = unicode;
        if (enoughAmountOfFood == 0d) {
            this.healthScale = 1;
        } else {
            this.healthScale = enoughAmountOfFood;
        }

    }

    /**
     * Метод размножения животных
     * @return новое животное
     */
    public abstract Animal reproduce();

    public void eat(Entity food) {
        if (food.getWeight() >= this.getEnoughAmountOfFood()) {
            this.setHealthScale((this.getEnoughAmountOfFood()));
        } else {
            double hungerAfterEating = this.getHealthScale() + food.getWeight();
            if (hungerAfterEating >= this.getEnoughAmountOfFood()) {
                this.setHealthScale((this.getEnoughAmountOfFood()));
            } else {
                this.setHealthScale(hungerAfterEating);
            }
        }
    }

    /**
     * Метод выбора действия животным
     * @return действие
     */
    public Action chooseAction() {
        Action action = Action.values()[ThreadLocalRandom.current()
                .nextInt(Action.values().length)];
        boolean isActiveAction = ThreadLocalRandom.current()
                .nextInt(MAX_CHANCE) < action.getActionChanceIndex();
        return isActiveAction ? action : Action.SLEEP;
    }

    /**
     * Метод выбора передвижения животного по локации
     * @return направление движения
     */
    public Direction chooseDirection() {
        return Direction.values()[ThreadLocalRandom.current()
                .nextInt(Direction.values().length)];
    }

//    @Override
//    public double getWeight() {
//        return weight;
//    }
//
//    @Override
//    public int getMaxOnCage() {
//        return maxOnCage;
//    }

    @Override
    public String toString() {
        return "Animal{" +
                "weight=" + weight +
                ", maxOnCage=" + maxOnCage +
                ", speed=" + speed +
                ", enoughAmountOfFood=" + enoughAmountOfFood +
                ", unicode='" + unicode + '\'' +
                ", healthScale=" + healthScale +
                '}';
    }
}
