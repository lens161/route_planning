package org.example;

import edu.princeton.cs.algs4.IndexMinPQ;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class BiDirectionalDijkstra {
    private HashMap<Long, Double> distToForward;  
    private HashMap<Long, Double> distToBackward; 
    private HashMap<Long, Edge> edgeToForward;   
    private HashMap<Long, Edge> edgeToBackward;   
    private Set<Long> visitedForward;            
    private Set<Long> visitedBackward;
    private IndexMinPQ<Double> pqForward;         
    private IndexMinPQ<Double> pqBackward;        
    private int relaxedEdgesCount;               

    private volatile double bestPathDistance;     
    private volatile Long meetingPoint;  

    private final Object lock = new Object();

    public BiDirectionalDijkstra() {
        this.relaxedEdgesCount = 0;
    }

    public double runBiDirectionalDijkstra(Graph g, long s, long t) {
        this.relaxedEdgesCount = 0;
        this.bestPathDistance = Double.POSITIVE_INFINITY;
        this.meetingPoint = null;

        // Initialize data structures for both directions
        distToForward = new HashMap<>();
        distToBackward = new HashMap<>();
        edgeToForward = new HashMap<>();
        edgeToBackward = new HashMap<>();
        visitedForward = new HashSet<>();
        visitedBackward = new HashSet<>();
        
        for (Long v : g.getVertexValues()) {
            distToForward.put(v, Double.POSITIVE_INFINITY);
            distToBackward.put(v, Double.POSITIVE_INFINITY);
            edgeToForward.put(v, null);
            edgeToBackward.put(v, null);
        }

        distToForward.put(s, 0.0);
        distToBackward.put(t, 0.0);

        int sIndex = g.getIndexForVertex(s);
        int tIndex = g.getIndexForVertex(t);

        pqForward = new IndexMinPQ<>(g.V());
        pqBackward = new IndexMinPQ<>(g.V());

        pqForward.insert(sIndex, 0.0);
        pqBackward.insert(tIndex, 0.0);

        AtomicBoolean pathFound = new AtomicBoolean(false);


        // Thread t1 for the forward search
        Thread t1 = new Thread(() -> {
            while (!pqForward.isEmpty() && !pathFound.get()) {
                relaxEdges(g, pqForward, distToForward, edgeToForward, distToBackward, visitedForward, visitedBackward, "Forward", pathFound);
            }
        });

        // Thread t2 for the backward search
        Thread t2 = new Thread(() -> {
            while (!pqBackward.isEmpty() && !pathFound.get()) {
                relaxEdges(g, pqBackward, distToBackward, edgeToBackward, distToForward, visitedBackward, visitedForward, "Backward", pathFound);
            }
        });
        t1.start();
        t2.start();

                try {
                    t1.join();
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

  
        return bestPathDistance == Double.POSITIVE_INFINITY ? -1 : bestPathDistance;
    }

    private void relaxEdges(Graph g, IndexMinPQ<Double> pq, HashMap<Long, Double> distTo, HashMap<Long, Edge> edgeTo,
                            HashMap<Long, Double> oppositeDistTo, Set<Long> visitedThisDirection,
                            Set<Long> visitedOppositeDirection, String direction, AtomicBoolean pathFound) {
        if (pq.isEmpty()) return;

        int currentIndex = pq.delMin();
        long currentVertex = g.getVertexValue(currentIndex);
        // System.out.println(direction + " processing vertex: " + currentVertex);

        synchronized (lock) {
            if (visitedThisDirection.contains(currentVertex)) {
                return; // Skip if this vertex is already processed
            }
            visitedThisDirection.add(currentVertex);
        }

        for (Edge e : g.adj(currentVertex)) {
            long w = e.other(currentVertex);
            int wIndex = g.getIndexForVertex(w);
            relaxedEdgesCount++;

            synchronized (lock) {
                if (distTo.get(currentVertex) + e.weight() < distTo.get(w)) {
                    distTo.put(w, distTo.get(currentVertex) + e.weight());
                    edgeTo.put(w, e);

                    if (pq.contains(wIndex)) {
                        pq.decreaseKey(wIndex, distTo.get(w));
                    } else {
                        pq.insert(wIndex, distTo.get(w));
                    }
                }

                if (visitedOppositeDirection.contains(w)) {
                    double potentialBestDistance = distTo.get(w) + oppositeDistTo.get(w);
                    if (potentialBestDistance < bestPathDistance) {
                        bestPathDistance = potentialBestDistance;
                        meetingPoint = w;  // Update meeting point
                        pathFound.set(true); 
                }
            }
        }
    }
}

    public int getRelaxedEdgesCount() {
        return relaxedEdgesCount;
    }

    public void clear() {
        distToForward.clear();
        distToBackward.clear();
        edgeToForward.clear();
        edgeToBackward.clear();
        visitedForward.clear();
        visitedBackward.clear();
        relaxedEdgesCount = 0;
        bestPathDistance = Double.POSITIVE_INFINITY;
        meetingPoint = null;
    }
}
