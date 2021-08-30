package sample.AttackFilters;

import sample.Type;
import sample.Unit;

import java.util.ArrayList;

public class PillBoxTypeFilter implements TypeFilter {
    @Override
    public ArrayList<Unit> filter(ArrayList<Unit> units) {
        ArrayList<Unit> result=new ArrayList();
        for(Unit u:units)
        {
            if(u.getType()== Type.Soldier)
                result.add(u);
        }
        return result;
    }
}
