package sample;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CreateMap {

    @FXML
    GridPane grid = new GridPane();
    @FXML
    ScrollPane scroller = new ScrollPane();
    @FXML
    Button valley = new Button();
    @FXML
    Button bridge = new Button();
    @FXML
    Button river = new Button();
    @FXML
    TextField MAPNAME;
    @FXML
    private SubType activeType = SubType.delete;
    @FXML
    private static double zoom = Settings.getGroundSize();

    public static double getZoom() {
        return zoom;
    }

    UnitsInRange uir;
    ArrayList<Unit> units;
    String mapname;

    public void initialize() {
        units = new ArrayList<Unit>();
        animator.start();
        uir = new UnitsInRange(0, new MyRectangle(0, 0, Settings.getGroundSize(), Settings.getGroundSize()));
        int base_X = Settings.get_base_x();
        int base_Y = Settings.get_base_y();
        Unit base = new Unit(base_X, base_Y, SubType.MainBase, null, null);
        units.add(base);
        scroller.setMaxWidth(Settings.getPaneWidth());
        scroller.setMaxHeight(Settings.getPaneWidth());
        zoom = Settings.getPaneWidth();
        grid.setPrefWidth(zoom);
        grid.setPrefHeight(zoom);
        grid.getChildren().add(base);
        grid.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.MIDDLE) {
                scroller.setPannable(true);
            } else if (e.getButton() == MouseButton.PRIMARY) {
                scroller.setPannable(false);
                int x = (int) (e.getX() * Settings.getGroundSize() / getZoom());
                int y = (int) (e.getY() * Settings.getGroundSize() / getZoom());
                if (activeType.equals(SubType.delete)) {
                    if (getUnit(x, y) != null) {
                        grid.getChildren().remove(getUnit(x, y));
                        try {
                            units.remove(getUnit(x, y));
                        } catch (NullPointerException ex) {
                        }
                    }
                } else {
                    if (canAdd(x, y, activeType)) {
                        Unit u = new Unit(x, y, activeType, null, null);
                        grid.getChildren().add(u);
                        units.add(u);
                    }
                }
            }
        });
    }

    AnimationTimer animator = new AnimationTimer() {
        public void handle(long now) {
            setUnits();
            editUnitsInRange();
        }
    };

    private synchronized void setUnits() {
        for (Node node : grid.getChildren()) {
            if (node instanceof Unit)
                ((Unit) node).set_CreateLocation();
        }
    }

    public synchronized void editUnitsInRange() {
        if (uir != null) {
            uir.clear();
//            uir = null;
        }
//        uir = new UnitsInRange(0, new MyRectangle(0, 0, Settings.getGroundSize(), Settings.getGroundSize()));

        for (Unit u : units) {
            uir.insert(u);
        }
    }

    public synchronized Unit getUnit(int x, int y) {
        MyRectangle r = new MyRectangle(x, y, 1, 1);
        ArrayList<Unit> a = new ArrayList<Unit>();
        ArrayList<Unit> b = new ArrayList<>();
        b = uir.canAdd(a, r);
        if (b.size() != 0) {
            return b.get(0);
        }
        return null;
    }

    public synchronized boolean canAdd(int x, int y, SubType type) {
        String path = null;
        path = "src/sample/TilesMap/" + type.toString() + ".txt";
        int w = 0, h = 0;
        try {
            File file = new File(path);
            Scanner sc = new Scanner(file);
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            w = h = Integer.parseInt(sc.next());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (x + w - 1 >= Settings.getGroundSize() || y + h - 1 >= Settings.getGroundSize())
            return false;
        MyRectangle r = new MyRectangle(x, y, w, h);
        ArrayList<Unit> a = new ArrayList<Unit>();
        if (uir.canAdd(a, r).size() != 0) {
            if (type != SubType.bridge && type != SubType.valley)
                a.removeIf(un -> un.getName() == SubType.river);
            else if (type != SubType.river && type != SubType.valley)
                a.removeIf(un -> un.getName() == SubType.bridge);
            if (a.size() != 0)
                return false;
            return true;
        }
        return true;
    }

    @FXML
    public void create() throws IOException {
        mapname = MAPNAME.getText();
        String path = "src/sample/Maps/" + mapname + ".txt";
        File f = new File("src/sample/Maps/Maps.txt");
        ArrayList<String> as = new ArrayList<>();
        Scanner sc = new Scanner(f);
        while ((sc.hasNext())) {
            as.add(sc.next());
        }
        for (String s : as)
            if (s.equals(mapname))
                return;
        as.clear();
        sc.close();
        FileWriter fw = new FileWriter(path, true);
        PrintWriter pw = new PrintWriter(fw);
        pw.println(Settings.getGroundSize());
        pw.println(Settings.getPaneWidth());
        for (Unit u : units) {
            pw.println(u.getx());
            pw.println(u.gety());
            pw.println(u.getName().toString());
        }
        fw.close();
        units = new ArrayList<Unit>();
        try {
            f = new File("src/sample/Maps/Maps.txt");
            as = new ArrayList<>();
            sc = new Scanner(f);
            while ((sc.hasNext())) {
                as.add(sc.next());
            }
            as.add(mapname);
            sc.close();
            f = new File(("src/sample/Maps/Maps.txt"));
            PrintWriter fww = new PrintWriter(f);
            for (String s : as)
                fww.println(s);
            fww.close();
        } catch (Exception e) {
        }
        Stage stage = Main.getPrimaryStage();
        Parent root = FXMLLoader.load(getClass().getResource("Maps.fxml"));
        stage.setTitle("ChooseMap");
        stage.setScene(new Scene(root, 1280, 720));
    }

    @FXML
    private void setType(ActionEvent actionEvent) {
        activeType = SubType.getType(((Button) actionEvent.getSource()).getId());
    }

    @FXML
    private TextField goToX, goToY;

    @FXML
    private void goToXY() {
        int x = Integer.parseInt(goToX.getText());
        int y = Integer.parseInt(goToY.getText());
        Unit unit = getUnit(x, y);
        zoom = (Settings.getGroundSize() * (Settings.getPaneWidth() / unit.getwidth() / 2));
        grid.setPrefHeight(zoom);
        grid.setPrefWidth(zoom);
        scroller.setHvalue((unit.getX() + unit.getWidth() / 2) / zoom);
        scroller.setVvalue((unit.getY() + unit.getHeight() / 2) / zoom);
    }

    @FXML
    private void moveBorder(ActionEvent actionEvent) {
        String pressed = ((Button) actionEvent.getSource()).getId();
        if (pressed.equals("out")) {
            if (zoom / 2 > scroller.getHeight()) {
                double preX = scroller.getHvalue(), preY = scroller.getVvalue();
                zoom /= 2;
                grid.setPrefHeight(zoom);
                grid.setPrefWidth(zoom);
                scroller.setHvalue(preX / 2);
                scroller.setVvalue(preY / 2);
            }
        } else if (pressed.equals("in")) {
            if (zoom <= 16384000) {
                double preX = scroller.getHvalue(), preY = scroller.getVvalue();
                zoom *= 2;
                grid.setPrefHeight(zoom);
                grid.setPrefWidth(zoom);
                scroller.setHvalue(preX * 2);
                scroller.setVvalue(preY * 2);
            }
        }
    }
}
