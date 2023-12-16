package com.thomson.entities.animals;

import lombok.Getter;

import java.util.Map;

@Getter
public class EatingMap {
    private Map<String, Map<String, Integer>> eatableIndexes;

    @Override
    public String toString() {
        return "EatingMap{" +
                "eatableIndexes=" + eatableIndexes +
                '}';
    }
}
