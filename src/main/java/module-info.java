module com.univr.gestoreimmagini {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.univr.gestoreimmagini to javafx.fxml;
    exports com.univr.gestoreimmagini;
    exports com.univr.gestoreimmagini.modello;
    opens com.univr.gestoreimmagini.modello to javafx.fxml;
}