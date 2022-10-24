module com.example.distributedsharedwhiteboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires json.simple;
    requires org.json;


    opens DistributedSharedWhiteboard to javafx.fxml;
    exports DistributedSharedWhiteboard;
    exports DistributedSharedWhiteboard.server;
    opens DistributedSharedWhiteboard.server to javafx.fxml;
    exports DistributedSharedWhiteboard.client;
    opens DistributedSharedWhiteboard.client to javafx.fxml;
    exports DistributedSharedWhiteboard.Application;
    opens DistributedSharedWhiteboard.Application to javafx.fxml;
    exports DistributedSharedWhiteboard.Util;
    opens DistributedSharedWhiteboard.Util to javafx.fxml;
    exports DistributedSharedWhiteboard.ShapeDrawing;
    opens DistributedSharedWhiteboard.ShapeDrawing to javafx.fxml;
}