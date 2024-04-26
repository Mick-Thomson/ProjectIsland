package com.thomson.dialog;

import com.thomson.simulation.SimulationSettings;

import java.text.MessageFormat;
import java.util.Scanner;

import static com.thomson.dialog.DialogText.*;

public class UserDialog {
    /** Поле настроек симуляции */
    private SimulationSettings simulationSettings;

    /**
     * Конструктор класса для выбора параметров и запуска симуляции
     * @param simulationSettings настройки симуляции
     */
    public UserDialog(SimulationSettings simulationSettings) {
        this.simulationSettings = simulationSettings;

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println(GREETING);
            System.out.println(DIFFICULTY_LEVEL);
            System.out.print(CHOOSING_DIFFICULTY_LEVEL);
            int difficultLevel = scanner.nextInt();
            System.out.println();

            if (difficultLevel == 1) {
                System.out.println(TO_THE_DEATH + "\n");
                System.out.print("Задайте ширину острова: ");
                simulationSettings.setWidthMap(scanner.nextInt()); //
                System.out.print("Задайте высоту острова: ");
                simulationSettings.setHeightMap(scanner.nextInt()); //
                System.out.print("Задайте максимальное количество животных на локации: ");
                simulationSettings.setMaxEntityCountOnLocation(scanner.nextInt()); //
//                System.out.print("Задайте уменьшение здоровья у животных за один день (в процентах): \n");
//                simulationSettings.setReduceHealthPercent(scanner.nextInt());
                System.out.print("Задайте максимальное количество растительности на локации: ");
                simulationSettings.setMaxPlantCountOnLocation(scanner.nextInt()); //
                System.out.print("Установите количество безрадостных дней: ");
                simulationSettings.setSimulationDays(scanner.nextInt()); //
            } else {
                System.out.println(EASY_WALK);
            }
            printSettings();
            startSim(scanner);
        }
    }

    /**
     * Метод печатает выбранные пользователем параметры
     */
    private void printSettings(){
        System.out.println(STARTUP_SETTINGS);

        System.out.println("----------------------------------");
        System.out.println(MessageFormat.format(SIZE_MAP, simulationSettings.getWidthMap(), simulationSettings.getHeightMap()));
        System.out.println(MessageFormat.format(ANIMAL_COUNT_ON_LOCATION, simulationSettings.getMaxEntityCountOnLocation()));
        System.out.println(MessageFormat.format(PLANT_COUNT_ON_LOCATION, simulationSettings.getMaxPlantCountOnLocation()));
        System.out.println(MessageFormat.format(DAY_COUNT, simulationSettings.getSimulationDays()));
        System.out.println("----------------------------------");
    }

    /**
     * Метод предлагает пользователя запустить симуляцию
     * @param scanner объект сканера для считывания данных
     */
    private void startSim(Scanner scanner){
        System.out.println("Надеюсь ты хорошо подумал");
        while (!scanner.nextLine().equals("С Богом")) {
            System.out.println("Для старта симуляции напиши \"С Богом\"");
        }
        System.out.println();
    }
}
