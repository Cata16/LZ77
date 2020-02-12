package main.java.userinterface;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import main.java.com.lz77.Lz;

import java.io.File;
import java.io.IOException;

public class Controller {

    Lz lz = new Lz();
    File inputFile;
    File outputFile;
    Alert alert;
    @FXML
    private Label inputFileLabel;
    @FXML
    private Label outputFileLabel;
    @FXML
    private Label stateLabel;

    public void uploadInputFile() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT Files", "*.txt"));
        inputFile = fileChooser.showOpenDialog(null);
        inputFileLabel.setText(inputFile.getName());

    }

    public void uploadOutputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT Files", "*.txt"));
        outputFile = fileChooser.showOpenDialog(null);
        outputFileLabel.setText(outputFile.getName());

    }

    public void encode() {
        if (checkFiles()) {
            return;
        }
        stateLabel.setText("Encoding ");
        try {

            lz.encode(inputFile, outputFile);
            stateLabel.setText("Done encoding" + "\n" + lz.getSpaceSaved(inputFile, outputFile));

        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("A aparut o eroare!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

    }

    public void decode() {
        if (checkFiles()) {
            return;
        }
        stateLabel.setText("Decoding ");
        try {

            lz.decode(inputFile, outputFile);
            stateLabel.setText("Done decoding");

        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("A aparut o eroare!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private boolean checkFiles() {
        if ((inputFile == null) || (outputFile == null)) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("A aparut o eroare!");
            alert.setContentText("Fisierele nu au fost incarcate corespunzator");
            alert.showAndWait();
            return true;


        }
        return false;
    }


}
