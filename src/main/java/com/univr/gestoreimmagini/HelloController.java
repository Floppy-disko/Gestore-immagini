package com.univr.gestoreimmagini;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}