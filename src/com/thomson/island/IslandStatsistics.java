package com.thomson.island;

import com.thomson.entities.Entity;
import com.thomson.entities.EntityType;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IslandStatsistics {

    /** Поле карта острова */
    private IslandMap islandMap;

    /**
     * Конструктор класса, инициализирует поле карты острова
     * @param islandMap объект карты острова
     */
    public IslandStatsistics(IslandMap islandMap) {
        this.islandMap = islandMap;
    }

    //TODO Доработать статистику

    /**
     * Метод собирает статистику по количеству животных в локациях к концу каждого дня
     */
    public void dailyStatistics() {
        Map<String, Integer> entitiesStats = new ConcurrentHashMap<>();
//        Map<String, Integer> entitiesStatsYesterday = new ConcurrentHashMap<>();

        for (int y = 0; y < islandMap.getHeight(); y++) {
            for (int x = 0; x < islandMap.getWidth(); x++) {
                Location location = islandMap.getLocations()[y][x];

                List<Entity> entities = location.getEntities();

                for (Entity entity : entities) {
                    String entityAsString = entity.getClass().getSimpleName();
                    String entityAsImage = EntityType.valueOf(entityAsString.toUpperCase()).getUnicodeSymbol();

                    entitiesStats.merge(entityAsImage, 1, (oldValue, newValue) -> oldValue + 1);
                }
            }
        }
        entitiesStats.forEach((key, value) -> System.out.println(MessageFormat.format("{0} - {1}", key, value) + "  -  популяция " + key.getClass().getSimpleName()));
        System.out.println();
//        entitiesStatsYesterday = entitiesStats;
//        for (int coordinateY = 0; coordinateY < islandMap.getHeight(); coordinateY++) {
//            for (int coordinateX = 0; coordinateX < islandMap.getWidth(); coordinateX++) {
//                Location location = islandMap.getLocations()[coordinateY][coordinateX];
//
////                    System.out.println("День: " + i + " Локация [" + coordinateY + "]" + "[" + coordinateX + "]");
//
//                List<Animal> animals = new CopyOnWriteArrayList<>(location.getAnimals());
//
//                List<String> animalsAsString = animals.stream()
//                        .map(el -> el.getClass().getSimpleName())
//                        .toList();
//                System.out.printf("Животные в локации [%d][%d]: %s\n", coordinateY, coordinateX, animalsAsString);
////                    System.out.println("Животные в локации:" + animalsAsString);
//            }
//        }
    }

//    private Map<String, Integer> collectStats() {
//        Map<String, Integer> entitiesStats = new ConcurrentHashMap<>();
//
//        for (int y = 0; y < islandMap.getHeight(); y++) {
//            for (int x = 0; x < islandMap.getWidth(); x++) {
//                Location location = islandMap.getLocations()[y][x];
//                List<Entity> entities = location.getEntities();
//
//                for (Entity entity : entities) {
//                    String entityAsString = entity.getClass().getSimpleName();
//                    String entityAsImage = EntityType.valueOf(entityAsString.toUpperCase().getUnicodeSymbol());
//                }
//            }
//        }
//    }
}
