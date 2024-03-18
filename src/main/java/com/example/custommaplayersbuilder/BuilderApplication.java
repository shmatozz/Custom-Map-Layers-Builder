package com.example.custommaplayersbuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class BuilderApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the root node from the FXML file
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("builder-view.fxml")));

        // Create a scene
        Scene scene = new Scene(root);

        // Set the scene for primaryStage
        primaryStage.setScene(scene);

        // Set the window title
        primaryStage.setTitle("Custom Map Layers Builder");
        primaryStage.setMaximized(true);

        // Show primaryStage
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
