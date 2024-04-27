package com.example.custommaplayersbuilder;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

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
    @FXML
    private Label logText;

    private Stage stage;
    private double[][] currentRoute;
    private double[][] currentLine;
    private double[][] currentPolygon;
    private ArrayList<JSONObject> currentPoints;
    private ArrayList<JSONObject> currentCustomPoints;


    public void initData(double[][] currentRoute, double[][] currentLine,
                         double[][] currentPolygon,  ArrayList<JSONObject> currentPoints,
                         ArrayList<JSONObject> currentCustomPoints) {
        this.currentRoute = currentRoute;
        this.currentLine = currentLine;
        this.currentPolygon = currentPolygon;
        this.currentPoints = currentPoints;
        this.currentCustomPoints = currentCustomPoints;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void convertAll() {
        try {
            converter.allToFeatureCollection(currentRoute, currentLine, currentPolygon, currentPoints, currentCustomPoints);
            logText.setText("Всё успешно записано");
        } catch (Exception e) {
            logText.setText("Упс, что-то пошло не так...");
        }
    }

    @FXML
    private void convertLine() {
        try {
            converter.convertLine(currentLine);
            logText.setText("Линия успешно записана");
        } catch (Exception e) {
            logText.setText("Упс, что-то пошло не так...");
        }
    }

    @FXML
    private void convertPolygon() {
        try {
            converter.convertPolygon(currentPolygon);
            logText.setText("Полигон успешно записан");
        } catch (Exception e) {
            logText.setText("Упс, что-то пошло не так...");
        }
    }

    @FXML
    private void convertPoints() {
        try {
            converter.convertPoints(currentPoints, currentCustomPoints);
            logText.setText("Точки успешно записаны");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logText.setText("Упс, что-то пошло не так...");
        }
    }

    @FXML
    private void convertRoute() {
        try {
            converter.convertRoute(currentRoute);
            logText.setText("Маршрут успешно записан");
        } catch (Exception e) {
            logText.setText("Упс, что-то пошло не так...");
        }
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
            if (!currentPoints.isEmpty() || !currentCustomPoints.isEmpty()) {
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

