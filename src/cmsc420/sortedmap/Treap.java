package cmsc420.meeshquest.part2.Structures.Dictionary;

import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Element;

import java.util.*;

public class Treap<K,V> extends AbstractMap<K,V> implements SortedMap<K,V>, Xmlable {
    private Node root = new Empty();
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

    private class Empty extends Node implements Xmlable {
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
    private class Node extends AbstractMap.SimpleEntry<K,V> implements SortedMap.Entry<K,V>, Xmlable {
        private Node left, right;
        private int priority;

        public Node(K key, V value) {
            super(key, value);
            priority = random.nextInt();
            left = new Empty();
            right = new Empty();
        }
        Node() {
            super(null, null);
        }

        Node put(K keyToAdd, V valueToAdd) {
            Node result = this;
            if (comparator.compare(this.getKey(), keyToAdd) > 0) {
                left = left.put(keyToAdd, valueToAdd);
                if (left.priority > this.priority) {
                    result = left;
                    this.left = result.right;
                    result.right = this;
                }
            } else {
                right = right.put(keyToAdd, valueToAdd);
                if (right.priority > this.priority) {
                    result = right;
                    this.right = result.left;
                    result.left = this;
                }
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

    public Treap() { }
    public Treap (Comparator<K> comparator) {
        this.comparator = comparator;
    }

    public V put(K key, V value) {
        if (key == null) throw new NullPointerException();
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

    public int size() {
        return size;
    }

    public boolean containsKey(Object key) {
        return root.containsKey((K)key);
    }
    public Comparator<? super K> comparator() {
        return comparator;
    }

    public void clear() {
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
                Map.Entry<K, V> me = (Map.Entry<K, V>) o;
                return Treap.this.containsKey(me);
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

            public boolean containsAll(Collection<?> c) {
                Collection<Map.Entry<K, V>> meC = (Collection<Map.Entry<K, V>>) c;
                for (Map.Entry<K, V> ele : meC) if (!contains(ele)) return false;
                return true;
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

            public Set<Entry<K, V>> entrySet() {
                return new AbstractSet<Entry<K, V>>() {
                    private K fromKey = from, toKey = to;

                    public Iterator<Entry<K, V>> iterator() {
                        return new Iterator<Entry<K, V>>() {
                            private int modCount = Treap.this.getModCount();
                            private Map.Entry<K,V>[] array = Treap.this.toArray();
                            private int index = getStartIndex();

                            private int getStartIndex() {
                                for (int i = 0; i < array.length; i++) {
                                    K current = array[i].getKey();
                                    int c = comp.compare(current, from);
                                    if (c >= 0) return i;
                                }
                                return array.length;
                            }
                            public boolean hasNext() {
                                return array.length > index && inRange(array[index].getKey());
                            }

                            public Entry<K, V> next() {
                                if (modCount != Treap.this.getModCount()) throw new ConcurrentModificationException();
                                if (hasNext()) return array[index++];
                                throw new NoSuchElementException();
                            }
                        };
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
}
