package cmsc420.meeshquest.part2.Structures.Dictionary;

import java.util.*;

public class Treap<K,V> extends AbstractMap<K,V> implements SortedMap<K,V> {
    TreapNode root = new Empty();
    int size = 0, modCount = 0;
    Comparator<K> comparator;
    static Random random = new Random();

    private class naturalComparator implements Comparator<K> {
        public int compare(K o1, K o2) {
            return ((Comparable<K>)o1).compareTo(o2);
        }

        public boolean equals(Object obj) {
            return false;
        }
    }
    private abstract class TreapNode extends AbstractMap.SimpleEntry<K,V> implements SortedMap.Entry<K,V> {
        int priority;
        TreapNode parent;
        TreapNode left, right;

        TreapNode(K key, V value) {
            super(key, value);
        }

        abstract boolean find(K key);
        abstract TreapNode put(K key, V value);
    }
    private class Empty extends TreapNode {
        TreapNode parent;
        Empty(TreapNode parent) {
            super(null, null);
        }
        Empty() { this(null); }

        boolean find(K key) {
            return false;
        }

        Full put(K key, V value) {
            return new Full(key, value, parent);
        }
    }
    private class Full extends TreapNode {
        public Full(K key, V value, TreapNode parent) {
            super(key, value);
            this.parent = parent;
            priority = random.nextInt();
            left = new Empty(this);
            right = new Empty(this);
        }

        TreapNode put(K keyToAdd, V valueToAdd) {
            K thisKey = this.getKey();
            int relation = comparator.compare(thisKey, keyToAdd);

            if (relation == -1) {
                left = left.put(keyToAdd, valueToAdd);
                if (left.priority > this.priority) {
                    TreapNode cRight = this.right;
                    this.right = parent;
                    parent.left = cRight;
                    this.parent = parent.parent;
                    parent.parent = this;
                }
            } else {
                right = right.put(keyToAdd, valueToAdd);
                if (right.priority > this.priority) {
                    TreapNode cLeft = this.left;
                    this.right = parent;
                    parent.right = cLeft;
                    this.parent = parent.parent;
                    parent.parent = this;
                }
            }
            return this;
        }

        boolean find(K key) {
            if (key.equals(this.getKey())) return true;
            return (comparator.compare(this.getKey(), key) < 0) ? left.find(key) : right.find(key);
        }


    }

    public Treap() {
        this.comparator = new naturalComparator();
    }

    public Treap (Comparator<K> comparator) {
        this.comparator = comparator;
    }

    public V put(K key, V value) {
        root = root.put(key, value);
        size++;
        modCount++;
        return value;
    }

    public Set<Entry<K, V>> entrySet() {
        return null;
    }
    public Comparator<? super K> comparator() {
        return null;
    }
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return null;
    }
    public K firstKey() {
        return null;
    }
    public K lastKey() {
        return null;
    }
    public SortedMap<K,V> headMap(K toKey) {throw new UnsupportedOperationException();}
    public SortedMap<K,V> tailMap(K fromKey) {throw new UnsupportedOperationException();}
    public Set<K> keySet() {throw new UnsupportedOperationException();}
    public Collection<V> values() {throw new UnsupportedOperationException();}
    public V remove(Object key) {throw new UnsupportedOperationException();}
}
