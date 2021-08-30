package sample.AttackFilters;

import sample.Settings;
import sample.SubType;
import sample.Unit;

import java.util.ArrayList;

public interface TypeFilter {
    public ArrayList<Unit> filter(ArrayList<Unit> units);

    static TypeFilter getFilter(SubType name) {
        switch (name) {
            case GrandCannon: {
                return (new GrandCannonTypeFilter());
            }
            case GrizzlyTank: {
                return (new GrizzlyTankTypeFilter());
            }
            case Infantry: {
                return new InfantryTypeFilter();
            }
            case MirageTank: {
                return new MirageTankTypeFilter();
            }
            case NavySeal: {
                return new NavySealTypeFilter();
            }
            case PatriotMissileSystem: {
                return new PatriotMissileSystemTypeFilter();
            }
            case Pillbox: {
                return new PillBoxTypeFilter();
            }
            case PrismTank: {
                return new PrismTankTypeFilter();
            }
            case PrismTower: {
                return new PrismTowerTypeFilter();
            }
            case Sniper: {
                return new SniperTypeFilter();
            }
            case TankDestroyer: {
                return new TankDestroyerTypeFilter();
            }
            case TeslaTank: {
                return (new TeslaTankTypeFilter());
            }
            case BlackEagle: {
                return (new BlackEagleTypeFilter());
            }
            default:
                return null;
        }
    }
}
