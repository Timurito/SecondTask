package main;

import java.io.File;

import static util.Constants.*;

public class Main {
    public static void main(String[] args) {
        if (validateArgs(args)) {
            new TaskProcess(args[INPUT_FILENAME_1], args[INPUT_FILENAME_2], args[OUTPUT_FILENAME]);
        }
    }

    private static boolean validateArgs(String[] args) {
        boolean flag = true;
        if (args.length != NUMBER_OF_ARGUMENTS) {
            printHelp();
            flag = false;
        } else if (!new File(args[INPUT_FILENAME_1]).exists() || !new File(args[INPUT_FILENAME_2]).exists()) {
            System.out.println("Sorry, input file is not found.");
            flag = false;
        }
        return flag;
    }

    private static void printHelp() {
        System.out.println("<program name> <input file name 1 (String)> <input file name 2 (String)> <output file name (String)>");
    }
}
