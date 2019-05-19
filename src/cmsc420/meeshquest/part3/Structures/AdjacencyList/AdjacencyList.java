package cmsc420.meeshquest.part2.Structures.AdjacencyList;

import cmsc420.meeshquest.part2.DataObject.City;
import cmsc420.meeshquest.part2.DataObject.Road;
import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Element;

import java.awt.geom.Arc2D;
import java.util.*;

public class AdjacencyList {
    private class Pair<A, B> {
        A key;
        B value;
        Pair(A key, B value) {
            this.key = key;
            this.value = value;
        }

        A getKey() { return this.key; }
        B getValue() {return this.value; }
    }
    public class Path implements Xmlable {
        LinkedList<Road> path;
        double length;
        int hops = 0;
        Path(HashMap<City, Pair<City, Double>> dist, City start, City end) {
            this.length = dist.get(end).getValue();
            this.path = new LinkedList<>();
            City prev  = end, current = dist.get(end).getKey();
            while (!prev.equals(start)) {
                hops++;
                path.push(new Road(current, prev));
                prev = current;
                current = dist.get(current).getKey();

            }
        }

        public Element toXml() {
            double a, b, c, theta, cM;
            String direction;
            Element Path = getBuilder().createElement("path");
            Path.setAttribute("length",  String.format("%.3f", length));
            Path.setAttribute("hops", Integer.toString(hops));

            Road path[] = this.path.toArray(new Road[0]);
            for (int i = 0; i < path.length; i++) {
                Road curr = path[i];
                Path.appendChild(curr.toReverseXml());
                if (i + 1 < path.length){
                    Road next = path[i + 1];
//                    a = curr.length();
//                    b = next.length();
//                    c = curr.getStart().distance(next.getEnd());
//                    cM = (next.getEnd().getY() - curr.getStart().getY())/(next.getEnd().getX() - curr.getStart().getX());
//                    theta = acos(
//                            (pow(a, 2) + pow(b, 2) - pow(c, 2))
//                                    /
//                                    (2*a*b)
//                    );

                    Arc2D.Double arc = new Arc2D.Double();
                    arc.setArcByTangent(curr.getStart(), curr.getEnd(), next.getEnd(),1);

                    theta = arc.getAngleExtent();
                    if (theta >= 45) direction = "right";
                    else if (theta < -45) direction = "left";
                    else direction = "straight";
                    Path.appendChild(getBuilder().createElement(direction));
                }
            }
            return Path;
        }
    }

    private HashMap<City, HashSet<City>> graph = new HashMap<>();

    public AdjacencyList() { }

    public void add(City city) {
        if (!graph.containsKey(city))
            graph.put(city, new HashSet<>());
    }

    public void addEdge(Road edge) {
        City c1 = edge.getStart(), c2 = edge.getEnd();
        add(c1);
        add(c2);
        graph.get(c1).add(c2);
        graph.get(c2).add(c1);
    }

    public boolean hasEdge(Road edge) {
        return graph.containsKey(edge.getStart())
                && graph.get(edge.getStart()).contains(edge.getEnd());
    }

    public boolean containsKey(City key) {
        return graph.containsKey(key);
    }

    public boolean isEmpty() {
        return graph.isEmpty();
    }

    public Collection<City> getNeighbors(City city) {
        return graph.get(city);
    }

    public Path shortestPath(City start, City end) {
        HashSet<City> visited = new HashSet<>();
        HashMap<City, Pair<City, Double>> dist  = new HashMap<>();
        PriorityQueue<City> Q = new PriorityQueue<>(new Comparator<City>() {
            public int compare(City o1, City o2) {
                double  dist1 = dist.get(o1).getValue(),
                        dist2 = dist.get(o2).getValue();
                if (dist1 < dist2) return -1;
                else if (dist1 > dist2) return 1;
                else return o1.compareTo(o2);
            }
        });


        for (City vertex : graph.keySet()) {
            if (!vertex.equals(start)) {
                dist.put(vertex, new Pair<>(null, Double.MAX_VALUE));
            }
        }
        dist.put(start, new Pair<City, Double>(null, 0.0));
        Q.add(start);
        while (!Q.isEmpty()) {
            City current = Q.poll();
            if (current.equals(end)) return new Path(dist, start, end);
            if (!visited.contains(current)) {
                visited.add(current);
                double currDist = dist.get(current).getValue();
                //loops through adjacency list to get neighbors
                for (City neighbor : graph.get(current)) {
                    //calculates new distance by routing path through current;
                    double distanceThroughCurr = currDist + current.distance(neighbor);
                    if (distanceThroughCurr < dist.get(neighbor).getValue()) {
                        dist.put(neighbor, new Pair<>(current, distanceThroughCurr));
                        Q.add(neighbor);
                    }
                }
            }
        }
        return null;

    }

}
