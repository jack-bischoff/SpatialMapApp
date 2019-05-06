package cmsc420.meeshquest.part2.Structures.AdjacencyList;

import cmsc420.meeshquest.part2.DataObject.Road;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

public class AdjacencyList {
    TreeMap<String, TreeSet<Road>> graph = new TreeMap<>();

    public AdjacencyList() { }

    public void add(String name) {
        if (!graph.containsKey(name))
            graph.put(name, new TreeSet<>());
    }

    public void addEdge(Road edge) {
        String c1 = edge.getStart().getName(), c2 = edge.getEnd().getName();
        add(c1);
        add(c2);
        graph.get(c1).add(edge);
        graph.get(c2).add(edge);
    }

    public boolean hasEdge(Road edge) {
        return graph.containsKey(edge.getStart().getName())
                && graph.get(edge.getStart().getName()).contains(edge);
    }

    public boolean containsKey(String name) {
        return graph.containsKey(name);
    }

    public boolean isEmpty() {
        return graph.isEmpty();
    }

    public Collection<Road> getNeighbors(String name) {
        return graph.get(name);
    }

    public Road shortestPath() {
        //dijkstra!
        return null;
    }

}
