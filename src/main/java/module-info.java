module com.example.distributedsharedwhiteboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires json.simple;


    opens com.example.distributedsharedwhiteboard to javafx.fxml;
    exports com.example.distributedsharedwhiteboard;
    exports com.example.distributedsharedwhiteboard.server;
    opens com.example.distributedsharedwhiteboard.server to javafx.fxml;
    exports com.example.distributedsharedwhiteboard.client;
    opens com.example.distributedsharedwhiteboard.client to javafx.fxml;
    exports com.example.distributedsharedwhiteboard.Application;
    opens com.example.distributedsharedwhiteboard.Application to javafx.fxml;
}