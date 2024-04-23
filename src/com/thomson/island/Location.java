package com.thomson.island;

import com.thomson.entities.Entity;
import com.thomson.entities.animals.Animal;
import com.thomson.entities.plants.Plant;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Location {
    private int coordinateX;
    private int coordinateY;
    /** Поле список всех сущностей локации */
    private List<Entity> entities;
    /** Поле тип-количество всех сущностей локации */ // Статистика
    private Map<String, Integer> entitiesCount;

    /**
     * Конструктор класса, инициализирует координаты, список сущностей локации и карту типов с их количеством
     *
     * @param coordinateY координата y локации
     * @param coordinateX координата x локации
     */
    public Location(int coordinateY, int coordinateX) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.entities = new CopyOnWriteArrayList<>();   // Для многопоточки используем CopyOnWriteArrayList
        this.entitiesCount = new ConcurrentHashMap<>();   // Для многопоточки используем ConcurrentHashMap
    }

    /**
     * Метод добавляет сущность в список
     *
     * @param entity сущность
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
        addToStatistic(entity);
    }

    /**
     * Метод добавляет в поле тип-количество сущностей новую сущность
     * (прибавляет к количеству уже имеющихся +1)
     *
     * @param entity сущность
     */
    private void addToStatistic(Entity entity) {
        var entityAsString = getEntityName(entity);
        entitiesCount.merge(entityAsString, 1, (oldValue, newValue) -> oldValue + 1);
    }

    /**
     * Метод удаляет сущность из локации
     *
     * @param entity сущность
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
        removeFromStatistic(entity);
    }

    /**
     * Метод удаляет из поля тип-количество сущностей старую сущность
     * (отнимает от количества уже имеющихся -1)
     * Проверяет, если новое количество определённой сущности <= 0, то возвращает null
     *
     * @param entity сущность
     */
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

    /**
     * Метод отфильтровывает из списка сущностей животных
     *
     * @return возвращает список всех животных локации
     */
    public List<Animal> getAnimals() {
        return entities.stream()
                .filter(entity -> entity instanceof Animal)
                .map(entity -> (Animal) entity)
                .toList();
    }

    /**
     * Метод отфильтровывает из списка сущностей растения
     *
     * @return возвращает список всех животных локации
     */
    public List<Plant> getPlants() {
        return entities.stream()
                .filter(Plant.class::isInstance)
                .map(Plant.class::cast)
                .toList();
    }
//----------------------------------------------------------------------------------------------------------------------

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
