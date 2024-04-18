module org.quijava.quijava {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.quijava.quijava to javafx.fxml;
    exports org.quijava.quijava;
}