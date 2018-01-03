package com.jeremierodriguez.services;


import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * @author Jeremie Rodriguez
 */
public class CommandLineActions {

    private CommandLineActions() {
    }

    public static void printHelpMessage(Options options, int exitStatus) {

        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("cs-check --no-gui [--algorithm] <hash> <pathtofile> \r\n\r\nOptions", options);

        System.exit(exitStatus);
    }

}
