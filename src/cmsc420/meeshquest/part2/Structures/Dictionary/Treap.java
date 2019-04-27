package cmsc420.meeshquest.part2.Structures.Dictionary;

import java.util.*;

public class Treap<K,V> extends AbstractMap<K,V> implements SortedMap<K,V> {
    private Node root = new Empty();
    private int size = 0, modCount = 0;
    private Comparator<K> comparator;
    private static Random random = new Random();

    private class naturalComparator implements Comparator<K> {
        public int compare(K o1, K o2) {
            return ((Comparable<K>)o1).compareTo(o2);
        }

        public boolean equals(Object obj) {
            return false;
        }
    }
    protected class EntrySet implements Set<Entry<K,V>> {
        EntrySet() {}
        public int size() { return Treap.this.size(); }
        public boolean isEmpty() { return size() == 0; }
        public boolean contains(Object o) {
            Map.Entry<K,V> me = (Map.Entry<K, V>)o;
            return Treap.this.containsKey(me);
        }

        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                private final int modCount = Treap.this.getModCount();
                private int currentIndex = 0;
                private Map.Entry<K,V>[] array = Treap.this.toArray();

                public boolean hasNext() {
                    return array.length > currentIndex;
                }
                public Entry<K, V> next() {
                    if (modCount != Treap.this.getModCount()) throw new ConcurrentModificationException();
                    if (hasNext()) return array[currentIndex++];
                    throw new NoSuchElementException();
                }
            };
        }
        public void clear() {
            Treap.this.clear();
        }

        public boolean containsAll(Collection<?> c) {
            Collection<Map.Entry<K,V>> meC = (Collection<Map.Entry<K,V>>)c;
            for (Map.Entry<K,V> ele : meC) if (!contains(ele)) return false;
            return true;
        }
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean add(Entry<K, V> kvEntry) {
            throw new UnsupportedOperationException();
        }
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
           throw new UnsupportedOperationException();
        }
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }
        public Object[] toArray() { throw new UnsupportedOperationException(); }
        public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException(); }

    }
    private class Empty extends Node {
        Empty(Node parent) {
            super();
            this.parent = parent;
        }
        Empty() { this(null); }

        boolean containsKey(K key) {
            return false;
        }
        Node put(K key, V value) {
            return new Node(key, value, parent);
        }
        V get(K key) { return null; }
        K firstKey() { throw new NoSuchElementException();}
        K lastKey() { throw new NoSuchElementException();}
        ArrayList<Entry<K, V>> toArray(ArrayList<Entry<K, V>> acc) { return acc; }
        ArrayList<Entry<K, V>> toDescendingArray(ArrayList<Entry<K, V>> acc) { return acc; }
    }
    private class Node extends AbstractMap.SimpleEntry<K,V> implements SortedMap.Entry<K,V> {
        Node parent;
        private Node left, right;
        private int priority;

        public Node(K key, V value, Node parent) {
            super(key, value);
            this.parent = parent;
            priority = random.nextInt();
            left = new Empty(this);
            right = new Empty(this);
        }
        Node() {super(null, null);}

        Node put(K keyToAdd, V valueToAdd) {
            K thisKey = this.getKey();
            int relation = comparator.compare(thisKey, keyToAdd);

            if (relation == -1) {
                left = left.put(keyToAdd, valueToAdd);
                if (left.priority > this.priority) {
                    Node cRight = this.right;
                    this.right = parent;
                    parent.left = cRight;
                    this.parent = parent.parent;
                    parent.parent = this;
                }
            } else {
                right = right.put(keyToAdd, valueToAdd);
                if (right.priority > this.priority) {
                    Node cLeft = this.left;
                    this.right = parent;
                    parent.right = cLeft;
                    this.parent = parent.parent;
                    parent.parent = this;
                }
            }
            return this;
        }

        boolean containsKey(K key) {
            if (key.equals(this.getKey())) return true;
            return (comparator.compare(this.getKey(), key) < 0)
                    ? left.containsKey(key)
                    : right.containsKey(key);
        }

        V get(K key) {
            if (this.getKey() == key || this.getKey().equals(key)) return this.getValue();
            return (comparator.compare(this.getKey(), key) < 0)
                    ? left.get(key)
                    : right.get(key);

        }
        ArrayList<Entry<K, V>> toArray(ArrayList<Entry<K, V>> acc) {
            acc = left.toArray(acc);
            acc.add(this);
            return right.toArray(acc);
        }

        K firstKey() { return (left instanceof Treap.Empty) ? this.getKey() : left.firstKey(); }
        K lastKey() { return (right instanceof Treap.Empty) ? this.getKey() : right.lastKey();}

    }

    public Treap() {
        this.comparator = new naturalComparator();
    }
    public Treap (Comparator<K> comparator) {
        this.comparator = comparator;
    }

    public V put(K key, V value) {
        if (!containsKey(key)) {
            root = root.put(key, value);
            size++;
            modCount++;
        }
        return value;
    }

    public int getModCount() {
        return this.modCount;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(Object key) {
        return root.containsKey((K)key);
    }

    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }
    public Comparator<? super K> comparator() {
        return comparator;
    }
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        throw new UnsupportedOperationException();
    }
    public void clear() { root = new Empty(); }
    public K firstKey() {
        return root.firstKey();
    }
    public K lastKey() { return root.lastKey(); }
    public Map.Entry<K,V>[] toArray() {
        return root.toArray(new ArrayList<>()).toArray(new Map.Entry[0]);
    }


//do not need to implement
    public SortedMap<K,V> headMap(K toKey) {throw new UnsupportedOperationException();}
    public SortedMap<K,V> tailMap(K fromKey) {throw new UnsupportedOperationException();}
    public Set<K> keySet() {throw new UnsupportedOperationException();}
    public Collection<V> values() {throw new UnsupportedOperationException();}
    public V remove(Object key) {throw new UnsupportedOperationException();}
}
