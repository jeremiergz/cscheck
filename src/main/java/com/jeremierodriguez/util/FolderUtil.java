package com.jeremierodriguez.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Class with static methods used to manipulate system folders.
 *
 * @author Jeremie Rodriguez
 */
public class FolderUtil {

    private static final Logger LOGGER = LogManager.getLogger(FolderUtil.class);

    private FolderUtil() {
    }

    /**
     * Static method that creates a folder with the name given as parameter if
     * doesn't exist already. After checking if exists/creating folder, it opens
     * it. The folder must be located in the System's {@code user.home}.
     *
     * @param appFolder name of the folder to check/create and open
     */
    public static void openOrCreateAppFolder(String appFolder) {

        Path homePath = Paths.get(System.getProperty("user.home") + appFolder);

        try {
            if (!homePath.toFile().exists()) {
                Files.createDirectory(homePath);
            }
            if (System.getProperty("os.name").contains("Linux")) {
                Runtime.getRuntime().exec("gnome-open " + homePath);
            } else {
                Desktop.getDesktop().open(homePath.toFile());
            }

        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
        }
    }

}
