package com.thomson.island;

import com.thomson.entities.Entity;
import com.thomson.entities.EntityFactory;
import com.thomson.entities.EntityType;
import com.thomson.simulation.SimulationStarter;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс карты острова
 */
@Getter
@Setter
public class IslandMap {
    /** Объект фабрика сущностей */
    private final EntityFactory entityFactory;
    /** Высота карты */
    private final int height; // 100
    /** Ширина карты */
    private final int width;  // 20
    /** Карта острова в виде двумерного массива локаций */
    private final Location[][] locations;

    /**
     * Конструктор класса, инициализирует высоту, ширину, фабрику, массив локаций
     * и творит какое-то сильное колдунство entityFactory.initEntitiesMap()
     * @param height ширина карты острова
     * @param width высота карты острова
     */
    @SneakyThrows
    public IslandMap(int height, int width) {
        this.height = height;
        this.width = width;
        this.entityFactory = new EntityFactory();
        this.locations = new Location[height][width];
        /** Тут вообще магия */
        entityFactory.initEntitiesMap();
    }

    /**
     * Метод инициализирует карту пустыми локациями
     */
    public void initialize() {
        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                locations[coordinateY][coordinateX] = new Location(coordinateY, coordinateX);

            }
        }
    }

    /**
     * Метод заполняет локацию животными
     * @param maxAnimalCount максимальное количество животных в локации
     */
    public void fillAnimals(int maxAnimalCount) {
        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                for (int i = 0; i < maxAnimalCount; i++) {
                    Entity entity = getRandomEntity();
                    String entityAsString = entity.getClass().getSimpleName();
                    Integer entityCountOnLocation = locations[coordinateY][coordinateX]
                            .getEntitiesCount().getOrDefault(entityAsString, 0);
                    if (entityCountOnLocation >= entity.getMaxOnCage()) {
                        continue;
                    }
                    locations[coordinateY][coordinateX].addEntity(entity);
                }
            }
        }
    }

    /**
     * Метод первоначально заполняет локацию травой
     * @param maxPlantCount максимальное количество травы в локации
     */
    public void fillPlants(int maxPlantCount) {
        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                for (int i = 0; i < maxPlantCount; i++) {
                    Entity entity = getPlant();
                    String entityAsString = entity.getClass().getSimpleName();
                    Integer entityCountOnLocation = locations[coordinateY][coordinateX]
                            .getEntitiesCount().getOrDefault(entityAsString, 0); // узнать больше про работу метода getOrDefault()

                    if (entityCountOnLocation >= entity.getMaxOnCage()) {
                        continue;
                    }

                    Random random = new Random();
                    int grassGrowthProbability = random.nextInt(100);
                    if (grassGrowthProbability > 50) {
                        locations[coordinateY][coordinateX].addEntity(entity);
                    }
                }
            }
        }
    }

    // TODO ДЛЯ МНОГОПОТОЧКИ
    /**
     * Метод создает задание роста растения в рандномной локации
     */
    public Runnable germinationGrassTask() {
        return () -> {
            int coordinateX = ThreadLocalRandom.current().nextInt(getWidth());
            int coordinateY = ThreadLocalRandom.current().nextInt(getHeight());
            Entity entity = getPlant();

            Location location = locations[coordinateY][coordinateX];
            location.addEntity(entity);

            if (SimulationStarter.executorService.isShutdown()) {
                stopGerminationGrassTask();
            }
        };
    }

    /**
     * Метод останавливает рост травы в локациях
     */
    private void stopGerminationGrassTask() {
        SimulationStarter.scheduledExecutorService.shutdown();
    }

    /**
     * Метод возвращает рандомно животное
     */
    private Entity getRandomEntity() {
        EntityType[] entityTypes = EntityType.values();
        EntityType entityType = entityTypes[ThreadLocalRandom.current().nextInt(entityTypes.length - 1)];
        return entityFactory.createEntity(entityType);
    }

    /**
     * Метод возвращает траву
     */
    private Entity getPlant() {
        EntityType[] entityTypes = EntityType.values();
        EntityType entityType = entityTypes[entityTypes.length - 1];
        return entityFactory.createEntity(entityType);
    }
}
