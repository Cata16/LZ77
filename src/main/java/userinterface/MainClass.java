package userinterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

public class MainClass extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(@NotNull Stage primaryStage) throws IOException {
        URL resource = MainClass.class.getResource("/fxml/lzGUI.fxml");
        AnchorPane root = FXMLLoader.load(resource);
        primaryStage.setTitle("LZ77");
        primaryStage.setScene(new Scene(root, 537, 550));
        primaryStage.show();
    }
}
