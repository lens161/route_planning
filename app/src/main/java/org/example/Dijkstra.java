package org.example;

import java.util.Stack;
import edu.princeton.cs.algs4.IndexMinPQ;

public class Dijkstra {
    private double[] distTo;         
    private Edge[] edgeTo;            
    private IndexMinPQ<Double> pq;    
    private int relaxedEdgesCount;    

    public Dijkstra() {

    }

    public double runDijkstra(Graph g, long s, long t) {

        int start = g.getIndexForVertex(s);
        int target = g.getIndexForVertex(t);

        if (start == -1 || target == -1) {
            throw new IllegalArgumentException("Source or target vertex not found in graph."+ s + start +" "+t+target);
        }

        int vertexCount = g.V();
        distTo = new double[vertexCount];
        edgeTo = new Edge[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }
        distTo[start] = 0.0;

        
        pq = new IndexMinPQ<>(vertexCount);
        pq.insert(start, distTo[start]);

        while (!pq.isEmpty()) {
            int v = pq.delMin();
            if (v == target) break;  
            for (Edge e : g.adj(v)) {
                relax(e, v);
            }
        }

        return distTo[target] != Double.POSITIVE_INFINITY ? distTo[target] : -1.0;
    }

   
    private void relax(Edge e, int v) {
        int w = e.other(v);
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pq.contains(w)) {
                pq.decreaseKey(w, distTo[w]);
            } else {
                pq.insert(w, distTo[w]);
            }
        }
        relaxedEdgesCount++;
    }

    public double distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    public boolean hasPathTo(Graph g, long vertexId) {
        int v = g.getIndexForVertex(vertexId);
        validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(Graph g, long vertexId) {
        int v = g.getIndexForVertex(vertexId);
        validateVertex(v);
        if (!hasPathTo(g, vertexId)) return null;

        Stack<Edge> path = new Stack<>();
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.either() == v ? e.other(v) : e.either()]) {
            path.push(e);
            v = e.other(v);
        }
        return path;
    }


    private void validateVertex(int v) {
        if (v < 0 || v >= distTo.length) {
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (distTo.length - 1));
        }
    }

    public int getRelaxedEdgesCount() {
        return relaxedEdgesCount;
    }

    public void clear() {
        this.edgeTo = null;
        this.distTo = null;
        this.pq = null;
        this.relaxedEdgesCount = 0;
    }
}
