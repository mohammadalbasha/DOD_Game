package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenu {
    @FXML
    private void newGame() throws IOException {
        Settings.loadSettings();
        Stage stage = Main.getPrimaryStage();
        Parent root = FXMLLoader.load(getClass().getResource("Team.fxml"));
        stage.setTitle("TeamGame");
        stage.setScene(new Scene(root, 1280 ,720));
    }
    @FXML
    private void loadSettings() throws IOException {
        Stage stage = Main.getPrimaryStage();
        Parent root = FXMLLoader.load(getClass().getResource("settings.fxml"));
        stage.setTitle("Settings");
        stage.setScene(new Scene(root, 1280 ,720));
    }
}
