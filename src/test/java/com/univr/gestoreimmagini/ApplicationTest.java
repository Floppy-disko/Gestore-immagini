package com.univr.gestoreimmagini;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.service.query.NodeQuery;
import org.testfx.service.query.impl.NodeQueryImpl;
import org.testfx.util.NodeQueryUtils;

import java.io.IOException;

// usare testfx non funziona con i moduli di java, ho risolto mettendo una riga nel pom.xml
@ExtendWith(ApplicationExtension.class)
class ApplicationTest {

    @Start
    public void Start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1020, 730);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
//    @Test
//    void should_contain_button_with_text(FxRobot robot) {
//        // or (lookup by css id):
//        FxAssert.verifyThat("#helloButton", LabeledMatchers.hasText("ciao"));
//        // or (lookup by css class):
//        FxAssert.verifyThat(".button", LabeledMatchers.hasText("ciao"));
//    }

    final String tagTextFieldID = "#tagTextField";
    @Test
    void testAddTag(FxRobot robot) {
        robot.clickOn(tagTextFieldID); //posso cercare tramite fx:id
        robot.write("Elemento");
        robot.clickOn("Aggiungi"); //o tramite il la proprietà text

    }

    @BeforeAll
    static void setup() {
        System.out.println("@BeforeAll - executes once before all test methods in this class");
    }

    @BeforeEach
    void init() {
        System.out.println("@BeforeEach - executes before each test method in this class");
    }


    @DisplayName("Single test successful")
    @Test
    void testSingleSuccessTest() {
        System.out.println("Success");
    }

    @Test
    @Disabled("Not implemented yet")
    void testShowSomething() {
    }

    @AfterEach
    void tearDown() {
        System.out.println("@AfterEach - executed after each test method.");
    }

    @AfterAll
    static void done() {
        System.out.println("@AfterAll - executed after all test methods.");
    }

}