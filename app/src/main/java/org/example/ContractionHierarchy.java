package org.example;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ContractionHierarchy {
    private final Graph graph;
    // this is the "stale" list
    private final int[] rank;
    private int contractionOrder;
    private TreeSet<Node> contractionQueue;
    private Map<Integer, Node> nodeReferences;
    // every Thread gets its own Dijkstra Object such that the individual calls do not interfere with each other
    // so whenever an instance of this special dijkstra is instanciated it will be local to only the thread it was created 
    private ThreadLocal<Dijkstra2> threadLocalDijkstra = ThreadLocal.withInitial(Dijkstra2::new);
    private long totalContractNodeTime = 0;
    private int contractedNodeCount = 0;
    private double totalSecs = 0;

    public ContractionHierarchy(Graph graph) {
        this.graph = graph;
        this.rank = new int[graph.V()];
        Arrays.fill(rank, -1);
        this.contractionOrder = 0;
        // this.threadLocalDijkstra = new Dijkstra2();
    }

    // Node class for priority queue
    // Nodes represent the items in the priority queue (treeset).
    private class Node implements Comparable<Node> {
        int index;
        int importance;

        public Node(int index, int importance) {
            this.index = index;
            this.importance = importance;
        }

        @Override
        public int compareTo(Node other) {
            if (this.importance != other.importance) {
                return Integer.compare(this.importance, other.importance);
            } else {
                return Integer.compare(this.index, other.index);
            }
        }
    }

    
    // Calculates the importance score of a node.
    // Includes heuristics from the Geisberger Paper.
    private int calculateNodeImportance(int v) {
        // the edge difference based on the estimated shortcuts per contracted vertex
        // the more shortcuts can be added by contracting the vertex, the higher the priority
        int edgeDifference = computeEdgeDifference(v);
        // number of already contracted neighbours. only important for contraction. 
        //not necessary for initialisation of contraction queue.
        int contractedNeighbors = computeContractedNeighbors(v);
        // degree heuristic: promtote vertices with higher degree to be contracted earlier.
        int degree = graph.adj[v].size();

        // Combine heuristics into a single importance score
        // low edge difference -> contract earlier
        // high contracted Neighbours and degree -> contract earlier
        // low importance -> higher up in queue:

        // int importance = -(contractedNeighbors*2  + degree*2) + edgeDifference*10;

        // faster heuristics:
        int importance = edgeDifference * 14 -(contractedNeighbors*5) +degree*15;

        // old faster:
        // int importance = edgeDifference * 14 +contractedNeighbors*25 +degree*15;

        return importance;
    }

    private int computeEdgeDifference(int v) {
        int originalEdges = 0;
        for (Edge e : graph.adj[v].values()) {
            if (!e.isShortcut()) {
                originalEdges++;
            }
        }

        // the amount of shortcuts is only estimated -> shortens preprocessing time
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

    private int estimateShortcuts(int v) {
        int shortcuts = 0;
        //get all neighbours of v
        List<Integer> neighbors = new ArrayList<>();
        for (Edge e : graph.adj[v].values()) {
            int w = e.other(v);
            if (rank[w] == -1) { // Only consider uncontracted neighbours
                neighbors.add(w);
            }
        }

        // find wether there is a direct connection between the two neighbours.
        // if not then the connection through v is most likely worth a shortcut
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
        contractionQueue = new TreeSet<>();
        nodeReferences = new HashMap<>();

        // add all vertices and their importance to the queue
        for (int i = 0; i < graph.V(); i++) {
            int importance = calculateNodeImportance(i);
            Node node = new Node(i, importance);
            contractionQueue.add(node);
            nodeReferences.put(i, node);
        }
    }

    public void preprocess() {
        initializeContractionQueue();

        int processedNodes = 0;
        long startTime = System.nanoTime(); 

        while (!contractionQueue.isEmpty()) {
            Node node = contractionQueue.pollFirst();
            int v = node.index;

            // Recalculate importance
            int newImportance = calculateNodeImportance(v);
            if (newImportance > node.importance) {
                // Reinsert with updated importance to the queue
                Node newNode = new Node(v, newImportance);
                contractionQueue.add(newNode);
                nodeReferences.put(v, newNode);
                continue;
            }

            contractNode(v);
            processedNodes++;

            // Update queue with affected neighbors
            updateContractionQueue(v);

            // Print progress only every 10,000 nodes to avoid spamming the bloody terminal
            if (contractionOrder % 10000 == 0) {
                System.out.println("Contracted " + contractionOrder + " nodes out of " + graph.V());

                // Calculate elapsed time
                long elapsedTime = System.nanoTime() - startTime;
                double elapsedSeconds = elapsedTime / 1e9;
                System.out.printf("Elapsed time: %.2f seconds%n", elapsedSeconds);

                // Calculate average contraction time per node
                double averageTime = (totalContractNodeTime / 1e9) / contractedNodeCount;
                System.out.printf("Average contraction time per node: %.6f seconds%n", averageTime);
            }
        }

        // Total time taken
        long totalTime = System.nanoTime() - startTime;
        double totalSeconds = totalTime / 1e9;
        totalSecs = totalSeconds;
        System.out.printf("Total contraction time: %.2f seconds%n", totalSeconds);
    }

    private void updateContractionQueue(int contractedNode) {
        for (Edge e : graph.adj[contractedNode].values()) {
            int w = e.other(contractedNode);
            if (rank[w] == -1) { // Only if not already contracted
                // Recalculates importance
                int newImportance = calculateNodeImportance(w);

                // Removes the old node from the queue
                Node oldNode = nodeReferences.get(w);
                contractionQueue.remove(oldNode);

                // Creates a new node with updated importance and add it to the queue
                Node newNode = new Node(w, newImportance);
                contractionQueue.add(newNode);
                nodeReferences.put(w, newNode);
            }
        }
    }

    private void contractNode(int v) {
        long startTime = System.nanoTime();

        rank[v] = contractionOrder++;
        Map<Integer, Edge> neighbors = graph.adj[v];

        List<Integer> neighborIndices = new ArrayList<>();
        for (Edge e : neighbors.values()) {
            int w = e.other(v);
            if (rank[w] == -1) { // Only consider uncontracted neighbors
                neighborIndices.add(w);
            }
        }

        // Collect neighbor pairs to process
        // pairs are stored as arrays of size 2 where arr[0] = v and arr[1] = w
        List<int[]> neighborPairs = new ArrayList<>();
        for (int i = 0; i < neighborIndices.size(); i++) {
            int u = neighborIndices.get(i);
            for (int j = i + 1; j < neighborIndices.size(); j++) {
                int w = neighborIndices.get(j);
                if (!graph.edgeExists(u, w)) {
                    neighborPairs.add(new int[]{u, w});
                }
            }
        }

        // non parallel implementation:
        for (int[] pair : neighborPairs) {
            int u = pair[0];
            int w = pair[1];
            double shortcutWeight = neighbors.get(u).weight() + neighbors.get(w).weight();

            // Limit witness search to a small number of hops
            boolean hasWitness = witnessSearch(u, w, shortcutWeight, 10);

            if (!hasWitness) {
                Edge shortcut = new Edge(u, w, shortcutWeight, v);
                // synchronized call to add graph edge to ensure mutual exclusion for concurrent operations by the stream.
                    graph.addEdge(shortcut);
            }
        }

        // Process neighbor pairs in parallell
        // neighborPairs.parallelStream().forEach(pair -> {
        //     int u = pair[0];
        //     int w = pair[1];
        //     double shortcutWeight = neighbors.get(u).weight() + neighbors.get(w).weight();

        //     // Limit witness search to a small number of hops
        //     boolean hasWitness = witnessSearch(u, w, shortcutWeight, 10);

        //     if (!hasWitness) {
        //         Edge shortcut = new Edge(u, w, shortcutWeight, v);
        //         // synchronized call to add graph edge to ensure mutual exclusion for concurrent operations by the stream.
        //         synchronized (graph) {
        //             graph.addEdge(shortcut);
        //         }
        //     }
        // });

        long endTime = System.nanoTime();
        totalContractNodeTime += (endTime - startTime);
        contractedNodeCount++;
    }

    
    //Do witness search from node u to node w within a limited distance and hops.
    private boolean witnessSearch(int uIndex, int wIndex, double maxDistance, int maxHops) {
        Dijkstra2 dijkstra = threadLocalDijkstra.get();
        double distance = dijkstra.runDijkstra2(graph, uIndex, wIndex, maxDistance, maxHops, rank);
        return distance <= maxDistance;
    }

    public void saveAugmentedGraph(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // First line: write the number of vertices and edges
            writer.write(graph.V() + " " + graph.E() + "\n");

            // Write each vertex's ID, longitude, latitude and rank
            for (int i = 0; i < graph.V(); i++) {
                Vertex2 vertex = graph.getVertexByIndex(i);
                writer.write(vertex.getId() + " " + vertex.getLongitude() + " " + vertex.getLatitude() + " " + rank[i] +  "\n");
            }

            // Write edges, marking shortcuts and original edges
            for (Edge e : graph.edges()) {
                long originalV = graph.getVertexId(e.V());
                long originalW = graph.getVertexId(e.W());

                // For original edges label -1 
                if (e.c == -1) {
                    writer.write(originalV + " " + originalW + " " + (int)e.weight() + " -1\n");
                } else {
                    // For shortcut edges label the vertex the shortcut has been contracted from
                    writer.write(originalV + " " + originalW + " " + (int)e.weight() + " " + graph.getVertexId(e.c) + "\n");
                }
            }
            double averageTime = (totalContractNodeTime / 1e9) / contractedNodeCount;
            // writer.write("average contraction time: " + averageTime + "\n") ;
            // writer.write("total contracttion time:" + totalSecs);
        }

    }

    public int[] getRank() {
        return rank;
    }

    public static void main(String[] args) throws IOException {
        // Replace with your graph file path
        Graph graph = new Graph(new File("/home/knor/AA/route4/route-planning/app/src/main/newdenmark.graph"));

        ContractionHierarchy ch = new ContractionHierarchy(graph);

        ch.preprocess();
        ch.saveAugmentedGraph("/home/knor/AA/route4/route-planning/app/src/main/newaug3.graph");
    }
}
