package sample.Tactics;

import sample.Unit;

import java.util.ArrayList;

public interface AttackTactic {
    public Unit getTarget(ArrayList<Unit> units);
}
