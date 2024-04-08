package com.example.custommaplayersbuilder;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class DialogController {
    private Converter converter;
    private Window window;
    private double[][] currentRoute;
    private double[][] currentLine;
    private double[][] currentPolygon;
    private double[][] currentPoints;
    private String selectedPath;

    public void initData(double[][] currentRoute, double[][] currentLine,
                         double[][] currentPolygon, double[][] currentPoints) {
        this.currentRoute = currentRoute;
        this.currentLine = currentLine;
        this.currentPolygon = currentPolygon;
        this.currentPoints = currentPoints;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    @FXML
    private void convertAll() {
        converter.allToFeatureCollection(currentRoute, currentLine, currentPolygon, currentPoints, selectedPath);
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
            selectedPath = file.getAbsolutePath();
        }
    }
}

