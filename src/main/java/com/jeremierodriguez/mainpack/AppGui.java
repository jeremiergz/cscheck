package com.jeremierodriguez.mainpack;

import com.jeremierodriguez.controllers.MainController;
import com.jeremierodriguez.services.Algs;
import com.jeremierodriguez.services.CommandLineActions;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.jeremierodriguez.views.MainScene;

import java.awt.*;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * This JavaFX powered GUI application is used to get a file's hash code from a bunch of different
 * algorithms such as MD5, SHA-256, etc.
 *
 * @author Jeremie Rodriguez
 */
public class AppGui extends Application {

    private GridPane grid = new GridPane();
    private static String[] ARGS;

    /**
     * Launches the JavaFX application thread.
     */
    @Override
    public void start(Stage mainStage) throws Exception {

        Locale.setDefault(new Locale("en", "US"));
        Security.addProvider(new BouncyCastleProvider());

        List<Image> imgList = new ArrayList<>();
        imgList.add(new Image(getClass().getResourceAsStream("/images/app512.png")));
        imgList.add(new Image(getClass().getResourceAsStream("/images/app256.png")));
        imgList.add(new Image(getClass().getResourceAsStream("/images/app128.png")));
        imgList.add(new Image(getClass().getResourceAsStream("/images/app64.png")));
        imgList.add(new Image(getClass().getResourceAsStream("/images/app32.png")));
        imgList.add(new Image(getClass().getResourceAsStream("/images/app16.png")));

        mainStage.getIcons().addAll(imgList);

        mainStage.setTitle("CSCheck - v2.0");
        mainStage.setResizable(false);
        mainStage.setMaxWidth(700);
        mainStage.setMaxHeight(400);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        MainScene mainScene = new MainController(grid).init();

        mainStage.setScene(mainScene);

        String algorithm = "algorithm";
        String noGUI = "no-gui";
        String help = "help";

        Options options = new Options();
        options.addOption("a", algorithm, true, "Sets hashing algorithm, defaults to SHA256");
        options.addOption("ng", noGUI, false, "Starts application in command-line without GUI");
        options.addOption("h", help, false, "Prints to console this help message");

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, AppGui.ARGS);

            if (cmd.hasOption(help)) {
                CommandLineActions.printHelpMessage(options, 0);
            }

            // Launches GUI if no-gui option set and if not in a headless environment, reverts to CLI otherwise
            if (!cmd.hasOption("no-gui")) {

                if (GraphicsEnvironment.isHeadless()) {
                    System.out.println("Cannot launch GUI in headless environment");

                } else {
                    System.out.println("Starting application GUI...");
                    mainStage.show();
//                    AppGui.main(AppGui.ARGS);
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

//        mainStage.show();
    }

    /**
     * Launches the JavaFX application window.
     *
     * @param args application arguments
     */
    public static void main(String[] args) {
        AppGui.ARGS = args;
        launch(args);
    }

}
