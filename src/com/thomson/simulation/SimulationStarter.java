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
        // Цикл 100 ходов по умолчанию
        for (int i = 0; i < simulationSettings.getSimulationCycles(); i++) {
            // Прикрутить статистику на каждый день

            // Здоровье начинает уменьшаться перед наступлением следующего дня
            islandController.getMap().fillPlants(simulationSettings.getMaxPlantCountOnLocation());
            if (i != 0) {
                deteriorationOfHealth();
            }

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
                System.out.println("-------------------------------------------------------------------");
//                islandController.getMap().fillPlants(simulationSettings.getMaxPlantCountOnLocation());
            }

        }
    }

    private void deteriorationOfHealth() {
        for (int coordinateY = 0; coordinateY < islandMap.getHeight(); coordinateY++) {
            for (int coordinateX = 0; coordinateX < islandMap.getWidth(); coordinateX++) {
                Location location = islandMap.getLocations()[coordinateY][coordinateX];
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
                    double previousHealthScale = animal.getHealthScale();
                    reduceHealth(animal);
                    System.out.println("Уровень жизни животного: " + animal.getClass().getSimpleName() + " " + animal.getUnicode() + " изменился с " + previousHealthScale + " на " + animal.getHealthScale());
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
            case EAT -> doEat(animal, location);
            case REPRODUCE -> doReproduce(animal, location);
            case SLEEP -> doSleep(animal);
        }
//        System.out.print("Уровень здоровья животного " + animal.getClass().getSimpleName() + " " + animal.getUnicode() + " - " + animal.getHealthScale() + " | ");
//        reduceHealth(animal);
//        System.out.println(animal.getHealthScale());
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
        List<Entity> entities = location.getEntities(); // Получаем список животных из локации, на которых находится животное которое ест
        System.out.println(entities);
        List<Entity> foodEntities;
        // Проверка на принодлежность к травоядным
        if (animal.getClass().getSuperclass().getSimpleName().equals("Herbivores")) {
            System.out.println(animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " - является травоядным");
            foodEntities = entities.stream()
                    .filter(foodEntity -> foodEntity.getClass().getSimpleName().equals("Grass"))    // Отфильтровываем животных, оставляем только траву если есть
                    .toList();
            System.out.println("Это может съесть травоядное: " + foodEntities);
        } else {
            System.out.println(animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " - является хищником");
            foodEntities = entities.stream()
                    .filter(foodEntity -> !foodEntity.getClass().getSimpleName().equals(animal.getClass().getSimpleName())) // Отфильтровываем животных, которые являются одного вида с животным, которое ест
                    .filter(foodEntity -> !foodEntity.getClass().getSimpleName().equals("Grass"))
                    .toList();
            System.out.println("Это может съесть хищник: " + foodEntities);
        }
        if (!foodEntities.isEmpty()) {  // Если отфильтрованный список не пуст
            Entity foodEntity;// То возвращаем рандомное животное, которое можно съесть
            if (animal.getClass().getSuperclass().getSimpleName().equals("Herbivores")) {
                foodEntity = foodEntities.get(ThreadLocalRandom.current().nextInt(foodEntities.size())); // 0
                System.out.println(animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " собирается съесть - " + foodEntity.getClass().getSimpleName() + ": " + foodEntity.getUnicode());
            } else {
                foodEntity = foodEntities.get(ThreadLocalRandom.current().nextInt(foodEntities.size()));
                System.out.println(animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " собирается съесть - " + foodEntity.getClass().getSimpleName() + ": " + foodEntity.getUnicode());
            }
            if (isEaten(animal, foodEntity)) {
                animal.eat(foodEntity);
                System.out.println(animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " съедает - " + foodEntity.getClass().getSimpleName() + ": " + foodEntity.getUnicode() + " - эта сущность удаляется с локации");
                location.removeEntity(foodEntity);
            } else {
                System.out.println("Животному " + animal.getClass().getSimpleName() + ": " + animal.getUnicode() + " не удалось съесть животное " + foodEntity.getClass().getSimpleName() + ": " + foodEntity.getUnicode());
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
        if (location.getEntitiesCount().get(animalsAsString) == null) {
            System.out.println("Новое животное не появилось");
        } else {
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
        System.out.println("Вероятность поедания - " + probabilityOfEating);
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
    private void reduceHealth(Animal animal) {
        double healthScale = animal.getHealthScale() - ((animal.getEnoughAmountOfFood() * simulationSettings.getReduceHealthPercent()) / 100);
        animal.setHealthScale(healthScale);
    }

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
