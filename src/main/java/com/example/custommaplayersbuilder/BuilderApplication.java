package com.example.custommaplayersbuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class BuilderApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        /* Load the root node from the FXML file */
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("builder-view.fxml")));

        /* Create a scene */
        Scene scene = new Scene(root);

        /* Set the scene for primaryStage */
        primaryStage.setScene(scene);

        /* Set window title */
        primaryStage.setTitle("Custom Map Layers Builder");

        /* Set window properties */
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/main.png"))));

        /* Show stage (open app) */
        primaryStage.show();
    }

    public static void main(String[] args) {
        /* Launch the JavaFX application */
        launch(args);
    }
}
