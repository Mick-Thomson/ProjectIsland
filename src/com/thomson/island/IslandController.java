package com.thomson.island;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.thomson.entities.animals.EatingMap;
import com.thomson.simulation.SimulationSettings;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Класс должен обеспечивать управление островом, но нихера не обеспечивает,
 * всё лежит в SimulatorStarter
 */
@Getter
@Setter
public class IslandController {
    /** Карта острова */
    private final IslandMap map;
    /** Объект класса с данными вероятностей животных есть */
    private final EatingMap eatingMap;
    /** Поле параметров настроек симуляции */
    private final SimulationSettings simulationSettings;
    /** Поле статистика по острову */
    private IslandStatistics islandStats; //TODO Не реализовано

    /**
     * Конструктор класса
     * @param map объект карты острова
     * @param simulationSettings параметры настроек симуляции
     */
    public IslandController(IslandMap map, SimulationSettings simulationSettings) {
        this.map = map;
        this.eatingMap = initEatingChanceData();
        this.simulationSettings = simulationSettings;
//        System.out.println("eatingMap = " + eatingMap);
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
            e.printStackTrace(System.out);
        }
        return eatingMap;
    }
}
