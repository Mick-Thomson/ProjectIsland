package com.thomson.island;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.thomson.entities.animals.Animal;
import com.thomson.entities.animals.EatingMap;
import com.thomson.simulation.SimulationSettings;
import com.thomson.simulation.SimulationStarter;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;

@Getter
@Setter
public class IslandController {

    /** Индекс максимальной вероятности животного есть */
    private static final int MAX_EATABLE_INDEX = 100;
    /** Карта острова */
    private final IslandMap map;
    /** Объект класса с данными вероятностей животных есть */
    private final EatingMap eatingMap;
    /** Поле параметров настроек симуляции */
    private final SimulationSettings simulationSettings;
    /** Поле статистика по острову */
    private IslandStatistics islandStats; //TODO Не реализовано
    private SimulationStarter simulationStarter;    // Если не будет работать - удалить
    /**
     * Поле сервис исполнения заданий по каждой локации острова
     */
//    private ExecutorService locationRunExecutor; //TODO Для многопоточки

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
//        this.simulationStarter = new SimulationStarter(); // Если не будет работать - удалить
        System.out.println("eatingMap = " + eatingMap);
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

    /**
     * Метод создает задание для запуска жизненного цикла острова.
     * Запускает внутри себя задание по каждой локации острова
     *
     * @return возвращает задание для запуска жизненного цикла острова
     */
//    public Runnable createLifeCycleTask() {
//        return () -> {
//            for (int coordY = 0; coordY < map.getHeight(); coordY++) {
//                for (int coordX = 0; coordX < map.getWidth(); coordX++) {
//
//                    Location location = map.getLocations()[coordY][coordX];
//                    locationRunExecutor.submit(createLocationTask(location));   //TODO Для многопоточки
//                }
//            }
//            int currentTact = TACT_NUMBER.getAndIncrement();
//
//            if (isEndLifeCycle(currentTact)) {
//                stopSimulation();
//            }
//        };
//    }

    /**
     * Метод создает задание для выполнения действий сущностями в каждой локации
     *
     * @param location отдельная локация карты острова
     * @return возвращает задание для выполнения действий сущностями в каждой локации
     */
//    private Runnable createLocationTask(Location location) {
//        return () -> {
//            List<Animal> animals = new CopyOnWriteArrayList<>(location.getAnimals());   //TODO Для многопоточки
//            for (Animal animal : animals) {
//                if (isDead(animal)) {
//                    location.removeEntity(animal);
//                    continue;
//                }
//                Action action = animal.chooseAction();
//                simulationStarter.doAction(action, animal, location);
//            }
//        };
//    }

    /**
     * Метод останавливает симуляцию жизненного цикла острова
     */
    private void stopSimulation() {
        SimulationStarter.executorService.shutdown();   //TODO Для многопоточки
    }

    /**
     * Метод проверяет жизненный цикл на возможность завершения
     *
     * @param currentTact номер текущего такта жизненного цикла
     * @return возвращает true, если текущий такт больше или равен максимальному
     */
    private boolean isEndLifeCycle(int currentTact) {
        return currentTact >= simulationSettings.getMaxNumberOfTact();  //TODO Для многопоточки
    }

    /**
     * Метод проверяет достаточный ли уровень здоровья для жизни животного
     *
     * @param animal проверяемое животное
     * @return возвращает true если животное умерло
     */
    private boolean isDead(Animal animal) {
        return animal.getHealthScale() < 0;     // Если не будет работать, удалить, метод есть в SimulatorStarter
    }


}
