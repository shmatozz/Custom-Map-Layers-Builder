package com.example.custommaplayersbuilder;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class BuilderController {

    @FXML
    private WebView webView = new WebView();

    @FXML
    private Label log;

    private double[][] currentRoute = {};
    private double[][] currentLine = {};
    private double[][] currentPolygon = {};
    private final ArrayList<JSONObject> currentPoints = new ArrayList<>();
    private final ArrayList<JSONObject> currentCustomPoints = new ArrayList<>();

    private final JavaCallback javaCallback = new JavaCallback();

    public void initialize() {
        WebEngine webEngine = webView.getEngine();

        // Add a listener for page load state
        webEngine.getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        // Page loaded successfully
                        javaCallback.log("Карта успешно загружена.");

                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaCallback", javaCallback);
                    }
                });

        // Load the HTML file containing the map
        webEngine.load(getClass().getResource("maps.html").toExternalForm());
        webEngine.setJavaScriptEnabled(true);

        webEngine.setOnAlert(event -> {
            if (Objects.equals(event.getData(), "undefined")) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaCallback", javaCallback);
            }
        });
    }


    /**
     * Class to process coordinates receiving from map
     */
    public class JavaCallback {
        /**
         * Receiving ROUTE coordinates array
         * @param route - JSObject string with coords
         */
        public void addRoute(Object route) {
            currentRoute = parseObject(route.toString());
            System.out.println(Arrays.deepToString(currentRoute));
        }

        /**
         * Receiving LINE coordinates array
         * @param line - JSObject string with coords
         */
        public void addLine(Object line) {
            currentLine = parseObject(line.toString());
            System.out.println(Arrays.deepToString(currentLine));
        }

        /**
         * Receiving POLYGON coordinates array
         * @param polygon - JSObject string with coords
         */
        public void addPolygon(Object polygon) {
            currentPolygon = parseObject(polygon.toString());
            System.out.println(Arrays.deepToString(currentPolygon));
        }

        /**
         * Receiving POINTS coordinates array
         * @param points - JSObject string with coords
         */
        public void addPoints(Object points, String pointsName) {
            double[][] parsedPointsCoords = parseObject(points.toString());
            currentPoints.clear();
            for (double[] parsedPointsCoord : parsedPointsCoords) {
                currentPoints.add(new JSONObject(Map.of("coords", parsedPointsCoord, "header", pointsName)));
            }
        }

        /**
         *
         */
        public void updatePoint(int index, Object new_coords) {
            double[][] parsedPointsCoords = parseObject(new_coords.toString());

            currentCustomPoints.get(index).put("coords", parsedPointsCoords[0]);
        }

        /**
         * Open dialog window to set up custom
         * @param points - JSObject string with coords
         */
        public void openPointCreateDialog(Object points) {
            onCreatePlacemark(parseObject(points.toString())[0]);
        }

        /**
         * Console logging from WebView console to Java console
         * @param text - log message
         */
        public void log(String text) {
            // System.out.println(text);
            log.setText(text);
        }

        /**
         * Parsing string JSObject to points coordinates array
         * @param object - JSObject string with coords
         * @return Array of double coordinates pairs
         */
        private double[][] parseObject(String object) {
            String[] coordinatesArray = object.split(",");
            int numPoints = coordinatesArray.length / 2;
            double[][] points = new double[numPoints][2];

            for (int i = 0; i < numPoints; i++) {
                double x = Double.parseDouble(coordinatesArray[2 * i]);
                double y = Double.parseDouble(coordinatesArray[2 * i + 1]);
                points[i][0] = x;
                points[i][1] = y;
            }

            return points;
        }
    }

    @FXML
    private void onResetMapButton() {
        webView.getEngine().load(getClass().getResource("maps.html").toExternalForm());
        currentRoute = new double[][] {};
        currentLine = new double[][] {};
        currentPolygon = new double[][] {};
        currentPoints.clear();
        currentCustomPoints.clear();
    }

    @FXML
    private void onCreatePlacemark(double[] points) {
        try {
            /* Setup dialog window */
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/setup-placemark-dialog.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Настройки новой точки");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/settings.png"))));

            /* Setup dialog controller */
            SetupPlacemarkController dialogController = loader.getController();
            dialogController.setStage(dialogStage);
            dialogController.setPoints(points);

            dialogStage.showAndWait();

            if (dialogController.isPointCreated) {
                /* Get new custom point data */
                JSONObject data = dialogController.getEnteredData();
                currentCustomPoints.add(data);

                System.out.println(data.toString());

                /* Pass new custom point data to WebView */
                webView.getEngine().executeScript("processCustomPoint('" + data + "')");
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void onConvertToJSON() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/create-layer-dialog.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Настройки конвертации");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/settings.png"))));

            DialogController dialogController = loader.getController();
            dialogController.setStage(dialogStage);
            dialogController.setWebView(webView);
            dialogController.initData(currentRoute, currentLine, currentPolygon, currentPoints, currentCustomPoints);

            dialogStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
