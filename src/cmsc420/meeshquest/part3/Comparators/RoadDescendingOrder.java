package cmsc420.meeshquest.part3.Comparators;

import cmsc420.meeshquest.part3.DataObject.Road;

import java.util.Comparator;

public class RoadDescendingOrder implements Comparator<Road> {
    public int compare(Road o1, Road o2) {
        int res = o1.getStart().getName().compareTo(o2.getStart().getName());
        if (res == 0)
            res = o1.getEnd().getName().compareTo(o2.getEnd().getName());
        return -1 * res;
    }
}
