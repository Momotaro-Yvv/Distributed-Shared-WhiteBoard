package com.example.distributedsharedwhiteboard.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class ManagerApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ManagerApplication.class.getResource("hello-view_manager.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ManagerApplication.class.getResource("styleSheet.css")).toString());
        Image icon = new Image("icon.png");
        stage.getIcons().add(icon);
        stage.setTitle("WhiteBoard - Manager Window");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
