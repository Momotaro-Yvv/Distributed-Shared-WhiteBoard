module com.example.distributedsharedwhiteboard {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.distributedsharedwhiteboard to javafx.fxml;
    exports com.example.distributedsharedwhiteboard;
}