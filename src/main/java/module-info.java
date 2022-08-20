module com.univr.gestoreimmagini {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.univr.gestoreimmagini to javafx.fxml;
    exports com.univr.gestoreimmagini;
}