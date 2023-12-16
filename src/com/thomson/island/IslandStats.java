package com.thomson.island;

import com.thomson.entities.Entity;
import com.thomson.entities.EntityType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IslandStats {

    private IslandMap islandMap;

    public IslandStats(IslandMap islandMap) {
        this.islandMap = islandMap;
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
