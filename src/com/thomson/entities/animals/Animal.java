package com.thomson.entities.animals;

import com.thomson.annotations.Property;
import com.thomson.entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
//        this.unicode = "unicode";
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

    public abstract Animal reproduce();

    public void eat(Entity food) {
        if (food.getWeight() >= this.getEnoughAmountOfFood()) {
            this.setHealthScale((this.getEnoughAmountOfFood()));
//            System.out.println("Если вес еды - " + food.getWeight() + " больше или равен достаточному количеству еды - " + this.getEnoughAmountOfFood() + " | Устанавливаем показатель здоровья сытого животного в " + this.getEnoughAmountOfFood());
        } else {
            double hungerAfterEating = this.getHealthScale() + food.getWeight();
            if (hungerAfterEating >= this.getEnoughAmountOfFood()) {
//                System.out.println("Слишком много еды, животное " + this.getClass().getSimpleName() + ": " + this.getUnicode() + " насытилось до максимального уровня здоровья");
                this.setHealthScale((this.getEnoughAmountOfFood()));
            } else {
//                System.out.println("Текущий показатель здоровья животного - " + this.getHealthScale() + " | После еды - " + hungerAfterEating);
                this.setHealthScale(hungerAfterEating);
            }
        }
    }

    public Action chooseAction() {
        Action action = Action.values()[ThreadLocalRandom.current()
                .nextInt(Action.values().length)];  // Если не будет многопоточки сменить на "new Random().nextInt(Direction.values().length)"
//        System.out.print("Выбранное действие - " + action + ", ");
        boolean isActiveAction = ThreadLocalRandom.current()
                .nextInt(MAX_CHANCE) < action.getActionChanceIndex();  // Если не будет многопоточки сменить на "new Random().nextInt(Direction.values().length)"
//        System.out.println("Будет выполнено? - " + isActiveAction);
        return isActiveAction ? action : Action.SLEEP;
//        return action;
    }

    public Direction chooseDirection() {
        return Direction.values()[ThreadLocalRandom.current()
                .nextInt(Direction.values().length)];   // Если не будет многопоточки сменить на "new Random().nextInt(Direction.values().length)"
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public int getMaxOnCage() {
        return maxOnCage;
    }

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
