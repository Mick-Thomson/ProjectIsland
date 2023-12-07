package com.thomson.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Entity {
    private double weight;
    private int maxOnCage;
    private String unicode;

}
