package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import edu.princeton.cs.algs4.IndexMinPQ;

public class Contract {
    private Graph g;
    private int HOP_LIMIT = 10;
    public static Dijkstra d;
    private IndexMinPQ<Integer> pq;
    private boolean[] stale;
    // Set to track added shortcuts and prevent duplicates
    private Set<String> shortcuts;
    private int[] ranks;

    public Contract(Graph g){
        this.g = g;
        int size = g.V();
        d = new Dijkstra();
        pq = new IndexMinPQ<>(size);
        stale = new boolean[size];
        Arrays.fill(stale, false);
        shortcuts = new HashSet<>();
        ranks = new int[size];
        // insert all vertices in the priority queue based on their edge difference
        for (int i = 0; i < size; i++) {
            System.out.println("adding:" + i);
            pq.insert(i, findEdgeDifference(i));
        }
        contractVertices();
    }

    // find the edge difference for a vertex 
    private int findEdgeDifference(int u){
        int shortcuts = 0;
        @SuppressWarnings({ "rawtypes", "unchecked" })
        ArrayList<Edge> edges = (ArrayList) g.adj(u);
        // System.out.println(edges.size());
        int degree = edges.size();
        // array of all vertices 
        int[] v = getNeighbours(u, edges);

        // TO-DO:
        // calculate the SP distance and the distance over u to decide whether a shortcut is needed
        // this is doing twice as many operations as necesary (every SP between vertices is calculated twice), 
        // please fix if you come up with something
        for (int i = 0; i < v.length; i++)
            for (int j = 0; j < v.length; j++)
                if(i!=j && getShortCut(v[i], u, v[j], edges) != null) 
                    shortcuts ++;
        // System.out.println(shortcuts/2);
        int ed = shortcuts/2 - degree;
        // System.out.println(u + ": " + ed);
        return ed;
    }


    private int[] getNeighbours(int u, ArrayList<Edge> edges) {
        int[] v = new int[edges.size()];

        for(int i = 0; i < edges.size(); i++){
            v[i] = edges.get(i).other(u);
        }
        return v;
    }

    // get the shortcut from v-w over u if there is any. return null if there is no shortcut
    private Edge getShortCut(int v, int u, int w, ArrayList<Edge> edges){
        double direct = findDirect(v, u, w, edges);
        long start = System.nanoTime();
        double sp = d.runDijkstraLim(g, g.getVertexId(v), g.getVertexId(w), direct, HOP_LIMIT);
        long finish = System.nanoTime();
        System.out.println("Dijkstra took: " + "\t" + ((finish-start)));
        d.clear();
        if(sp == direct || sp == -1)
            return new Edge(v, w, direct, u);
        else 
            return null;
    }

    // helper method for findEdgeDifference 
    // find the direct distance between two vertices in u's neigbourhood going over u
    public double findDirect(int v, int u, int w, ArrayList<Edge> edges){
        Edge first = new Edge(0, 0, 0, -1);
        Edge second = new Edge(0, 0, 0, -1);
        for(Edge e : edges){
            if(e.other(v) == u)
                first = e;
            else if(e.other(w) == u)
                second = e;
        }
        return first.weight() + second.weight();
    }
    
    // contracts all vertices in the priority queue 
    public void contractVertices(){
        int contracted = 0;
        int size = g.V();
        while(!pq.isEmpty() && contracted < size){
            int u = pq.delMin();
            // if the vertex is on the stale list, update its priority and add back to queue
            if (stale[u] == true) {
                pq.insert(u, findEdgeDifference(u));
                // System.out.println(u);
            }
            else{
                // System.out.println("u");
                contract(u);
            }
            contracted++;
        }
    }

    // contracts a vertex
    private void contract(int u) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        ArrayList<Edge> edges = (ArrayList) g.adj(u);
        int[] v = getNeighbours(u, edges);
        // TO-DO:
        // this has the same problem as the nested for loops in findEdgeDifference. also this is becoming some boilerplate bullshit.
        // please solve the redundancy problem and refactor somehow.
        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < v.length; j++) {
                int V = Integer.min(v[i], v[j]);
                int W = Integer.max(v[i], v[j]);
                Edge shortcut = getShortCut(V, u, W, edges);
                String shortcutString = Integer.toString(V) + "" + Integer.toString(W);
                if(shortcuts.contains(shortcutString))
                    continue;
                else if (i!=j && shortcut != null){
                    shortcuts.add(shortcutString);
                    g.addEdge(shortcut);
                    //set neighbours of u to stale for lazy updates
                    stale[v[i]] = true;
                    stale[v[j]] = true;
                }
            }
        }
    }

    public Graph saveGraph(){
        // return g;
        File contracted;
        try {
            contracted = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/contracted.graph");
            if(contracted.createNewFile())
                System.out.println(contracted.getName() + " was created");
            else 
                System.out.println(contracted.getName() + "alredy exists");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/contracted.graph");
            writer.write(g.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("writing contracted graph file failed");
            e.printStackTrace();
        }

        return g;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File input1 = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/denmark_newnew.graph");
        Graph g = new Graph(input1);

        System.out.println("started");
        Contract c = new Contract(g);
        // System.out.println(g.adj(7));
        // System.out.println(g.getVertexId(1) + "-" + g.getVertexId(7) + "-" +g.getVertexId(9));
        // System.out.println(c.findDirect(1, 7, 9, edges));
        // System.out.println(c.findEdgeDifference(7));
        Iterable<Edge> e = g.edges();
        for (Edge edge : e) {
            System.out.println(edge.toString());
        }
        // System.out.println(g.toString());

        System.out.println("shortcuts: " + c.shortcuts.size());
        // for (int i = 0; i < g.V(); i++) {
        //     System.out.println(c.pq.keyOf(i));
        // }
    }
}
