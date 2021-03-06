module mediaplayer {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires junit;

    opens com.javafx.mediaplayer to javafx.fxml;
    exports com.javafx.mediaplayer;
    exports com.javafx.tests;
    opens com.javafx.tests to javafx.fxml;
}