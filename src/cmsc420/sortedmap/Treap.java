package cmsc420.sortedmap;

import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Element;

import java.util.*;

public class Treap<K,V> extends AbstractMap<K,V> implements SortedMap<K,V>, Xmlable {
    private Node root;
    private final Node Empty = new Empty();
    private int size = 0, modCount = 0;
    private Comparator<K> comparator = new Comparator<K>(){
        public int compare(K o1, K o2) {
            return ((Comparable<K>)o1).compareTo(o2);
        }
        public boolean equals(Object obj) {
            return false;
        }
    };
    private static Random random = new Random();

    class Empty extends Node implements Xmlable {
        private Empty() {
            super();
        }

        boolean containsKey(K key) {
            return false;
        }
        Node put(K key, V value) {
            return new Node(key, value);
        }
        V get(K key) { return null; }
        K firstKey() { throw new NoSuchElementException();}
        K lastKey() { throw new NoSuchElementException();}
        ArrayList<Entry<K, V>> toArray(ArrayList<Entry<K, V>> acc) { return acc; }

        public Element toXml() {
            return getBuilder().createElement("emptyChild");
        }
    }
    public class Node extends AbstractMap.SimpleEntry<K,V> implements SortedMap.Entry<K,V>, Xmlable {
        private Node left, right;
        private int priority;

        public Node(K key, V value) {
            super(key, value);
            priority = random.nextInt();
            left =  Empty;
            right = Empty;
        }
        Node() {
            super(null, null);
        }

        Node put(K keyToAdd, V valueToAdd) {
            Node result = this;
            int res = comparator.compare(this.getKey(), keyToAdd);

            if (res > 0) {
                left = left.put(keyToAdd, valueToAdd);
                if (left.priority > this.priority) {
                    result = left;
                    this.left = result.right;
                    result.right = this;
                }
            } else if (res < 0){
                right = right.put(keyToAdd, valueToAdd);
                if (right.priority > this.priority) {
                    result = right;
                    this.right = result.left;
                    result.left = this;
                }
            } else {
                this.setValue(valueToAdd);
            }
            return result;
        }

        boolean containsKey(K key) {
            if (key.equals(this.getKey())) return true;
            return (comparator.compare(this.getKey(), key) < 0)
                    ? right.containsKey(key)
                    : left.containsKey(key);
        }

        V get(K key) {
            if (this.getKey() == key || this.getKey().equals(key)) return this.getValue();
            return (comparator.compare(this.getKey(), key) < 0)
                    ? right.get(key)
                    : left.get(key);

        }

        ArrayList<Entry<K, V>> toArray(ArrayList<Entry<K, V>> acc) {
            acc = left.toArray(acc);
            acc.add(this);
            return right.toArray(acc);
        }

        K firstKey() { return (left instanceof Treap.Empty) ? this.getKey() : left.firstKey(); }
        K lastKey() { return (right instanceof Treap.Empty) ? this.getKey() : right.lastKey();}

        public Element toXml() {
            Element node = getBuilder().createElement("node");
            node.setAttribute("key", this.getKey().toString());
            node.setAttribute("priority", Integer.toString(priority));
            node.appendChild(right.toXml());
            node.appendChild(left.toXml());
            return node;
        }

    }

    public Treap() {
        root = Empty;
    }

    public Treap (Comparator<K> comparator) {
        this();
        this.comparator = comparator;
    }

    public V put(K key, V value) {
        if (key == null) throw new NullPointerException();
        if (!root.containsKey(key)) size++;
        root = root.put(key, value);
        modCount++;

        return value;
    }

    public V get(Object key) {
        if (key == null) throw new NullPointerException();
        return root.get((K)key);
    }
    public int getModCount() {
        return this.modCount;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean containsKey(Object key) {
        if (key == null) throw new NullPointerException();
        return root.containsKey((K)key);
    }
    public Comparator<? super K> comparator() {
        return comparator;
    }

    public void clear() {
        size = 0;
        modCount = 0;
        root = new Empty();
    }
    public K firstKey() {
        return root.firstKey();
    }
    public K lastKey() {
        return root.lastKey();
    }
    public Map.Entry<K,V>[] toArray() {
        return root.toArray(new ArrayList<>()).toArray(new Map.Entry[0]);
    }

    public Element toXml() {
        Element treap = getBuilder().createElement("treap");
        treap.setAttribute("cardinality", Integer.toString(this.size));
        treap.appendChild(this.root.toXml());
        return treap;
    }
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            public int size() {
                return Treap.this.size();
            }

            public boolean isEmpty() {
                return size() == 0;
            }

            public boolean contains(Object o) {
                if (!(o instanceof Entry)) return false;
                Entry e = (Entry)o;
                V val = Treap.this.get(e.getKey());
                return e.getValue().equals(val);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                Set<Entry<K,V>> s = (Set<Entry<K,V>>)c;
                for (Entry<K,V> ele : s) {
                    if (ele == null) throw new NullPointerException();
                    if (!this.contains(ele)) return false;
                }
                return true;
            }

            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    private final int modCount = Treap.this.getModCount();
                    private int currentIndex = 0;
                    private Map.Entry<K, V>[] array = Treap.this.toArray();

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

            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof Set)) return false;
                Set s = (Set)o;
                if (s.size() != size()) return false;
                return this.containsAll(s);
            }

            public int hashCode() {
                int sum = 0;
                for (Map.Entry<K,V> me : this) {
                    sum += me.hashCode();
                }

                return sum;
            }
        };
    }
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        class SubMap extends AbstractMap<K, V> implements SortedMap<K, V> {
            private K from, to;
            private Comparator<? super K> comp = Treap.this.comparator();

            SubMap(K fromKey, K toKey) {
                if (((Comparable<K>)fromKey).compareTo(toKey) > 0) throw new IllegalArgumentException("from must be less than to");
                this.from = fromKey;
                this.to = toKey;
            }

            private boolean inRange(K key) {
                return (comp.compare(from, key) <= 0 && comp.compare(to, key) > 0);
            }

            private Entry<K,V>[] circumscribe(K fromKey, K toKey) {
                Map.Entry<K,V>[] arr = Treap.this.toArray();
                int i = 0, start = 0;
                while (i < arr.length && comp.compare(arr[i].getKey(), toKey) < 0) {
                    if (comp.compare(arr[i].getKey(), fromKey) < 0) start++;
                    i++;
                }

                return Arrays.copyOfRange(arr,start, i);
                //at this point start corresponds to the closest key to fromKey and i is closest key to toKey;\
            }

            public Set<Entry<K, V>> entrySet() {
                return new AbstractSet<Entry<K, V>>() {
                    private K fromKey = from, toKey = to;

                    public Iterator<Entry<K, V>> iterator() {
                        return new Iterator<Entry<K, V>>() {
                            private int modCount = Treap.this.getModCount();
                            private Map.Entry<K,V>[] array = circumscribe(fromKey, toKey);
                            private int index = 0;

                            public boolean hasNext() {
                                return array.length > index;
                            }

                            public Entry<K, V> next() {
                                if (modCount != Treap.this.getModCount()) throw new ConcurrentModificationException();
                                if (hasNext()) return array[index++];
                                throw new NoSuchElementException();
                            }
                        };
                    }

                    @Override
                    public boolean contains(Object o) {
                        Entry<K,V> e = (Entry<K,V>)o;
                        if (!inRange(e.getKey())) return false;
                        V val = Treap.this.get(e.getKey());
                        return val.equals(e.getValue());
                    }

                    @Override
                    public boolean containsAll(Collection<?> c) {
                        Set<Entry<K,V>> s = (Set<Entry<K,V>>)c;
                        for (Entry<K,V> ele : s) {
                            if (ele == null) throw new NullPointerException();
                            if (!this.contains(ele)) return false;
                        }

                        return true;
                    }

                    @Override
                    public int hashCode() {
                        int sum = 0;
                        for (Entry<K,V> ele : this) sum += ele.hashCode();
                        return sum;
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (o == this) return true;
                        if (!(o instanceof Set)) return false;
                        Set s = (Set)o;
                        if (s.size() != size()) return false;
                        return this.containsAll(s);
                    }

                    public int size() {
                        int count = 0;
                        Iterator I = iterator();
                        while (I.hasNext()) {
                            count++;
                            I.next();
                        }
                        return count;
                    }

                };
            }

            public Comparator<? super K> comparator() {
                return Treap.this.comparator();
            }

            public int size() { return this.entrySet().size(); }

            public V get(Object key) {
                K ele = (K)key;
                if (!inRange(ele)) return null;
                return Treap.this.get(key);
            }

            public V put(K key, V value) {
                if (key == null) throw new NullPointerException();
                if (!inRange(key)) throw new IllegalArgumentException();
                return Treap.this.put(key, value);
            }

            public SortedMap<K, V> subMap(K fromKey, K toKey) {
                if (comparator().compare(fromKey, this.from) < 0 || comparator().compare(toKey, this.to) > 0) {
                    throw new IllegalArgumentException("from or to lies outside of range");
                }
                return Treap.this.subMap(fromKey, toKey);
            }

            @Override
            public SortedMap<K, V> headMap(K toKey) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SortedMap<K, V> tailMap(K fromKey) {
                throw new UnsupportedOperationException();
            }

            @Override
            public K firstKey() {
                return entrySet().iterator().next().getKey();
            }

            @Override
            public K lastKey() {
                Iterator<Map.Entry<K,V>> i = entrySet().iterator();
                Map.Entry<K,V> curr = i.next();
                while (i.hasNext()) curr = i.next();
                return curr.getKey();
            }
        }
        return new SubMap(fromKey, toKey);
    }

    //do not need to implement
    public SortedMap<K,V> headMap(K toKey) {throw new UnsupportedOperationException();}
    public SortedMap<K,V> tailMap(K fromKey) {throw new UnsupportedOperationException();}
    public Set<K> keySet() {throw new UnsupportedOperationException();}
    public Collection<V> values() {throw new UnsupportedOperationException();}
    public V remove(Object key) {throw new UnsupportedOperationException();}

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SortedMap)) return false;
        SortedMap sm = (SortedMap)o;
        return this.entrySet().equals(sm.entrySet());
    }


    public int hashCode() {
        return this.entrySet().hashCode();
    }

}
