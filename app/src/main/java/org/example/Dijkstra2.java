package org.example;


import java.util.*;

public class Dijkstra2 {
    private Map<Integer, Double> distTo;    
    private Map<Integer, Edge> edgeTo;      
    private PriorityQueue<Node> pq; 
    // Hop count from start vertices        
    private Map<Integer, Integer> hopCounts;
    // Settled vertices to prevent visiting those again 
    private Set<Integer> settled;
    // Nodes currently in the queue           
    private Set<Integer> inQueue; 
    // Node ranks for contraction hierarchy          
    private int[] rank; 

    public Dijkstra2() {
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        hopCounts = new HashMap<>();
        settled = new HashSet<>();
        inQueue = new HashSet<>();
    }
    

    public double runDijkstra2(Graph g, int s, int t, double maxDistance, int maxHops, int[] nodeRanks) {
        this.rank = nodeRanks;

        // Clear everything before a new run
        distTo.clear();
        edgeTo.clear();
        hopCounts.clear();
        settled.clear();
        inQueue.clear();

        distTo.put(s, 0.0);
        hopCounts.put(s, 0);

        // Initialize the priority queue with the starting node
        pq = new PriorityQueue<>();
        pq.add(new Node(s, 0.0));
        inQueue.add(s);

        int settledNodes = 0;
        int maxSettledNodes = 100;

        // Main loop
        while (!pq.isEmpty() && settledNodes < maxSettledNodes) {
            Node currentNode = pq.poll();
            int v = currentNode.id;
            inQueue.remove(v);

            if (settled.contains(v)) continue; // Skip if already processed
            settled.add(v);
            settledNodes++;

            // Stop search if we reach the target within maxDistance
            double currentDist = distTo.get(v);
            if (v == t && currentDist <= maxDistance) {
                return currentDist;
            }

            // stop eraly if distance or hop count exceed the limits.
            int currentHops = hopCounts.getOrDefault(v, 0);
            if (currentDist > maxDistance || currentHops > maxHops) {
                continue;
            }

            // relax edges from v 
            for (Edge e : g.adj(v)) {
                relax(e, v);
            }
        }

        // if no path is found return infinity
        return Double.POSITIVE_INFINITY;
    }

    // Relax edge e and update pq if needed
    private void relax(Edge e, int v) {
        int w = e.other(v);

        // Skip if node has higher contraction level because the algorithm will get to this later automatically
        if (rank[w] > rank[v]) {
            return;
        }

        // do the normal dijkstra stuffs - sort of copied from the algs4 library
        double distV = distTo.get(v);
        double newDist = distV + e.weight();
        int newHopCount = hopCounts.getOrDefault(v, 0) + 1;

        double distW = distTo.getOrDefault(w, Double.POSITIVE_INFINITY);
        if (newDist < distW) {
            distTo.put(w, newDist);
            edgeTo.put(w, e);
            hopCounts.put(w, newHopCount);

            if (!inQueue.contains(w)) {
                // Add new entry to priority queue
                pq.add(new Node(w, newDist));
                inQueue.add(w);
            }
        }
    }
}
