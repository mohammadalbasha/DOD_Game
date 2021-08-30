package sample.AttackFilters;

import sample.Type;
import sample.Unit;

import java.util.ArrayList;

public class PrismTankTypeFilter implements TypeFilter {
    @Override
    public ArrayList<Unit> filter(ArrayList<Unit> units) {
        ArrayList<Unit> result=new ArrayList();
        for(Unit u:units)
        {
            if(u.getType()== Type.Tank||u.getType()==Type.Soldier||u.getType()==Type.Structure)
                result.add(u);
        }
        return result;
    }
}
