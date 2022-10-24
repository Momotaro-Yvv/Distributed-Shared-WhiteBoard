package DistributedSharedWhiteboard.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Application for manager
 */
public class ManagerApplication extends Application {

    static private Manager manager;

    @Override
    public void start(Stage stage) throws IOException {

        // load resources for starting application
        FXMLLoader fxmlLoader = new FXMLLoader(ManagerApplication.class.getResource("manager.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ManagerApplication.class.getResource("styleSheet.css")).toString());
        Image icon = new Image("icon.png");

        // change window attributes
        stage.getIcons().add(icon);
        stage.setTitle("WhiteBoard - Manager Window");
        stage.setScene(scene);
        stage.setResizable(false);

        // display the window
        stage.show();

        // create custom method for handling user exit
        managerController controller = (managerController) fxmlLoader.getController();
        controller.setStage(stage);

        stage.setOnCloseRequest(e -> {
            e.consume();
            controller.handleQuit(null);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setManager(Manager manager) {
        System.out.println("New Manager set up.");
        ManagerApplication.manager = manager;
    }

    public static Manager getManager() {
        return manager;
    }
}