package com.thomson.entities.plants;

import com.thomson.entities.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Plant extends Entity {
    private double weight = 1;
    private int maxOnCage = 200;
    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public int getMaxOnCage() {
        return maxOnCage;
    }
}
