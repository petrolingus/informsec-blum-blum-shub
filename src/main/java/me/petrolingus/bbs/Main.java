package me.petrolingus.bbs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/root.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Blum Blum Shub");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
