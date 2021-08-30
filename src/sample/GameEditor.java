package sample;

import sample.Tactics.AttackTactic;
import sample.Teams.Player;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GameEditor {
    private long pauseTime;
    private long startTime = System.currentTimeMillis();
    private String gameType;
    private static GameEditor instance = new GameEditor();
    Unit base;


    private GameEditor() {

    }

    public static GameEditor getInstance() {
        return instance;
    }

    private static long runGameTime = 0;
    private String current_player_team;
    private int current_player_index;
    Thread preGameTimer = new Thread(new Runnable() {
        public void run() {
            waitForAttackers();
            waitForDefenders();
            runGameTime = Settings.getRunTime();
            GameEditor.getInstance().setStartTime(System.currentTimeMillis());
            Game.getInstance().setGameState(GameState.Running);
            rungame.start();
            Game.getInstance().goUnit();
            waitTilGameIsOver();
        }
    });
    Thread rungame = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                waitFrameTime();
                checkGameState();
                if(Game.getInstance().getGameState()==GameState.GameOver||Game.getInstance().getGameState()==GameState.AttackerWon||Game.getInstance().getGameState()==GameState.DefenderWon)
                    break;
            }
        }
    });


    /*********************************************/
    //Threads functions

    /************************/
    //Pre game thread
    private void waitForAttackers() {
        for (int i = 0; i < Game.getInstance().getAttackerPlayer(); i++) {
            current_player_index = i;
            current_player_team = "Attacker";
            try {
                Thread.sleep(Settings.getSetTime() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForDefenders() {
        for (int i = 0; i < Game.getInstance().getDefenderPlayer(); i++) {
            current_player_index = i;
            current_player_team = "Defender";
            try {
                Thread.sleep(Settings.getSetTime() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitTilGameIsOver() {
        try {
            Thread.sleep(Settings.getRunTime() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if ((runGameTime < (System.currentTimeMillis() - GameEditor.getInstance().getStartTime()) / 1000) && Game.getInstance().getGameState() != GameState.Paused)
                Game.getInstance().setGameState(GameState.DefenderWon);
        }
    }
    /************************/

    /************************/
    //Run game thread

    /************************/
    private void waitFrameTime() {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkGameState() {
        if (Game.getInstance().getGameState() != GameState.PreGame) {
            refreshUIR();
        }
        try {
            if (Game.getInstance().getUnit(Settings.get_base_x(), Settings.get_base_y()) == null) {
                Game.getInstance().setGameState(GameState.AttackerWon);
            }
        } catch (NullPointerException e) {
        }
        if (Game.getInstance().attackersAreDead()) {
            Game.getInstance().setGameState(GameState.DefenderWon);
        }
    }
    /************************/

    /*********************************************/

    /*********************************************/
    //Game states functions
    public void setGameState(GameState gs) {
        Game.getInstance().setGameState(gs);
    }

    public GameState getGameState() {
        return Game.getInstance().getGameState();
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getGameType() {
        return gameType;
    }

    public ArrayList<Unit> getAllUnits() {
        return Game.getInstance().getAllUnits();
    }
    /*********************************************/

    /*********************************************/
    //Game management functions
    public void setPreGame() {
        Game.getInstance().setGameState(GameState.PreGame);
    }

    public void startGame() {
        Game.getInstance().uir = new UnitsInRange(0, new MyRectangle(0, 0, Settings.getGroundSize(), Settings.getGroundSize()));
        GameEditor.getInstance().setPreGame();
        GameEditor.getInstance().refreshUIR();
        int base_X = Settings.get_base_x();
        int base_Y = Settings.get_base_y();
        if (Game.getInstance().getUnit(base_X, base_Y) == null)
            base = new Unit(base_X, base_Y, SubType.MainBase, null, null);
        else
            base = Game.getInstance().getUnit(base_X, base_Y);
       /* new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(1000);
                    System.out.println(Game.getInstance().getGameState());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();*/
        new Thread(()->{(new Console()).start();}).start();
        preGameTimer.start();
    }

    public void refreshUIR() {
        Game.getInstance().editUnitsInRange();
    }

    public Unit getBase() {
        return base;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void pause() {
        if (Game.getInstance().getGameState() == GameState.Running) {
            pauseTime = System.currentTimeMillis();
            Game.getInstance().setGameState(GameState.Paused);
        } else if (Game.getInstance().getGameState() == GameState.Paused) {
            startTime += (System.currentTimeMillis() - pauseTime);
            Game.getInstance().setGameState(GameState.Running);
        }
    }

    /*********************************************/

    /*********************************************/
    //Access game functions
    public Player getPlayer(String current_player_team, int current_player_index) {
        return Game.getInstance().getPlayer(current_player_team, current_player_index);
    }

    public Unit getUnit(int x, int y) {
        return Game.getInstance().getUnit(x, y);
    }

    public boolean canPlayerBuy(Player player, SubType activeType) throws FileNotFoundException {
        return Game.getInstance().canPlayerBuy(player, activeType);
    }

    public boolean canAdd(int x, int y, SubType activeType) {
        return Game.getInstance().canAdd(x, y, activeType);
    }

    public Unit buy(Player player, int x, int y, SubType activeType, AttackTactic attackTactic) throws FileNotFoundException {
        return Game.getInstance().buy(player, x, y, activeType, attackTactic);
    }

    public void sell(Player player, Unit unit) {
        Game.getInstance().sell(player, unit);
    }

    public void setUnit(Unit unit) {
        Game.getInstance().setUnit(unit);
        refreshUIR();
    }
    /*********************************************/
}
