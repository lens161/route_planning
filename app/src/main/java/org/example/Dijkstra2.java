package org.example;

import java.util.*;

public class Dijkstra2 {
    private Map<Integer, Double> distTo;    // distTo[v] = distance of shortest s->v path
    private Map<Integer, Edge> edgeTo;      // edgeTo[v] = last Edge on shortest s->v path
    private PriorityQueue<Node> pq;         // priority queue of nodes
    private Map<Integer, Integer> hopCounts; // Hop count from start for each vertex
    private Set<Integer> settled;           // Settled vertices to prevent revisiting

    public Dijkstra2() {
        // No need to initialize with size V
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        hopCounts = new HashMap<>();
        settled = new HashSet<>();
    }

    public double runDijkstra2(Graph g, int s, int t, double maxDistance, int maxHops, int[] nodeRanks) {
        // Clear data structures for a fresh run
        distTo.clear();
        edgeTo.clear();
        hopCounts.clear();
        settled.clear();

        distTo.put(s, 0.0);
        hopCounts.put(s, 0);

        // Initialize the priority queue with the starting node
        pq = new PriorityQueue<>();
        pq.add(new Node(s, 0.0));

        int settledNodes = 0;
        int maxSettledNodes = 1000; // Limit the number of nodes to settle

        // Main loop
        while (!pq.isEmpty() && settledNodes < maxSettledNodes) {
            Node currentNode = pq.poll();
            int v = currentNode.id;

            if (settled.contains(v)) continue; // Skip if already processed
            settled.add(v);
            settledNodes++;

            // Stop search if we reach the target within maxDistance
            double currentDist = distTo.get(v);
            if (v == t && currentDist <= maxDistance) {
                return currentDist;
            }

            // Early stopping if distance or hop count exceeds the limits
            int currentHops = hopCounts.getOrDefault(v, 0);
            if (currentDist > maxDistance || currentHops > maxHops) {
                continue;
            }

            // Relax edges from v
            for (Edge e : g.adj(v)) {
                relax(e, v, nodeRanks);
            }
        }

        // If no path is found within limits, return infinity
        return Double.POSITIVE_INFINITY;
    }

    // Relax edge e and update pq if needed
    private void relax(Edge e, int v, int[] nodeRanks) {
        int w = e.other(v);

        // Skip nodes with higher contraction levels
        if (nodeRanks[w] > nodeRanks[v]) {
            return;
        }

        double distV = distTo.get(v);
        double newDist = distV + e.weight();
        int newHopCount = hopCounts.getOrDefault(v, 0) + 1;

        double distW = distTo.getOrDefault(w, Double.POSITIVE_INFINITY);
        if (newDist < distW) {
            distTo.put(w, newDist);
            edgeTo.put(w, e);
            hopCounts.put(w, newHopCount);

            // Add new entry to priority queue
            pq.add(new Node(w, newDist));
            // Note: We might have multiple entries for the same node, but we check settled nodes before processing
        }
    }

    // Helper class to represent nodes in the priority queue
    private static class Node implements Comparable<Node> {
        int id;
        double dist;

        public Node(int id, double dist) {
            this.id = id;
            this.dist = dist;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.dist, other.dist);
        }
    }
}
