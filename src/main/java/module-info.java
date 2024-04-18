module org.quijava.quijava {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.quijava.views to javafx.fxml;
    exports org.quijava.views;
    exports org.quijava.controllers;
    opens org.quijava.controllers to javafx.fxml;
}