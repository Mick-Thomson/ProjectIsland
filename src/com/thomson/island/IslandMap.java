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
    /** Фабрика сущностей */
    private final EntityFactory entityFactory;
    /** Высота карты */
    private final int height; // 100
    /** Ширина карты */
    private final int width;  // 20
    /** Карта острова в виде двумерного массива локаций */
    private final Location[][] locations;


    @SneakyThrows
    public IslandMap(int height, int width) {
        this.height = height;
        this.width = width;
        this.entityFactory = new EntityFactory();
        this.locations = new Location[height][width];
        /**  */
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
// Если что, поменять на:
//        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
//            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
//                locations[coordinateX][coordinateY] = new Location(coordinateX, coordinateY);
//            }
//        }
    }

    /**
     * Метод заполняет локацию животными
     *
     * @param maxEntityCount максимальное количество животных в локации
     * @return
     */
    public void fillAnimals(int maxEntityCount) {
        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                for (int i = 0; i < maxEntityCount; i++) {
                    Entity entity = getRandomEntity();
//                    System.out.println("Рандомное  животное: " + entity.getClass().getSimpleName() + " - " + entity);
                    String entityAsString = entity.getClass().getSimpleName();
                    Integer entityCountOnLocation = locations[coordinateY][coordinateX].getEntitiesCount().getOrDefault(entityAsString, 0);
                    if (entityCountOnLocation >= entity.getMaxOnCage()) {
                        continue;
                    }
                    locations[coordinateY][coordinateX].addEntity(entity);
                }
            }
        }
    }

    /**
     * Метод заполняет локацию травой
     */
    public void fillPlants(int maxEntityCount) {
        for (int coordinateY = 0; coordinateY < height; coordinateY++) {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                for (int i = 0; i < maxEntityCount; i++) {
                    Entity entity = getPlant();
//                    System.out.println("Трава: " + entity.getClass().getSimpleName() + " - " + entity);
                    String entityAsString = entity.getClass().getSimpleName();
                    Integer entityCountOnLocation = locations[coordinateY][coordinateX].getEntitiesCount().getOrDefault(entityAsString, 0); // узнать больше про работу метода getOrDefault()
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
//        System.out.println("Arrays.toString(locations) = " + Arrays.deepToString(locations));
    }



//    public void createPlantGrow() {
//            int coordinateY = ThreadLocalRandom.current().nextInt(getHeight());
//            int coordinateX = ThreadLocalRandom.current().nextInt(getWidth());
//            Location location = locations[coordinateY][coordinateX];
//            location.addEntity(entityFactory.createEntity(EntityType.PLANT));
//    }

    /**
     * Метод возвращает рандомно животное
     */
    private Entity getRandomEntity() {
        EntityType[] entityTypes = EntityType.values();
//        System.out.println("entityTypes = " + Arrays.toString(entityTypes));
        EntityType entityType = entityTypes[ThreadLocalRandom.current().nextInt(entityTypes.length - 1)];
//        System.out.println("Выбрано рандомная сущность: " + entityType);
        return entityFactory.createEntity(entityType);
    }

    /**
     * Метод возвращает траву
     */
    private Entity getPlant() {
        EntityType[] entityTypes = EntityType.values();
//        System.out.println("entityTypes = " + Arrays.toString(entityTypes));
        EntityType entityType = entityTypes[entityTypes.length - 1];
//        System.out.println("Выбрана трава: " + entityType);
        return entityFactory.createEntity(entityType);
    }

    /**
     * Метод создает задание роста растения в радномной локации
     * @return возвращает задание роста растения в радномной локации
     */
    public Runnable createPlantGrowTask(){
        return () -> {
            int coordinateX = ThreadLocalRandom.current().nextInt(getWidth());
            int coordinateY = ThreadLocalRandom.current().nextInt(getHeight());
            Location location = locations[coordinateY][coordinateX];
            location.addEntity(entityFactory.createEntity(EntityType.GRASS));
        };
    }



}
