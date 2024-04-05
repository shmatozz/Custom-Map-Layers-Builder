package com.example.custommaplayersbuilder;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.Arrays;
import java.util.Objects;

public class BuilderController {

    @FXML
    private WebView webView = new WebView();

    @FXML
    private Button resetMapButton;
    @FXML
    private Button convertToJSON;

    @FXML
    private Label log;

    private double[][] currentRoute = {};
    private double[][] currentLine = {};
    private double[][] currentPolygon = {};
    private double[][] currentPoints = {};

    private final JavaCallback javaCallback = new JavaCallback();
    private final Converter converter = new Converter();

    public void initialize() {
        WebEngine webEngine = webView.getEngine();

        // Add a listener for page load state
        webEngine.getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        // Page loaded successfully
                        javaCallback.log("Page loaded successfully!");

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
         * Console logging from WebView console to Java console
         * @param text - log message
         */
        public void log(String text) {
            System.out.println(text);
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
                points[i][0] = y;
                points[i][1] = x;
            }

            return points;
        }
    }

    @FXML
    private void onResetMapButton() {
        webView.getEngine().load(getClass().getResource("maps.html").toExternalForm());
    }

    @FXML
    private void onConvertToJSON() {
        converter.allToFeatureCollection(currentRoute, currentLine, currentPolygon, currentPoints, "output.json");
    }
}