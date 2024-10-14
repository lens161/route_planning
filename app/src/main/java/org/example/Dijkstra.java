

/******************************************************************************
 *  Compilation:  javac DijkstraUndirectedSP.java
 *  Execution:    java DijkstraUndirectedSP input.txt s
 *  Dependencies: Grap.java IndexMinPQ.java Stack.java Edg.java
 *  Data files:   https://algs4.cs.princeton.edu/43mst/tinyEWG.txt
 *                https://algs4.cs.princeton.edu/43mst/mediumEWG.txt
 *                https://algs4.cs.princeton.edu/43mst/largeEWG.txt
 *
 *  Dijkstra's algorithm. Computes the shortest path tree.
 *  Assumes all weights are non-negative.
 *
 *  % java DijkstraUndirectedSP tinyEWG.txt 6
 *  6 to 0 (0.58)  6-0 0.58000
 *  6 to 1 (0.76)  6-2 0.40000   1-2 0.36000
 *  6 to 2 (0.40)  6-2 0.40000
 *  6 to 3 (0.52)  3-6 0.52000
 *  6 to 4 (0.93)  6-4 0.93000
 *  6 to 5 (1.02)  6-2 0.40000   2-7 0.34000   5-7 0.28000
 *  6 to 6 (0.00)
 *  6 to 7 (0.74)  6-2 0.40000   2-7 0.34000
 *
 *  % java DijkstraUndirectedSP mediumEWG.txt 0
 *  0 to 0 (0.00)
 *  0 to 1 (0.71)  0-44 0.06471   44-93  0.06793  ...   1-107 0.07484
 *  0 to 2 (0.65)  0-44 0.06471   44-231 0.10384  ...   2-42  0.11456
 *  0 to 3 (0.46)  0-97 0.07705   97-248 0.08598  ...   3-45  0.11902
 *  ...
 *
 *  % java DijkstraUndirectedSP largeEWG.txt 0
 *  0 to 0 (0.00)
 *  0 to 1 (0.78)  0-460790 0.00190  460790-696678 0.00173   ...   1-826350 0.00191
 *  0 to 2 (0.61)  0-15786  0.00130  15786-53370   0.00113   ...   2-793420 0.00040
 *  0 to 3 (0.31)  0-460790 0.00190  460790-752483 0.00194   ...   3-698373 0.00172
 *
 ******************************************************************************/

// package edu.princeton.cs.algs4;
package org.example;

import java.util.Stack;

// import edu.princeton.cs.algs4.Edg;
// import edu.princeton.cs.algs4.Grap;
import edu.princeton.cs.algs4.IndexMinPQ;
import java.util.HashMap;
public class Dijkstra{
    private HashMap<Long, Double> distTo; // distances
    private HashMap<Long, Edge> edgeTo;   // last edge on shortest path
    private IndexMinPQ<Double> pq;    // priority queue of vertices
    private int relaxedEdgesCount;  

    /**
     * Computes a shortest-paths tree from the source vertex {@code s} to every
     * other vertex in the Edg-weighted grap {@code G}.
     *
     * @param  G the Edg-weighted digrap
     * @param  s the source vertex
     * @throws IllegalArgumentException if an Edg weight is negative
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */

    public Dijkstra(){
        this.relaxedEdgesCount = 0;

    }

    public double runDijkstra(Graph g, long s, long t) {
        this.relaxedEdgesCount = 0;

        int sIndex = g.getIndexForVertex(s);


        distTo = new HashMap<>();
        edgeTo = new HashMap<>();

        for (Long v: g.getVertexValues()){
            distTo.put(v, Double.POSITIVE_INFINITY);
            edgeTo.put(v, null);}

        distTo.put((long) s, 0.0);



        pq = new IndexMinPQ<Double>(g.V());
        pq.insert(sIndex, distTo.get(s));

        while (!pq.isEmpty()) {
            int vIndex  = pq.delMin();
            long vertexValue = g.getVertexValue(vIndex);
            // System.out.println("Processing vertex " + vertexValue);

            for (Edge e : g.adj(vertexValue)) {
                relax(g, e, vertexValue);
            }
        
            if (vertexValue == t) {
                return distTo.get(t);
        }
    }

        return distTo.getOrDefault(t, Double.POSITIVE_INFINITY);
    
    }

    public void clear() {
        distTo.clear();
        edgeTo.clear();
        relaxedEdgesCount = 0;
    }   

    private void relax(Graph g, Edge e, long vertexValue) {
        // System.out.println("Relaxing edge: " + e + " from vertex: " + vertexValue);
        long w = e.other(vertexValue);
        int wIndex = g.getIndexForVertex(w);  
        relaxedEdgesCount++;
        
    // can maybe remove this
    if (distTo.get(w) == null) {
        System.err.println("distTo[" + w + "] is null");
        return; 
    }

    double newDist = distTo.get(vertexValue) + e.weight();
    if (distTo.get(w) > newDist) {
        distTo.put( w, newDist);
        edgeTo.put( w, e);

        if (pq.contains(wIndex)) {
            pq.decreaseKey(wIndex, newDist);

        } else {
            pq.insert(wIndex, newDist);
            }
        }
    }

    /**
     * Returns the length of a shortest path between the source vertex {@code s} and
     * vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return the length of a shortest path between the source vertex {@code s} and
     *         the vertex {@code v}; {@code Double.POSITIVE_INFINITY} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public double distTo(int v) {
        validateVertex(v);
        return distTo.get(v);
    }


    /**
     * Returns true if there is a path between the source vertex {@code s} and
     * vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return {@code true} if there is a path between the source vertex
     *         {@code s} to vertex {@code v}; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
        validateVertex(v);
        return distTo.get(v)< Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path between the source vertex {@code s} and vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return a shortest path between the source vertex {@code s} and vertex {@code v};
     *         {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Edge> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        int x = v;
        for (Edge e = edgeTo.get(v); e != null; e = edgeTo.get(x)) {
            path.push(e);
            x = (int) e.other(x);
        }
        return path;
    }

    public int getRelaxedEdgesCount() {
        return relaxedEdgesCount;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = distTo.size();
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }
}

/******************************************************************************
 *  Copyright 2002-2022, Robert SEdgwick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert SEdgwick and Kevin Wayne,
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