/******************************************************************************
*  Compilation:  javac EdgeWeightedGraph.java
 *  Execution:    java EdgeWeightedGraph filename.txt
 *  Dependencies: LinkedList.java Edge.java In.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/43mst/tinyEWG.txt
 *
 *  An edge-weighted undirected graph, implemented using adjacency lists.
 *  Parallel edges and self-loops are permitted.
 *
 *  % java EdgeWeightedGraph tinyEWG.txt 
 *  8 16
 *  0: 6-0 0.58000  0-2 0.26000  0-4 0.38000  0-7 0.16000  
 *  1: 1-3 0.29000  1-2 0.36000  1-7 0.19000  1-5 0.32000  
 *  2: 6-2 0.40000  2-7 0.34000  1-2 0.36000  0-2 0.26000  2-3 0.17000  
 *  3: 3-6 0.52000  1-3 0.29000  2-3 0.17000  
 *  4: 6-4 0.93000  0-4 0.38000  4-7 0.37000  4-5 0.35000  
 *  5: 1-5 0.32000  5-7 0.28000  4-5 0.35000  
 *  6: 6-4 0.93000  6-0 0.58000  3-6 0.52000  6-2 0.40000
 *  7: 2-7 0.34000  1-7 0.19000  0-7 0.16000  5-7 0.28000  4-7 0.37000
 *
 ******************************************************************************/

//  package edu.princeton.cs.algs4;
 package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
// import java.util.Stack;

// import edu.princeton.cs.algs4.Edge;

/**
  *  The <tt>EdgeWeightedGraph</tt> class represents an edge-weighted
  *  graph of vertices named 0 through <em>V</em> - 1, where each
  *  undirected edge is of type {@link Edge} and has a real-valued weight.
  *  It supports the following two primary operations: add an edge to the graph,
  *  iterate over all of the edges incident to a vertex. It also provides
  *  methods for returning the number of vertices <em>V</em> and the number
  *  of edges <em>E</em>. Parallel edges and self-loops are permitted.
  *  <p>
  *  This implementation uses an adjacency-lists representation, which 
  *  is a vertex-indexed array of @link{LinkedList} objects.
  *  All operations take constant time (in the worst case) except
  *  iterating over the edges incident to a given vertex, which takes
  *  time proportional to the number of such edges.
  *  <p>
  *  For additional documentation,
  *  see <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
  *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
  *
  *  @author Robert Sedgewick
  *  @author Kevin Wayne
  */
 public class Graph {
     private static final String NEWLINE = System.getProperty("line.separator");
 
     private final int V;
     private int E;
     private HashMap<Integer, Set<Edge>> adj;
     
     /**
      * Initializes an empty edge-weighted graph with <tt>V</tt> vertices and 0 edges.
      *
      * @param  V the number of vertices
      * @throws IllegalArgumentException if <tt>V</tt> < 0
      */

    // @SuppressWarnings("unchecked")
    public Graph(int V) {
         if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
         this.V = V;
         this.E = 0;
         adj = new HashMap<>();
     }

     // build graph from input file
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
        System.out.println(V);
        System.out.println(E);

        for (int i = 0; i < V; i++) {
            String[] vertex = sc.nextLine().split(" ");
            int v = Integer.parseInt(vertex[0]);
            System.out.println(v);
            float lo = Float.parseFloat(vertex[1]);
            float la = Float.parseFloat(vertex[2]);
            addVertex(v);
        }
        for (int i = 0; i < E; i++) {
            String line = sc.nextLine();
            String[] edge = line.split(" ");
            int v = Integer.parseInt(edge[0]);
            int w = Integer.parseInt(edge[1]);
            System.out.println(v + " " + w);
            int weight = Integer.parseInt(edge[2]);
            Edge e = new Edge(v, w, weight);
            addEdge(e);
        }
        sc.close();
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
         int v = e.either();
         int w = e.other(v);
        //  validateVertex(v);
        //  validateVertex(w);
         adj.get(v).add(e);
         adj.get(w).add(e);
         E++;
     }

     public void addVertex(int v){
        Set<Edge> s = new HashSet<Edge>();
        adj.put(v, s);
     }
 
     /**
      * Returns the edges incident on vertex <tt>v</tt>.
      *
      * @param  v the vertex
      * @return the edges incident on vertex <tt>v</tt> as an Iterable
      * @throws IndexOutOfBoundsException unless 0 <= v < V
      */
     public Iterable<Edge> adj(int v) {
        //  validateVertex(v);
         return adj.get(v);
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
         for (int v : adj.keySet()) {
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
         for (int v : adj.keySet()) {
             s.append(v + ": ");
             for (Edge e : adj.get(v)) {
                 s.append(e + "  ");
             }
             s.append(NEWLINE);
         }
         return s.toString();
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
 
 /******************************************************************************
  *  Copyright 2002-2015, Robert Sedgewick and Kevin Wayne.
  *
  *  This file is part of algs4.jar, which accompanies the textbook
  *
  *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
  *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
  *      http://algs4.cs.princeton.edu
  *
  *
  *  algs4.jar is free software: you can redistribute it and/or modify
  *  it under the terms of the GNU General Public License as published by
  *  the Free Software Foundation, either version 3 of the License, or
  *  (at your option) any later version.
  *
  *  algs4.jar is distributed in the hope that it will be useful,
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  *  GNU General Public License for more details.
  *
  *  You should have received a copy of the GNU General Public License
  *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
  ******************************************************************************/
 