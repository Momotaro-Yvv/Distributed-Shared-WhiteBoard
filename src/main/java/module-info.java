module com.example.distributedsharedwhiteboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires json.simple;
    requires org.json;


    opens com.example.distributedsharedwhiteboard to javafx.fxml;
    exports com.example.distributedsharedwhiteboard;
    exports com.example.distributedsharedwhiteboard.server;
    opens com.example.distributedsharedwhiteboard.server to javafx.fxml;
    exports com.example.distributedsharedwhiteboard.client;
    opens com.example.distributedsharedwhiteboard.client to javafx.fxml;
    exports com.example.distributedsharedwhiteboard.Application;
    opens com.example.distributedsharedwhiteboard.Application to javafx.fxml;
    exports com.example.distributedsharedwhiteboard.Util;
    opens com.example.distributedsharedwhiteboard.Util to javafx.fxml;
}