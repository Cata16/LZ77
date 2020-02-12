package userinterface;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import lz77.Lz;

import java.io.File;
import java.io.IOException;

public class Controller {

    Lz lz = new Lz();
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
        File inputFile = fileChooser.showOpenDialog(null);
        if (inputFile != null) {
            inputFileLabel.setText(inputFile.getName());
        }
    }

    public void uploadOutputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT Files", "*.txt"));
        File outputFile = fileChooser.showOpenDialog(null);
        if (outputFile != null) {
            outputFileLabel.setText(outputFile.getName());
        }
    }

    public void encode() {
        if (checkFiles()) {
            return;
        }
        stateLabel.setText("Encoding ");
        try {

            lz.encode(inputFileLabel.getText(), outputFileLabel.getText());
            stateLabel.setText("Done encoding" + "\n" + lz.getSpaceSaved(inputFileLabel.getText(), outputFileLabel.getText()));

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

            lz.decode(inputFileLabel.getText(), outputFileLabel.getText());
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
        if ((inputFileLabel.getText() == null) || (outputFileLabel.getText() == null)) {
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