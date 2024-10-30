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
    private Dijkstra2 dijkstra; // Reuse the Dijkstra2 instance

    public ContractionHierarchy(Graph graph) {
        this.graph = graph;
        this.rank = new int[graph.V()];
        Arrays.fill(rank, -1);
        this.contractionOrder = 0;
        this.dijkstra = new Dijkstra2(); // No need to pass V
    }

    /**
     * Calculates the importance score of a node.
     * Now includes various heuristics as per the Geisberger paper.
     */
    private int calculateNodeImportance(int v) {
        int edgeDifference = computeEdgeDifference(v);
        int contractedNeighbors = computeContractedNeighbors(v);
        int shortcutCover = computeShortcutCover(v);
        int degree = graph.adj[v].size();

        // Combine heuristics into a single importance score
        int importance = edgeDifference * 14 + contractedNeighbors * 25 + shortcutCover * 10 + degree * 15;

        return importance;
    }

    private int computeEdgeDifference(int v) {
        int originalEdges = 0;
        for (Edge e : graph.adj[v].values()) {
            if (!e.isShortcut()) {
                originalEdges++;
            }
        }

        int shortcutEdges = estimateShortcuts(v);
        return shortcutEdges - originalEdges;
    }

    private int computeContractedNeighbors(int v) {
        int contracted = 0;
        for (Edge e : graph.adj[v].values()) {
            int w = e.other(v);
            if (rank[w] != -1) {
                contracted++;
            }
        }
        return contracted;
    }

    private int computeShortcutCover(int v) {
        return estimateShortcuts(v);
    }

    private int estimateShortcuts(int v) {
        int shortcuts = 0;
        List<Integer> neighbors = new ArrayList<>();
        for (Edge e : graph.adj[v].values()) {
            int w = e.other(v);
            if (rank[w] == -1) { // Only consider uncontracted neighbors
                neighbors.add(w);
            }
        }

        for (int i = 0; i < neighbors.size(); i++) {
            int u = neighbors.get(i);
            for (int j = i + 1; j < neighbors.size(); j++) {
                int w = neighbors.get(j);
                if (!graph.edgeExists(u, w)) {
                    shortcuts++;
                }
            }
        }
        return shortcuts;
    }

    private void initializeContractionQueue() {
        contractionQueue = new PriorityQueue<>(Comparator.comparingInt(v -> calculateNodeImportance(v)));
        for (int i = 0; i < graph.V(); i++) {
            contractionQueue.add(i);
        }
    }

    public void preprocess() {
        initializeContractionQueue();

        int processedNodes = 0;

        while (!contractionQueue.isEmpty()) {
            int v = contractionQueue.poll();

            // Recalculate importance if necessary
            int oldImportance = calculateNodeImportance(v);
            int newImportance = calculateNodeImportance(v);

            if (newImportance > oldImportance) {
                // Reinsert with updated importance
                contractionQueue.add(v);
                continue;
            }

            contractNode(v);
            processedNodes++;

            // Update queue with affected neighbors
            updateContractionQueue(v);

            // Print progress every 10,000 nodes
            if (contractionOrder % 10000 == 0) {
                System.out.println("Contracted " + contractionOrder + " nodes out of " + graph.V());
            }
        }
    }

    private void updateContractionQueue(int contractedNode) {
        Set<Integer> affectedNodes = new HashSet<>();
        // Affected nodes are neighbors of the contracted node
        for (Edge e : graph.adj[contractedNode].values()) {
            int w = e.other(contractedNode);
            if (rank[w] == -1) { // Only if not already contracted
                affectedNodes.add(w);
            }
        }

        // Recalculate importance and update queue
        for (int v : affectedNodes) {
            contractionQueue.remove(v); // Remove outdated entry
            contractionQueue.add(v);    // Re-add with updated importance
        }
    }

    private void contractNode(int v) {
        rank[v] = contractionOrder++;
        Map<Integer, Edge> neighbors = graph.adj[v];

        List<Integer> neighborIndices = new ArrayList<>();
        for (Edge e : neighbors.values()) {
            int w = e.other(v);
            if (rank[w] == -1) { // Only consider uncontracted neighbors
                neighborIndices.add(w);
            }
        }

        // For performance, avoid nested loops over all neighbor pairs
        for (int i = 0; i < neighborIndices.size(); i++) {
            int u = neighborIndices.get(i);
            Edge edgeUV = neighbors.get(u);

            for (int j = i + 1; j < neighborIndices.size(); j++) {
                int w = neighborIndices.get(j);
                Edge edgeVW = neighbors.get(w);

                if (!graph.edgeExists(u, w)) {
                    double shortcutWeight = edgeUV.weight() + edgeVW.weight();

                    // Limit witness search to a small number of hops
                    boolean hasWitness = witnessSearch(u, w, shortcutWeight, 3);

                    if (!hasWitness) {
                        Edge shortcut = new Edge(u, w, shortcutWeight, v);
                        graph.addEdge(shortcut);
                    }
                }
            }
        }
    }

    /**
     * Performs a witness search from node u to node w within a limited distance and hops.
     *
     * @param uIndex - the index of the starting node
     * @param wIndex - the index of the target node
     * @param maxDistance - maximum allowable distance for a shortcut
     * @param maxHops - maximum number of hops to consider
     * @return true if a path exists within maxDistance and maxHops; otherwise, false
     */
    private boolean witnessSearch(int uIndex, int wIndex, double maxDistance, int maxHops) {
        double distance = dijkstra.runDijkstra2(
            graph,
            uIndex,
            wIndex,
            maxDistance,
            maxHops,
            rank // Pass the nodeRanks array
        );
        return distance <= maxDistance;
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

                // For original edges, we output -1 for the shortcut indicator
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
        Graph graph2 = new Graph(new File("/home/knor/route/newdenmark.graph"));

        ContractionHierarchy ch = new ContractionHierarchy(graph2);

        ch.preprocess();
        ch.saveAugmentedGraph("test");
    }
}
