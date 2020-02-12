package main.java.userinterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainClass extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("lzGUI.fxml"));
        primaryStage.setTitle("LZ77");
        primaryStage.setScene(new Scene(root, 537, 550));
        primaryStage.show();
    }
}
