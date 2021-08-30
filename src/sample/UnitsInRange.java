
package sample;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

class UnitsInRange {
    private int MAX_LEVELS = 10;
    private int level;
    public ArrayList<Unit> objects;
    private MyRectangle bounds;
    private UnitsInRange[] nodes;

    public UnitsInRange(int pLevel, MyRectangle pBounds) {
        level = pLevel;
        objects = new ArrayList();
        bounds = pBounds;
        nodes = new UnitsInRange[4];
    }

    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private void split() {
        int subWidth = (int) (bounds.getwidth() / 2);
        int subHeight = (int) (bounds.getheight() / 2);
        int x = (int) bounds.getx();
        int y = (int) bounds.gety();
//topright
        nodes[0] = new UnitsInRange(level + 1, new MyRectangle(x + subWidth, y, subWidth, subHeight));
//topleft
        nodes[1] = new UnitsInRange(level + 1, new MyRectangle(x, y, subWidth, subHeight));
//bottomleft
        nodes[2] = new UnitsInRange(level + 1, new MyRectangle(x, y + subHeight, subWidth, subHeight));
//bottomright
        nodes[3] = new UnitsInRange(level + 1, new MyRectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private int getIndex(MyRectangle pRect) {
        int index = -1;
        double verticalMidpoint = bounds.getx() + (bounds.getwidth() / 2);
        double horizontalMidpoint = bounds.gety() + (bounds.getheight() / 2);
        boolean topQuadrant = (pRect.gety() <= horizontalMidpoint && pRect.gety() + pRect.getheight() <= horizontalMidpoint);
        boolean bottomQuadrant = (pRect.gety() > horizontalMidpoint);
        if (pRect.getx() <= verticalMidpoint && pRect.getx() + pRect.getwidth() <= verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (pRect.getx() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }
        return index;
    }

    private int getRIndex(MyRectangle pRect) {
        int index = -1;
        double verticalMidpoint = bounds.getx() + (bounds.getwidth() / 2);
        double horizontalMidpoint = bounds.gety() + (bounds.getheight() / 2);
        boolean topQuadrant = (pRect.gety() <= horizontalMidpoint && pRect.gety() + pRect.getheight() <= horizontalMidpoint);
        boolean bottomQuadrant = (pRect.gety() >= horizontalMidpoint);
        //centre
        if (pRect.getx() <= verticalMidpoint && pRect.getx() + pRect.getwidth() >= verticalMidpoint && pRect.gety() <= horizontalMidpoint && pRect.gety() + pRect.getheight() >= horizontalMidpoint)
            index = 4;
            //top
        else if (pRect.getx() <= verticalMidpoint && pRect.getx() + pRect.getwidth() >= verticalMidpoint && pRect.gety() <= horizontalMidpoint && pRect.gety() + pRect.getheight() <= horizontalMidpoint)
            index = 5;
            //bottom
        else if (pRect.getx() <= verticalMidpoint && pRect.getx() + pRect.getwidth() >= verticalMidpoint && pRect.gety() >= horizontalMidpoint && pRect.gety() + pRect.getheight() >= horizontalMidpoint)
            index = 6;
            //left
        else if (pRect.getx() <= verticalMidpoint && pRect.getx() + pRect.getwidth() <= verticalMidpoint && pRect.gety() <= horizontalMidpoint && pRect.gety() + pRect.getheight() >= horizontalMidpoint)
            index = 7;
            //right
        else if (pRect.getx() >= verticalMidpoint && pRect.getx() + pRect.getwidth() >= verticalMidpoint && pRect.gety() <= horizontalMidpoint && pRect.gety() + pRect.getheight() >= horizontalMidpoint)
            index = 8;
        if (pRect.getx() <= verticalMidpoint && pRect.getx() + pRect.getwidth() <= verticalMidpoint) {
            //topleft
            if (topQuadrant) {
                index = 1;
            }
            //bottomleft
            else if (bottomQuadrant) {
                index = 2;
            }
        } else if (pRect.getx() >= verticalMidpoint) {
            if (topQuadrant) {
                //topright
                index = 0;
            } else if (bottomQuadrant) {
                //bottomright
                index = 3;
            }
        }

        return index;
    }

    public synchronized void insert(Unit u) {
        if (nodes[0] != null) {
            try {
                int index = getIndex(new MyRectangle(u.getx(), u.gety(), u.getwidth(), u.getheight()));
                if (index != -1) {
                    nodes[index].insert(u);
                    return;
                }
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
        objects.add(u);
        if (level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(new MyRectangle(objects.get(i).getx(), objects.get(i).gety(), objects.get(i).getwidth(), objects.get(i).getheight()));
                if (index != -1) {
                    nodes[index].insert(objects.get(i));
                    objects.remove(i);
                } else {
                    i++;
                }
            }
        }
    }

    public boolean intersect(MyRectangle r1, MyRectangle r2) {
        if (r1.x >= r2.x + r2.width || r2.x >= r1.x + r1.width || r1.y >= r2.y + r2.height || r2.y >= r1.y + r1.height)
            return false;
        return true;
    }

    public synchronized ArrayList<Unit> canAdd(ArrayList<Unit> returnObjects, MyRectangle u) {
        int index = getRIndex(u);
        if (index != -1 && nodes[0] != null && index < 4) {
            nodes[index].canAdd(returnObjects, u);
        } else if (index == 4 && nodes[0] != null) {
            nodes[0].canAdd(returnObjects, u);
            nodes[1].canAdd(returnObjects, u);
            nodes[2].canAdd(returnObjects, u);
            nodes[3].canAdd(returnObjects, u);
        } else if (index == 5 && nodes[0] != null) {
            nodes[0].canAdd(returnObjects, u);
            nodes[1].canAdd(returnObjects, u);
        } else if (index == 6 && nodes[0] != null) {
            nodes[2].canAdd(returnObjects, u);
            nodes[3].canAdd(returnObjects, u);
        } else if (index == 7 && nodes[0] != null) {
            nodes[1].canAdd(returnObjects, u);
            nodes[2].canAdd(returnObjects, u);
        } else if (index == 8 && nodes[0] != null) {
            nodes[0].canAdd(returnObjects, u);
            nodes[3].canAdd(returnObjects, u);
        }
        returnObjects.addAll(objects);
        if (!returnObjects.isEmpty())
            returnObjects.removeIf(r -> !intersect(u, new MyRectangle(r.getx(), r.gety(), r.getwidth(), r.getheight())));
        return returnObjects;
    }
}
