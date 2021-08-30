package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Maps {
    @FXML
    ScrollPane sp;
    @FXML
    Button nm;
    @FXML
    Button st;
    @FXML
    VBox vb;
    @FXML
    ListView<String> lv;
    String selectedMap;

    public Maps() throws FileNotFoundException {

        sp = new ScrollPane();
        nm = new Button();
        st = new Button();
        lv = new ListView<>();
        selectedMap = new String();
        vb = new VBox();
    }

    public void initialize() throws FileNotFoundException {
        try {
            ArrayList<String> maps = new ArrayList<>();
            ;
            File file = new File("src/sample/Maps/Maps.txt");
            Scanner sc = new Scanner(file);

            while (sc.hasNext()) {
                String s = new String();
                s = sc.next();
                maps.add(s);
            }
            lv.getItems().setAll(maps);
            sc.close();
        } catch (Exception FileNotFound) {
        }
        lv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1 != null)
                    selectedMap = t1;
            }
        });
    }

    @FXML
    public void start() {
        try {
            File file = new File("src/sample/Maps/" + selectedMap + ".txt");
            Scanner sc = new Scanner(file);
            Settings.setGroundSize(sc.nextInt());
            Settings.setPaneWidth(sc.nextInt());
            int basex = sc.nextInt();
            int basey = sc.nextInt();
            String base =sc.next();
            Settings.set_base_x(basex);
            Settings.set_base_y(basey);
            Game.getInstance().setUnit( new Unit(basex, basey, SubType.getType(base), null, null));
            while (sc.hasNext()) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                String s = sc.next();
                Unit u = new Unit(x, y, SubType.getType(s), null, null);
                Game.getInstance().setUnit(u);
            }
            GameEditor.getInstance().startGame();
                Stage stage = Main.getPrimaryStage();
                Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
                stage.setTitle("Game");
                stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception FileNotFound) {
        }
    }

    @FXML
    public void newMap() throws IOException {
        Stage stage = Main.getPrimaryStage();
        Parent root = FXMLLoader.load(getClass().getResource("CreateMap.fxml"));
        stage.setTitle("Createmap");
        stage.setScene(new Scene(root, 1280, 720));
    }
}





