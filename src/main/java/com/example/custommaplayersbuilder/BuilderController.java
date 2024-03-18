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

public class BuilderController {

    @FXML
    private WebView webView;

    @FXML
    private Button resetMapButton;

    private final ArrayList<Pair<Double, Double>> currentCoords = new ArrayList<>();

    public void initialize() {
        WebEngine webEngine = webView.getEngine();

        // Add a listener for page load state
        webEngine.getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        // Page loaded successfully
                        System.out.println("Page loaded successfully!");

                        // Get the window object and add the JavaCallback object to it
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaCallback", new JavaCallback());
                    }
                });

        // Load the HTML file containing the map
        webEngine.load(getClass().getResource("maps.html").toExternalForm());
    }

    // Click handler for the map
    public class JavaCallback {
        public void addPoint(double latitude, double longitude) {
            // Handle the received coordinates
            currentCoords.add(new Pair<>(latitude, longitude));
            System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
        }
    }

    @FXML
    private void onResetMapButton() {
        webView.getEngine().load(getClass().getResource("maps.html").toExternalForm());
        currentCoords.clear();
    }
}