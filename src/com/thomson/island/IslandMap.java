package com.thomson.island;

import com.thomson.entities.Entity;
import com.thomson.entities.EntityFactory;
import com.thomson.entities.EntityType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

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

//    public void createPlantGrow() {
//            int coordinateY = ThreadLocalRandom.current().nextInt(getHeight());
//            int coordinateX = ThreadLocalRandom.current().nextInt(getWidth());
//            Location location = locations[coordinateY][coordinateX];
//            location.addEntity(entityFactory.createEntity(EntityType.PLANT));
//    }

    private Entity getRandomEntity() {
        EntityType[] entityTypes = EntityType.values();
        EntityType entityType = entityTypes[ThreadLocalRandom.current().nextInt(entityTypes.length)];
        return entityFactory.createEntity(entityType);
    }

}
