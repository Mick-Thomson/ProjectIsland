package com.thomson.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.thomson.dialog.UserDialog;
import com.thomson.entities.Entity;
import com.thomson.entities.animals.Action;
import com.thomson.entities.animals.Animal;
import com.thomson.entities.animals.Direction;
import com.thomson.entities.animals.EatingMap;
import com.thomson.island.IslandController;
import com.thomson.island.IslandMap;
import com.thomson.island.Location;
import com.thomson.island.service.StepService;
import com.thomson.island.service.StepServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationStarter {
    private final UserDialog userDialog;
    private final StepService stepServiceImpl;
    private final SimulationSettings simulationSettings;
    private final IslandMap islandMap;
    private final IslandController islandController;


    public SimulationStarter() {
        this.stepServiceImpl = new StepServiceImpl();
        this.simulationSettings = new SimulationSettings();
        this.userDialog = new UserDialog(simulationSettings);
        this.islandMap = new IslandMap(simulationSettings.getHeightMap(), simulationSettings.getWidthMap());
        this.islandController = new IslandController(islandMap, simulationSettings);  // Проверить второй параметр eatingMap
    }

    public void start() {
        islandController.getMap().initialize();
        islandController.getMap().fill(simulationSettings.getMaxEntityCountOnLocation());
        islandController.getMap().fillPlants(simulationSettings.getMaxPlantCountOnLocation());

        for (int i = 0; i < simulationSettings.getSimulationCycles(); i++) {
            // Цикл 100 ходов по умолчанию
            for (int coordinateY = 0; coordinateY < islandMap.getHeight(); coordinateY++) {
                for (int coordinateX = 0; coordinateX < islandMap.getWidth(); coordinateX++) {
                    Location location = islandMap.getLocations()[coordinateY][coordinateX];

                    System.out.println("День: " + i + " Локация [" + coordinateY + "]" + "[" + coordinateX + "]");

                    List<Animal> animals = new ArrayList<>(location.getAnimals());

                    List<String> animalsAsString = animals.stream()
                            .map(el -> el.getClass().getSimpleName())
                            .toList();
                    System.out.println("Животные в локации:" + animalsAsString);

                    for (Animal animal : animals) {     // Цикл по животным на локации
                        System.out.print("Выбранное животное: " + animal.getClass().getSimpleName() + " " + animal.getUnicode() + " ");
                        if (isDead(animal)) {
                            System.out.println("Мертво");
                            location.removeEntity(animal);
                            continue;
                        }
                        Action action = animal.chooseAction();
                        doAction(action, animal, location);
                    }
                }
            }
        }
    }

    /**
     * ... Метод выполняет одно из действий для животного с последующим изменением уровня здоровья
     * @param action ...
     * @param animal животное, совершающее действие
     * @param location текущая локация
     */
    private void doAction(Action action, Animal animal, Location location) {
        System.out.println("Животное: " + animal.getClass().getSimpleName() + " " + animal.getUnicode() + " выполняет действие - " + action);
        switch (action) {
            case MOVE -> doMove(animal, location);
//            case EAT -> doEat(animal, location);
            case REPRODUCE -> doReproduce(animal, location);
            case SLEEP -> doSleep(animal);
        }
//        reduceHealth(animal);
    }

    /**
     * Метод выполняет действие MOVE в рандомном направлении не более возможного количества шагов для каждого животного
     * @param animal животное, которое двигается
     * @param location текущая локация
     */
    public void doMove(Animal animal, Location location) {
        int stepCount = ThreadLocalRandom.current().nextInt(animal.getSpeed() + 1);
        System.out.println("Количество шагов: " + stepCount);

        while (stepCount > 0) {
            Direction direction = animal.chooseDirection();
            switch (direction) {
                case DOWN -> location = stepServiceImpl.stepDown(animal, location, islandMap);
                case UP -> location = stepServiceImpl.stepUp(animal, location, islandMap);
                case LEFT -> location = stepServiceImpl.stepLeft(animal, location, islandMap);
                case RIGHT -> location = stepServiceImpl.stepRight(animal, location, islandMap);
            }
            stepCount--;
        }
    }

    /**
     * Метод выполняет действие EAT, если есть еда
     * @param animal животное, которое есть
     * @param location текущая локация
     */
    private void doEat(Animal animal, Location location) {
        List<Entity> entities = location.getEntities();
        List<Entity> foodEntities = entities.stream()
                .filter(foodEntity -> foodEntity.getClass().getSimpleName().equals(animal.getClass().getSimpleName()))
                .toList();
        if (foodEntities.size() > 0) {
            Entity foodEntity = foodEntities.get(ThreadLocalRandom.current().nextInt(foodEntities.size()));

            if (isEaten(animal, foodEntity)) {
                animal.eat(foodEntity);
                location.removeEntity(foodEntity);
            }
        }
    }

    /**
     * Метод выполняет действие REPRODUCE, если есть пара в локации
     * @param animal животное, которое хочет размножиться
     * @param location текущая локация
     */
    private void doReproduce(Animal animal, Location location) {
        String animalsAsString = animal.getClass().getSimpleName();
        if (location.getEntitiesCount().get(animalsAsString) >= animal.getMaxOnCage()) {
            System.out.println("Новое живетное не появилось");
            return;
        }

        List<Animal> animals = location.getAnimals();
        List<Animal> sameAnimalType = animals.stream()
                .filter(animalType -> animalType.getClass().getSimpleName().equals(animal.getClass().getSimpleName()))
                .toList();
        System.out.println("Животных " + animalsAsString + " в локации - " + sameAnimalType.size());
        if (sameAnimalType.size() > 1) {
            Animal newAnimal = animal.reproduce();
            location.addEntity(newAnimal);
            System.out.println("На локации появилось новое животное: " + animalsAsString + " " + animal.getUnicode());
        } else {
            System.out.println("Новое живетное не появилось");
        }

    }

    /**
     * Метод выполняет действие SLEEP и увеличивает уровень здоровья
     * @param animal животное, которе спит
     */
    private void doSleep(Animal animal) {
        System.out.print(animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " - спит ");
        increaseHealth(animal);
        System.out.println();
    }

    /**
     * Метод определяет возможность сожрать другое животное (или растение)
     * @param hungryAnimal животное, которое ест
     * @param foodEntity животное (или растение) которое едят
     * @return возвращает true, если вторую сущность съедают
     */
    private boolean isEaten(Animal hungryAnimal, Entity foodEntity) {
        int probabilityOfEating = getEatableChanceIndex(hungryAnimal, foodEntity);
        return ThreadLocalRandom.current().nextInt(100) < probabilityOfEating;    // TODO MAX_EATABLE_INDEX расхардкодить
    }

    /**
     * Метод определяет значение вероятности животного съест что-то по данным из файла
     * @param hungryAnimal голодное животное
     * @param foodEntity еда
     * @return возвращает значение вероятности
     */
    private Integer getEatableChanceIndex(Animal hungryAnimal, Entity foodEntity) {
        Map<String, Integer> map = islandController.getEatingMap().getEatableIndexes().get(hungryAnimal.getClass().getSimpleName());
        return map.get(foodEntity.getClass().getSimpleName());
    }

    /**
     * Метод уменьшает уровень здоровья животного на N процентов
     * @param animal животное, у которого уменьшается уровень здоровья
     */
//    private void reduceHealth(Animal animal) {
//        double healthScale = animal.getHealthScale() - ((animal.getEnoughAmountOfFood() * simulationSettings.getReduceHealthPercent()) / 100);
//        animal.setHealthScale(healthScale);
//    }

    /**
     * Метод увеличивает уровень здоровья животного на N процентов
     * @param animal животное, у которого увеличивается уровень здоровья
     */
    private void increaseHealth(Animal animal) {
        System.out.print("Уровень жизни был " + animal.getHealthScale() + " | Стал ");
        double healthScale = animal.getHealthScale() + ((animal.getEnoughAmountOfFood() * simulationSettings.getIncreaseHealthScale()) / 100);
        animal.setHealthScale(healthScale);
        System.out.println(healthScale);
    }

    /**
     * Метод проверяет достаточный ли уровень здоровья животного
     * @param animal проверяемое животное
     * @return возвращает true если животное умерло
     */
    private boolean isDead(Animal animal) {
        return animal.getHealthScale() <= 0;
    }












    // TODO ДЛЯ МНОГОПОТОЧКИ
    private void stopSimulation() {

    }

    /**
     * Метод проверяет жизненный цикл на возможность завершения
     * @param currentTact номер текущего такта жизненного цикла
     * @return возвращает true, если текущий такт больше или равен максимальному
     */
    private boolean isEndLifeCycle(int currentTact) {
        return currentTact >= simulationSettings.getMaxNumberOfTact();
    }
}
