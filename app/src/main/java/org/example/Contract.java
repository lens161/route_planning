package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Contract {
    private int MAX_HOPS = 10;
    private Graph g;
    private int contractionOrder;
    private PriorityQueue<PqItem> pq;
    private Dijkstra2 dijkstra;
    private long totalContracted = 0;
    private long totalContractNodeTime = 0;
    private int contractedNodeCount = 0;
    private double totalSecs = 0;

    private int[] rank;
    private boolean[] stale;

    public Contract(Graph g){
        this.g = g;
        rank = new int[g.V()];
        stale = new boolean[g.V()];
        Arrays.fill(rank, -1);
        Arrays.fill(stale, false);

        contractionOrder = 0;
        dijkstra = new Dijkstra2();
    }

    public int calcImportance(int v){
        // int edgeDifference = edgeDifference(v);
        // // int contractedNeighbors = contractedNeighbors(v);
        // // int degree = g.adj[v].size();

        // // return edgeDifference*14 -(contractedNeighbors*5) + degree*15;
        int degree = g.adj[v].size();
        int contractedNeighbors = contractedNeighbors(v);
        // // Approximate edge difference using degree
        int edgeDifferenceEstimate = degree - contractedNeighbors;
        return edgeDifferenceEstimate;
        //  * 14 - (contractedNeighbors * 5) + degree * 15;
    }

    public int edgeDifference(int v){
        int shortcuts = 0;
        Iterable<Edge> edges = g.adj(v);
        int originalEdgeCount = g.getAdj(v).size();
        for (Edge e1 : edges) {
            for (Edge e2 : edges) {
                double maxdist = e1.weight() + e2.weight();
                int u = e1.other(v);
                int w = e2.other(v);
                // avoid duplicates
                if(u<w || u==w){
                    continue;
                }
                double dist = dijkstra.runDijkstra2(g, u, w, maxdist, MAX_HOPS, rank);
                // if shortest path is less than distance over contracted node, add shortcut
                if(maxdist <= dist && dist!= Double.NEGATIVE_INFINITY){
                    shortcuts++;
                }
            }
        }
        return shortcuts - originalEdgeCount;
    }
    private int contractedNeighbors(int v) {
        int contracted = 0;
        for (Edge e : g.adj[v].values()) {
            int w = e.other(v);
            if (rank[w] != -1) {
                contracted++;
            }
        }
        return contracted;
    }

    public void intitialisePQ(){
        pq = new PriorityQueue<PqItem>();
        for (int i = 0; i < g.V(); i++) {
            PqItem item = new PqItem(i, calcImportance(i));
            System.out.println("add " + i + " " + item.getImportance());
            pq.add(item);
        }
    }

    public void preprocess(){
        intitialisePQ();

        long startTime= System.nanoTime();
        while(!pq.isEmpty()){
            PqItem item = pq.poll();
            int v = item.getIndex();
            if(stale[v]){
                // System.out.println("updating: " + v);
                PqItem updatedItem = new PqItem(v, calcImportance(v));
                pq.add(updatedItem);
                stale[v] = false;
                continue;
            }

            if(rank[v]==-1){
                contract(v);    
                totalContracted++;
            }

            if (contractionOrder % 10000 == 0) {
                System.out.println("Contracted " + contractionOrder + " nodes out of " + g.V());

                // Calculate elapsed time
                long elapsedTime = System.nanoTime() - startTime;
                double elapsedSeconds = elapsedTime / 1e9;
                System.out.printf("Elapsed time: %.2f seconds%n", elapsedSeconds);

                // Calculate average contraction time per node
                double averageTime = (totalContractNodeTime / 1e9) / contractedNodeCount;
                System.out.printf("Average contraction time per node: %.6f seconds%n", averageTime);
            }
        }
        long totalTime = System.nanoTime() - startTime;
        double totalSeconds = totalTime / 1e9;
        totalSecs = totalSeconds;
        System.out.printf("Total contraction time: %.2f seconds%n", totalSeconds);
    }

    private void contract(int v) {
        rank[v] = contractionOrder++;
        // System.out.println("contracting: " + v);
        Iterable<Edge> edges = g.adj(v);
        Set<Integer> neighbours = new HashSet<>();
        for (Edge e : edges) {
            neighbours.add(e.other(v));
        }
        for (Edge e1 : edges) {
            int u = e1.other(v);
            if(rank[u]!=-1)
                continue;
            for (Edge e2 : edges) {
                double maxDist = e1.weight() + e2.weight();
                // System.out.println("weight 1: " + e1.weight());
                int w = e2.other(v);
                // System.out.println(u+"-"+w);
                if(u>=w || rank[w]!=-1){
                    continue;
                }
                else{
                    double dist = dijkstra.runDijkstra2(g, u, w, maxDist, MAX_HOPS, rank);
                    // System.out.println("dist: "  +dist);
                    if(maxDist<=dist && dist!=Double.NEGATIVE_INFINITY && !g.edgeExists(u, w)){
                        Edge shortcut = new Edge(u, w, maxDist, v);
                        // System.out.println("added shortcut: " + u+"-"+w+" "+maxDist);
                        g.addEdge(shortcut);
                    }
                }
                }
        }
        for (Integer i : neighbours) {
            stale[i] =true;
        }
    }

        public void saveAugmentedGraph(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // First line: write the number of vertices and edges
            writer.write(g.V() + " " + g.E() + "\n");

            // Write each vertex's ID, longitude, latitude and rank
            for (int i = 0; i < g.V(); i++) {
                Vertex2 vertex = g.getVertexByIndex(i);
                writer.write(vertex.getId() + " " + vertex.getLongitude() + " " + vertex.getLatitude() + " " + rank[i] +  "\n");
            }

            // Write edges, marking shortcuts and original edges
            for (Edge e : g.edges()) {
                long originalV = g.getVertexId(e.V());
                long originalW = g.getVertexId(e.W());

                // For original edges label -1 
                if (e.c == -1) {
                    writer.write(originalV + " " + originalW + " " + e.weight() + " -1\n");
                } else {
                    // For shortcut edges label the vertex the shortcut has been contracted from
                    writer.write(originalV + " " + originalW + " " + e.weight() + " " + g.getVertexId(e.c) + "\n");
                }
            }
            double averageTime = (totalContractNodeTime / 1e9) / contractedNodeCount;
            // writer.write("average contraction time: " + averageTime + "\n") ;
            // writer.write("total contracttion time:" + totalSecs);
        }
        }

    public static void main(String[] args) throws IOException {
        Graph graph = new Graph(new File("//Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/newdenmark.graph"));

        Contract c = new Contract(graph);

        c.preprocess();

        // for (Edge e : graph.edges()) {
        //     System.out.println(e.toString());
        // }
        c.saveAugmentedGraph("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/augmented_graph_output.graph");


    }   


}
