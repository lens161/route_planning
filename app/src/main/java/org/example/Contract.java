package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.princeton.cs.algs4.IndexMinPQ;

public class Contract {
    private Graph g;
    private Dijkstra d;
    public IndexMinPQ<Integer> pq;

    public Contract(Graph g) throws FileNotFoundException{
        this.g = g;
        d = new Dijkstra();
        pq = new IndexMinPQ<>(g.V());

        // insert all vertices in the priority queue based on their edge difference
        for (int i = 0; i < g.V(); i++) {
            pq.insert(i, findEdgeDifference(i));
        }

        contractVertices();
    }


    // find the edge difference for a vertex 
    public int findEdgeDifference(int u){
        int shortcuts = 0;
        ArrayList<Edge> edges = (ArrayList) g.adj(u);
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
        return shortcuts/2 - degree;
    }


    private int[] getNeighbours(int u, ArrayList<Edge> edges) {
        int[] v = new int[edges.size()];

        for(int i = 0; i < edges.size(); i++){
            v[i] = edges.get(i).other(u);
        }
        return v;
    }

    // get the shortcut from v-w over u if there is any. return null if there is no shortcut
    public Edge getShortCut(int v, int u, int w, ArrayList<Edge> edges){
        double direct = findDirect(v, u, w, edges);
        double sp = d.runDijkstra(g, g.getVertexId(v), g.getVertexId(w));
        if(sp == direct)
            return new Edge(v, w, sp, true);
        else 
            return null;
    }

    // helper method for findEdgeDifference 
    // find the direct distance between two vertices in u's neigbourhood going over u
    public double findDirect(int v, int u, int w, ArrayList<Edge> edges){
        Edge first = new Edge(0, 0, 0, false);
        Edge second = new Edge(0, 0, 0, false);
        for(Edge e : edges){
            if(e.other(v) == u)
                first = e;
            else if(e.other(w) == u)
                second = e;
        }
        return first.weight() + second.weight();
    }
    
    public void contractVertices(){
        while(!pq.isEmpty()){
            int u = pq.delMin();
            ArrayList<Edge> edges = (ArrayList) g.adj(u);
            int[] v = getNeighbours(u, edges);

            // TO-DO:
            // this has the same problem as the nested for loops in findEdgeDifference. also this is becoming some boilerplate bullshit.
            // please solve the redundancy problem and refactor somehow.
            for (int i = 0; i < v.length; i++) {
                for (int j = 0; j < v.length; j++) {
                    Edge shortcut = getShortCut(v[i], u, v[j], edges);
                    if(i!=j && shortcut != null)
                        g.addEdge(shortcut);
                }
            }
        }
    } 

    public static void main(String[] args) throws FileNotFoundException {
        File input1 = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/SmallTest.graph");
        Graph g = new Graph(input1);

        Contract c = new Contract(g);
        ArrayList<Edge> edges = (ArrayList) g.adj(7);
        System.out.println(g.adj(7));
        System.out.println(g.getVertexId(1) + "-" + g.getVertexId(7) + "-" +g.getVertexId(9));
        System.out.println(c.findDirect(1, 7, 9, edges));
        System.out.println(c.findEdgeDifference(7));

        for (int i = 0; i < g.V(); i++) {
            System.out.println(c.pq.keyOf(i));
        }
    }
}
