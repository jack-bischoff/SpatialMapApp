package cmsc420.meeshquest.part2.Structures.AdjacencyList;

import cmsc420.meeshquest.part2.DataObject.City;
import cmsc420.meeshquest.part2.DataObject.Road;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class AdjacencyList {
    private HashMap<City, HashSet<Road>> graph = new HashMap<>();

    public AdjacencyList() { }

    public void add(City city) {
        if (!graph.containsKey(city))
            graph.put(city, new HashSet<>());
    }

    public void addEdge(Road edge) {
        City c1 = edge.getStart(), c2 = edge.getEnd();
        add(c1);
        add(c2);
        graph.get(c1).add(edge);
        graph.get(c2).add(edge);
    }

    public boolean hasEdge(Road edge) {
        return graph.containsKey(edge.getStart())
                && graph.get(edge.getStart()).contains(edge);
    }

    public boolean containsKey(City key) {
        return graph.containsKey(key);
    }

    public boolean isEmpty() {
        return graph.isEmpty();
    }

    public Collection<Road> getNeighbors(City city) {
        return graph.get(city);
    }

    public Road shortestPath() {
        //dijkstra!
        return null;
    }

}
