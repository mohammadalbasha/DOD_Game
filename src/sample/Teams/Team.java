package sample.Teams;


import sample.Side;

abstract class Team {
    Player[] players;
    Side side;

    public Player getPlayer(int id) {
        return players[id];
    }

    public void removePlayer(int id) {
        players[id] = null;
    }

    public boolean isEmpty() {
        boolean isEmpty = true;
        for (Player player : players) {
            if (player != null)
                player.removeMe();
            if (player != null) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
    }
}
