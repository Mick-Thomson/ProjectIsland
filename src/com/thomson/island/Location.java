package com.thomson.island;

import com.thomson.entities.Entity;
import com.thomson.entities.animals.Animal;
import com.thomson.entities.plants.Plant;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Location {
    private int coordinateX;
    private int coordinateY;
    private List<Entity> entities;
    private Map<String, Integer> entitiesCount;

    public Location(int coordinateY, int coordinateX) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.entities = new ArrayList<>();
        this.entitiesCount = new HashMap<>();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        addToStatistic(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        removeFromStatistic(entity);
    }

    public List<Animal> getAnimals() {
        return entities.stream()
                .filter(entity -> entity instanceof Animal)
                .map(entity -> (Animal) entity)
                .toList();
    }

    public List<Plant> getPlants() {
        return entities.stream()
                .filter(Plant.class::isInstance)
                .map(Plant.class::cast)
                .toList();
    }

    private void addToStatistic(Entity entity) {
        var entityAsString = getEntityName(entity);
        entitiesCount.merge(entityAsString, 1, (oldValue, newValue) -> oldValue + 1);
    }

    private void removeFromStatistic(Entity entity) {
        var entityString = getEntityName(entity);
        entitiesCount.merge(entityString, 1, (oldValue, newValue) -> {
            int newCount = oldValue - 1;
            if (newCount <= 0) {
                return null;
            }
            return newCount;
        });
    }

    private String getEntityName(Entity entity) {
        return entity.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "Location{" +
                "coordinateX=" + coordinateX +
                ", coordinateY=" + coordinateY +
                ", entities=" + entities +
                ", entitiesCount=" + entitiesCount +
                '}';
    }
}
