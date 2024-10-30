package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ContractionHierarchy {
    private final Graph graph;
    private final int[] rank;
    private int contractionOrder;
    private PriorityQueue<Integer> contractionQueue;

    public ContractionHierarchy(Graph graph) {
        this.graph = graph;
        this.rank = new int[graph.V()];
        Arrays.fill(rank, -1);
        this.contractionOrder = 0;
    }

    private int nodeImportanceScore(int v) {
        int degree = graph.adj[v].size();
        return degree; // Prioritize low-degree nodes
    }

    private void initializeContractionQueue() {
        contractionQueue = new PriorityQueue<>(Comparator.comparingInt(this::nodeImportanceScore));
        for (int i = 0; i < graph.V(); i++) {
            contractionQueue.add(i);
        }
    }

    public void preprocess() {
        initializeContractionQueue();

        int processedNodes = 0;
        int batchSize = Math.max(1000, (graph.V() - processedNodes) / 10); // Larger batches near the end

        while (!contractionQueue.isEmpty()) {
            List<Integer> batch = new ArrayList<>();
            for (int i = 0; i < batchSize && !contractionQueue.isEmpty(); i++) {
                batch.add(contractionQueue.poll());
            }

            for (int v : batch) {
                contractNode(v);
                processedNodes++;
            }

            // Update queue after each batch and print progress
            updateContractionQueue(batch);
            System.out.println("Processed nodes: " + processedNodes + " / " + graph.V() + " (" + (processedNodes * 100 / graph.V()) + "%)");
        }
    }

    private void updateContractionQueue(List<Integer> contractedNodes) {
        for (int v : contractedNodes) {
            if (rank[v] == -1) contractionQueue.add(v); // Re-add only unranked nodes
        }
    }

    private void contractNode(int v) {
        rank[v] = contractionOrder++;
        Map<Integer, Edge> neighbors = graph.adj[v];

        for (Edge edge1 : neighbors.values()) {
            int u = edge1.other(v);
            for (Edge edge2 : neighbors.values()) {
                int w = edge2.other(v);

                if (u != w && !edgeExists(u, w)) {
                    double shortcutWeight = edge1.weight() + edge2.weight();
                    if (!witnessSearch(u, w, shortcutWeight)) {
                        Edge shortcut = new Edge(u, w, shortcutWeight, v);
                        graph.addEdge(shortcut);
                    }
                }
            }
        }

        // Print progress for every 1000 nodes contracted
        if (contractionOrder % 1000 == 0) {
            System.out.println("Contracted " + contractionOrder + " nodes out of " + graph.V());
        }
    }

    /**
     * Performs a witness search from node u to node w within a limited distance.
     *
     * @param uIndex - the index of the starting node
     * @param wIndex - the index of the target node
     * @param maxDistance - maximum allowable distance for a shortcut
     * @return true if a path exists within maxDistance; otherwise, false
     */
    private boolean witnessSearch(int uIndex, int wIndex, double maxDistance) {
        Dijkstra2 dijkstra = new Dijkstra2();
        double distance = dijkstra.runDijkstra2(
            graph,
            graph.getVertexId(uIndex),
            graph.getVertexId(wIndex),
            maxDistance,
            3,
            rank // Pass the nodeRanks array
        );
        return distance <= maxDistance;
    }

    private boolean edgeExists(int u, int w) {
        return graph.adj[u].containsKey(w);
    }

    public void saveAugmentedGraph(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // First line: write the number of vertices and edges
            writer.write(graph.V() + " " + graph.E() + "\n");

            // Write each vertex's ID, longitude, and latitude
            for (int i = 0; i < graph.V(); i++) {
                Vertex2 vertex = graph.getVertexByIndex(i);
                writer.write(vertex.getId() + " " + vertex.getLongitude() + " " + vertex.getLatitude() + "\n");
            }

            // Write edges, marking shortcuts and original edges separately
            for (Edge e : graph.edges()) {
                long originalV = graph.getVertexId(e.V());
                long originalW = graph.getVertexId(e.W());

                // For original edges, we output `-1` for the shortcut indicator
                if (e.c == -1) {
                    writer.write(originalV + " " + originalW + " " + e.weight() + " -1\n");
                } else {
                    // For shortcut edges, we output the contracted node's ID
                    writer.write(originalV + " " + originalW + " " + e.weight() + " " + graph.getVertexId(e.c) + "\n");
                }
            }
        }
    }

    public int[] getRanks() {
        return rank;
    }

    public static void main(String[] args) throws IOException {
        Graph graph2 = new Graph(new File("/home/knor/route/route-planning/SmallTest.graph"));
        // Graph graph = new Graph(new File("/home/knor/route/route-planning/SmallTest.graph"));

        ContractionHierarchy ch = new ContractionHierarchy(graph2);

        ch.preprocess();
        // int[] ranks = ch.getRanks();

        // Uncomment if you have a CHQuery class implemented
        // CHQuery query = new CHQuery(graph, ranks);
        // System.out.println(query.query(1, 6));

        ch.saveAugmentedGraph("test");
    }
}
