package org.example;

import edu.princeton.cs.algs4.IndexMinPQ;
import java.util.Arrays;

public class BiDirectionalDijkstra {
    private double[] distToForward;
    private double[] distToBackward;
    private Edge[] edgeToForward;
    private Edge[] edgeToBackward;
    private boolean[] visitedForward;
    private boolean[] visitedBackward;
    private IndexMinPQ<Double> pqForward;
    private IndexMinPQ<Double> pqBackward;
    private int relaxedEdgesCount;

    private double bestPathDistance;
    private int meetingPoint; // index of the meeting point

    public BiDirectionalDijkstra() {
        this.relaxedEdgesCount = 0;
    }

    public double runBiDirectionalDijkstra(Graph g, long sVertexId, long tVertexId) {
        this.relaxedEdgesCount = 0;
        this.bestPathDistance = Double.POSITIVE_INFINITY;
        this.meetingPoint = -1;

        int V = g.V();
        distToForward = new double[V];
        distToBackward = new double[V];
        edgeToForward = new Edge[V];
        edgeToBackward = new Edge[V];
        visitedForward = new boolean[V];
        visitedBackward = new boolean[V];

        Arrays.fill(distToForward, Double.POSITIVE_INFINITY);
        Arrays.fill(distToBackward, Double.POSITIVE_INFINITY);

        int s = g.getIndexForVertex(sVertexId);
        int t = g.getIndexForVertex(tVertexId);

        distToForward[s] = 0.0;
        distToBackward[t] = 0.0;

        pqForward = new IndexMinPQ<>(V);
        pqBackward = new IndexMinPQ<>(V);

        pqForward.insert(s, distToForward[s]);
        pqBackward.insert(t, distToBackward[t]);

        while (!pqForward.isEmpty() && !pqBackward.isEmpty()) {
            // Termination condition
            double minDistForward = distToForward[pqForward.minIndex()];
            double minDistBackward = distToBackward[pqBackward.minIndex()];
            if (minDistForward + minDistBackward >= bestPathDistance) {
                break;
            }

            // Decide which direction to expand
            if (minDistForward <= minDistBackward) {
                int v = pqForward.delMin();
                if (!visitedForward[v]) {
                    visitedForward[v] = true;
                    relaxEdges(g, v, distToForward, edgeToForward, distToBackward, visitedForward, visitedBackward, pqForward);
                }
            } else {
                int v = pqBackward.delMin();
                if (!visitedBackward[v]) {
                    visitedBackward[v] = true;
                    relaxEdges(g, v, distToBackward, edgeToBackward, distToForward, visitedBackward, visitedForward, pqBackward);
                }
            }
        }

        return bestPathDistance == Double.POSITIVE_INFINITY ? -1 : bestPathDistance;
    }

    private void relaxEdges(Graph g, int v, double[] distTo, Edge[] edgeTo,
                            double[] oppositeDistTo, boolean[] visitedThisDirection,
                            boolean[] visitedOppositeDirection, IndexMinPQ<Double> pq) {
        for (Edge e : g.adj(v)) {
            int w = e.other(v);
            relaxedEdgesCount++;

            if (distTo[w] > distTo[v] + e.weight()) {
                distTo[w] = distTo[v] + e.weight();
                edgeTo[w] = e;

                if (pq.contains(w)) {
                    pq.decreaseKey(w, distTo[w]);
                } else {
                    pq.insert(w, distTo[w]);
                }
            }

            if (visitedOppositeDirection[w]) {
                double potentialBestDistance = distTo[w] + oppositeDistTo[w];
                if (potentialBestDistance < bestPathDistance) {
                    bestPathDistance = potentialBestDistance;
                    meetingPoint = w;
                }
            }
        }
    }

    public int getRelaxedEdgesCount() {
        return relaxedEdgesCount;
    }

    public void clear() {
        distToForward = null;
        distToBackward = null;
        edgeToForward = null;
        edgeToBackward = null;
        visitedForward = null;
        visitedBackward = null;
        pqForward = null;
        pqBackward = null;
        relaxedEdgesCount = 0;
        bestPathDistance = Double.POSITIVE_INFINITY;
        meetingPoint = -1;
    }
}
