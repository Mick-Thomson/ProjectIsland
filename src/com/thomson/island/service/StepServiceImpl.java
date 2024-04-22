package com.thomson.island.service;

import com.thomson.entities.animals.Animal;
import com.thomson.island.IslandMap;
import com.thomson.island.Location;

public class StepServiceImpl implements StepService {
    /**
     * Метод даёт животному двигаться по карте на 1 клетку если есть возможность
     * @param animal передвигаемое животное
     * @param currentlocation текущая локация
     * @return возвращает новую локацию или прежнюю, если животное не могло двигаться
     */
    public Location stepDown(Animal animal, Location currentlocation, IslandMap islandMap) {
//        System.out.println("Хочет пойти вниз");
        int currentX = currentlocation.getCoordinateX();
        int currentY = currentlocation.getCoordinateY();
        if (currentY < islandMap.getHeight() - 1) {
            Location newLocation = islandMap.getLocations()[currentY + 1][currentX];
            if (cantStep(animal, newLocation)) {
//                System.out.println("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " осталось на месте");
                return currentlocation;
            }
            newLocation.addEntity(animal);
            currentlocation.removeEntity(animal);
//            System.out.println("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " переместилось вниз");
            return newLocation;
        }
//        System.out.println(("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " осталось на месте, не смогло выйти за пределы острова"));
        return currentlocation;
    }
    public Location stepUp(Animal animal, Location currentlocation, IslandMap islandMap) {
//        System.out.println("Хочет пойти вверх");
        int currentX = currentlocation.getCoordinateX();
        int currentY = currentlocation.getCoordinateY();
        if (currentY > 0) {
            Location newLocation = islandMap.getLocations()[currentY - 1][currentX];
            if (cantStep(animal, newLocation)) {
//                System.out.println("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " осталось на месте");
                return currentlocation;
            }
            newLocation.addEntity(animal);
            currentlocation.removeEntity(animal);
//            System.out.println("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " переместилось вверх");
            return newLocation;
        }
//        System.out.println(("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " осталось на месте, не смогло выйти за пределы острова"));
        return currentlocation;
    }
    public Location stepLeft(Animal animal, Location currentlocation, IslandMap islandMap) {
//        System.out.println("Хочет пойти влево");
        int currentX = currentlocation.getCoordinateX();
        int currentY = currentlocation.getCoordinateY();
        if (currentX > 0) {
            Location newLocation = islandMap.getLocations()[currentY][currentX - 1];
            if (cantStep(animal, newLocation)) {
//                System.out.println("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " осталось на месте");
                return currentlocation;
            }
            newLocation.addEntity(animal);
            currentlocation.removeEntity(animal);
//            System.out.println("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " переместилось влево");
            return newLocation;
        }
//        System.out.println(("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " осталось на месте, не смогло выйти за пределы острова"));
        return currentlocation;
    }
    public Location stepRight(Animal animal, Location currentlocation, IslandMap islandMap) {
//        System.out.println("Хочет пойти вправо");
        int currentX = currentlocation.getCoordinateX();
        int currentY = currentlocation.getCoordinateY();
        if (currentX < islandMap.getWidth() - 1) {
            Location newLocation = islandMap.getLocations()[currentY][currentX + 1];
            if (cantStep(animal, newLocation)) {
//                System.out.println("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " осталось на месте");
                return currentlocation;
            }
            newLocation.addEntity(animal);
            currentlocation.removeEntity(animal);
//            System.out.println("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " переместилось вправо");
            return newLocation;
        }
//        System.out.println(("Животное " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " осталось на месте, не смогло выйти за пределы острова"));
        return currentlocation;
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
