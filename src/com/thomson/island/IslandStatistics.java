package com.thomson.island;

import com.thomson.ColorConsole;
import com.thomson.entities.Entity;
import com.thomson.entities.EntityType;
import com.thomson.simulation.SimulationStarter;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IslandStatistics {
    private static final String INCREASED = "{0}\t-\t{1}\t\t↑ популяция стала больше на {2}";
    private static final String DECREASED = "{0}\t-\t{1}\t\t↓ популяция стала меньше на {2}";
    private static final String NOT_CHANGED = "{0}\t-\t{1}\t\t- популяция не изменилась";
    private static final String EXTINCT = "{0}\t-\t{1}\t- вымерло";

    /** Поле карта острова */
    private final IslandMap islandMap;
    /** Поле карта с эмодзи сущностей и изменением популяции на конец текущего дня */
    private final Map<String, Integer> entityPopulationStatistics;
    /** Поле крата с эмодзи сущностей и их количеством на острове за вчерашний день */
    private Map<String, Integer> yesterdayEntitiesStatistics;
    /** Поле карта с эмодзи сущностей и их количеством на острове на конец текущего дня */
    private Map<String, Integer> todayEntitiesStatistics;

    /**
     * Конструктор класса, инициализирует поле карты острова
     * @param islandMap объект карты острова
     */
    public IslandStatistics(IslandMap islandMap) {
        this.islandMap = islandMap;
        this.yesterdayEntitiesStatistics = new HashMap<>();
//        this.todayEntitiesStatistics = new HashMap<>();
        this.entityPopulationStatistics = new HashMap<>(); // Не может быть ConcurrentHashMap так как будет хранить value = null
    }

    //TODO Доработать статистику
    /**
     * Метод печати статистики с количеством сущностей на острове
     * @param todayEntitiesStatistics карта с эмодзи сущностей и их кол-вом на острове на конец текущего дня
     */

    public void printStatistics(Map<String, Integer> todayEntitiesStatistics) { //TODO Заменить на private и закинуть в Runnable для многопоточки
        System.out.println(ColorConsole.DOWN + MessageFormat.format("\nДень {0}-й", SimulationStarter.DAY_NUMBER.get())); // Вынести в диалоговую константу TACT_STATS

        if (SimulationStarter.DAY_NUMBER.get() > 1) {
            for (String entityAsImage : yesterdayEntitiesStatistics.keySet()) {
                if (entityPopulationStatistics.get(entityAsImage) == null) {
                    System.out.println(ColorConsole.ANSI_YELLOW + MessageFormat.format(
                            EXTINCT, entityAsImage, entityPopulationStatistics.get(entityAsImage)));
                } else if (entityPopulationStatistics.get(entityAsImage) > 0) {
                    System.out.println(ColorConsole.ANSI_GREEN + MessageFormat.format(
                            INCREASED, entityAsImage, todayEntitiesStatistics.get(entityAsImage), entityPopulationStatistics.get(entityAsImage)) );
                } else if (entityPopulationStatistics.get(entityAsImage) < 0) {
                    System.out.println(ColorConsole.ANSI_RED + MessageFormat.format(
                            DECREASED, entityAsImage, todayEntitiesStatistics.get(entityAsImage), Math.abs(entityPopulationStatistics.get(entityAsImage))));
                } else {
                    System.out.println(ColorConsole.ANSI_BLUE + MessageFormat.format(
                            NOT_CHANGED, entityAsImage, todayEntitiesStatistics.get(entityAsImage)));
                }
            }
        } else {
            todayEntitiesStatistics.forEach((key, value) -> System.out.println(ColorConsole.ANSI_BLUE + MessageFormat.format("{0}\t-\t{1}", key, value))); // Вынести в диалоговую константу EMOJI_KEY_COUNT_VALUE
        }
        System.out.println("\u001b[0m");
    }

    /**
     * Метод высчитывает изменение популяции сущностей
     * @param yesterdayEntitiesStatistics карта с эмодзи сущностей и их количеством на острове за прошлый день
     */
    private void populationEntity(Map<String, Integer> yesterdayEntitiesStatistics) {
        int result;
        Set<String> keyEntityYesterday = yesterdayEntitiesStatistics.keySet();
        for (String key : keyEntityYesterday) {
            // Если во сегодняшней статистике есть сущность из вчерашней статистики, то
            if (todayEntitiesStatistics.containsKey(key)) {
                result = todayEntitiesStatistics.get(key) - yesterdayEntitiesStatistics.get(key);
                entityPopulationStatistics.put(key, result);
            } else { // Если во вчерашней сущность есть, а в сегодняшней нет, то животное вымерло
                entityPopulationStatistics.put(key, null);
            }
        }
    }

    /**
     * Метод собирает статистику по количеству животных в локациях к концу каждого дня
     */
    public void dailyStatistics() {
        todayEntitiesStatistics = new HashMap <>(); //TODO Concurrent для многопоточки

        for (int y = 0; y < islandMap.getHeight(); y++) {
            for (int x = 0; x < islandMap.getWidth(); x++) {
                Location location = islandMap.getLocations()[y][x];

                List<Entity> entities = location.getEntities();

                for (Entity entity : entities) {
                    String entityAsString = entity.getClass().getSimpleName();
                    String entityAsImage = EntityType.valueOf(entityAsString.toUpperCase()).getUnicodeSymbol();

                    todayEntitiesStatistics.merge(entityAsImage, 1, (oldValue, newValue) -> oldValue + 1);
                }
            }
        }
        if(SimulationStarter.DAY_NUMBER.get() > 1) {
            populationEntity(yesterdayEntitiesStatistics);
        }
        printStatistics(todayEntitiesStatistics);
        yesterdayEntitiesStatistics = todayEntitiesStatistics;
    }

    //TODO Реализовать под многопоточку
//    /**
//     * Метод создает задание печати статистики по острову
//     * @return задание печати статистики по острову
//     */
//    public Runnable createShowStatsTask() {
//        return () -> printStats(collectStats());
//    }
//
//    /**
//     * Метод собирает статистику по всем сущностям острова
//     * @return возвращает карту с эмодзи сущностей и их кол-вом на острове в текущий момент времени
//     */
//    private Map<String, Integer> collectStats() {
//        Map<String, Integer> entitiesStats = new ConcurrentHashMap<>();
//
//        for (int y = 0; y < islandMap.getHeight(); y++) {
//            for (int x = 0; x < islandMap.getWidth(); x++) {
//                Location location = islandMap.getLocations()[y][x];
//
//                List<Entity> entities = location.getEntities();
//
//                for (Entity entity : entities) {
//                    String entityAsString = entity.getClass().getSimpleName();
//                    String entityAsImage = EntityType.valueOf(entityAsString.toUpperCase()).getUnicodeSymbol();
//
//                    entitiesStats.merge(entityAsImage, 1, (oldValue, newValue) -> oldValue + 1);
//                }
//            }
//        }
//        return entitiesStats;
//    }
//    /**
//     * Метод печати статистики с кол-вом сущностей на острове (с очищением консоли)
//     * @param entitiesStatistics карта с эмодзи сущностей и их кол-вом на острове в текущий момент времени
//     */
//    private void printStats(Map<String, Integer> entitiesStatistics) {
//        clearConsole();
//        System.out.println(MessageFormat.format(TACT_STATS, IslandController.TACT_NUMBER.get()));
//        System.out.println();
//        entitiesStatistics.forEach((key, value) -> System.out.println(MessageFormat.format(EMOJI_KEY_COUNT_VALUE, key, value)));
//        System.out.println("\n");
//    }
//
//    /**
//     * Метод очищает консоль каждый такт жизненного цикла острова
//     */
//    private void clearConsole() {
//        try {
//            if (System.getProperty("os.name").contains("Windows")) {
//                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
//            } else {
//                System.out.print("\033\143");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
