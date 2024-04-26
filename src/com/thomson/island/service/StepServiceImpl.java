package com.thomson.island.service;

import com.thomson.entities.animals.Animal;
import com.thomson.island.IslandMap;
import com.thomson.island.Location;

/**
 * Класс содержит методы перемещения животных по локациям
 */
public class StepServiceImpl implements StepService {
    //TODO проверить возможность рефакторинга
    /**
     * Метод даёт животному двигаться по карте ВНИЗ на 1 клетку если есть возможность
     * @param animal передвигаемое животное
     * @param currentLocation текущая локация
     * @return возвращает новую локацию или прежнюю, если животное не могло двигаться
     */
    public Location stepDown(Animal animal, Location currentLocation, IslandMap islandMap) {
        int currentX = currentLocation.getCoordinateX();
        int currentY = currentLocation.getCoordinateY();
        if (currentY < islandMap.getHeight() - 1) {
            Location newLocation = islandMap.getLocations()[currentY + 1][currentX];
            if (cantStep(animal, newLocation)) {
                return currentLocation;
            }
            newLocation.addEntity(animal);
            currentLocation.removeEntity(animal);
            return newLocation;
        }
        return currentLocation;
    }
    /**
     * Метод даёт животному двигаться по карте ВВЕРХ на 1 клетку если есть возможность
     * @param animal передвигаемое животное
     * @param currentLocation текущая локация
     * @return возвращает новую локацию или прежнюю, если животное не могло двигаться
     */
    public Location stepUp(Animal animal, Location currentLocation, IslandMap islandMap) {
        int currentX = currentLocation.getCoordinateX();
        int currentY = currentLocation.getCoordinateY();
        if (currentY > 0) {
            Location newLocation = islandMap.getLocations()[currentY - 1][currentX];
            if (cantStep(animal, newLocation)) {
                return currentLocation;
            }
            newLocation.addEntity(animal);
            currentLocation.removeEntity(animal);
            return newLocation;
        }
        return currentLocation;
    }
    /**
     * Метод даёт животному двигаться по карте ВЛЕВО на 1 клетку если есть возможность
     * @param animal передвигаемое животное
     * @param currentLocation текущая локация
     * @return возвращает новую локацию или прежнюю, если животное не могло двигаться
     */
    public Location stepLeft(Animal animal, Location currentLocation, IslandMap islandMap) {
        int currentX = currentLocation.getCoordinateX();
        int currentY = currentLocation.getCoordinateY();
        if (currentX > 0) {
            Location newLocation = islandMap.getLocations()[currentY][currentX - 1];
            if (cantStep(animal, newLocation)) {
                return currentLocation;
            }
            newLocation.addEntity(animal);
            currentLocation.removeEntity(animal);
            return newLocation;
        }
        return currentLocation;
    }
    /**
     * Метод даёт животному двигаться по карте ВПРАВО на 1 клетку если есть возможность
     * @param animal передвигаемое животное
     * @param currentLocation текущая локация
     * @return возвращает новую локацию или прежнюю, если животное не могло двигаться
     */
    public Location stepRight(Animal animal, Location currentLocation, IslandMap islandMap) {
        int currentX = currentLocation.getCoordinateX();
        int currentY = currentLocation.getCoordinateY();
        if (currentX < islandMap.getWidth() - 1) {
            Location newLocation = islandMap.getLocations()[currentY][currentX + 1];
            if (cantStep(animal, newLocation)) {
                return currentLocation;
            }
            newLocation.addEntity(animal);
            currentLocation.removeEntity(animal);
            return newLocation;
        }
        return currentLocation;
    }

    /**
     * Метод проверяет возможность перемещения в новую клетку
     * @param animal перемещаемое животное
     * @param newLocation новая локация
     * @return возвращает true если нельзя сделать ход в новую локацию
     */
    private boolean cantStep(Animal animal, Location newLocation) {
        String animalAsString = animal.getClass().getSimpleName();
        int newLoc;
        if (newLocation.getEntitiesCount().get(animalAsString) == null) {
            newLoc = 0;
        } else {
            newLoc = newLocation.getEntitiesCount().get(animalAsString);
        }
        return newLoc >= animal.getMaxOnCage();
    }
}
