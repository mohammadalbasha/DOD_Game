package sample;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;

import java.io.IOException;

public class TeamSetting {
    @FXML
    Spinner<Integer> AttTeam = new Spinner<>();
    @FXML
    Spinner<Integer> DefTeam = new Spinner<>();
    @FXML
    Button create = new Button();
    public void initialize(){
        create.setDisable(true);
        create.setStyle("-fx-background-color: #16697a ");
        create.setText("Start");
        animator1.start();
    }
    AnimationTimer animator1 = new AnimationTimer() {
        public void handle(long now) {
            if (AttTeam.getValue() == 0 || DefTeam.getValue() == 0)
                create.setDisable(true);
            else create.setDisable(false);
        }
    };
    @FXML
    public void setTeam() throws IOException {
        int attPlayers = AttTeam.getValue();
        int defPlayers = DefTeam.getValue();
        Game.getInstance().setTeams(attPlayers, defPlayers);
        Stage stage = Main.getPrimaryStage();
        Parent root = FXMLLoader.load(getClass().getResource("Maps.fxml"));
        stage.setTitle("ChooseMap");
        stage.setScene(new Scene(root, 1280, 720));
    }
}
