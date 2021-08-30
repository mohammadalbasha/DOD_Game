package sample;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import sample.Tactics.AttackTactic;
import sample.Tactics.HighestDamageAttack;
import sample.Tactics.LowestHealthAttack;
import sample.Tactics.RandomAttack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    @FXML
    Pane grid = new Pane();
    @FXML
    ScrollPane scroller = new ScrollPane();
    @FXML
    Label playerNum = new Label();
    @FXML
    Label coins = new Label();
    /*Zoom Stuff*/
    /*************************************************************/
    private static double zoom = Settings.getGroundSize();

    public static double getZoom() {
        return zoom;
    }

    /*************************************************************/
    private long constantStartTime = System.currentTimeMillis();
    private static long preGameTime = 0;
    private static long runGameTime = 0;
    private static long startTime = System.currentTimeMillis();
    private String current_player_team;
    private int current_player_index;
    private Rectangle visionDisplay = new Rectangle();
    private Rectangle attackDisplay = new Rectangle();
    private Unit selectedUnit = null;
    @FXML
    private Label currentUnit;
    @FXML
    private Label timeLeft;
    @FXML
    Menu store = new Menu();

    /*************************************************************/
    private SubType activeType = SubType.delete;
    private AttackTactic attackTactic = new RandomAttack();
    Thread preGameTimer = new Thread(new Runnable() {
        public void run() {
            setPregameDate();
            Menu unitsMenu = new Menu("Units");
            Menu tacticsMenu = new Menu("Tactics");
            store.getItems().addAll(unitsMenu, tacticsMenu);
            EventHandler<ActionEvent> tacticEvent = new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    setTactic(actionEvent);
                }
            };
            setTacticsMenu(tacticsMenu, tacticEvent);
            EventHandler<ActionEvent> unitevent = new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    setType(actionEvent);
                }
            };
            MenuItem BlackEagle = new MenuItem("BlackEagle");
            BlackEagle.setId("BlackEagle");
            BlackEagle.setOnAction(unitevent);
            setUnitsMenuForAttacker(unitsMenu, unitevent);
            unitsMenu.getItems().addAll(BlackEagle);
            waitForAttackers();
            activeType = SubType.delete;
            unitsMenu.getItems().remove(BlackEagle);
            setUnitsMenuForDefender(unitsMenu, unitevent);
            waitForDefenders();
            runGameTime = Settings.getRunTime();
            current_player_team = "";
        }
    });

    public void initialize() {
        initData();
        setMouseHandler();
    }

    AnimationTimer animator = new AnimationTimer() {
        public void handle(long now) {
            setCurrentPlayer();
            setCurrentUnit();
            setUnits();
            displayTime();
            refreshGrid();
            checkState();
        }
    };
    /**********************************************/

    /**********************************************/
    //Pregame timer functions
    private void setPregameDate() {
        startTime = System.currentTimeMillis();
    }

    private void setTacticsMenu(Menu tacticsMenu, EventHandler tacticevent) {
        MenuItem randomtacticitem = new MenuItem("Random");
        randomtacticitem.setId("random");
        randomtacticitem.setOnAction(tacticevent);
        MenuItem lowesthealthtacticitem = new MenuItem("lowes health");
        lowesthealthtacticitem.setId("lowest");
        lowesthealthtacticitem.setOnAction(tacticevent);
        MenuItem highestdamage = new MenuItem("highest damage");
        highestdamage.setId("highest");
        highestdamage.setOnAction(tacticevent);
        tacticsMenu.getItems().addAll(randomtacticitem, lowesthealthtacticitem, highestdamage);
    }

    private void setUnitsMenuForAttacker(Menu unitsMenu, EventHandler unitevent) {
        MenuItem Sniper = new MenuItem("Sniper");
        Sniper.setId("Sniper");
        Sniper.setOnAction(unitevent);
        MenuItem NavySeal = new MenuItem("NavySeal");
        NavySeal.setId("NavySeal");
        NavySeal.setOnAction(unitevent);
        MenuItem TeslaTank = new MenuItem("TeslaTank");
        TeslaTank.setId("TeslaTank");
        TeslaTank.setOnAction(unitevent);
        MenuItem MirageTank = new MenuItem("MirageTank");
        MirageTank.setId("MirageTank");
        MirageTank.setOnAction(unitevent);
        MenuItem Infantry = new MenuItem("Infantry");
        Infantry.setId("Infantry");
        Infantry.setOnAction(unitevent);
        MenuItem GrizzlyTank = new MenuItem("GrizzlyTank");
        GrizzlyTank.setId("GrizzlyTank");
        GrizzlyTank.setOnAction(unitevent);
        MenuItem TankDestroyer = new MenuItem("TankDestroyer");
        TankDestroyer.setId("TankDestroyer");
        TankDestroyer.setOnAction(unitevent);
        MenuItem PrismTank = new MenuItem("PrismTank");
        PrismTank.setId("PrismTank");
        PrismTank.setOnAction(unitevent);
        unitsMenu.getItems().addAll(Sniper, NavySeal, TeslaTank, MirageTank, Infantry, GrizzlyTank, TankDestroyer, PrismTank);
    }

    private void waitForAttackers() {
        for (int i = 0; i < Game.getInstance().getAttackerPlayer(); i++) {
            preGameTime += Settings.getSetTime();
            current_player_index = i;
            current_player_team = "Attacker";
            try {
                Thread.sleep(Settings.getSetTime() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUnitsMenuForDefender(Menu unitsMenu, EventHandler unitevent) {
        MenuItem Pillbox = new MenuItem("Pillbox");
        Pillbox.setId("Pillbox");
        Pillbox.setOnAction(unitevent);
        MenuItem PrismTower = new MenuItem("PrismTower");
        PrismTower.setId("PrismTower");
        PrismTower.setOnAction(unitevent);
        MenuItem GrandCannon = new MenuItem("GrandCannon");
        GrandCannon.setId("GrandCannon");
        GrandCannon.setOnAction(unitevent);
        MenuItem PatriotMissileSystem = new MenuItem("PatriotMissileSystem");
        PatriotMissileSystem.setId("PatriotMissileSystem");
        PatriotMissileSystem.setOnAction(unitevent);
        unitsMenu.getItems().addAll(Pillbox, PrismTower, GrandCannon, PatriotMissileSystem);
    }

    private void waitForDefenders() {
        for (int i = 0; i < Game.getInstance().getDefenderPlayer(); i++) {
            preGameTime += Settings.getSetTime();
            current_player_index = i;
            current_player_team = "Defender";
            try {
                Thread.sleep(Settings.getSetTime() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**********************************************/

    /**********************************************/
    //Initializer functions
    private void initData() {
        scroller.setMaxWidth(Settings.getPaneWidth());
        scroller.setMaxHeight(Settings.getPaneWidth());
        zoom = Settings.getPaneWidth();
        grid.setPrefWidth(zoom);
        grid.setPrefHeight(zoom);
        ArrayList<Unit> units = GameEditor.getInstance().getAllUnits();
        for (Unit u : units) {
            grid.getChildren().add(u);
        }
        animator.start();
        preGameTimer.start();
    }

    private void setMouseHandler() {
        grid.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.MIDDLE) {
                scroller.setPannable(true);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                scroller.setPannable(false);
                deselectUnit();
            } else if (e.getButton() == MouseButton.PRIMARY) {
                scroller.setPannable(false);
                int x = (int) (e.getX() * Settings.getGroundSize() / Controller.getZoom());
                int y = (int) (e.getY() * Settings.getGroundSize() / Controller.getZoom());
                if (GameEditor.getInstance().getGameState() == GameState.PreGame) {
                    if (activeType.equals(SubType.delete)) {
                        deleteUnit(x, y);
                    } else if (GameEditor.getInstance().canAdd(x, y, activeType)) {
                        addUnit(x, y);
                    }
                } else if (GameEditor.getInstance().getGameState() == GameState.Running || GameEditor.getInstance().getGameState() == GameState.Paused) {
                    if (((selectedUnit == null || GameEditor.getInstance().getUnit(x, y) != null))) {
                        selectUnit(x, y);
                    } else {
                        goToXY(x, y);
                    }
                }
            }
        });
    }

    /**************/
    //Mouse handler functions
    private void deselectUnit() {
        selectedUnit = null;
        grid.getChildren().remove(visionDisplay);
        visionDisplay = null;
        grid.getChildren().remove(attackDisplay);
        attackDisplay = null;
    }

    private void deleteUnit(int x, int y) {
        try {
            if (GameEditor.getInstance().getUnit(x, y) != null && GameEditor.getInstance().getUnit(x, y).getPlayer().equals(GameEditor.getInstance().getPlayer(current_player_team, current_player_index))) {
                grid.getChildren().remove(GameEditor.getInstance().getUnit(x, y));
                GameEditor.getInstance().sell(GameEditor.getInstance().getPlayer(current_player_team, current_player_index), GameEditor.getInstance().getUnit(x, y));
                try {
                    GameEditor.getInstance().getUnit(x, y).remove();
                } catch (NullPointerException ignored) {
                }
            }
        } catch (NullPointerException ignored) {

        }
    }

    private void addUnit(int x, int y) {
        {
            try {
                if (GameEditor.getInstance().canPlayerBuy(GameEditor.getInstance().getPlayer(current_player_team, current_player_index), activeType)) {
                    Unit unit = GameEditor.getInstance().buy(GameEditor.getInstance().getPlayer(current_player_team, current_player_index), x, y, activeType, attackTactic);
                    grid.getChildren().add(unit);
                    GameEditor.getInstance().setUnit(unit);
                }
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    }

    private void selectUnit(int x, int y) {
        if ((GameEditor.getInstance().getUnit(x, y) != null)) {
            if ((GameEditor.getInstance().getUnit(x, y).getType() != Type.Environment))
                selectedUnit = GameEditor.getInstance().getUnit(x, y);
        } else
            selectedUnit = GameEditor.getInstance().getUnit(x, y);

    }

    private void goToXY(int x, int y) {
        try {
            selectedUnit.gotoXY(x, y);
        } catch (NumberFormatException ignored) {

        }
    }
    /**************/

    /*********************************************/

    /*********************************************/
    //Animator functions
    private void displayTime() {
        if (GameEditor.getInstance().getGameState() == GameState.PreGame)
            timeLeft.setText("Time left : " + ((preGameTime - (System.currentTimeMillis() - startTime) / 1000) / 60) + ":" + ((preGameTime - (System.currentTimeMillis() - startTime) / 1000) % 60));
        if (GameEditor.getInstance().getGameState() == GameState.Running) {
            timeLeft.setText("Time left : " + ((runGameTime - (System.currentTimeMillis() - GameEditor.getInstance().getStartTime()) / 1000) / 60) + ":" + ((runGameTime - (System.currentTimeMillis() - GameEditor.getInstance().getStartTime()) / 1000) % 60));
        }
    }

    private void setUnits() {
        for (Node node : grid.getChildren()) {
            if (node instanceof Unit)
                ((Unit) node).setLocation();
        }
    }

    private void setCurrentPlayer() {
        if (GameEditor.getInstance().getGameState() == GameState.PreGame) {
            playerNum.setText(current_player_team + " " + (current_player_index + 1));
            coins.setText("Coins : " + GameEditor.getInstance().getPlayer(current_player_team, current_player_index).getCoins());
        } else {
            playerNum.setText("");
            coins.setText("");
        }
    }

    private void setVisionDisplay() {
        grid.getChildren().remove(visionDisplay);
        double scale = (Controller.getZoom() / Settings.getGroundSize());
        int vx = (int) Math.max(0, selectedUnit.getX() - selectedUnit.getVision() * scale);
        int vy = (int) Math.max(0, selectedUnit.getY() - selectedUnit.getVision() * scale);
        double width = selectedUnit.getwidth() + 2 * selectedUnit.getVision();
        int vw = (int) Math.min(width * scale, width * scale - (0 - (selectedUnit.getX() - selectedUnit.getVision() * scale)));
        double height = selectedUnit.getheight() + 2 * selectedUnit.getVision();
        int vh = (int) Math.min(height * scale, height * scale - (0 - (selectedUnit.getY() - selectedUnit.getVision() * scale)));
        visionDisplay = new Rectangle();
        visionDisplay.setTranslateX(vx);
        visionDisplay.setTranslateY(vy);
        visionDisplay.setWidth(vw);
        visionDisplay.setHeight(vh);
        visionDisplay.setStroke(Color.GREEN);
        visionDisplay.setFill(null);
        grid.getChildren().add(visionDisplay);
    }

    private void setAttackDisplay() {
        grid.getChildren().remove(attackDisplay);
        double scale = (Controller.getZoom() / Settings.getGroundSize());
        int ax = (int) Math.max(0, selectedUnit.getX() - selectedUnit.getAttackRange() * scale);
        int ay = (int) Math.max(0, selectedUnit.getY() - selectedUnit.getAttackRange() * scale);
        double width = selectedUnit.getwidth() + 2 * selectedUnit.getAttackRange();
        int aw = (int) Math.min(width * scale, width * scale - (0 - (selectedUnit.getX() - selectedUnit.getAttackRange() * scale)));
        double height = selectedUnit.getheight() + 2 * selectedUnit.getAttackRange();
        int ah = (int) Math.min(height * scale, height * scale - (0 - (selectedUnit.getY() - selectedUnit.getAttackRange() * scale)));
        attackDisplay = new Rectangle();
        attackDisplay.setTranslateX(ax);
        attackDisplay.setTranslateY(ay);
        attackDisplay.setWidth(aw);
        attackDisplay.setHeight(ah);
        attackDisplay.setStroke(Color.RED);
        attackDisplay.setFill(null);
        grid.getChildren().add(attackDisplay);
    }

    private void setCurrentUnit() {
        if (selectedUnit != null) {
            setVisionDisplay();
            setAttackDisplay();
            currentUnit.setText("Selected Unit : " + selectedUnit.toString() +
                    "\n X : " + selectedUnit.getx() + " Y : " + selectedUnit.gety() +
                    "\n HP : " + selectedUnit.getMaxHealth() +
                    "\n Side : " + selectedUnit.getSide() +
                    "\n Type : " + SubType.getSuperType(selectedUnit.getName()) +
                    "\n Name : " + selectedUnit.getName() +
                    "\n ");
        } else {
            currentUnit.setText("");
        }
    }

    private void refreshGrid() {
        ArrayList<Unit> toRemove = new ArrayList<>();
        for (Node unit : grid.getChildren()) {
            if (unit instanceof Unit)
                if (((Unit) unit).getMaxHealth() <= 0) {
                    toRemove.add((Unit) unit);
                }
        }
        for (Unit unit : toRemove) {
            grid.getChildren().remove(unit);
        }
    }

    private void checkState() {
        if (GameEditor.getInstance().getGameState() == GameState.AttackerWon || GameEditor.getInstance().getGameState() == GameState.DefenderWon) {
            animator.stop();
        }
        if (GameEditor.getInstance().getGameState() == GameState.AttackerWon || GameEditor.getInstance().getGameState() == GameState.DefenderWon) {
            if (GameEditor.getInstance().getGameState() != GameState.GameOver) {
                try {
                    endGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /*********************************************/

    /*********************************************/
    //FXML functions
    @FXML
    TextField interruptionTime;

    @FXML
    private void interruptUnit() {
        if (selectedUnit != null) {
            try {
                selectedUnit.interrupt(Integer.parseInt(interruptionTime.getText()));
            } catch (NumberFormatException ignored) {

            }
        }
    }

    @FXML
    private void setType(ActionEvent actionEvent) {
        String type;
        try {
            type = ((MenuItem) actionEvent.getSource()).getId();
        } catch (ClassCastException e) {
            type = ((Button) actionEvent.getSource()).getId();
        }
        activeType = SubType.getType(type);
    }

    @FXML
    private void setTactic(ActionEvent actionEvent) {
        String tactic = ((MenuItem) actionEvent.getSource()).getId();
        switch (tactic) {
            case "random" -> attackTactic = new RandomAttack();
            case "lowest" -> attackTactic = new LowestHealthAttack();
            case "highest" -> attackTactic = new HighestDamageAttack();
        }
    }

    @FXML
    private void moveBorder(ActionEvent actionEvent) {
        String pressed = ((Button) actionEvent.getSource()).getId();
        switch (pressed) {
            case "out" -> zoomOut();
            case "in" -> zoomIn();
            case "pause" -> GameEditor.getInstance().pause();
        }
    }

    private void zoomOut() {
        if (zoom / 2 > scroller.getHeight()) {
            double preX = scroller.getHvalue(), preY = scroller.getVvalue();
            zoom /= 2;
            grid.setPrefHeight(zoom);
            grid.setPrefWidth(zoom);
            scroller.setHvalue(preX / 2);
            scroller.setVvalue(preY / 2);
        }
    }

    private void zoomIn() {
        if (zoom <= 16384000) {
            zoom *= 2;
            grid.setPrefHeight(zoom);
            grid.setPrefWidth(zoom);
            scroller.layout();
            if (scroller.getHvalue() < 0.5d) {
                double preX = scroller.getHvalue();
                scroller.setHvalue((preX * 2));
            } else {
                double preX = 1.0d - scroller.getHvalue();
                scroller.setHvalue(1.0d - (preX * 2));
            }
            if (scroller.getVvalue() < 0.5d) {
                double preY = scroller.getVvalue();
                scroller.setVvalue((preY * 2));
            } else {
                double preY = 1.0d - scroller.getVvalue();
                scroller.setVvalue(1.0d - (preY * 2));
            }
            scroller.layout();
        }
    }

    static void endGame() throws IOException {
        if (GameEditor.getInstance().getGameState() == GameState.AttackerWon) {
            GameEditor.getInstance().setGameState(GameState.GameOver);
            Settings.loadSettings();
            Stage stage = Main.getPrimaryStage();
            Parent root = FXMLLoader.load(Controller.class.getResource("AttackersWonScreen.fxml"));
            stage.setTitle("EndGame");
            stage.setScene(new Scene(root, 1280, 720));
        } else if (GameEditor.getInstance().getGameState() == GameState.DefenderWon) {
            GameEditor.getInstance().setGameState(GameState.GameOver);
            Settings.loadSettings();
            Stage stage = Main.getPrimaryStage();
            Parent root = FXMLLoader.load(Controller.class.getResource("DefendersWonScreen.fxml"));
            stage.setTitle("EndGame");
            stage.setScene(new Scene(root, 1280, 720));
        }
    }
    /*********************************************/
}
