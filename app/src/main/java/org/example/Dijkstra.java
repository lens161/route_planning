package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Vector;

public class Dijkstra {
    private double[] distTo;         
    private Edge[] edgeTo;            
    private PriorityQueue<Node> pq;    
    private long relaxedEdgesCount;    

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
        
        pq = new PriorityQueue<>();
        pq.add(new Node(start, distTo[start]));

        while (!pq.isEmpty()) {
            int v = pq.poll().id;
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
            pq.add(new Node(w, distTo(w)));
            // if (pq.contains(w)) {
            //     pq.decreaseKey(w, distTo[w]);
            // } else {
            //     pq.insert(w, distTo[w]);
            // }
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

    public long getRelaxedEdgesCount() {
        return relaxedEdgesCount;
    }

    public void clear() {
        this.edgeTo = null;
        this.distTo = null;
        this.pq = null;
        this.relaxedEdgesCount = 0;
    }
    public List<Long> getPath(Graph g, long targetId) {
        int target = g.getIndexForVertex(targetId);
        if (target == -1 || !hasPathTo(g, targetId)) {
            return null; // No path found
        }
    
        List<Long> path = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
    
        // Trace back the path using edgeTo array
        int current = target;
        while (edgeTo[current] != null) {
            Edge e = edgeTo[current];
            int next = e.other(current);
    
            // Debugging information
            System.out.printf("Tracing back edge: current = %d, next = %d%n", current, next);
    
            stack.push(current); // Add current node to the stack
            current = next; // Move to the next node in the path
    
            // If the path loops back to itself or has invalid nodes, break to avoid infinite loops
            if (current == target) {
                System.err.println("Loop detected in path reconstruction.");
                return null;
            }
        }
    
        // Add the source node to the stack
        stack.push(current);
    
        // Convert indices to vertex IDs and store them in the path
        while (!stack.isEmpty()) {
            path.add(g.getVertexId(stack.pop()));
        }
    
        // Debugging output for the path
        System.out.println("Path details:");
        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            int v = g.getIndexForVertex(path.get(i));
            int w = g.getIndexForVertex(path.get(i + 1));
            Edge edge = ((Vector<Edge>) g.adj(v)).get(w); // Access the adjacency list for debugging
    
            if (edge != null) {
                totalDistance += edge.weight();
                System.out.printf("Edge %d -> %d (IDs: %d -> %d), weight: %.1f, cumulative distance: %.1f%n",
                        v, w, path.get(i), path.get(i + 1), edge.weight(), totalDistance);
            }
        }
    
        System.out.println("Total calculated distance: " + totalDistance);
        System.out.println("Best path distance stored: " + distTo[g.getIndexForVertex(targetId)]);
    
        return path;
    }
}
