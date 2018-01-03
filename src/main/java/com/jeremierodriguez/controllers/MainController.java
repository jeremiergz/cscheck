package com.jeremierodriguez.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jeremierodriguez.services.Algs;
import com.jeremierodriguez.services.HashTask;
import com.jeremierodriguez.util.FolderUtil;
import com.jeremierodriguez.views.MainScene;

import java.io.File;
import java.text.DecimalFormat;


/**
 * This class is used as the {@code com.jeremierodriguez.main controller} of this app.
 *
 * @author Jeremie Rodriguez
 */
public class MainController {

    private static final Logger LOGGER = LogManager.getLogger(MainController.class);
    private static final String APPFOLDER = "/CSCheck";
    private static final String FX_BLACK = "-fx-text-fill: black";
    private static final String FX_GREEN = "-fx-text-fill: #228b22";
    private static final String FX_RED = "-fx-text-fill: #db1e26";
    private static final String FX_ACC_TRANSPARENT = "-fx-accent: transparent";
    private static final String FX_ACC_BLUE = "-fx-accent: #006A9A";
    private static final String FX_ACC_GREEN = "-fx-accent: #228b22";
    private static final String FX_ACC_RED = "-fx-accent: #db1e26";
    private static final String COLOR_RED = "#db1e26";
    private static final String COLOR_GREEN = "#228b22";
    private boolean logDateAdded = false;
    private MainScene mainScene = null;
    private FileChooser chooser = null;
    private ProgressBar progBar = null;
    private Button browseBtn = null;
    private Button processBtn = null;
    private Button cancelBtn = null;
    private Button folderBtn = null;
    private ComboBox<Algs> comboBox = null;
    private TextField hashField = null;
    private TextField fileField = null;
    private TextField genHashField = null;
    private Label progIndic = null;
    private Label matchLabel = null;
    private Label statusLabel = null;
    private Label bytesLabel = null;
    private Label genHashLabel = null;
    private HashTask task = null;
    private File file = null;
    private boolean threadFinished = true;
    private ReadOnlyStringWrapper previousFile = new ReadOnlyStringWrapper();
    private DecimalFormat decForm = new DecimalFormat("0");

    /**
     * This constructor needs a{@code GridPane} object as parameter to function correctly.
     *
     * @param grid GridPane grabbed from application launcher class
     */
    public MainController(GridPane grid) {
        mainScene = new MainScene(grid);
        chooser = new FileChooser();
        chooser.setTitle("Browse file");
        progBar = mainScene.getProgBar();
        progIndic = mainScene.getProgIndic();
        browseBtn = mainScene.getBrowseBtn();
        processBtn = mainScene.getProcessBtn();
        cancelBtn = mainScene.getCancelBtn();
        folderBtn = mainScene.getFolderBtn();
        comboBox = mainScene.getComboBox();
        hashField = mainScene.getHashField();
        fileField = mainScene.getFileField();
        genHashField = mainScene.getGenHashField();
        statusLabel = mainScene.getStatusLabel();
        bytesLabel = mainScene.getBytesLabel();
        genHashLabel = mainScene.getGenHashLabel();
        matchLabel = mainScene.getMatchLabel();
    }

    /**
     * This method initialize {@code MainScene} with all its components and mapped actions.
     *
     * @return MainScene object for use by AppGui class
     */
    public MainScene init() {

        browseBtn.setOnAction(e -> {
            if (file != null && file.getParentFile().isDirectory()) {
                chooser.setInitialDirectory(file.getParentFile());
            }

            File temp = chooser.showOpenDialog(mainScene.getWindow());

            if (temp != null) {
                file = temp;
                changeFileField();

                addListenersOnFile();
            }

            if (file != null) {
                previousFile.set(file.getAbsolutePath());
            }
        });

        processBtn.setOnAction(e -> process());

        cancelBtn.setOnAction(e -> task.interrupt());

        folderBtn.setOnAction(e -> FolderUtil.openOrCreateAppFolder(APPFOLDER));

        hashField.textProperty().addListener(e -> {
            hashField.setStyle(FX_BLACK);
            matchLabel.setVisible(false);
        });

        previousFile.addListener(e -> {
            matchLabel.setVisible(false);
            genHashLabel.setVisible(false);
            genHashField.setVisible(false);
            hashField.setStyle(FX_BLACK);
        });

        mainScene.setOnDragOver(event -> {
            Dragboard dragbd = event.getDragboard();

            if (dragbd.hasFiles()) {
                if (dragbd.getFiles().size() > 1) {
                    event.consume();
                } else if (dragbd.getFiles().get(0).isDirectory()) {
                    event.consume();
                } else {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            } else {
                event.consume();
            }
        });

        mainScene.setOnDragDropped(event -> {
            Dragboard dragbd = event.getDragboard();

            file = dragbd.getFiles().get(0);
            addListenersOnFile();
            changeFileField();
        });

        return mainScene;
    }

    private void process() {

        if (!logDateAdded) {
            LOGGER.log(Level.TRACE, "");
            logDateAdded = true;
        }

        LOGGER.log(Level.INFO,
            "NEW PROCESS -----------------------------------------------------------------------------------------------------------------------------------");
        LOGGER.log(Level.INFO, "File path: " + file);

        progBar.setStyle(FX_ACC_TRANSPARENT);
        threadFinished = false;
        cancelBtn.setDisable(false);
        cancelBtn.requestFocus();
        processBtn.setDisable(true);
        progIndic.setVisible(true);
        genHashLabel.setVisible(false);
        genHashField.setVisible(false);
        matchLabel.setVisible(false);
        hashField.setStyle(FX_BLACK);

        task = new HashTask(hashField.getText(), file, comboBox.getValue());

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {

            modifyViewOnSucceed();

            if (task.getValue() != null) {
                if (task.getValue() == 1) {
                    matchDisplay();
                    LOGGER.log(Level.INFO, "Result: identical hashes");
                } else if (task.getValue() == 0) {
                    mismatchDisplay();
                    LOGGER.log(Level.INFO, "Result: different hashes");
                } else if (task.getValue() == -1) {
                    cancelledDisplay();
                    LOGGER.log(Level.INFO, "Operation cancelled (" + progIndic.getText() + ")");
                }
            }
            LOGGER.log(Level.INFO,
                "PROCESS FINISHED -----------------------------------------------------------------------------------------------------------------------------");
        });

        progBar.progressProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.doubleValue() < 0) {
                progIndic.setText("Starting...");
            } else if (newValue.doubleValue() < 1) {
                progBar.setStyle(FX_ACC_BLUE);
                progIndic.setText(decForm.format(newValue.doubleValue() * 100) + "%");
            } else if (newValue.doubleValue() >= 1) {
                progIndic.setText("Done");
            }
        });

        progBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());
        bytesLabel.textProperty().bind(task.titleProperty());
        genHashField.textProperty().bind(task.getReadOnlyGenHash());

        Platform.runLater(() -> {
            Thread thread = new Thread(task);
            thread.setName("HashTask Thread");
            thread.setDaemon(true);
            thread.start();
        });
    }

    private void mismatchDisplay() {
        matchLabel.setText("MISMATCH");
        matchLabel.setTextFill(Paint.valueOf(COLOR_RED));
        hashField.setStyle(FX_RED);
        genHashLabel.setVisible(true);
        genHashField.setVisible(true);
    }

    private void matchDisplay() {
        matchLabel.setText("MATCH");
        matchLabel.setTextFill(Paint.valueOf(COLOR_GREEN));
        hashField.setStyle(FX_GREEN);
        genHashLabel.setVisible(true);
        genHashField.setVisible(true);
    }

    private void cancelledDisplay() {
        progBar.setStyle(FX_ACC_RED);
        genHashLabel.setVisible(false);
        genHashField.setVisible(false);
        matchLabel.setVisible(false);
        processBtn.requestFocus();
    }

    private void doOnKeyPressed(KeyEvent event) {
        if (threadFinished && event.getCode() == KeyCode.ENTER) {
            process();
        }
        if (!threadFinished && event.getCode() == KeyCode.ESCAPE) {
            task.interrupt();
        }
    }

    private void addListenersOnFile() {
        if (file != null) {
            comboBox.setOnKeyPressed(this::doOnKeyPressed);
            mainScene.setOnKeyPressed(this::doOnKeyPressed);
        }
    }

    private void changeFileField() {
        processBtn.setDisable(false);
        fileField.setText(file.getAbsolutePath());
        fileField.positionCaret(fileField.getLength());
    }

    private void modifyViewOnSucceed() {
        threadFinished = true;
        matchLabel.setVisible(true);
        processBtn.setDisable(false);
        cancelBtn.setDisable(true);
        genHashLabel.setVisible(true);
        genHashField.setVisible(true);
        genHashField.requestFocus();
        progBar.setStyle(FX_ACC_GREEN);
    }

}
