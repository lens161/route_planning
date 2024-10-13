package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Graph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;
    private int E;
    private HashMap<Long, Set<Edge>> adj;
    private HashMap<Long, Integer> vertexToIndexMap;
    private List<Long> vertexValues;
    
    /**
     * Initializes an empty edge-weighted graph with <tt>V</tt> vertices and 0 edges.
     *
     * @param  V the number of vertices
     * @throws IllegalArgumentException if <tt>V</tt> < 0
     */


   public Graph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        adj = new HashMap<>();
        vertexToIndexMap = new HashMap<>();
        vertexValues = new LinkedList<>();
    }


    public Graph(File file){
        File input1 = file;
        Scanner sc = new Scanner(System.in);
       try {
           sc = new Scanner(input1);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }

       String[] VE = sc.nextLine().split(" ");
       int V = Integer.parseInt(VE[0]);
       int E = Integer.parseInt(VE[1]);
       this.V = V;
       this.E = E;
       adj = new HashMap<>();
       vertexToIndexMap = new HashMap<>();
       vertexValues = new LinkedList<>();
    //    System.out.println(V);
    //    System.out.println(E);

       for (int i = 0; i < V; i++) {
           String[] vertex = sc.nextLine().split(" ");
           long v = Long.parseLong(vertex[0]);
           vertexToIndexMap.put(v, i);
           vertexValues.add(v);
           float lo = Float.parseFloat(vertex[1]);
           float la = Float.parseFloat(vertex[2]);
           addVertex(v);
       }
       for (int i = 0; i < E; i++) {
           String line = sc.nextLine();
           String[] edge = line.split(" ");
           long v = Long.parseLong(edge[0]);
           long w = Long.parseLong(edge[1]);
           int weight = Integer.parseInt(edge[2]);
           Edge e = new Edge(v, w, weight);
           addEdge(e);
       }
       sc.close();
       
   }

   public long  getVertexValue(int index) {
    if (index < 0 || index >= vertexValues.size()) {
        throw new IllegalArgumentException("Index " + index + " is out of bounds.");
    }
    return vertexValues.get(index);
}

    /**
     * Returns the number of vertices in this edge-weighted graph.
     *
     * @return the number of vertices in this edge-weighted graph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in this edge-weighted graph.
     *
     * @return the number of edges in this edge-weighted graph
     */
    public int E() {
        return E;
    }

    // throw an IndexOutOfBoundsException unless 0 <= v < V
   //  private void validateVertex(int v) {
   //      if (v < 0 || v >= V)
   //          throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
   //  }

    /**
     * Adds the undirected edge <tt>e</tt> to this edge-weighted graph.
     *
     * @param  e the edge
     * @throws IndexOutOfBoundsException unless both endpoints are between 0 and V-1
     */
    public void addEdge(Edge e) {
        long v = e.either();
        long w = e.other(v);

        adj.get(v).add(e);
        adj.get(w).add(e);
        E++;
    }

    public void addVertex(long v2){
       Set<Edge> s = new HashSet<Edge>();
       adj.put(v2, s);
    }

    /**
     * Returns the edges incident on vertex <tt>v</tt>.
     *
     * @param  vertexValue the vertex
     * @return the edges incident on vertex <tt>v</tt> as an Iterable
     * @throws IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<Edge> adj(long vertexValue) {
       if (!adj.containsKey(vertexValue)) {
           System.err.println("Warning: Vertex " + vertexValue + " not found in graph.");
           return new HashSet<Edge>(); // Return an empty set instead of null
       }
        return adj.get(vertexValue);
    }

    /**
     * Returns the degree of vertex <tt>v</tt>.
     *
     * @param  v the vertex
     * @return the degree of vertex <tt>v</tt>               
     * @throws IndexOutOfBoundsException unless 0 <= v < V
     */
    public int degree(int v) {
       //  validateVertex(v);
        return adj.get(v).size();
    }

    /**
     * Returns all edges in this edge-weighted graph.
     * To iterate over the edges in this edge-weighted graph, use foreach notation:
     * <tt>for (Edge e : G.edges())</tt>.
     *
     * @return all edges in this edge-weighted graph, as an iterable
     */
    public Iterable<Edge> edges() {
        LinkedList<Edge> list = new LinkedList<Edge>();
        for (Long v : adj.keySet()) {
            int selfLoops = 0;
            for (Edge e : adj.get(v)) {
                if (e.other(v) > v) {
                    list.add(e);
                }
                // only add one copy of each self loop (self loops will be consecutive)
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }
    public Set<Long> getVertexValues() {
        return adj.keySet(); 
    }

    /**
     * Returns a string representation of the edge-weighted graph.
     * This method takes time proportional to <em>E</em> + <em>V</em>.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *         followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (Long v : adj.keySet()) {
            s.append(v + ": ");
            for (Edge e : adj.get(v)) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    public int getIndexForVertex(long w) {
       return vertexToIndexMap.getOrDefault(w, -1);
   }

    /**
     * Unit tests the <tt>EdgeWeightedGraph</tt> data type.
     */
    public static void main(String[] args) {
       //  In in = new In(args[0]);
       //  EdgeWeightedGraph G = new EdgeWeightedGraph(in);
       //  StdOut.println(G);
    }

}