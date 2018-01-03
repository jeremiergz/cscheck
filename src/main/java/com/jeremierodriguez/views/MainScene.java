package com.jeremierodriguez.views;

import com.jeremierodriguez.services.Algs;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


/**
 * This class {@code MainScene} extends JavaFX's {@code Scene} class. It adds all the components
 * the Scene is going to have with all the parameters needed to comply with the desired design.
 *
 * @author Jeremie Rodriguez
 */
public class MainScene extends Scene {

    private ProgressBar progBar = null;
    private TextField hashField = null;
    private TextField genHashField = null;
    private TextField fileField = null;
    private Label progIndic = null;
    private Label matchLabel = null;
    private Label genHashLabel = null;
    private Label statusLabel = null;
    private Label bytesLabel = null;
    private Button browseBtn = null;
    private Button processBtn = null;
    private Button cancelBtn = null;
    private Button folderBtn = null;
    private ComboBox<Algs> comboBox = null;

    /**
     * Constructor with a {@code Parent} given as parameter.
     *
     * @param root such as a {@code GridPane}
     */
    public MainScene(Parent root) {
        super(root);
        this.getStylesheets().add("style.css");

        GridPane grid = (GridPane) root;

        Text cscTitle = new Text("CSC");
        cscTitle.getStyleClass().add("bigtitle");
        cscTitle.applyCss();

        Text heckTitle = new Text("heck");
        heckTitle.getStyleClass().add("smalltitle");
        heckTitle.applyCss();

        HBox titleBox = new HBox();
        titleBox.getChildren().add(cscTitle);
        titleBox.setAlignment(Pos.BASELINE_CENTER);
        titleBox.getChildren().add(heckTitle);

        grid.add(titleBox, 0, 0, 4, 1);

        ImageView titleIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/app64.png")));

        grid.add(titleIcon, 0, 0, 2, 1);

        Label hashLabel = new Label("Hash:");
        grid.add(hashLabel, 0, 2);

        hashField = new TextField();
        hashField.setPrefWidth(400);
        grid.add(hashField, 1, 2, 2, 1);

        matchLabel = new Label();
        matchLabel.setVisible(false);
        matchLabel.getStyleClass().add("bold");
        matchLabel.applyCss();
        GridPane.setHalignment(matchLabel, HPos.CENTER);
        matchLabel.setAlignment(Pos.CENTER);
        grid.add(matchLabel, 3, 2);

        genHashLabel = new Label("Gen hash:");
        genHashLabel.setVisible(false);
        grid.add(genHashLabel, 0, 3);

        genHashField = new TextField();
        genHashField.setPrefWidth(400);
        genHashField.setEditable(false);
        genHashField.setVisible(false);
        grid.add(genHashField, 1, 3, 2, 1);

        Label fileLabel = new Label("File:");
        grid.add(fileLabel, 0, 4);

        fileField = new TextField();
        fileField.setEditable(false);
        grid.add(fileField, 1, 4, 2, 1);

        browseBtn = new Button("Browse");
        browseBtn.setPrefWidth(100);
        browseBtn.setMinWidth(100);
        grid.add(browseBtn, 3, 4);

        Label algoLabel = new Label("Algo:");
        grid.add(algoLabel, 0, 5);

        comboBox = new ComboBox<>();
        comboBox.getItems().setAll(Algs.values());
        comboBox.getSelectionModel().select(Algs.SHA256);
        grid.add(comboBox, 1, 5);

        progBar = new ProgressBar(0);
        progBar.setPrefWidth(300);
        progBar.setPrefHeight(25);
        grid.add(progBar, 2, 5);

        progIndic = new Label();
        progIndic.setVisible(false);
        GridPane.setHalignment(progIndic, HPos.CENTER);
        grid.add(progIndic, 3, 5);

        statusLabel = new Label();
        GridPane innergrid1 = new GridPane();
        innergrid1.setAlignment(Pos.TOP_CENTER);
        innergrid1.add(statusLabel, 0, 0);
        grid.add(innergrid1, 2, 6);

        bytesLabel = new Label();
        GridPane innergrid2 = new GridPane();
        innergrid2.setAlignment(Pos.CENTER);
        innergrid2.add(bytesLabel, 0, 0);
        grid.add(innergrid2, 2, 7);

        folderBtn = new Button("Open Folder");
        grid.add(folderBtn, 0, 8, 2, 1);

        processBtn = new Button("Process");
        processBtn.setPrefWidth(100);
        processBtn.setMinWidth(100);
        processBtn.setDisable(true);
        GridPane.setHalignment(processBtn, HPos.CENTER);
        GridPane innergrid3 = new GridPane();
        innergrid3.setAlignment(Pos.CENTER_RIGHT);
        innergrid3.add(processBtn, 0, 0);
        grid.add(innergrid3, 2, 8);

        cancelBtn = new Button("Cancel");
        cancelBtn.setPrefWidth(100);
        cancelBtn.setMinWidth(100);
        cancelBtn.setDisable(true);
        grid.add(cancelBtn, 3, 8);
    }

    public ProgressBar getProgBar() {
        return progBar;
    }

    public Label getProgIndic() {
        return progIndic;
    }

    public TextField getFileField() {
        return fileField;
    }

    public TextField getGenHashField() {
        return genHashField;
    }

    public TextField getHashField() {
        return hashField;
    }

    public Label getGenHashLabel() {
        return genHashLabel;
    }

    public Label getMatchLabel() {
        return matchLabel;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public Label getBytesLabel() {
        return bytesLabel;
    }

    public Button getBrowseBtn() {
        return browseBtn;
    }

    public Button getProcessBtn() {
        return processBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public ComboBox<Algs> getComboBox() {
        return comboBox;
    }

    public Button getFolderBtn() {
        return folderBtn;
    }

}
