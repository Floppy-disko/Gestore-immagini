module com.univr.gestoreimmagini {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.commons.io;


    opens com.univr.gestoreimmagini to javafx.fxml;
    opens com.univr.gestoreimmagini.modello to javafx.fxml;
    exports com.univr.gestoreimmagini;
    exports com.univr.gestoreimmagini.modello;
    exports com.univr.gestoreimmagini.controllori;
    opens com.univr.gestoreimmagini.controllori to javafx.fxml;
    exports com.univr.gestoreimmagini.custom;
    opens com.univr.gestoreimmagini.custom to javafx.fxml;
}