package com.thomson.simulation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimulationSettings {
    /** Путь к файлу с данными таблицы вероятностей */
    public static final String PATH_TO_EATING_CHANCE_DATA = "resources/eating-chance-data.yaml";
    /** Минимальная высота карты острова */
    public static final int MIN_LIMIT_HEIGHT_MAP = 5;
    /** Максимальная высота карты острова */
    public static final int MAX_LIMIT_HEIGHT_MAP = 200;
    /** Минимальная ширина карты острова */
    public static final int MIN_LIMIT_WIDTH_MAP = 5;
    /** Максимальная ширина карты острова */
    public static final int MAX_LIMIT_WIDTH_MAP = 200;
    /** Высота острова (настраиваемое значение) */
    private int heightMap = 20;
    /** Ширина острова (настраиваемое значение) */
    private int widthMap = 100;
    /** Максимальное количество животных в локации (настраиваемое значение) */
    private int maxAnimalCount = 100;
    /** Максимальное количество животных в одной клетке (настраиваемое значение) */
    private int maxEntityCountOnLocation = 100; // TODO Максимальное возможное количество животных в одной клетке
    /** Параметр увеличения здоровья, % (настраиваемое значение) */
    private int increaseHealthScale = 30;
    /** Параметр уменьшения здоровья каждый такт, % (настраиваемое значение) */
    private double reduceHealthPercent = 30;
    /** Параметр частоты роста растений, мс (настраиваемое значение) */
    private int plantGrowTime = 100;
    /** Параметр максимального номера такта для завершения симуляции */
    private int maxNumberOfTact = 300;
    /** Параметр частоты вывода статистики в консоль, мс */
    private int statPeriod = 100;
    /** Количество циклов симуляции (настраиваемое значение) */
    private int simulationCycles = 100;

    public static final String ENTER_WIDTH_MAP = "Ширина острова по-умолчанию";

//    private String Path = "src/resources/eating-chance-data.yaml";
}
