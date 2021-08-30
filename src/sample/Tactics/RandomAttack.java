package sample.Tactics;

import sample.Unit;

import java.util.ArrayList;
import java.util.Random;

public class RandomAttack implements AttackTactic {

    @Override
    public Unit getTarget(ArrayList<Unit> units) {
        return units.get(new Random().nextInt(units.size()));
    }
}
