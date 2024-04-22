package com.thomson.entities;

import com.thomson.annotations.Property;
import com.thomson.entities.animals.herbivores.*;
import com.thomson.entities.animals.predators.*;
import com.thomson.entities.plants.grass.Grass;

import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class EntityFactory {
    public static final String CURRENT_PATH = "com.thomson.entities";
    /** Поле каталог всех сущностей */
    private final Map<Class<?>, Object> entitiesMap = new HashMap<>();

    public Entity createEntity(EntityType entityTypes) {
        return switch (entityTypes) {
            case WOLF -> (Wolf) entitiesMap.get(Wolf.class);
            case BOA -> (Boa) entitiesMap.get(Boa.class);
            case FOX -> (Fox) entitiesMap.get(Fox.class);
            case BEAR -> (Bear) entitiesMap.get(Bear.class);
            case EAGLE -> (Eagle) entitiesMap.get(Eagle.class);

            case HORSE -> (Horse) entitiesMap.get(Horse.class);
            case DEER -> (Deer) entitiesMap.get(Deer.class);
            case RABBIT -> (Rabbit) entitiesMap.get(Rabbit.class);
            case MOUSE -> (Mouse) entitiesMap.get(Mouse.class);
            case GOAT -> (Goat) entitiesMap.get(Goat.class);
            case SHEEP -> (Sheep) entitiesMap.get(Sheep.class);
            case BOAR -> (Boar) entitiesMap.get(Boar.class);
            case BUFFALO -> (Buffalo) entitiesMap.get(Buffalo.class);
            case DUCK -> (Duck) entitiesMap.get(Duck.class);
            case CATERPILLAR -> (Caterpillar) entitiesMap.get(Caterpillar.class);

            case GRASS -> (Grass) entitiesMap.get(Grass.class);
        };
    }

    /**
     * Метод с помощью рефлексии создаёт каталог всех сущностей:
     *   1 Множество всех классов в моём пакете
     *   2 Для каждого класса из множества:
     *   3 Получаем аннотацию типа Entity, если такая есть, иначе null
     *   4 Получаем имя класса сущности, помеченного аннотаций
     *   5 Получение родительских классов (Herbivore и Predator)
     *   6 Получение общего родительского класса Animal
     *   7 Получаем массив всех полей, которые есть у класса Animal
     *   8 Создаем список всех полей каждой сущности типа [wolf.weight, wolf.maxOnCage, wolf.speed, wolf.enoughAmountOfFood, wolf.unicode]
     *   9 Для каждого поля из массива полей:
     *  10 Если поле помечено аннотацией Property, то
     *  11 Получаем аннотацию типа Property
     *  12 Достаём из неё значение propertyName (имя поля сущности)
     *  13 Добавляем каждое поле в список полей сущностей -> 8
     */
    @SneakyThrows
    public void initEntitiesMap() {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("src/resources/animals-data.properties")) {
            properties.load(reader);
            System.out.println("wolf.weight = " + properties.getProperty("wolf.weight"));  // Удалить
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        Set<Class<?>> allClassesFromMyPackage = findAllClassesUsingClassLoader();                    //  1 Множество всех классов в моём пакете
        for (Class<?> aClass : allClassesFromMyPackage) {                                            //  2 Для каждого класса из множества:
            Annotation entityAnnotation = aClass.getAnnotation(com.thomson.annotations.Entity.class);//  3 Получаем аннотацию типа Entity, если такая есть, иначе null
            String entityName = ((com.thomson.annotations.Entity) entityAnnotation).className();     //  4 Получаем имя класса сущности, помеченного аннотаций
            List<String> propertyValues = getPropertyValues(aClass, entityName);
            List<String> valuesToSearch = propertyValues.stream()                                    // 14 Создаём список значений, из которых получим значения полей сущности -> 17
                    .filter(el -> el.startsWith(entityName))                                         // 15 Фильтруем элементы те, которые начинаются с имени сущности, типа "wolf.weight"
                    .toList();  // Возвращаем списком
            Constructor<?> constructor = aClass.getDeclaredConstructor(Double.class, Integer.class, Integer.class, Double.class, String.class); // 16 Получаем конструктор сущности с определёнными параметрами
            // Если отсортировать, то порядок будет такой: 4, 1, 2, 0, 3
            Double weight = Double.valueOf((String) properties.get(valuesToSearch.get(0)));          // 17 Получаем значения каждого поля сущности из properties (propertyValues.get(0))
            Integer maxOnCage = Integer.parseInt((String) properties.get(valuesToSearch.get(1)));
            Integer speed = Integer.parseInt((String) properties.get(valuesToSearch.get(2)));
            Double enoughAmountOfFood = Double.valueOf((String) properties.get(valuesToSearch.get(3)));
            String unicode = String.valueOf(properties.get(valuesToSearch.get(4)));
            entitiesMap.put(aClass, constructor.newInstance(weight, maxOnCage, speed, enoughAmountOfFood, unicode)); // 17 Добавляем в Map ново созданные сущности где K(Класс сущности), а V(объект, созданный по конструктору этой сущности -> 16)
        }
    }
    /**
     * Метод формирует список названий полей сущности
     * @param aClass класс из множества классов
     * @param entityName имя класса сущности, помеченного аннотаций Entity
     * @return возвращается список всех полей каждой сущности вида:
     * [wolf.weight, wolf.maxOnCage, wolf.speed, wolf.enoughAmountOfFood, wolf.unicode]
     */
    private static List<String> getPropertyValues(Class<?> aClass, String entityName) {
        Class<?> animalCategoryClass = aClass.getSuperclass();                                   //  5 Получение родительских классов (Herbivore и Predator)
        Class<?> animalClass = animalCategoryClass.getSuperclass();                              //  6 Получение общего родительского класса Animal
        Field[] parentClassFields = animalClass.getDeclaredFields();                             //  7 Получаем массив всех полей, которые есть у класса Animal
        List<String> propertyValues = new ArrayList<>();                                         //  8 Создаем список всех полей каждой сущности типа [wolf.weight, wolf.maxOnCage, wolf.speed, wolf.enoughAmountOfFood, wolf.unicode]
        for (Field parentClassField : parentClassFields) {                                       //  9 Для каждого поля из массива полей:
            if (parentClassField.isAnnotationPresent(Property.class)) {                          // 10 Если поле помечено аннотацией Property, то
                Annotation propertyAnnotation = parentClassField.getAnnotation(Property.class);  // 11 Получаем аннотацию типа Property
                String propertyName = ((Property) propertyAnnotation).propertyName();            // 12 Достаём из неё значение propertyName (имя поля сущности)
                propertyValues.add(entityName + "." + propertyName);                             // 13 Добавляем каждое поле в список полей сущностей -> 8
            }
        }
        return propertyValues;
    }
    /**
     * Метод ищет все классы с помощью загрузчика классов
     */
    @SneakyThrows
    private Set<Class<?>> findAllClassesUsingClassLoader()
    {
        Class<? extends Annotation> annotationClass = com.thomson.annotations.Entity.class; //TODO Докомментировать
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage(CURRENT_PATH)
                .filterInputsBy(new FilterBuilder().includePackage(CURRENT_PATH)));
        return reflections.getTypesAnnotatedWith(annotationClass);
    }





    /*    @SneakyThrows
    private Set<Class> findAllClassesUsingClassLoader(String packageName) {
        Set<Class> classes = new HashSet<>();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File packageDir = new File(packageName);
        if (packageDir.isDirectory()) {
            File[] files = packageDir.listFiles();
            for (File file : files) {
                String className = file.getName();
                if (className.endsWith(".class")) {
                    className = packageName + "." + className.substring(0, className.length() - 6);
                    Class clazz = classLoader.loadClass(className);
                    classes.add(clazz);
                }
                if (file.isDirectory()) {
                    classes.addAll(findAllClassesUsingClassLoader(file.toString()));
                }
            }
        }
        return classes;
    }*/

//    private Class getClass(String className, String packageName) {
//        try {
//            return Class.forName(packageName + "."
//                    + className.substring(0, className.lastIndexOf('.')));
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
