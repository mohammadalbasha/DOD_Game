package sample.Tactics;

import sample.Unit;

import java.util.ArrayList;
import java.util.Random;

public class LowestHealthAttack implements AttackTactic {
    @Override
    public Unit getTarget(ArrayList<Unit> units) {
        Unit target = units.get(0);
        for (Unit unit : units) {
            if (unit.getMaxHealth() < target.getMaxHealth()) {
                target = unit;
            }
        }
        return target;
    }
}
