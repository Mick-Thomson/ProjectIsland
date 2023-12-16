package com.thomson.island;

import com.thomson.entities.Entity;
import com.thomson.entities.EntityFactory;
import com.thomson.entities.EntityType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class IslandMap {
    private final EntityFactory entityFactory;

    private final int height; // 100
    private final int width;  // 20
    private final Location[][] locations;

    @SneakyThrows
    public IslandMap(int height, int width) {
        this.height = height;
        this.width = width;
        this.entityFactory = new EntityFactory();
        this.locations = new Location[height][width];
        entityFactory.initEntitiesMap();
    }

    /**
     * Метод инициализации карты пустыми локациями
     */
    public void initialize() {
        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                locations[coordinateY][coordinateX] = new Location(coordinateY, coordinateX);

            }
        }
// Если что, поменять на:
//        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
//            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
//                locations[coordinateX][coordinateY] = new Location(coordinateX, coordinateY);
//            }
//        }
    }

    public void fill(int maxEntityCount) {
        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                for (int i = 0; i < maxEntityCount; i++) {
                    Entity entity = getRandomEntity();
//                    System.out.println("Рандомное  животное: " + entity.getClass().getSimpleName() + " - " + entity);

                    var entityAsString = entity.getClass().getSimpleName();
                    var entityCountOnLocation = locations[coordinateY][coordinateX].getEntitiesCount().getOrDefault(entityAsString, 0);
                    if (entityCountOnLocation >= entity.getMaxOnCage()) {
                        continue;
                    }
                    locations[coordinateY][coordinateX].addEntity(entity);
                }
            }
        }
    }

    public void fillPlants(int maxEntityCount) {
        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                for (int i = 0; i < maxEntityCount; i++) {
                    Entity entity = getPlant();
//                    System.out.println("Трава: " + entity.getClass().getSimpleName() + " - " + entity);

                    var entityAsString = entity.getClass().getSimpleName();
                    var entityCountOnLocation = locations[coordinateY][coordinateX].getEntitiesCount().getOrDefault(entityAsString, 0);
                    if (entityCountOnLocation >= entity.getMaxOnCage()) {
                        continue;
                    }
                    Random random = new Random();
                    int grassGrowthProbability = random.nextInt(2);
                    if (grassGrowthProbability == 1) {
                        locations[coordinateY][coordinateX].addEntity(entity);
                    }
                }
            }
        }
    }

//    public void createPlantGrow() {
//            int coordinateY = ThreadLocalRandom.current().nextInt(getHeight());
//            int coordinateX = ThreadLocalRandom.current().nextInt(getWidth());
//            Location location = locations[coordinateY][coordinateX];
//            location.addEntity(entityFactory.createEntity(EntityType.PLANT));
//    }

    private Entity getRandomEntity() {
        EntityType[] entityTypes = EntityType.values();
//        System.out.println(Arrays.toString(entityTypes));
        EntityType entityType = entityTypes[ThreadLocalRandom.current().nextInt(entityTypes.length - 1)];
//        System.out.println("Выбрано рандомная сущность: " + entityType);
        return entityFactory.createEntity(entityType);
    }

    private Entity getPlant() {
        EntityType[] entityTypes = EntityType.values();
//        System.out.println(Arrays.toString(entityTypes));
        EntityType entityType = entityTypes[entityTypes.length - 1];
//        System.out.println("Выбрана рандомная сущность: " + entityType);
        return entityFactory.createEntity(entityType);
    }

}
