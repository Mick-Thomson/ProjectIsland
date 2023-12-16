package com.thomson.island.service;

import com.thomson.entities.animals.Animal;
import com.thomson.island.IslandMap;
import com.thomson.island.Location;

public interface StepService {
    Location stepDown(Animal animal, Location currentlocation, IslandMap islandMap);
    Location stepUp(Animal animal, Location currentlocation, IslandMap islandMap);
    Location stepLeft(Animal animal, Location currentlocation, IslandMap islandMap);
    Location stepRight(Animal animal, Location currentlocation, IslandMap islandMap);

}
