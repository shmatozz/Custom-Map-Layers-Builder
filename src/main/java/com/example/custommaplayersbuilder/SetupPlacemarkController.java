package com.example.custommaplayersbuilder;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONObject;

public class SetupPlacemarkController {
    private double[] points;

    @FXML
    public Button createPlacemark;
    @FXML
    public TextField headerInput;
    @FXML
    public TextField textInput;
    @FXML
    public TextField hintInput;
    @FXML
    public ColorPicker colorPicker;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPoints(double[] points) {
        this.points = points;
    }

    public JSONObject getEnteredData() {
        JSONObject data = new JSONObject();

        data.put("coords", points);
        data.put("header", headerInput.getText());
        data.put("body", textInput.getText());
        data.put("hint", hintInput.getText());
        data.put("color", getColor());

        return data;
    }

    private String getColor() {
        Color selectedColor = colorPicker.getValue();

        return String.format("#%02X%02X%02X",
                (int) (selectedColor.getRed() * 255),
                (int) (selectedColor.getGreen() * 255),
                (int) (selectedColor.getBlue() * 255));
    }

    @FXML
    public void createPlacemark() {
        stage.close();
    }
}
