package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ContractionHierarchy {
    private final Graph graph;
    private final int[] rank;
    private int contractionOrder;

    public ContractionHierarchy(Graph graph) {
        this.graph = graph;
        this.rank = new int[graph.V()];
        Arrays.fill(rank, -1);
        this.contractionOrder = 0;
    }

    public void preprocess() {
        for (int v = 0; v < graph.V(); v++) {
            contractNode(v);
        }
    }

    private void contractNode(int v) {
        rank[v] = contractionOrder++;
        List<Edge> neighbors = (List<Edge>) graph.adj(v);

        // Iterate through each pair of neighbors to add shortcuts if necessary
        for (Edge edge1 : neighbors) {
            int u = edge1.other(v);
            for (Edge edge2 : neighbors) {
                int w = edge2.other(v);
                if (u != w && !edgeExists(u, w)) {
                    double shortcutWeight = edge1.weight() + edge2.weight();
                    Edge shortcut = new Edge(u, w, shortcutWeight, v); // Create shortcut with `v` as the contracted node
                    graph.addEdge(shortcut); // Add shortcut to the graph
                    System.out.println(shortcut);
                }
            }
        }
    }

    private boolean edgeExists(int u, int w) {
        // Check if an edge already exists between u and w
        for (Edge e : graph.adj(u)) {
            if (e.other(u) == w) return true;
        }
        return false;
    }

    public void saveAugmentedGraph(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(graph.V() + " " + graph.E() + "\n");
            
            // Write vertex ranks
            for (int i = 0; i < graph.V(); i++) {
                writer.write(i + " " + rank[i] + "\n");
            }
            
            // Write edges, indicating if each edge is a shortcut
            for (Edge e : graph.edges()) {
                writer.write(e.V() + " " + e.W() + " " + e.weight() + " " + (e.c == -1 ? -1 : e.c) + "\n");
            }
        }
    }

    public int[] getRanks() {
        return rank;
    }
    public static void main(String[] args) throws IOException {
        // Graph graph = new Graph(new File("/home/knor/route/route-planning/app/src/main/resources/denmark.graph")); 
        Graph graph = new Graph(new File("/home/knor/route/route-planning/SmallTest.graph")); 

        ContractionHierarchy ch = new ContractionHierarchy(graph);

        ch.preprocess();
        int[] ranks = ch.getRanks();

        CHQuery query = new CHQuery(graph, ranks);
        System.out.println(query.query(1, 6));
        ch.saveAugmentedGraph("test");
    }
}
