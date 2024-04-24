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

import java.util.ArrayList;
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
    private static ExecutorService locationRunExecutor; //TODO Для многопоточки //newWorkStealingPool
    /** Поле статистика по острову */
    private final IslandStatistics islandStatistics;
    private final UserDialog userDialog;
    private final StepService stepServiceImpl;
    private final SimulationSettings simulationSettings;
    private final IslandMap islandMap;
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

        executorService = Executors.newFixedThreadPool(3); // Вынести в константу SimulationSettings.INITIAL_CORE_POOL_SIZE
        locationRunExecutor = Executors.newWorkStealingPool();
        scheduledExecutorService = Executors.newScheduledThreadPool(3); // Вынести в константу SimulationSettings.INITIAL_CORE_POOL_SIZE
    }

    /**
     * Метод стартует основные процессы для запуска симуляции:
     * инициализация карты, ее заполнение, запуск заданий в потоках
     */
    public void start() throws InterruptedException {
        // Инициализируем локации (работает)
        islandController.getMap().initialize();
        // Заполняем локации животными (работает)
        islandController.getMap().fillAnimals(simulationSettings.getMaxEntityCountOnLocation());
        // Первоначальное заполнение локаций травой (работает, но требует настройки)
        islandController.getMap().fillPlants(simulationSettings.getMaxPlantCountOnLocation());
//        Thread.sleep(1000);
        // Прорастание травы в течении жизни острова (работает, но требует настройки)
        scheduledExecutorService.scheduleWithFixedDelay(islandController.getMap().germinationGrassTask(),
                100, // Вынести в константу SimulationSettings.INITIAL_DELAY_PLANT_GROW_MILLIS = 1000
                100, // Вынести в константу SimulationSettings. ... plantGrowTime = 100
                TimeUnit.MILLISECONDS);

        int dayCount = 1;
        // Цикл 10 дней по умолчанию
        for (int i = 0; i < simulationSettings.getSimulationCycles(); i++) {

//            // Прорастание травы без многопоточки
//            islandController.getMap().fillPlants(simulationSettings.getMaxPlantCountOnLocation());

            // Активность животных
            executorService.submit(createLifeCycleTask()); // newFixedThreadPool
            Thread.sleep(1000);



//            if (i != 0) {
//                // Здоровье начинает уменьшаться перед наступлением следующего дня
//                deteriorationOfHealth();
//            }

//            for (int coordinateY = 0; coordinateY < islandMap.getHeight(); coordinateY++) {
//                for (int coordinateX = 0; coordinateX < islandMap.getWidth(); coordinateX++) {
//                    Location location = islandMap.getLocations()[coordinateY][coordinateX];
//
//                    System.out.println("День: " + i + " Локация [" + coordinateY + "]" + "[" + coordinateX + "]");
//
//                    List<Animal> animals = new ArrayList<>(location.getAnimals());
//
//                    List<String> animalsAsString = animals.stream()
//                            .map(el -> el.getClass().getSimpleName())
//                            .toList();
//                    System.out.println("Животные в локации:" + animalsAsString);
//
//                    for (Animal animal : animals) {     // Цикл по животным на локации
////                        System.out.print("Выбранное животное: " + animal.getClass().getSimpleName() + " " + animal.getUnicode() + " ");
//                        if (isDead(animal)) {
////                            System.out.println("Мертво");
//                            location.removeEntity(animal);
//                            continue;
//                        }
//                        Action action = animal.chooseAction();
//                        doAction(action, animal, location);
//                    }
//                }
//                System.out.println("-------------------------------------------------------------------");
////                islandController.getMap().fillPlants(simulationSettings.getMaxPlantCountOnLocation());
//            }



//            System.out.println("День: " + dayCount);
//            dayCount++;
            // Статистика по острову
            islandStatistics.printStatistics(islandStatistics.dailyStatistics());
//            islandStatistics.dailyStatistics();
            // Остановка симуляции
            if (DAY_NUMBER.incrementAndGet() > simulationSettings.getSimulationCycles()) {
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
//            int currentTact = TACT_NUMBER.getAndIncrement();
//                if (isEndLifeCycle(currentTact)) {
//                stopSimulation();
//            }
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

//            List<String> animalsAsString = animals.stream()
//                    .map(el -> el.getClass().getSimpleName())
//                    .toList();
//            System.out.println("Животные в локации:" + animalsAsString);

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
//        System.out.println(executorService.isShutdown());
    }

    //----------------------------------------------------------------------------------------------------------------------
    private void deteriorationOfHealth() {
        for (int coordinateY = 0; coordinateY < islandMap.getHeight(); coordinateY++) {
            for (int coordinateX = 0; coordinateX < islandMap.getWidth(); coordinateX++) {
                Location location = islandMap.getLocations()[coordinateY][coordinateX];
                List<Animal> animals = new ArrayList<>(location.getAnimals());

//                List<String> animalsAsString = animals.stream()
//                        .map(el -> el.getClass().getSimpleName())
//                        .toList();
//                System.out.println("Животные в локации:" + animalsAsString);

                for (Animal animal : animals) {     // Цикл по животным на локации
//                    System.out.print("Выбранное животное: " + animal.getClass().getSimpleName() + " " + animal.getUnicode() + " ");
                    if (isDead(animal)) {
//                        System.out.println("Мертво");
                        location.removeEntity(animal);
                        continue;
                    }
//                    double previousHealthScale = animal.getHealthScale();
                    reduceHealth(animal);
//                    System.out.println("Уровень жизни животного: " + animal.getClass().getSimpleName() + " " + animal.getUnicode() + " изменился с " + previousHealthScale + " на " + animal.getHealthScale());
                }
            }
        }
    }

    /**
     * ... Метод выполняет одно из действий для животного с последующим изменением уровня здоровья
     *
     * @param action   ...
     * @param animal   животное, совершающее действие
     * @param location текущая локация
     */
    private void doAction(Action action, Animal animal, Location location) {
//        System.out.println("Животное: " + animal.getClass().getSimpleName() + " " + animal.getUnicode() + " выполняет действие - " + action);
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
//        System.out.println("Количество шагов: " + stepCount);

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



    /**
     * Метод проверяет жизненный цикл на возможность завершения
     *
     * @param currentTact номер текущего такта жизненного цикла
     * @return возвращает true, если текущий такт больше или равен максимальному
     */
    private boolean isEndLifeCycle(int currentTact) {
        return currentTact >= 5;// simulationSettings.getMaxNumberOfTact();
    }
}
