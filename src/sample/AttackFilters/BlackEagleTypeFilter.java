package sample.AttackFilters;

import sample.SubType;
import sample.Type;
import sample.Unit;

import java.util.ArrayList;

public class BlackEagleTypeFilter implements TypeFilter {
    @Override
    public ArrayList<Unit> filter(ArrayList<Unit> units) {
        ArrayList<Unit> result=new ArrayList();
        for(Unit u:units)
        {
            if(u.getName()== SubType.MainBase)
                result.add(u);
        }
        return result;
    }
}
