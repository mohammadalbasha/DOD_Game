package sample;

import sample.AttackFilters.TypeFilter;
import sample.Tactics.*;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import sample.Teams.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Unit extends Pane {
    private Side side = Side.Defence;


    public Side getSide() {
        return side;
    }
    /*X,Y are location on grid, x,y are location on ground*/
    /*****************************************/
    private double X, Y;
    private int x, y;
    private int tempx = -1, tempy = -1;
    private long moveClock = System.currentTimeMillis();
    private long attackClock = moveClock;
    private int defaultX, defaultY;
    private int defaultMovementSpeed;
    private long lastFrameTime = System.currentTimeMillis();

    /*****************************************/
    private Type type = Type.Structure;
    private SubType name = SubType.MainBase;
    private double maxHealth;
    private double armour;
    private int attackDamage;
    private int attackRange;
    private double attackFrequency;
    private int width = 1, height = 1;
    private int movementSpeed;
    private int unitPrice;
    /*****************************************/
    private int vision;
    private Player player;
    private ArrayList<Unit> aroundMe = new ArrayList<>();
    private ArrayList<Unit> inAttackRange = new ArrayList<>();
    private AttackTactic attackTactic;
    private Log log;
    private TypeFilter attackFilter;
    private int lastx = -1;
    private int lasty = -1;
    public boolean reachBase = false;
    private boolean interrupted = false;
    private int interruptionTime = 0;
    Rectangle rectangle;

    /*****************************************/

    /*****************************************/
    //Getters and setters
    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public int getx() {
        return x;
    }

    public void reducex() {
        x--;
    }

    public void increasex() {
        x++;
    }

    public int gety() {
        return y;
    }

    public void reducey() {
        y--;
    }

    public void increasey() {
        y++;
    }

    public int getVision() {
        return vision;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public int getwidth() {
        return this.width;
    }

    public int getheight() {
        return this.height;
    }

    public Player getPlayer() {
        return player;
    }

    public int getUnitPrice() {
        return this.unitPrice;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public Type getType() {
        return type;
    }

    public SubType getName() {
        return name;
    }
    /*****************************************/



    public int getUnitPrice(SubType unitType) throws FileNotFoundException {
        String path = "src/sample/Units/" + unitType.toString() + ".txt";
        File file = new File(path);
        try {
            Scanner sc = new Scanner(file);
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            return sc.nextInt();
        } catch (FileNotFoundException e) {

        }
        return 0;
    }

    /*****************************************/
    public Boolean canMove() {
        try {
            if (((System.currentTimeMillis() - moveClock) > (1000 / movementSpeed))) {
                moveClock = System.currentTimeMillis();
//                attackClock = System.currentTimeMillis();
                return true;
            }
        } catch (ArithmeticException e) {
        }
        return false;
    }

    public Boolean canAttack() {
        if (((System.currentTimeMillis() - attackClock) > (1000 / attackFrequency))) {
            attackClock = System.currentTimeMillis();
//            moveClock = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /*****************************************/
    public Unit() {
    }


    public Unit(int x, int y, SubType subType, Player player, AttackTactic attackTactic) {
        this.player = player;
        this.attackTactic = attackTactic;
        this.name = subType;
        this.type = SubType.getSuperType(name);
        if (subType != SubType.MainBase && type != Type.Environment)
            this.side = player.getType();
        try {
            String path;
            if (type == Type.Environment) path = "src/sample/TilesMap/" + subType.toString() + ".txt";
            else {
                path = "src/sample/Units/" + subType.toString() + ".txt";
            }
            String sideString;
            if (side == Side.Attack) {
                sideString = "A";
            } else {
                sideString = "D";
            }
            String url;
            if (type == Type.Environment)
                url = "src/sample/Textures/" + subType.toString() + ".png";
            else {
                url = "src/sample/Textures/" + subType.toString() + sideString + ".png";
            }
            this.attackFilter = TypeFilter.getFilter(this.name);
            File file = new File(path);
            Scanner sc = new Scanner(file);
            this.maxHealth = Double.parseDouble(sc.next());
            this.armour = Double.parseDouble(sc.next());
            this.attackDamage = Integer.parseInt(sc.next());
            this.attackRange = Integer.parseInt(sc.next());
            this.attackFrequency = Double.parseDouble(sc.next());
            this.height = Integer.parseInt(sc.next());
            this.width = height;
            this.movementSpeed = Integer.parseInt(sc.next());
            this.defaultMovementSpeed = movementSpeed;
            this.unitPrice = Integer.parseInt(sc.next());
            this.vision = attackRange * 2;
            rectangle = new Rectangle(this.width, this.height);
            rectangle.setFill(new ImagePattern(new Image(new FileInputStream(url))));
        } catch (FileNotFoundException e) {
        }
        setTranslateX(this.X);
        setTranslateY(this.Y);
        rectangle.setStroke(Color.BLACK);
        getChildren().add(rectangle);
        this.defaultX = this.x = x;
        this.defaultY = this.y = y;
        if (this.type != Type.Environment)
            log = new Log(this);
    }
    public void go() {
        while (true) {
            if (Game.getInstance().getGameState() == GameState.AttackerWon || Game.getInstance().getGameState() == GameState.DefenderWon || Game.getInstance().getGameState() == GameState.GameOver) {
                this.remove();
                if (player != null)
                    player.removeUnit(this);
                break;
            }
            if (this.maxHealth <= 0) {
                die();
                break;
            }
            lastFrameTime = System.currentTimeMillis();
            if (Game.getInstance().getGameState() == GameState.Paused) {
                pause();
            }
            if (Game.getInstance().getGameState() == GameState.Running) {
                checkForRivers();
                sleep();
                if (Game.getInstance().getGameState() == GameState.Paused) {
                    continue;
                }
                if (!interrupted) {
                    if (x == tempx && y == tempy) {
                        tempx = -1;
                        tempy = -1;
                    }
                    moveToTemp();
                    getAround();
                    moveIfAirplane();
                    if (!aroundMe.isEmpty()) {
                        if (!inAttackRange.isEmpty()) {
                            attack();
                        } else {
                            moveToTarget();
                        }
                    } else {
                        moveToDefault();
                    }
                }
                if (interruptionTime > 0)
                    interruptionTime -= ((System.currentTimeMillis() - lastFrameTime));
                if (interruptionTime > 0) {
                    interrupted = true;
                } else {
                    interrupted = false;
                }
            }
        }
    }
    /******************************************/

    /******************************************/
    //Outside called functions
    public void setLocation() {
        setTranslateX(X = (x * (Controller.getZoom() / Settings.getGroundSize())));
        setTranslateY(Y = (y * (Controller.getZoom() / Settings.getGroundSize())));
        setWidth(this.width * (Controller.getZoom() / Settings.getGroundSize()));
        rectangle.setWidth(this.width * (Controller.getZoom() / Settings.getGroundSize()));
        setHeight(this.height * (Controller.getZoom() / Settings.getGroundSize()));
        rectangle.setHeight(this.height * (Controller.getZoom() / Settings.getGroundSize()));
    }
      public void set_CreateLocation() {
          setTranslateX(X = (x * (CreateMap.getZoom() / Settings.getGroundSize())));
          setTranslateY(Y = (y * (CreateMap.getZoom() / Settings.getGroundSize())));
          setWidth(this.width * (CreateMap.getZoom() / Settings.getGroundSize()));
          rectangle.setWidth(this.width * (CreateMap.getZoom() / Settings.getGroundSize()));
          setHeight(this.height * (CreateMap.getZoom() / Settings.getGroundSize()));
          rectangle.setHeight(this.height * (CreateMap.getZoom() / Settings.getGroundSize()));
      }
    public void remove() {
        Game.getInstance().destroyThat(this);
    }

    public void takeDamage(int damage, String attacker) {
        if (this.type != Type.Environment)
            log.gotAttacked(x, y, attacker);
        maxHealth -= ((1 - armour) * damage);
        if (this.maxHealth <= 0) {
            Game.getInstance().destroyThat(this);
            try {
                this.remove();
                if (player != null)
                    player.removeUnit(this);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void interrupt(int interruptionTime) {
        this.interruptionTime = (interruptionTime) * 1000;
    }

    public void gotoXY(int x, int y) {
        interruptionTime = 0;
        tempx = x;
        tempy = y;
    }
    /******************************************/

    /******************************************/
    //Movement functions
    private void die() {
        try {
            ArrayList<Unit> ismeonbridge = new ArrayList<>();
            ArrayList<Unit> allunitonbridge = new ArrayList<>();
            Game.getInstance().uir.canAdd(ismeonbridge, new MyRectangle(x, y, width, height));
            ismeonbridge.removeIf(u -> u.getName() != SubType.bridge);
            for (Unit br : ismeonbridge) {
                allunitonbridge.clear();
                Game.getInstance().uir.canAdd(allunitonbridge, new MyRectangle(br.getx(), br.gety(), br.getwidth(), br.getheight()));
                for (Unit un : allunitonbridge) {
                    un.takeDamage((int) 1e10, "Falling off a bridge");
                    un.remove();
                    log.addDeath(un.getx(), un.gety());
                    if (player != null)
                        player.removeUnit(un);
                    Game.getInstance().destroyThat(un);
                }
            }
            allunitonbridge.clear();
            log.addDeath(this.x, this.y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkForRivers() {
        ArrayList<Unit> checkingEnvironment = new ArrayList<>();
        try {
            Game.getInstance().uir.canAdd(checkingEnvironment, new MyRectangle(this.x, this.y, this.width, this.height));
        } catch (NullPointerException n) {
            this.go();
        }
        boolean found_river = true;
        for (Unit ce : checkingEnvironment)
            if(ce!=null)
            if (ce.name.equals(SubType.river) && this.getType() != Type.Airplane) {
                found_river = false;
                movementSpeed = defaultMovementSpeed / 2;
            }
        if (found_river) movementSpeed = defaultMovementSpeed;
    }

    private void sleep() {
        try {
            double sleep1 = 1e10, sleep2 = 10e10;
            try {
                sleep1 = (1000 / movementSpeed);
            } catch (ArithmeticException e) {
                sleep1 = 1e10;
            }
            try {
                sleep2 = (1000 / attackFrequency);
            } catch (ArithmeticException e) {
                sleep2 = 1e10;
            }
            double sleepTime = Math.min(sleep1, sleep2);
            Thread.sleep((long) (sleepTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void moveToTemp() {
        if (tempx != -1 || tempy != -1) {
            if (canMove()) {
                Game.getInstance().moveThisTo(this, tempx, tempy);
                if (x != lastx && y != lasty)
                    log.addMovement(getx(), gety());
                lastx = x;
                lasty = y;
            }
        }
    }

    private void getAround() {
        try {
            aroundMe = attackFilter.filter(Game.getInstance().getAround(this));
            inAttackRange = attackFilter.filter(Game.getInstance().getInAttackRange(this));
        } catch (NullPointerException e) {
        }
    }
    private void moveIfAirplane()
    {
        if (this.name == SubType.BlackEagle) {
            if (this.reachBase == true && (Game.getInstance().getGameState() != GameState.Paused))
                while (this.getx() != defaultX || this.gety() != defaultY) {
                    if ((Game.getInstance().getGameState() != GameState.Paused))
                        Game.getInstance().moveThisTo(this, defaultX, defaultY);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            this.reachBase = false;
        }
    }
    private void attack()
    {
        if (canAttack()) {
        Unit target = attackTactic.getTarget(inAttackRange);
        Game.getInstance().attack(this, target);
        log.addAttack(target);
        if (this.name == SubType.BlackEagle) {
            this.reachBase = true;
        }
    }
    }
    private void moveToTarget()
    {
        if (canMove()) {
            Unit ran = attackTactic.getTarget(aroundMe);

            Game.getInstance().moveThisTo(this, ran.getx(), ran.gety());

            if (x != lastx && y != lasty)
                log.addMovement(getx(), gety());
            lastx = x;
            lasty = y;
        }
    }
    private void moveToDefault()
    {
        if (canMove()) {
            if (side == Side.Attack) {

                Game.getInstance().moveThisTo(this, Settings.get_base_x(), Settings.get_base_y());

            } else {
                Game.getInstance().moveThisTo(this, defaultX, defaultY);

            }
            if (x != lastx && y != lasty)
                log.addMovement(getx(), gety());
            lastx = x;
            lasty = y;
        }
    }
    /******************************************/

    /******************************************/
    //Log and its functions
    private class Log {
        String path;
        File file;
        FileWriter writer;

        Log(Unit unit) {
            path = ("src/sample/Logs/$" + unit.side + "$" + unit.toString() + "$" + unit.name + ".txt");
            file = new File(path);
            try {
                writer = new FileWriter(file, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void addAttack(Unit target) {
            try {
                try {
                    writer.write("Attacked " + target + "at position " + target.getx() + ", " + target.gety() + " at time " + System.currentTimeMillis() + "\n");
                    writer.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void addMovement(int x, int y) {
            try {
                writer.write("Moved to " + x + ", " + y + " at time " + System.currentTimeMillis() + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void addDeath(int x, int y) throws IOException {
            writer.write("Died at " + x + ", " + y + " at time " + System.currentTimeMillis() + "\n");
            writer.flush();
//            writer.close();
        }

        void gotAttacked(int x, int y, String attacker) {
            try {
                writer.write("Got attacked at " + x + ", " + y + " by " + attacker + " at time " + System.currentTimeMillis() + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /******************************************/

}
