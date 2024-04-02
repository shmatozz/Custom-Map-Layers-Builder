package com.example.custommaplayersbuilder;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class BuilderController {

    @FXML
    private WebView webView = new WebView();

    @FXML
    private Button resetMapButton;

    private double[][] currentRoute = {};
    private double[][] currentLine = {};
    private double[][] currentPolygon = {};

    public void initialize() throws IOException, ExecutionException, InterruptedException {
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
         * Receiving route coordinates array
         * @param object - JSObject string with coords
         */
        public void addRoute(Object object) {
            currentRoute = parseObject(object.toString());
            System.out.println(Arrays.deepToString(currentRoute));
        }

        public void addLine(Object object) {
            currentLine = parseObject(object.toString());
            System.out.println(Arrays.deepToString(currentLine));
        }

        public void addPolygon(Object object) {
            currentPolygon = parseObject(object.toString());
            System.out.println(Arrays.deepToString(currentPolygon));
        }

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
    }
}