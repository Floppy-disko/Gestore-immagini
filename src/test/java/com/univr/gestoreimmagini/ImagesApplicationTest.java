package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.ContenitoreTag;
import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// usare testfx non funziona con i moduli di java, ho risolto mettendo una riga nel pom.xml
@ExtendWith(ApplicationExtension.class)
class ImagesApplicationTest {

    @Start
    public void Start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ImagesApplication.class.getResource("ImageManagerView.fxml"));
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

    @Test
    void testAddTag(FxRobot robot) {
        Model modello = Model.getModel();
        ContenitoreTag tags = modello.getTags();

        robot.interact(()->{ //per modificare la scena devo per forza passare l'azione a robot.interact
            tags.removeRisorsa("Elemento"); //elimino "Elemento" (se esiste già) prima di aggiungerlo
        });
        robot.clickOn("#tagTextField"); //posso cercare tramite fx:id
        robot.write("Elemento");
        robot.clickOn("Aggiungi"); //o tramite il la proprietà text
        ObservableList<Tag> itemsList = ((ListView)robot.lookup("#tagsList").query()).getItems(); //potrei controllare da modello ma preferisco controllare dalla view per essere sicuro che siano mostrati

        assertTrue(() -> {  //controllo che ora la view mostri l'item "Elemento"
            for(Tag t: itemsList)
                if(t.toString().equals("Elemento"))
                    return true;
            return false;
        });

    }

    @Test
    void testRemoveTag(FxRobot robot){
        Model modello = Model.getModel();
        ContenitoreTag tags = modello.getTags();

        robot.interact(()->{ //per modificare la scena devo per forza passare l'azione a robot.interact
            tags.addRisorsa("Elemento2"); //elimino "Elemento" (se esiste già) prima di aggiungerlo
        });

        robot.clickOn("#Elemento2Button"); //clicco sul bottone che elimina l'elemento della lista

        ObservableList<Tag> itemsList = ((ListView)robot.lookup("#tagsList").query()).getItems();
        assertTrue(() -> {  //controllo che ora la view mostri l'item "Elemento"
            for(Tag t: itemsList)
                if(t.toString().equals("Elemento2"))
                    return false;
            return true;
        });
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