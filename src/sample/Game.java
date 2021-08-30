package sample;

import sample.Tactics.AttackTactic;
import sample.Teams.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    private Game() {
    }

    public static Game getInstance() {
        return instance;
    }


    private static Game instance = new Game();
    private AttackerTeam attackTeam;
    private DefenderTeam defenceTeam;
    private int numberOfAttackers;
    private int numberOfDefenders;
    UnitsInRange uir;
    private ArrayList<Unit> units = new ArrayList<>();
    private GameState gameState;

    /*********************************************/
    //Teams & Players functions
    public void setTeams(int numberOfAttackers, int numberOfDefenders) {
        this.numberOfAttackers = numberOfAttackers;
        this.numberOfDefenders = numberOfDefenders;
        attackTeam = new AttackerTeam(this.numberOfAttackers, Side.Attack);
        defenceTeam = new DefenderTeam(this.numberOfDefenders, Side.Defence);
    }

    public boolean canPlayerBuy(Player player, SubType activeType) throws FileNotFoundException {
        int price = new Unit().getUnitPrice(activeType);
        return player.canBuy(price);
    }

    public Unit buy(Player player, int x, int y, SubType activeType, AttackTactic attackTactic) throws FileNotFoundException {
        player.setCoins(player.getCoins() - new Unit().getUnitPrice(activeType));
        Unit unit = new Unit(x, y, activeType, player, attackTactic);
        player.addUnit(unit);
        return unit;
    }

    public void sell(Player player, Unit unit) {
        player.setCoins(player.getCoins() + unit.getUnitPrice());
        player.removeUnit(unit);
    }

    public int getAttackerPlayer() {
        return numberOfAttackers;
    }

    public int getDefenderPlayer() {
        return numberOfDefenders;
    }

    public boolean attackersAreDead() {
        return attackTeam.isEmpty();
    }

    public Player getPlayer(String side, int id) {
        if (side == "Attacker")
            return attackTeam.getPlayer(id);
        return defenceTeam.getPlayer(id);
    }
    /*********************************************/

    /*********************************************/
    //Game functions
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
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
    public ArrayList<Unit> getAllUnits(){return  units;}

    /*********************************************/

    /*********************************************/
    //Units functions
    public boolean canAdd(int x, int y, SubType type) {
        if(type == SubType.BlackEagle)
            return true;
        String path = null;
        path = "src/sample/Units/" + type.toString() + ".txt";
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
        }
        MyRectangle r = new MyRectangle(x, y, w, h);
        ArrayList<Unit> a = new ArrayList<Unit>();
        uir.canAdd(a, r);
        boolean ok = true;
        if (!a.isEmpty()) {
            a.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);
            for (Unit u : a)
                if (!u.getName().equals(SubType.BlackEagle))
                    ok = false;
            return ok;
        }
        return true;
    }

    public void setUnit(Unit unit) {
        units.add(unit);
    }

    public Unit getUnit(int x, int y) {
        MyRectangle r = new MyRectangle(x, y, 1, 1);
        ArrayList<Unit> a = new ArrayList<Unit>();
        ArrayList<Unit> b = new ArrayList<>();
        b = uir.canAdd(a, r);
        if (b.size() != 0) {
            {
                for (Unit u : b)
                    if ((u.getName().equals(SubType.BlackEagle)))
                        return u;
            }
            return b.get(0);
        }
        return null;
    }

    public synchronized void destroyThat(Unit unit) {
        units.remove(unit);
    }

    public synchronized ArrayList<Unit> getAround(Unit unit) {
        ArrayList<Unit> around = new ArrayList<Unit>();
        int leftx = Math.max(0, unit.getx() - unit.getVision());
        int lefty = Math.max(0, unit.gety() - unit.getVision());
        int width = unit.getwidth() + 2 * unit.getVision();
        int height = unit.getheight() + 2 * unit.getVision();
        int rightwidth = Math.min(width, width - (0 - (unit.getx() - unit.getVision())));
        int rightheight = Math.min(height, height - (0 - (unit.gety() - unit.getVision())));

        MyRectangle r = new MyRectangle(leftx, lefty, rightwidth, rightheight);
        around = uir.canAdd(around, r);
        around.removeIf(u -> u.getSide() == unit.getSide());
        around.removeIf(u -> u.getSide() == Side.Environment);
        if (unit.getSide() == Side.Defence)
            around.removeIf(u -> u.getName() == SubType.MainBase);
        return around;
    }

    public synchronized ArrayList<Unit> getInAttackRange(Unit unit) {

        ArrayList<Unit> attack = new ArrayList<Unit>();
        int leftx = Math.max(0, unit.getx() - unit.getAttackRange());
        int lefty = Math.max(0, unit.gety() - unit.getAttackRange());
        int width = unit.getwidth() + 2 * unit.getAttackRange();
        int height = unit.getheight() + 2 * unit.getAttackRange();
        int rightwidth = Math.min(width, width - (0 - (unit.getx() - unit.getAttackRange())));
        int rightheight = Math.min(height, height - (0 - (unit.gety() - unit.getAttackRange())));

        MyRectangle r = new MyRectangle(leftx, lefty, rightwidth, rightheight);
        attack = uir.canAdd(attack, r);
        attack.removeIf(u -> u.getSide() == unit.getSide());
        attack.removeIf(u -> u.getSide() == Side.Environment);
        if (unit.getSide() == Side.Defence)
            attack.removeIf(u -> u.getName() == SubType.MainBase);
        attack.removeIf(uu -> uu.getSide() == unit.getSide());
        return attack;
    }

    public synchronized void attack(Unit unit1, Unit unit2) {
        unit2.takeDamage(unit1.getAttackDamage(), unit1.toString());
    }

    /*public synchronized void moveThisTo(Unit unit, int i, int j) {
        int x = (unit.getx());
        int y = (unit.gety());
        boolean can = true;
        //Go Up Left
        if (x - 1 >= 0 && y - 1 >= 0) {
            MyRectangle r = new MyRectangle(x - 1, y - 1, 1, unit.getheight());
            MyRectangle rr = new MyRectangle(x - 1, y - 1, unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            if (uir.canAdd(a, r).size() != 0) {
                can = false;
            } else if (uir.canAdd(a, rr).size() != 0) {
                can = false;
            }
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i < unit.getx() && j < unit.gety() && can) {
            unit.reducex();
            unit.reducey();
            return;
        }
        can = true;
        //Go Down Left
        if (x - 1 >= 0 && y + 1 <= Settings.getGroundSize()) {
            MyRectangle r = new MyRectangle(x - 1, y + 1, 1, unit.getheight());
            MyRectangle rr = new MyRectangle(x - 1, y + unit.getheight(), unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            if (uir.canAdd(a, r).size() != 0)
                can = false;
            else if (uir.canAdd(a, rr).size() != 0)
                can = false;
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i < unit.getx() && j > unit.gety() && can) {
            unit.reducex();
            unit.increasey();
            return;
        }
        can = true;
        //Go Right Up
        if (x + 1 <= Settings.getGroundSize() && y - 1 >= 0) {
            MyRectangle r = new MyRectangle(x + unit.getwidth(), y - 1, 1, unit.getheight());
            MyRectangle rr = new MyRectangle(x + 1, y - 1, unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            if (uir.canAdd(a, r).size() != 0)
                can = false;
            else if (uir.canAdd(a, rr).size() != 0)
                can = false;
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i > unit.getx() && j < unit.gety() && can) {
            unit.increasex();
            unit.reducey();
            return;
        }
        can = true;
        if (x + 1 <= Settings.getGroundSize() && y + 1 <= Settings.getGroundSize()) {
            MyRectangle r = new MyRectangle(x + unit.getwidth(), y + 1, 1, unit.getheight());
            MyRectangle rr = new MyRectangle(x + 1, y + unit.getheight(), unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            if (uir.canAdd(a, r).size() != 0)
                can = false;
            else if (uir.canAdd(a, rr).size() != 0)
                can = false;
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i > unit.getx() && j > unit.gety() && can) {
            unit.increasex();
            unit.increasey();
            return;
        }
        can = true;
//Go left
        if (x - 1 >= 0) {
            MyRectangle r = new MyRectangle(x - 1, y, 1, unit.getheight());
            ArrayList<Unit> a = new ArrayList<Unit>();
            if (uir.canAdd(a, r).size() != 0)
                can = false;
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i < unit.getx() && can) {
            unit.reducex();
            return;
        }
        can = true;
        //Go Right
        if (x + 1 <= Settings.getGroundSize()) {
            MyRectangle r = new MyRectangle(x + unit.getwidth(), y, 1, unit.getheight());
            ArrayList<Unit> a = new ArrayList<Unit>();
            if (uir.canAdd(a, r).size() != 0)
                can = false;
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i > unit.getx() && can) {
            unit.increasex();
            return;
        }
        can = true;
        //Go Up
        if (y - 1 >= 0) {
            MyRectangle r = new MyRectangle(x, y - 1, unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            if (uir.canAdd(a, r).size() != 0)
                can = false;
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if ((j < unit.gety()) && can) {

            unit.reducey();
            return;
        }
        can = true;
        //Go Down
        if (y + 1 <= Settings.getGroundSize()) {
            MyRectangle r = new MyRectangle(x, y + unit.getheight(), unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            if (uir.canAdd(a, r).size() != 0)
                can = false;
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if ((j > unit.gety()) && can) {
            unit.increasey();
            return;
        }
    }*/
    public synchronized void moveThisTo(Unit unit, int i, int j) {
        int x = (unit.getx());
        int y = (unit.gety());
        boolean can = true;
        //Go Up Left
        if (x - 1 >= 0 && y - 1 >= 0) {
            MyRectangle r = new MyRectangle(x - 1, y - 1, 1, unit.getheight());
            MyRectangle rr = new MyRectangle(x - 1, y - 1, unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            ArrayList<Unit> b = new ArrayList<>();
            b = uir.canAdd(a, r);
            b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);
            if (b.size() != 0) {
                can = false;
            } else {
                b = uir.canAdd(a, rr);
                b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);

                if (b.size() != 0) {
                    can = false;
                }
            }
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i < unit.getx() && j < unit.gety() && can) {
            unit.reducex();
            unit.reducey();
            return;
        }
        can = true;
        //Go Down Left
        if (x - 1 >= 0 && y + 1 <= Settings.getGroundSize()) {
            MyRectangle r = new MyRectangle(x - 1, y + 1, 1, unit.getheight());
            MyRectangle rr = new MyRectangle(x - 1, y + unit.getheight(), unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            ArrayList<Unit> b = new ArrayList<>();
            b = uir.canAdd(a, r);
            b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);
            if (b.size() != 0) {
                can = false;
            } else {
                b = uir.canAdd(a, rr);
                b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);

                if (b.size() != 0) {
                    can = false;
                }
            }
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i < unit.getx() && j > unit.gety() && can) {
            unit.reducex();
            unit.increasey();
            return;
        }
        can = true;
        //Go Right Up
        if (x + 1 <= Settings.getGroundSize() && y - 1 >= 0) {
            MyRectangle r = new MyRectangle(x + unit.getwidth(), y - 1, 1, unit.getheight());
            MyRectangle rr = new MyRectangle(x + 1, y - 1, unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            ArrayList<Unit> b = new ArrayList<>();
            b = uir.canAdd(a, r);
            b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);
            if (b.size() != 0) {
                can = false;
            } else {
                b = uir.canAdd(a, rr);
                b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);

                if (b.size() != 0) {
                    can = false;
                }
            }
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i > unit.getx() && j < unit.gety() && can) {
            unit.increasex();
            unit.reducey();
            return;
        }
        can = true;
        if (x + 1 <= Settings.getGroundSize() && y + 1 <= Settings.getGroundSize()) {
            MyRectangle r = new MyRectangle(x + unit.getwidth(), y + 1, 1, unit.getheight());
            MyRectangle rr = new MyRectangle(x + 1, y + unit.getheight(), unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            ArrayList<Unit> b = new ArrayList<>();
            b = uir.canAdd(a, r);
            b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);
            if (b.size() != 0) {
                can = false;
            } else {
                b = uir.canAdd(a, rr);
                b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);

                if (b.size() != 0) {
                    can = false;
                }
            }
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i > unit.getx() && j > unit.gety() && can) {
            unit.increasex();
            unit.increasey();
            return;
        }
        can = true;
//Go left
        if (x - 1 >= 0) {
            MyRectangle r = new MyRectangle(x - 1, y, 1, unit.getheight());
            ArrayList<Unit> a = new ArrayList<Unit>();
            ArrayList<Unit> b = new ArrayList<>();
            b = uir.canAdd(a, r);
            b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);
            if (b.size() != 0) {
                can = false;
            }
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i < unit.getx() && can) {
            unit.reducex();
            return;
        }
        can = true;
        //Go Right
        if (x + 1 <= Settings.getGroundSize()) {
            MyRectangle r = new MyRectangle(x + unit.getwidth(), y, 1, unit.getheight());
            ArrayList<Unit> a = new ArrayList<Unit>();
            ArrayList<Unit> b = new ArrayList<>();
            b = uir.canAdd(a, r);
            b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);
            if (b.size() != 0) {
                can = false;
            }
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if (i > unit.getx() && can) {
            unit.increasex();
            return;
        }
        can = true;
        //Go Up
        if (y - 1 >= 0) {
            MyRectangle r = new MyRectangle(x, y - 1, unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            ArrayList<Unit> b = new ArrayList<>();
            b = uir.canAdd(a, r);
            b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);
            if (b.size() != 0) {
                can = false;
            }
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if ((j < unit.gety()) && can) {

            unit.reducey();
            return;
        }
        can = true;
        //Go Down
        if (y + 1 <= Settings.getGroundSize()) {
            MyRectangle r = new MyRectangle(x, y + unit.getheight(), unit.getwidth(), 1);
            ArrayList<Unit> a = new ArrayList<Unit>();
            ArrayList<Unit> b = new ArrayList<>();
            b = uir.canAdd(a, r);
            b.removeIf(un -> un.getName() == SubType.bridge || un.getName() == SubType.river);
            if (b.size() != 0) {
                can = false;
            }
            if (unit.getName().equals(SubType.BlackEagle)) can = true;
        } else {
            can = false;
        }
        if ((j > unit.gety()) && can) {
            unit.increasey();
            return;
        }
    }

    public synchronized void goUnit() {
        for (Unit unit : units)
            if (unit.getType() != Type.Environment)
                new Thread(() -> {
                    try {
                        unit.go();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
    }
    /*********************************************/
}
