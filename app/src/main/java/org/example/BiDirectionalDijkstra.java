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


        // Create a thread pool with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Thread for the forward search
        Callable<Void> forwardTask = () -> {
            while (!pqForward.isEmpty() && !pathFound.get()) {
                relaxEdges(g, pqForward, distToForward, edgeToForward, distToBackward, visitedForward, visitedBackward, pathFound);
            }
            return null;
        };

        // Thread for the backward search
        Callable<Void> backwardTask = () -> {
            while (!pqBackward.isEmpty() && !pathFound.get()) {
                relaxEdges(g, pqBackward, distToBackward, edgeToBackward, distToForward, visitedBackward, visitedForward, pathFound);
            }
            return null;
        };

        // Submit the tasks to the executor
        try {
            Future<Void> forwardFuture = executorService.submit(forwardTask);
            Future<Void> backwardFuture = executorService.submit(backwardTask);

            forwardFuture.get();
            backwardFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

  
        return bestPathDistance == Double.POSITIVE_INFINITY ? -1 : bestPathDistance;
    }

    private void relaxEdges(Graph g, IndexMinPQ<Double> pq, HashMap<Long, Double> distTo, HashMap<Long, Edge> edgeTo,
                            HashMap<Long, Double> oppositeDistTo, Set<Long> visitedThisDirection,
                            Set<Long> visitedOppositeDirection, AtomicBoolean pathFound) {
        if (pq.isEmpty()) return;

        int currentIndex = pq.delMin();
        long currentVertex = g.getVertexValue(currentIndex);

        // Check if the vertex is already visited
        if (visitedThisDirection.contains(currentVertex)) {
            return; 
        }
        visitedThisDirection.add(currentVertex);

        // Relax edges
        for (Edge e : g.adj(currentVertex)) {
            long w = e.other(currentVertex);
            int wIndex = g.getIndexForVertex(w);
            relaxedEdgesCount++;

            // Update distance if a shorter path is found
            if (distTo.get(currentVertex) + e.weight() < distTo.get(w)) {
                distTo.put(w, distTo.get(currentVertex) + e.weight());
                edgeTo.put(w, e);

                if (pq.contains(wIndex)) {
                    pq.decreaseKey(wIndex, distTo.get(w));
                } else {
                    pq.insert(wIndex, distTo.get(w));
                }
            }

            // Check if the opposite direction has visited this node
            if (visitedOppositeDirection.contains(w)) {
                double potentialBestDistance = distTo.get(w) + oppositeDistTo.get(w);
                if (potentialBestDistance < bestPathDistance) {
                    synchronized (lock) {
                        if (potentialBestDistance < bestPathDistance) {
                            bestPathDistance = potentialBestDistance;
                            meetingPoint = w;
                            pathFound.set(true);
                }
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
