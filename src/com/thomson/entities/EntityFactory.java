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
    private Map<Class, Object> entitiesMap = new HashMap<>();

    @SneakyThrows
    public void initEntitiesMap() throws NoSuchMethodException {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("src/resources/animals-data.properties")) {
            properties.load(reader);
            System.out.println("wolf.weight = " + properties.getProperty("wolf.weight"));  // Удалить
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<Class<?>> allClassesFromMyPackage = findAllClassesUsingClassLoader();
        for (Class<?> aClass : allClassesFromMyPackage) {
            Annotation entityAnnotation = aClass.getAnnotation(com.thomson.annotations.Entity.class);   // Получаем название каждого класса
            String entityName = ((com.thomson.annotations.Entity) entityAnnotation).className();        // помеченного аннотацией Entity
//            System.out.println(entityName); // Удалить
            Class<?> animalCategoryClass = aClass.getSuperclass();         // Получение родительских классов (Herbivore и Predator)   // Class parentClass = aClass.getSuperclass();
            Class<?> animalClass = animalCategoryClass.getSuperclass();    // Получение общего родительского класса Animal
            Field[] parentClassFields = animalClass.getDeclaredFields();    // Получаем список всех полей, которые есть у класса Animal    // Field[] parentClassFields = parentClass.getDeclaredFields();
            List<String> propertyValues = new ArrayList<>();
            for (Field parentClassField : parentClassFields) {  // Берём каждое поле
                if (parentClassField.isAnnotationPresent(Property.class)) {     // Если поле помечено аннотацией Property, то
                    Annotation propertyAnnotation = parentClassField.getAnnotation(Property.class); // Берём эту Property
                    String propertyName = ((Property) propertyAnnotation).propertyName();   // Достаём из неё значение propertyName
//                    System.out.println(propertyName); // Удалить
                    propertyValues.add(entityName + "." + propertyName);
                }
            }
//            System.out.println();   // Удалить
            var valuesToSearch = propertyValues.stream()
                    .filter(el -> el.startsWith(entityName))
                    .sorted()
                    .toList();
            Constructor<?> constructor = aClass.getDeclaredConstructor(Double.class, Integer.class, Integer.class, Double.class, String.class);
            Double weight = Double.valueOf((String) properties.get(valuesToSearch.get(4))); // propertyValues.get(0))
            Integer maxOnCage = Integer.parseInt((String) properties.get(valuesToSearch.get(1)));
            Integer speed = Integer.parseInt((String) properties.get(valuesToSearch.get(2)));
            Double enoughAmountOfFood = Double.valueOf((String) properties.get(valuesToSearch.get(0)));
            String unicode = String.valueOf(properties.get(valuesToSearch.get(3)));
            entitiesMap.put(aClass, constructor.newInstance(weight, maxOnCage, speed, enoughAmountOfFood, unicode));
//            System.out.println();
        }
//        System.out.println(entitiesMap.toString()); // Удалить
    }
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

    @SneakyThrows
    private Set<Class<?>> findAllClassesUsingClassLoader()
    {
        Class<? extends Annotation> annotationClass = com.thomson.annotations.Entity.class;
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

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
