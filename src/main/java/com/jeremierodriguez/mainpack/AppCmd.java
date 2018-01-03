package com.jeremierodriguez.mainpack;

import com.jeremierodriguez.services.Algs;
import com.jeremierodriguez.services.CommandLineActions;
import org.apache.commons.cli.*;

import java.awt.*;
import java.util.Arrays;


/**
 * @author Jeremie Rodriguez
 */
public class AppCmd {

    public static void main(String[] args) {

        String algorithm = "algorithm";
        String noGUI = "no-gui";
        String help = "help";

        Options options = new Options();
        options.addOption("a", algorithm, true, "Sets hashing algorithm, defaults to SHA256");
        options.addOption("ng", noGUI, false, "Starts application in command-line without GUI");
        options.addOption("h", help, false, "Prints to console this help message");

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(help)) {
                CommandLineActions.printHelpMessage(options, 0);
            }

            // Launches GUI if no-gui option set and if not in a headless environment, reverts to CLI otherwise
            if (!cmd.hasOption("no-gui")) {

                if (GraphicsEnvironment.isHeadless()) {
                    System.out.println("Cannot launch GUI in headless environment");

                } else {
                    System.out.println("Starting application GUI...");
                    AppGui.main(args);
                }

            } else {
                // Checks if given hashing algorithm is known by the application
                boolean isKnownAlgorithm = Arrays.stream(Algs.values()).anyMatch(
                    a -> a.name().equals(cmd.getOptionValue(algorithm).toUpperCase()));

                if (!isKnownAlgorithm) {
                    System.out.println("Unknown hashing algorithm, default back to SHA256");
                }

            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            CommandLineActions.printHelpMessage(options, 1);
        }
    }

}
