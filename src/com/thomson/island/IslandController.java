package com.thomson.island;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.thomson.entities.Entity;
import com.thomson.entities.animals.Animal;
import com.thomson.entities.animals.EatingMap;
import com.thomson.simulation.SimulationSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class IslandController {
    /** Поле номер текущего такта жизненного цикла острова */
    public static final AtomicInteger TACT_NUMBER = new AtomicInteger(0);
    /** Индекс максимальной вероятности животного есть */
    private static final int MAX_EATABLE_INDEX = 100;
    /** Карта острова */
    private final IslandMap map;
    /** Объект класса с данными вероятностей животных есть */
    private final EatingMap eatingMap;
    /** Поле параметров настроек симуляции */
    private final SimulationSettings simulationSettings;

    private IslandStats islandStats;

    /**
     *
     * @param map
     * @param simulationSettings
     */
    public IslandController(IslandMap map, SimulationSettings simulationSettings) {
        this.map = map;
//        this.islandStats = new IslandStats();
        this.eatingMap = initEatingChanceData();
        this.simulationSettings = simulationSettings;
        System.out.println(eatingMap);
    }

    /**
     * Метод генерирует объект карты вероятностей животных есть из файла с данными eating-chance-data.yaml
     * @return возвращает объект карты вероятностей
     */
    private EatingMap initEatingChanceData() {
        ObjectMapper mapper = new YAMLMapper();
        EatingMap eatingMap = null;
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SimulationSettings.PATH_TO_EATING_CHANCE_DATA)) {
            eatingMap = mapper.readValue(inputStream, EatingMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return eatingMap;
    }


}
