package com.thomson.entities.plants;

import com.thomson.annotations.Property;
import com.thomson.entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public abstract class Plant extends Entity {
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

    protected Plant(double weight, int maxOnCage, int speed, double enoughAmountOfFood, String unicode){
        this.weight = weight;
        this.maxOnCage = maxOnCage;
        this.speed  = speed;
        this.enoughAmountOfFood = enoughAmountOfFood;
        this.unicode = unicode;
    }

    protected abstract Plant germination();

    @Override
    public String toString() {
        return "Plant{" +
                "weight=" + weight +
                ", maxOnCage=" + maxOnCage +
                ", speed=" + speed +
                ", enoughAmountOfFood=" + enoughAmountOfFood +
                ", unicode='" + unicode + '\'' +
                '}';
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
}
