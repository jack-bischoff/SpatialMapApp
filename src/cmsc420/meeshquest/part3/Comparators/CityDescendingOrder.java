package cmsc420.meeshquest.part3.Comparators;

import cmsc420.meeshquest.part3.DataObject.City;

import java.util.Comparator;

public class CityDescendingOrder implements Comparator<City> {

    public int compare(City o1, City o2) {
        return -1 * o1.getName().compareTo(o2.getName());
    }
}
