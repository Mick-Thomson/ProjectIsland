package com.thomson.dialog;

import com.thomson.simulation.SimulationSettings;

import java.text.MessageFormat;
import java.util.Scanner;

import static com.thomson.simulation.SimulationSettings.*;

public class UserDialog {
    private static final String START_DEFAULT_SETTINGS = "Стартовать с настройками по-умолчанию?\nЕсли Да - введите \"1\" \nЕсли Нет - введите \"0\"";
    private final SimulationSettings simulationSettings;

    public UserDialog(SimulationSettings simulationSettings) {
        this.simulationSettings = simulationSettings;

//        try (Scanner scan = new Scanner(System.in)) {
//            System.out.println();
//            System.out.println();
//            System.out.println();
//
//            boolean isDefaultRunFlag = initDefaultRunFlag(scan);
//            if (!isDefaultRunFlag) {
//                System.out.println();   // ISLAND_MAP
//
////                simulationSettings.setHeightMap(initHeightMap(scan));
//                simulationSettings.setWidthMap(initWidthMap(scan));
//            }
//        }



        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Симулятор острова");
            System.out.println("Текущие настройки: ");
//            System.out.println("Height = " + simulationSettings.getHeightMap());
//            System.out.println("Width = " + simulationSettings.getWidthMap());
            // TODO: добавить возможность изменения параметров
        }
    }

//    private int initWidthMap(Scanner scan) {
//        int width = simulationSettings.getWidthMap();
//
//        while (true) {
//            System.out.println(MessageFormat.format(ENTER_WIDTH_MAP, simulationSettings.getWidthMap(), MIN_LIMIT_WIDTH_MAP, MAX_LIMIT_WIDTH_MAP));
//
//            String widthAsString = scan.nextLine();
//            if (widthAsString.equals("")) {
//                return width;
//            }
//
//        }
//    }

//    private boolean initDefaultRunFlag(Scanner scan) {
//        while (true) {
//            System.out.println(START_DEFAULT_SETTINGS);
//
//            String defaultStart = scan.nextLine();
//            if (defaultStart.equals("")) {
//                return true;
//            }
//            int def = Integer.parseInt(defaultStart);
//            if (def == 1) {
//                return true;
//            } else if (def == 0) {
//                return false;
//            } else {
//                System.out.println("Дан неверный ответ");
//            }
//        }
//    }
}
