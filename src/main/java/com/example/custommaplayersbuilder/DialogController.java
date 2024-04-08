package com.example.custommaplayersbuilder;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class DialogController {
    private Converter converter;

    @FXML
    private Button pathSelect;
    @FXML
    private Button convertAllButton;
    @FXML
    private Button convertLineButton;
    @FXML
    private Button convertPolygonButton;
    @FXML
    private Button convertPointsButton;
    @FXML
    private Button convertRouteButton;

    private Window window;
    private double[][] currentRoute;
    private double[][] currentLine;
    private double[][] currentPolygon;
    private double[][] currentPoints;


    public void initData(double[][] currentRoute, double[][] currentLine,
                         double[][] currentPolygon, double[][] currentPoints) {
        this.currentRoute = currentRoute;
        this.currentLine = currentLine;
        this.currentPolygon = currentPolygon;
        this.currentPoints = currentPoints;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    @FXML
    private void convertAll() {
        converter.allToFeatureCollection(currentRoute, currentLine, currentPolygon, currentPoints);
    }

    @FXML
    private void convertLine() {
        converter.convertLine(currentLine);
    }

    @FXML
    private void convertPolygon() {
        converter.convertPolygon(currentPolygon);
    }

    @FXML
    private void convertPoints() {
        converter.convertPoints(currentPoints);
    }

    @FXML
    private void convertRoute() {
        converter.convertRoute(currentRoute);
    }

    @FXML
    private void selectSavePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите путь сохранения");

        /* Устанавливаем стандартное имя файла */
        fileChooser.setInitialFileName("output.json");

        /* Фильтр для json файлов */
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        /* Открываем диалог и получаем выбранный путь */
        Stage stage = (Stage) window;
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            String selectedPath = file.getAbsolutePath();

            this.converter = new Converter(selectedPath);
            pathSelect.setText(selectedPath);

            boolean any = false;
            if (currentLine.length > 0) {
                convertLineButton.setDisable(false); any = true;
            }
            if (currentPolygon.length > 0) {
                convertPolygonButton.setDisable(false); any = true;
            }
            if (currentPoints.length > 0) {
                convertPointsButton.setDisable(false); any = true;
            }
            if (currentRoute.length > 0) {
                convertRouteButton.setDisable(false); any = true;
            }
            if (any) {
                convertAllButton.setDisable(false);
            }
        }
    }
}

