package sample.Teams;

import sample.Settings;
import sample.Side;

public class AttackerTeam extends Team {
    public AttackerTeam(int playerNumber, Side side) {
        this.players = new Player[playerNumber];
        this.side = side;
        for (int i = 0; i < playerNumber; i++)
            players[i] = new Player(Settings.getMoney(), side, i, this);
    }
}



