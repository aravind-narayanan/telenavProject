package test;

import dictionary.CreateDictionary;
import java.io.IOException;
import java.util.Scanner;
//import org.apache.commons.cli.*;

public class TestCreateDictionary {
    public static void main(String[] args) throws IOException {
//        Options options = new Options();
//
//        Option input = new Option("i", "input", true, "input file path");
//        input.setRequired(true);
//        options.addOption(input);
//
//        Option output = new Option("o", "output", true, "output file");
//        output.setRequired(true);
//        options.addOption(output);
//
//        CommandLineParser parser = new DefaultParser();
//        HelpFormatter formatter = new HelpFormatter();
//        CommandLine cmd;
//
//        try {
//            cmd = parser.parse(options, args);
//        } catch (ParseException e) {
//            System.out.println(e.getMessage());
//            formatter.printHelp("utility-name", options);
//
//            System.exit(1);
//        }
//
//        String inputFilePath = cmd.getOptionValue("input");
//        String outputFilePath = cmd.getOptionValue("output");
//
//        System.out.println(inputFilePath);
//        System.out.println(outputFilePath);

        if (args.length != 2) {
            System.out.println("Please give two File path with input and output");
        } else {
            CreateDictionary create = new CreateDictionary(args[0], args[1]);
            print();
            Scanner sc = new Scanner(System.in);
            String userInput = sc.nextLine();
            while (userInput.length() != 0 && !userInput.trim().equals("3")) {
                if (userInput.trim().equals("2")) {
                    create.originateDictionary();
                } else if (userInput.trim().equals("1")) {
                    create.incrementalUpdateDictionary();
                }
                print();
                userInput = sc.nextLine();
            }
        }
    }

    private static void print() {
        System.out.println("what do you want to do?");
        System.out.println("1: update a dictionary");
        System.out.println("2: originate a dictionary (will overwrite)");
        System.out.println("3: quit");
    }
}
