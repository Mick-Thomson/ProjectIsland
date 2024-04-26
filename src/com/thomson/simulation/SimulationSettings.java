package com.thomson.simulation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimulationSettings {
    /** Путь к файлу с данными таблицы вероятностей */
    public static final String PATH_TO_EATING_CHANCE_DATA = "resources/eating-chance-data.yaml";

    /** Размер пула потоков */
    public static final int CORE_POOL_SIZE = 3;
    /** Продолжительность дня */
    public static final int LENGTH_OF_DAY_MILLIS = 1000;
    /** Задержка роста травы от начала симуляции */
    public static final int DELAY_PLANT_GROW_MILLIS = 1000;
    /** Частота роста травы */
    public static final int FREQUENCY_GRASS_GERMINATION_MILLIS = 100;

    /** Высота острова (настраиваемое значение) */
    private int heightMap = 3; // По умолчанию 3
    /** Ширина острова (настраиваемое значение) */
    private int widthMap = 3; // По умолчанию 3
    /** Количество дней симуляции (настраиваемое значение) */
    private int simulationDays = 15; // По умолчанию 15
    /** Максимальное количество животных в одной клетке (настраиваемое значение) */
    private int maxEntityCountOnLocation = 10; // Максимальное возможное количество животных в одной клетке По умолчанию 10
    /** Параметр уменьшения здоровья каждый такт, % (настраиваемое значение) */
    private double reduceHealthPercent = 30; // По умолчанию 25
    /** Максимальное количество растений в одной клетке (настраиваемое значение) */
    private int maxPlantCountOnLocation = 10; // По умолчанию 10
    /** Параметр увеличения здоровья, % (настраиваемое значение) */
    private int increaseHealthScale = 30;


}
