
package org.example;

import java.util.Stack;
import edu.princeton.cs.algs4.IndexMinPQ;

public class Dijkstra{
    private double [] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;            // edgeTo[v] = last Edg on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices
    // private HashMap<Long, Integer> index;
    // private Graph g;
    private int relaxedEdgesCount;

    public Dijkstra(){
        // index = g.getVertexINdex();
    }

    public double runDijkstra(Graph g, long s, long t) {
        int start = g.getIndexForVertex(s);
        int target = g.getIndexForVertex(t);
        // for (Edge e : g.edges()) {
        //     if (e.weight() < 0)
        //         throw new IllegalArgumentException("Edg " + e + " has negative weight");
        // }
        int VertexCount = g.V();

        distTo = new double[VertexCount];
        edgeTo = new Edge[VertexCount];

        for (int i = 0; i < g.V(); i++)
            distTo[i] = Double.POSITIVE_INFINITY;
        distTo[start] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(VertexCount);
        pq.insert(start, distTo[start]);

        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : g.adj(v))
                relax(e, v, g);
            if(v==target)
                return distTo(v);
        }
        // check optimality conditions
        assert check(g, s);
        return distTo(target);
    }

    // relax Edge e and update pq if changed
    // capital V is the the ID of the vertex, not the index used in the dijkstra implementation
    private void relax(Edge e, int v, Graph g) {
        int w = e.other(v);
        // Vertex W = g.getVertices().get(w);
        
        if (distTo[w] > distTo[v] + e.weight()) {
            // distTo.get(w) = distTo[v] + e.weight();
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
        relaxedEdgesCount ++;
    }

    public double distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    public boolean hasPathTo(Graph g, int V) {
        int v = g.getIndexForVertex(V);
        validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(Graph g, int v) {
        //TO DO: make new path to
        validateVertex(v);
        if (!hasPathTo(g, v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        // int x = v;
        // for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
        //     path.push(e);
        //     x = e.other(x);
        // }
        return path;
    }


    // check optimality conditions:
    // (i) for all Edgs e = v-w:            distTo[w] <= distTo[v] + e.weight()
    // (ii) for all Edg e = v-w on the SPT: distTo[w] == distTo[v] + e.weight()
    private boolean check(Graph g, Long S) {
        int s = g.getIndexForVertex(S);

        // check that Edg weights are non-negative
        for (Edge e : g.edges()) {
            if (e.weight() < 0) {
                System.err.println("negative Edg weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < g.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all Edgs e = v-w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < g.V(); v++) {
            for (Edge e : g.adj[v]) {
                int w = e.other(v);
                if (distTo[v] + e.weight() < distTo[w]) {
                    System.err.println("Edg " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all Edgs e = v-w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < g.V(); w++) {
            if (edgeTo[w] == null) continue;
            Edge e = edgeTo[w];
            if (w != e.either() && w != e.other(e.either())) return false;
            int v = (int)e.other(w);
            if (distTo[v] + e.weight() != distTo[w]) {
                System.err.println("Edg " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = distTo.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Unit tests the {@code DijkstraUndirectedSP} data type.
     *
     * @param args the command-line arguments
     */

    public int getRelaxedEdgesCount(){
        return relaxedEdgesCount;
    }

    public void clear(){
        this.edgeTo = null;
        this.distTo = null;
        this.pq = null;
        this.relaxedEdgesCount=0;
    }

}