package sample;

public enum SubType {
    TeslaTank, Sniper, MirageTank, Infantry, GrizzlyTank, NavySeal, TankDestroyer, PrismTank, Pillbox, PrismTower, GrandCannon, MainBase, BlackEagle, PatriotMissileSystem, delete, valley, bridge, river;

    public static SubType getType(String type) {
        return SubType.valueOf(type);
    }

    public static Type getSuperType(SubType type) {
        if (type.equals(Sniper) | type.equals(NavySeal) || type.equals(Infantry)) {
            return Type.Soldier;
        } else if (type.equals(TeslaTank) || type.equals(MirageTank) || type.equals(GrizzlyTank) || type.equals(TankDestroyer) || type.equals(PrismTank)) {
            return Type.Tank;
        } else if (type.equals(Pillbox) || type.equals(PrismTower) || type.equals(GrandCannon) || type.equals(MainBase) || type.equals(PatriotMissileSystem)) {
            return Type.Structure;
        } else if (type.equals(BlackEagle)) {
            return Type.Airplane;
        } else if (type.equals(river) || type.equals(bridge) || type.equals(valley)) {
            return Type.Environment;
        }
        return null;
    }
}
