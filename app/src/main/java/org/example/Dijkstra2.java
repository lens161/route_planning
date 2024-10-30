package org.example;

import edu.princeton.cs.algs4.IndexMinPQ;
import java.util.HashMap;
import java.util.Map;

public class Dijkstra2 {
    private double[] distTo;            // distTo[v] = distance of shortest s->v path
    private Edge[] edgeTo;              // edgeTo[v] = last Edge on shortest s->v path
    private IndexMinPQ<Double> pq;      // priority queue of vertices
    private Map<Integer, Integer> hopCounts; // Hop count from start for each vertex

    public Dijkstra2() {
        // No initialization needed here
    }

    public double runDijkstra2(Graph g, long s, long t, double maxDistance, int maxHops) {
        int start = g.getIndexForVertex(s);
        int target = g.getIndexForVertex(t);

        int vertexCount = g.V();
        distTo = new double[vertexCount];
        edgeTo = new Edge[vertexCount];
        hopCounts = new HashMap<>();  // Initialize hop count map

        for (int i = 0; i < vertexCount; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }
        distTo[start] = 0.0;
        hopCounts.put(start, 0);  // Start node has 0 hops

        // Initialize the priority queue with the starting node
        pq = new IndexMinPQ<>(vertexCount);
        pq.insert(start, distTo[start]);

        // Main loop
        while (!pq.isEmpty()) {
            int v = pq.delMin();

            // Stop search if we reach the target within maxDistance
            if (v == target && distTo[v] <= maxDistance) {
                return distTo[v];
            }

            // Early stopping if distance or hop count exceeds the limits
            if (distTo[v] > maxDistance || hopCounts.getOrDefault(v, 0) > maxHops) {
                continue;
            }

            // Relax edges from v
            for (Edge e : g.adj(v)) {
                relax(e, v);
            }
        }

        // If no path is found within limits, return infinity
        return Double.POSITIVE_INFINITY;
    }

    // Relax edge e and update pq if needed
    private void relax(Edge e, int v) {
        int w = e.other(v);
        double newDist = distTo[v] + e.weight();
        int newHopCount = hopCounts.getOrDefault(v, 0) + 1;

        if (newDist < distTo[w]) {
            distTo[w] = newDist;
            edgeTo[w] = e;

            // Update hop count for node w
            hopCounts.put(w, newHopCount);

            if (pq.contains(w)) {
                pq.decreaseKey(w, distTo[w]);
            } else {
                pq.insert(w, distTo[w]);
            }
        }
    }
}
