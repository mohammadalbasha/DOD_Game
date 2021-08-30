package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Settings {
    /*Settings*/
    /**************************************/
    private static int groundSize;

    private static int paneWidth;
    private static int _base_x;
    private static int _base_y;
    private static int setTime;
    private static int runTime;
    private static int money;

    /*Settings getters*/

    /**************************************/
    public static int getGroundSize() {
        return groundSize;
    }

    public static int getPaneWidth() {
        return paneWidth;
    }

    public static int get_base_x() {
        return _base_x;
    }

    public static int get_base_y() {
        return _base_y;
    }

    public static int getSetTime() {
        return setTime;
    }

    public static int getMoney() {
        return money;
    }

    public static int getRunTime() {
        return runTime;
    }

    /**************************************/
    @FXML
    private Label error;
    @FXML
    private TextField ground;
    @FXML
    private TextField pane;
    @FXML
    private TextField base_x, base_y;
    @FXML
    private TextField settingTime;
    @FXML
    private TextField moneyTF;
    @FXML
    private TextField runningTime;

    public static void setGroundSize(int groundsize) {
        Settings.groundSize = groundsize;
    }

    public static void setPaneWidth(int paneWidth) {
        Settings.paneWidth = paneWidth;
    }

    public static void set_base_x(int basex) {
        _base_x = basex;
    }

    public static void set_base_y(int basey) {
        _base_y = basey;
    }

    public static void setSetTime(int timebefore) {
        setTime = timebefore;
    }

    public static void setRunTime(int runingtime) {
        runTime = runingtime;
    }

    public static void setMoney(int money2) {
        money = money2;
    }

    @FXML
    private void saveSettings() throws IOException {
        boolean correctInput = true;
        int groundSize = -1;
        try {
            groundSize = Integer.parseInt(ground.getText());
            try {
                checkInput("GroundSize", groundSize, groundSize);
            } catch (WrongSettingsException e) {
                error.setText("Check ground size, must be between 25 and 1000000");
                correctInput = false;
            }
        } catch (NumberFormatException e) {
            correctInput = false;
        }
        int paneWidth = -1;
        try {
            paneWidth = Integer.parseInt(pane.getText());
            try {
                checkInput("PaneWidth", paneWidth, groundSize);
            } catch (WrongSettingsException e) {
                error.setText("Check portview size, must be between 100 and 1000");
                correctInput = false;
            }
        } catch (NumberFormatException e) {
            correctInput = false;
        }
        int _base_x = -1;
        int _base_y = -1;
        try {
            _base_x = Integer.parseInt(base_x.getText());
            _base_y = Integer.parseInt(base_y.getText());
            try {
                checkInput("Base_x", _base_x, groundSize);
            } catch (WrongSettingsException e) {
                error.setText("Wrong base x");
                correctInput = false;
            }
            try {
                checkInput("Base_y", _base_y, groundSize);
            } catch (WrongSettingsException e) {
                error.setText("Wrong base y");
                correctInput = false;
            }
        } catch (NumberFormatException e) {
            correctInput = false;
        }
        int setTime = -1;
        try {
            setTime = Integer.parseInt(settingTime.getText());
            try {
                checkInput("SetTime", setTime, groundSize);
            } catch (WrongSettingsException e) {
                error.setText("Set time can't be negative");
                correctInput = false;
            }
        } catch (NumberFormatException e) {
            correctInput = false;
        }
        try {
            money = Integer.parseInt(moneyTF.getText());
            try {
                checkInput("Money", money, groundSize);
            } catch (WrongSettingsException e) {
                error.setText("Money can't be negative");
                correctInput = false;
            }

        } catch (NumberFormatException e) {
            correctInput = false;
        }
        int gameTime = -1;
        try {
            gameTime = Integer.parseInt(runningTime.getText());
            try {
                checkInput("GameTime", gameTime, groundSize);
            } catch (WrongSettingsException e) {
                error.setText("Game time can't be negative");
                correctInput = false;
            }
        } catch (NumberFormatException e) {
            correctInput = false;
        }
        if (correctInput) {
            String path = "Settings.txt";
            File file = new File(path);
            PrintWriter writer = new PrintWriter(file);
            writer.println(groundSize);
            writer.println(paneWidth);
            writer.println(_base_x);
            writer.println(_base_y);
            writer.println(setTime);
            writer.println(money);
            writer.println(gameTime);
            writer.close();
            Stage stage = Main.getPrimaryStage();
            Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
            stage.setTitle("Main Menu");
            stage.setScene(new Scene(root, 1280, 720));
        }
    }
    @FXML
    private void goBack() throws IOException {
        Stage stage = Main.getPrimaryStage();
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        stage.setTitle("Main Menu");
        stage.setScene(new Scene(root, 1280, 720));
    }

    public static void loadSettings() {
        String path = "Settings.txt";
        File file = new File(path);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        groundSize = sc.nextInt();
        paneWidth = sc.nextInt();
        _base_x = sc.nextInt();
        _base_y = sc.nextInt();
        setTime = sc.nextInt();
        money = sc.nextInt();
        runTime = sc.nextInt();
    }

    public void checkInput(String input, int value, int ground) throws WrongSettingsException {
        switch (input) {
            case "GroundSize":
                if (value < 1000 || value > 1000000) {
                    throw (new WrongSettingsException());
                }
                break;
            case "PaneWidth":
                if (value < 100 || value > 1000) {
                    throw (new WrongSettingsException());
                }
                break;
            case "Base_x":
            case "Base_y":
                if (value < 0 || ((value + 100) > ground)) {
                    throw (new WrongSettingsException());
                }
                break;
            case "SetTime":
            case "Money":
            case "GameTime":
                if (value < 0) {
                    throw (new WrongSettingsException());
                }
                break;
        }
    }

    static class WrongSettingsException extends Exception {

    }
}
