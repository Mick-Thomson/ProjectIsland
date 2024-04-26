package com.thomson.simulation;

import com.thomson.dialog.UserDialog;
import com.thomson.entities.Entity;
import com.thomson.entities.animals.Action;
import com.thomson.entities.animals.Animal;
import com.thomson.entities.animals.Direction;
import com.thomson.island.IslandController;
import com.thomson.island.IslandMap;
import com.thomson.island.IslandStatistics;
import com.thomson.island.Location;
import com.thomson.island.service.StepService;
import com.thomson.island.service.StepServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс обеспечивает запуск симуляции в разных потоках
 */
public class SimulationStarter {
    /** Поле номер текущего такта жизненного цикла острова */
    public static final AtomicInteger DAY_NUMBER = new AtomicInteger(1);
    /** Поле запуск заданий активности животных */
    public static ExecutorService executorService; // newFixedThreadPool
    /** Поле запуск заданий роста травы по расписанию */
    public static ScheduledExecutorService scheduledExecutorService;
    /** Поле сервис исполнения заданий по каждой локации острова */
    private static ExecutorService locationRunExecutor; // newWorkStealingPool
    //TODO добавить многопоточку для статистики
    /** Поле статистика по острову */
    private final IslandStatistics islandStatistics;
    /** Поле диалога пользователя */
    private final UserDialog userDialog;
    /** Поле сервиса перемещения по локациям */
    private final StepService stepServiceImpl;
    /** Поле настроек симуляции */
    private final SimulationSettings simulationSettings;
    /** Поле карта острова */
    private final IslandMap islandMap;
    /** Поле управление островом */
    private final IslandController islandController;

    /**
     * Конструктор класса, инициализирует поле настроек симуляции, поле диалога с пользователем,
     * поле карты острова, поле управления островом, поле запуска заданий по расписанию
     */
    public SimulationStarter() {
        this.stepServiceImpl = new StepServiceImpl();
        this.simulationSettings = new SimulationSettings();
        this.userDialog = new UserDialog(simulationSettings);
        this.islandMap = new IslandMap(simulationSettings.getHeightMap(), simulationSettings.getWidthMap());
        this.islandController = new IslandController(islandMap, simulationSettings);  // Проверить второй параметр eatingMap
        this.islandStatistics = new IslandStatistics(islandMap);

        executorService = Executors.newFixedThreadPool( SimulationSettings.CORE_POOL_SIZE);
        locationRunExecutor = Executors.newWorkStealingPool(); //newWorkStealingPool
        scheduledExecutorService = Executors.newScheduledThreadPool( SimulationSettings.CORE_POOL_SIZE);
    }

    /**
     * Метод стартует основные процессы для запуска симуляции:
     * инициализация карты, ее заполнение, запуск заданий в потоках
     */
    public void start() {
        // Инициализируем локации (работает)
        islandController.getMap().initialize();
        // Заполняем локации животными (работает)
        islandController.getMap().fillAnimals(simulationSettings.getMaxEntityCountOnLocation());
        // Первоначальное заполнение локаций травой (работает, но требует настройки)
        islandController.getMap().fillPlants(simulationSettings.getMaxPlantCountOnLocation());
//        Thread.sleep(1000);
        // Прорастание травы в течении жизни острова (работает, но требует настройки)
        scheduledExecutorService.scheduleWithFixedDelay(islandController.getMap().germinationGrassTask(),
                SimulationSettings.DELAY_PLANT_GROW_MILLIS, // Вынести в константу SimulationSettings.INITIAL_DELAY_PLANT_GROW_MILLIS = 1000
                SimulationSettings.FREQUENCY_GRASS_GERMINATION_MILLIS, // Вынести в константу SimulationSettings. ... plantGrowTime = 100
                TimeUnit.MILLISECONDS);

        // Цикл 15 дней по умолчанию
        for (int i = 0; i < simulationSettings.getSimulationDays(); i++) {

            // Активность животных
            executorService.submit(createLifeCycleTask()); // newFixedThreadPool
            try {
                Thread.sleep(SimulationSettings.LENGTH_OF_DAY_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            }
            // Статистика по острову
//            islandStatistics.printStatistics(islandStatistics.dailyStatistics());
            islandStatistics.dailyStatistics();
//            islandStatistics.dailyStatistics();
            // Остановка симуляции
            if (DAY_NUMBER.incrementAndGet() > simulationSettings.getSimulationDays()) {
                stopSimulation();
            }
        }
    }
//----------------------------------------------------------------------------------------------------------------------
    /**
     * Метод создает задание для запуска жизненного цикла острова.
     * Запускает внутри себя задание по каждой локации острова
     *
     * @return возвращает задание для запуска жизненного цикла острова
     */
    public Runnable createLifeCycleTask() {
        return () -> {
            for (int coordinateY = 0; coordinateY < islandMap.getHeight(); coordinateY++) {
                for (int coordinateX = 0; coordinateX < islandMap.getWidth(); coordinateX++) {
                    Location location = islandMap.getLocations()[coordinateY][coordinateX];
                    locationRunExecutor.submit(createLocationTask(location));
                }
            }
        };
    }

    /**
     * Метод создает задание для выполнения действий сущностями в каждой локации
     *
     * @param location отдельная локация карты острова
     * @return возвращает задание для выполнения действий сущностями в каждой локации
     */
    private Runnable createLocationTask(Location location) {
        return () -> {
            List<Animal> animals = new CopyOnWriteArrayList<>(location.getAnimals());
            for (Animal animal : animals) {
                if (isDead(animal)) {
                    location.removeEntity(animal);
                    continue;
                }
                Action action = animal.chooseAction();
                doAction(action, animal, location);
            }
        };
    }

    // TODO ДЛЯ МНОГОПОТОЧКИ
    private void stopSimulation() {
        executorService.shutdown(); // newFixedThreadPool
        scheduledExecutorService.shutdown();
    }

    //----------------------------------------------------------------------------------------------------------------------
    /**
     * Метод выполняет одно из действий для животного с последующим изменением уровня здоровья
     *
     * @param action выбранное действие
     * @param animal животное, совершающее действие
     * @param location текущая локация
     */
    private void doAction(Action action, Animal animal, Location location) {
        switch (action) {
            case MOVE -> doMove(animal, location);
            case EAT -> doEat(animal, location);
            case REPRODUCE -> doReproduce(animal, location);
            case SLEEP -> doSleep(animal);
        }
//        System.out.print("Уровень здоровья животного " + animal.getClass().getSimpleName() + " " + animal.getUnicode() + " - " + animal.getHealthScale() + " | ");
//        reduceHealth(animal);
//        System.out.println(animal.getHealthScale());
    }

    /**
     * Метод выполняет действие MOVE в рандомном направлении не более возможного количества шагов для каждого животного
     *
     * @param animal   животное, которое двигается
     * @param location текущая локация
     */
    public void doMove(Animal animal, Location location) {
        int stepCount = ThreadLocalRandom.current().nextInt(animal.getSpeed() + 1);

        while (stepCount > 0) {
            Direction direction = animal.chooseDirection();
            switch (direction) {
                case DOWN -> location = stepServiceImpl.stepDown(animal, location, islandMap);
                case UP -> location = stepServiceImpl.stepUp(animal, location, islandMap);
                case LEFT -> location = stepServiceImpl.stepLeft(animal, location, islandMap);
                case RIGHT -> location = stepServiceImpl.stepRight(animal, location, islandMap);
            }
            stepCount--;
        }
    }

    /**
     * Метод выполняет действие EAT, если есть еда
     *
     * @param animal   животное, которое есть
     * @param location текущая локация
     */
    //TODO Метода изменён, если нее будет работать, вернуть старый
    private void doEat(Animal animal, Location location) {
        List<Entity> entities = location.getEntities(); // Получаем список животных из локации, на которых находится животное которое ест
        List<Entity> foodEntities;

        if (animal.getClass().getSuperclass().getSimpleName().equals("Herbivores")) {// Проверка на принадлежность к травоядным
            foodEntities = entities.stream()
                    .filter(foodEntity -> foodEntity.getClass().getSimpleName().equals("Grass"))    // Отфильтровываем животных, оставляем только траву если есть
                    .toList();
        } else {
            foodEntities = entities.stream()
                    .filter(foodEntity -> !foodEntity.getClass().getSimpleName().equals(animal.getClass().getSimpleName())) // Отфильтровываем животных, которые являются одного вида с животным, которое ест
                    .filter(foodEntity -> !foodEntity.getClass().getSimpleName().equals("Grass"))
                    .toList();
        }
        if (!foodEntities.isEmpty()) {  // Если отфильтрованный список не пуст
            Entity foodEntity;// То возвращаем рандомное животное, которое можно съесть
            foodEntity = foodEntities.get(ThreadLocalRandom.current().nextInt(foodEntities.size())); // 0
            if (isEaten(animal, foodEntity)) {
                animal.eat(foodEntity);
                location.removeEntity(foodEntity);
            }
        }
    }

    /**
     * Метод выполняет действие REPRODUCE, если есть пара в локации
     *
     * @param animal   животное, которое хочет размножиться
     * @param location текущая локация
     */
    //TODO Метода изменён, если нее будет работать, вернуть старый
    private void doReproduce(Animal animal, Location location) {
        String animalsAsString = animal.getClass().getSimpleName();
        if (location.getEntitiesCount().get(animalsAsString) != null) {
            if (location.getEntitiesCount().get(animalsAsString) >= animal.getMaxOnCage()) {
                return;
            }

            List<Animal> animals = location.getAnimals();
            List<Animal> sameAnimalType = animals.stream()
                    .filter(animalType -> animalType.getClass().getSimpleName().equals(animal.getClass().getSimpleName()))
                    .toList();

            if (sameAnimalType.size() > 1) {
                Animal newAnimal = animal.reproduce();
                location.addEntity(newAnimal);
            }
        }
    }

    /**
     * Метод выполняет действие SLEEP и увеличивает уровень здоровья
     *
     * @param animal животное, которе спит
     */
    private void doSleep(Animal animal) {
        increaseHealth(animal);
    }

    /**
     * Метод определяет возможность сожрать другое животное (или растение)
     *
     * @param hungryAnimal животное, которое ест
     * @param foodEntity   животное (или растение) которое едят
     * @return возвращает true, если вторую сущность съедают
     */
    private boolean isEaten(Animal hungryAnimal, Entity foodEntity) {
        int probabilityOfEating = getEatableChanceIndex(hungryAnimal, foodEntity);
        return ThreadLocalRandom.current().nextInt(100) < probabilityOfEating;    // TODO MAX_EATABLE_INDEX расхардкодить
    }

    /**
     * Метод определяет значение вероятности животного съест что-то по данным из файла
     *
     * @param hungryAnimal голодное животное
     * @param foodEntity   еда
     * @return возвращает значение вероятности
     */
    private Integer getEatableChanceIndex(Animal hungryAnimal, Entity foodEntity) {
        Map<String, Integer> map = islandController.getEatingMap().getEatableIndexes().get(hungryAnimal.getClass().getSimpleName());
        return map.get(foodEntity.getClass().getSimpleName());
    }

    /**
     * Метод уменьшает уровень здоровья животного на N процентов
     *
     * @param animal животное, у которого уменьшается уровень здоровья
     */
    private void reduceHealth(Animal animal) {
        double healthScale = animal.getHealthScale() - ((animal.getEnoughAmountOfFood() * simulationSettings.getReduceHealthPercent()) / 100);
        animal.setHealthScale(healthScale);
    }

    /**
     * Метод увеличивает уровень здоровья животного на N процентов
     *
     * @param animal животное, у которого увеличивается уровень здоровья
     */
    private void increaseHealth(Animal animal) {
        double healthScale = animal.getHealthScale() + ((animal.getEnoughAmountOfFood() * simulationSettings.getIncreaseHealthScale()) / 100);
        animal.setHealthScale(healthScale);
    }

    /**
     * Метод проверяет достаточный ли уровень здоровья животного
     *
     * @param animal проверяемое животное
     * @return возвращает true если животное умерло
     */
    private boolean isDead(Animal animal) {
        return animal.getHealthScale() <= 0;
    }

    // Для ScheduledExecutorService нужен метод проверки на возможность завершения жизненного цикла isEndLifeCycle
}
