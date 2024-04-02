package com.example.custommaplayersbuilder;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import netscape.javascript.JSObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BuilderController {

    @FXML
    private WebView webView = new WebView();

    @FXML
    private Button resetMapButton;

    private final ArrayList<Pair<Double, Double>> currentCoords = new ArrayList<>();
    private double[][] currentRoute = {};
    private final Class<JavaCallback> callbacks = JavaCallback.class;

    public void initialize() {
        WebEngine webEngine = webView.getEngine();

        // Add a listener for page load state
        webEngine.getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        // Page loaded successfully
                        System.out.println("Page loaded successfully!");

                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaCallback", new JavaCallback());
                    }
                });

        // Load the HTML file containing the map
        webEngine.load(getClass().getResource("maps.html").toExternalForm());

        webEngine.setOnAlert(event -> {
            if (Objects.equals(event.getData(), "undefined")) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaCallback", new JavaCallback());
            }
        });
    }


    /**
     * Class to process coordinates receiving from map
     */
    public class JavaCallback {
        /**
         * Receive point by Lat Lng coordinates
         * @param latitude - latitude of point
         * @param longitude - longitude of point
         */
        public void addPoint(double latitude, double longitude) {
            currentCoords.add(new Pair<>(latitude, longitude));
            System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
        }

        public void addRoute(Object array) {
            String arrayString = array.toString();

            String[] coordinatesArray = arrayString.split(",");
            int numPoints = coordinatesArray.length / 2;
            double[][] points = new double[numPoints][2];

            for (int i = 0; i < numPoints; i++) {
                double x = Double.parseDouble(coordinatesArray[2 * i]);
                double y = Double.parseDouble(coordinatesArray[2 * i + 1]);
                points[i][0] = x;
                points[i][1] = y;
            }

            currentRoute = points;
            System.out.println(Arrays.deepToString(points));
        }
    }

    @FXML
    private void onResetMapButton() {
        webView.getEngine().load(getClass().getResource("maps.html").toExternalForm());
        currentCoords.clear();
    }
}