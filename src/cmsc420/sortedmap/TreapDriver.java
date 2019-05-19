package cmsc420.sortedmap;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TreapDriver {
    public static void main(String[] args) {
        Treap<String, Integer> treap = new Treap<>();
        Treap<String, Integer> treap2 = new Treap<>();
        TreeMap<String, Integer> treemap = new TreeMap<>();

        String[] strings = new String[]{"Zack", "Joseph", "David", "Daniel", "Jacob", "Ernest", "Fred"};
        String[] strings2 = new String[]{"Wack", "Loseph", "Lavid", "Waniel", "Wacob", "Wernest", "Wred"};
        Integer[] integers = new Integer[]{30, 29, 41, 36, 78, 12, 22};
        Integer[] integers2 = new Integer[]{31, 291, 411, 361, 781, 112, 212};

        for (int i = 0; i < integers.length; i++) {
            String Str = strings[i];
            Integer Int = integers[i];

            treap.put(Str, Int);
            treemap.put(Str, Int);
        }


    }



}
