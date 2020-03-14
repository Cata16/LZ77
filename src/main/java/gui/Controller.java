package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import lz77.Lz;

import java.io.File;
import java.io.IOException;

public class Controller {
    private Lz lz = new Lz(15, 6);
    @FXML
    private Label outputFileLabel;
    @FXML
    private Label inputFileLabel;
    @FXML
    private Label stateLabel;
    @FXML
    private TextField messageField;

    public void encode(ActionEvent actionEvent) {
        final int[] numberOfBytes = new int[1];
        System.out.println(messageField.getText());
        Thread t = new Thread() {
            public void run() {
                try {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stateLabel.setText("Encoding .. encoding..encoding");
                        }
                    });
                    numberOfBytes[0] = lz.hideMessage(inputFileLabel.getText(), outputFileLabel.getText(), messageField.getText());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            messageField.setText("");
                            if (numberOfBytes[0] == 0) {


                                stateLabel.setText("Done");
                            } else {
                                stateLabel.setText("Nu au putut fi codificati :" + numberOfBytes[0]);
                            }
                        }
                    });

                } catch (IOException e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setHeaderText("Error");
                            errorAlert.setContentText("Eroare la incarcarea fisierelor");
                            errorAlert.showAndWait();
                        }
                    });

                }

            }
        };

        t.start();


    }

    public void uploadOutputFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectFile = fileChooser.showOpenDialog(null);
        if (selectFile != null) {
            outputFileLabel.setText(selectFile.getPath());
        }

    }

    public void decode(ActionEvent actionEvent) {
        final String[] message = new String[1];
        Thread t = new Thread() {
            public void run() {
                try {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stateLabel.setText("Decoding .. decoding..decoding");
                        }
                    });
                    message[0] = lz.getHiddenMessage(inputFileLabel.getText(), outputFileLabel.getText());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stateLabel.setText(message[0]);
                        }
                    });

                } catch (IOException e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setHeaderText("Error");
                            errorAlert.setContentText("Eroare la incarcarea fisierelor");
                            errorAlert.showAndWait();
                        }
                    });

                }

            }
        };

        t.start();
    }

    public void uploadInputFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectFile = fileChooser.showOpenDialog(null);
        if (selectFile != null) {
            inputFileLabel.setText(selectFile.getPath());
        }
    }
}
