package sample.Tactics;

import sample.Unit;

import java.util.ArrayList;

public class HighestDamageAttack implements AttackTactic{
    @Override
    public Unit getTarget(ArrayList<Unit> units) {
        Unit target = units.get(0);
        for (Unit unit : units) {
            if (unit.getAttackDamage()>target.getAttackDamage()) {
                target = unit;
            }
        }
        return target;
    }
}
