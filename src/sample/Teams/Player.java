package sample.Teams;

import sample.Game;
import sample.GameState;
import sample.Side;
import sample.Unit;

import java.util.ArrayList;

public class Player {

    protected int coins;
    protected Side type;
    protected ArrayList<Unit> units = new ArrayList<>();
    protected int id;
    protected Team myTeam;

    Player(int coins, Side type, int id, Team team) {
        this.coins = coins;
        this.type = type;
        this.id = id;
        this.myTeam = team;
    }

    public Side getType() {
        return type;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public boolean canBuy(int price) {
        return coins >= price;
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public void removeMe() {
        if (Game.getInstance().getGameState() == GameState.Running && units.isEmpty()) {
            myTeam.removePlayer(id);
        }
    }
}
