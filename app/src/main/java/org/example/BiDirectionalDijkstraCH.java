package org.example;


import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class BiDirectionalDijkstraCH {
    private double[] distToForward;
    private double[] distToBackward;
    private EdgeCh[] edgeToForward;
    private EdgeCh[] edgeToBackward;
    private boolean[] visitedForward;
    private boolean[] visitedBackward;
    private PriorityQueue<Node> pqForward;
    private PriorityQueue<Node> pqBackward;
    private int relaxedEdgesCount;

    private double bestPathDistance;
    private int meetingPoint; // index of the meeting point

    private long[] nodeRank; // Rank of each node for CH

    public BiDirectionalDijkstraCH(int V, long[] nodeRank) {
        this.nodeRank = nodeRank; // Rank array for contraction hierarchy
    }

    public double runBiDirectionalCHDijkstra(GraphCh g, long sVertexId, long tVertexId) {
        this.relaxedEdgesCount = 0;
        this.bestPathDistance = Double.POSITIVE_INFINITY;
        this.meetingPoint = -1;

        int V = g.V();
        distToForward = new double[V];
        distToBackward = new double[V];
        edgeToForward = new EdgeCh[V];
        edgeToBackward = new EdgeCh[V];
        visitedForward = new boolean[V];
        visitedBackward = new boolean[V];

        Arrays.fill(distToForward, Double.POSITIVE_INFINITY);
        Arrays.fill(distToBackward, Double.POSITIVE_INFINITY);

        int s = g.getIndexForVertex(sVertexId);
        int t = g.getIndexForVertex(tVertexId);

        if (s == -1 || t == -1) {
            // Source or target vertex not found
            return -1;
        }
        if (s == t)
            return 0;

        distToForward[s] = 0.0;
        distToBackward[t] = 0.0;

        pqForward = new PriorityQueue<>();
        pqBackward = new PriorityQueue<>();

        pqForward.add(new Node(s, distToForward[s]));
        pqBackward.add(new Node(t, distToBackward[t]));

        boolean directionIsForward = true; // r = true (forward)

        while (!pqForward.isEmpty() || !pqBackward.isEmpty()) {
            double minForward = pqForward.isEmpty() ? Double.POSITIVE_INFINITY : pqForward.peek().dist;
            double minBackward = pqBackward.isEmpty() ? Double.POSITIVE_INFINITY : pqBackward.peek().dist;

            if (bestPathDistance <= Math.min(minForward, minBackward)) {
                break;
            }

            // Switch direction if the other queue is not empty
            if ((directionIsForward && !pqBackward.isEmpty()) || (!directionIsForward && !pqForward.isEmpty())) {
                directionIsForward = !directionIsForward;
            }

            if (directionIsForward) {
                if (!pqForward.isEmpty()) {
                    Node node = pqForward.poll();
                    int u = node.id;
                    if (!visitedForward[u]) {
                        visitedForward[u] = true;

                        // Update bestPathDistance
                        if (distToForward[u] + distToBackward[u] < bestPathDistance) {
                            bestPathDistance = distToForward[u] + distToBackward[u];
                            meetingPoint = u;
                        }

                        relaxEdgesCH(g, u, distToForward, edgeToForward, pqForward, true);
                    }
                }
            } else {
                if (!pqBackward.isEmpty()) {
                    Node node = pqBackward.poll();
                    int u = node.id;
                    if (!visitedBackward[u]) {
                        visitedBackward[u] = true;

                        // Update bestPathDistance
                        if (distToForward[u] + distToBackward[u] < bestPathDistance) {
                            bestPathDistance = distToForward[u] + distToBackward[u];
                            meetingPoint = u;
                        }

                        relaxEdgesCH(g, u, distToBackward, edgeToBackward, pqBackward, false);
                    }
                }
            }
        }

        return bestPathDistance == Double.POSITIVE_INFINITY ? -1 : bestPathDistance;
    }

    private void relaxEdgesCH(GraphCh g, int u, double[] distTo, EdgeCh[] edgeTo,
                        PriorityQueue<Node> pq, boolean isForward) {
    for (EdgeCh e : g.adj(u)) {
        int v = e.other(u); // Get the neighbor node, treating as undirected

        // Ensure only neighbors with higher ranks are considered for relaxation
        if (nodeRank[v] < nodeRank[u]) continue;

        relaxedEdgesCount++;

        // Relax edge in the current direction
        if (distTo[v] > distTo[u] + e.weight()) {
            distTo[v] = distTo[u] + e.weight();
            edgeTo[v] = e;
            pq.add(new Node(v, distTo[v]));
        }
    }
}


    public int getRelaxedEdgesCount() {
        return relaxedEdgesCount;
    }

    public void clear() {
        distToForward = null;
        distToBackward = null;
        edgeToForward = null;
        edgeToBackward = null;
        visitedForward = null;
        visitedBackward = null;
        pqForward = null;
        pqBackward = null;
        relaxedEdgesCount = 0;
        bestPathDistance = Double.POSITIVE_INFINITY;
        meetingPoint = -1;
    }

    // Checks if the given node has the highest rank in the graph
    private boolean isHighRankNode(int v) {
        return nodeRank[v] == Arrays.stream(nodeRank).max().orElse(Long.MAX_VALUE);
    }

    // Checks if there are edges from 'v' to higher-ranked nodes in the given direction
    private boolean hasHigherRankedEdges(GraphCh g, int v, boolean isForward) {
        for (EdgeCh e : g.adj(v)) {
            int w = e.other(v);
            // Only check edges that point to higher-ranked nodes
            if (nodeRank[w] > nodeRank[v]) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        File graphFile = new File("/home/najj/applied_algo/route-planning/app/src/main/newaug.graph");
        File randomPairsFile = new File("/home/najj/applied_algo/route-planning/app/src/main/newrandom_pairs.txt");
        File outputFile = new File("/home/najj/applied_algo/route-planning/app/src/main/resources/naja_debug2_CHbidijkstra_results.csv");

        try {
            // Load the graph
            GraphCh graph = new GraphCh(graphFile);
            long[] nodeRanks = graph.getNodeRanks();

            // Initialize BiDirectionalDijkstraCH instance
            BiDirectionalDijkstraCH biDijkstra = new BiDirectionalDijkstraCH(graph.V(), nodeRanks);

            // Read pairs from the file
            List<long[]> pairs = readPairs(randomPairsFile);

            long totalExecutionTime = 0;
            int totalRelaxedEdges = 0;
            int numberOfPairs = pairs.size();

            // Prepare to write the output
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.println("Source,Target,Distance,ExecutionTime");

                // Perform bidirectional search for each pair
                for (long[] pair : pairs) {
                    long source = pair[0];
                    long target = pair[1];

                    long startTime = System.nanoTime();
                    double distance = biDijkstra.runBiDirectionalCHDijkstra(graph, source, target);
                    long endTime = System.nanoTime();

                    long duration = endTime - startTime;
                    totalExecutionTime += duration;
                    totalRelaxedEdges += biDijkstra.getRelaxedEdgesCount();

                    writer.printf("%d,%d,%.5f,%d%n", source, target, distance, duration);
                    writer.flush();

                    // Clear the BiDirectionalDijkstraCH instance for the next pair
                    biDijkstra.clear();
                }

                // Calculate and print averages
                double averageExecutionTime = totalExecutionTime / (numberOfPairs * 1_000_000_000.0);
                double averageRelaxedEdges = (double) totalRelaxedEdges / numberOfPairs;

                // Write averages to the console and file
                System.out.println("Average Execution Time: " + averageExecutionTime + " seconds");
                System.out.println("Average Relaxed Edges: " + averageRelaxedEdges);
                writer.println("Average Execution Time," + averageExecutionTime + " seconds");
                writer.println("Average Relaxed Edges," + averageRelaxedEdges);
            }

            System.out.println("Bidirectional CH Dijkstra results saved to " + outputFile.getPath());

        } catch (IOException e) {
            System.err.println("Error loading files: " + e.getMessage());
        }
    }

    private static List<long[]> readPairs(File file) {
        List<long[]> pairs = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().trim().split("\\s+");
                if (line.length < 2) {
                    continue;
                }
                long source = Long.parseLong(line[0]);
                long target = Long.parseLong(line[1]);
                pairs.add(new long[]{source, target});
            }
        } catch (FileNotFoundException e) {
            System.err.println("Random pairs file not found: " + e.getMessage());
        }
        return pairs;
    }
}
