package com.example.distributedsharedwhiteboard.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class UserApplication extends Application {


    // shape lists
//    private ObservableList<Shape> outList = FXCollections.observableArrayList();
//    private ObservableList<String> inputList = FXCollections.observableArrayList();


    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserApplication.class.getResource("hello-view_user.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(UserApplication.class.getResource("styleSheet.css")).toString());
        Image icon = new Image("icon.png");
        stage.getIcons().add(icon);
        stage.setTitle("WhiteBoard - User Window");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}