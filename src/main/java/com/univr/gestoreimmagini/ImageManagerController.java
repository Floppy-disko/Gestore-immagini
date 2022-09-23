package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.AnnotatedImage;
import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageManagerController implements Initializable, AutoCloseable {

    @FXML
    private ListView<Tag> tagsList;

    @FXML
    private TextField tagTextField;

    @FXML
    private TextField imageTextField;

    @FXML
    private ImageView placedImage;

    @FXML
    private TilePane imageGrid;

    @FXML
    private StackPane imageDnD;

    @FXML
    private Button browse;

    private Model modello = Model.getModel();

    private boolean placedImageSet=false;

    private String placedImageExtension;

    private int selectedImageIndex;

    FileChooser fc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {  //lancia all'avvio
        tagsList.setItems(modello.getTags().getRisorse()); //linka la lista di tag nella view alla lista di tag nel modello
        //modello.getTags().addRisorsa(new Tag("Test"));
        tagsList.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {  //faccio celle della lista custom, la loro composizione è nella classe CustomCell
            @Override
            public ListCell<Tag> call(ListView<Tag> listView) {
                TagListCell cell = new TagListCell();
                cell.setButtonOnAction((actionEvent)->removeTag(cell.getItem())); //quando premo il bottone elimina l'item della cella
                return cell;
            }
        });

        modello.getImages().getRisorse().addListener((ListChangeListener<AnnotatedImage>) c -> {  //binding tra la lista di immagini nel modello e i figli di ImageGrid
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        //permutate
                    }
                } else if (c.wasUpdated()) {
                    //update item
                } else {
                    for (AnnotatedImage remitem : c.getRemoved()) {
                        imageGrid.getChildren().remove(imageGrid.lookup("#" + remitem.getName() + "Image"));
                    }
                    for (AnnotatedImage additem : c.getAddedSubList()) {
                        displayImage(additem);
                    }
                }
            }
        });

        fc = new FileChooser();
        FileChooser.ExtensionFilter jpg = new FileChooser.ExtensionFilter("jpg", "*.jpg");
        FileChooser.ExtensionFilter jpeg = new FileChooser.ExtensionFilter("jpeg", "*.jpeg");
        FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter("png", "*.png");
        fc.getExtensionFilters().addAll(png,jpg,jpeg);

        populateLists();
    }

    private void populateLists() {
        modello.getTags().populateList("tags");
        modello.getImages().populateList("images");
    }

    private void displayImage(AnnotatedImage annotatedImage){
        ImageBox imageBox = new ImageBox();
        imageBox.setDisplayedImage(annotatedImage.getImage());
        imageBox.setNameLabelText(annotatedImage.getName());
        imageBox.setRemoveButtonOnAction((actionEvent) -> {
            modello.getImages().removeRisorsa(annotatedImage); //sarebbe il metodo removeImage
        });
        imageBox.setImageOnClick((mouseEvent) -> {
            selectedImageIndex = modello.getImages().getRisorse().indexOf(annotatedImage);
            switchToWorkingImageView(mouseEvent);
        });
        imageBox.setId(annotatedImage.getName() + "Image");
        imageGrid.getChildren().add(imageBox);
    }

    @FXML
    private void keyListener(KeyEvent keyEvent){
        if(keyEvent.getCode() == KeyCode.ENTER) {
            if(tagTextField.equals(keyEvent.getSource()))
                addTag(keyEvent);

            else if (imageTextField.equals(keyEvent.getSource())) {
                addImage(keyEvent);
            }

            keyEvent.consume();
        }
    }

    @FXML
    private void addTag(Event event) {
        String nome = tagTextField.getText();

        if(nameIsInvalid(nome, "tag")){
            return;
        }

        modello.getTags().addRisorsa(nome);  //aggiungo il valore del textfield alla lista di tag nel modello
        tagTextField.clear();
    }

    private boolean nameIsInvalid(String name, String resourceType){
        Pattern p = Pattern.compile("[^A-Za-z0-9-_]");
        Matcher m = p.matcher(name);

        if(modello.getTags().nomeInLista(name)) {  //Non puoi aggiungere due tag uguali
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(String.format("Errore nome %s", resourceType));
            alert.setContentText(String.format("Non puoi aggiungere più %s con lo stesso nome", resourceType));
            alert.showAndWait();
        } else if(name==null || name.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(String.format("Errore nome %s", resourceType));
            alert.setContentText(String.format("Non puoi aggiungere %s con nome vuoto", resourceType));
            alert.showAndWait();
        } else if(m.find()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(String.format("Errore nome %s", resourceType));
            alert.setContentText(String.format("Non puoi utilizzare il carattere: '%c'", name.charAt(m.end()-1)));  //Scrivo che carattere ha scatenato l'errore
            alert.showAndWait();
        } else if(name.length()>20){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(String.format("Errore nome %s", resourceType));
            alert.setContentText(String.format("I %s possono essere di massimo 20 caratteri (ne hai usati %d)", resourceType, name.length()));
            alert.showAndWait();
        } else
            return false; //se invece il nome è valido

        return true;
    }

    private void removeTag(Tag t){
        modello.getTags().removeRisorsa(t);
    }

    @FXML
    private void acceptImage(DragEvent event){
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }

    @FXML
    private void placeImage(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        setImage(files.get(0).getPath());
    }

    private void setImage(String path){

        placedImageExtension = FilenameUtils.getExtension(path);

        if(!placedImageExtension.matches("(?i)jpg|jpeg|png"))
            return;

        System.out.printf("\nGot %s file", placedImageExtension);

        Image image = null;
        try(FileInputStream stream = new FileInputStream(path)) {
            image = new Image(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(FilenameUtils.getExtension(image.getUrl()));
        placedImage.setImage(image);
        placedImageSet=true;
        imageDnD.getStyleClass().remove("imgDnD");  // Rimuove l'immagine di sfondo rimuovendo la classe che gliela assegna
    }

    @FXML
    private void addImage(Event event){
        //System.out.println(imageDnD.getImage());
        //getClass().getClassLoader().getResourceAsStream("Simo.jpg");
        if(placedImageSet==false)  //Salvo solo se l'immagine è stata settata
            return;

        String name = imageTextField.getText();

        if(nameIsInvalid(name, "immagini"))  //Non puoi asseganare lo stesso nome a due immagini diverse
            return;

        modello.getImages().addRisorsa(placedImage.getImage(), name, placedImageExtension);

        imageTextField.clear();

        placedImageSet=false; //Comunico che non ho più immagini settate
        placedImage.setImage(null);
        imageDnD.getStyleClass().add("imgDnD");     // Riassegna la classe per lo sfondo dell'immagine
        imageTextField.getParent().requestFocus();  // Sposta il focus dal text field
    }

    private void switchToWorkingImageView(MouseEvent mouseEvent) {

        Parent root = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("WorkingImageView.fxml"));
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WorkingImageController controller = loader.getController();
        controller.setSelectedImageIndex(selectedImageIndex); //Comunico al secondo controller che immagine è stata selezionata

        Scene scene = new Scene(root, 1280, 900);
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.setTitle("Working Image");
        stage.setScene(scene);
        stage.show();

        if(modello.getImages().resourceFileExists(selectedImageIndex) == false)
            modello.getImages().restoreImage(selectedImageIndex);

        System.out.println(selectedImageIndex + String.valueOf(modello.getImages().resourceFileExists(selectedImageIndex)));


        try {
            close();
        } catch (Exception e) {
            System.err.println("Non è stato possibile chiudere il Controller");
        }
    }

    @Override
    public void close() throws Exception {
        System.out.println("close");
        modello.updateMemory();
    }

    @FXML
    private void browseFiles(Event event) {
        File file = fc.showOpenDialog(imageTextField.getScene().getWindow());

        if(file!=null)
            setImage(file.getPath());
    }
}