package org.example;


import java.io.*;
import java.util.*;

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

    // Node class for use in PriorityQueue
    private class Node implements Comparable<Node> {
        int vertex;
        double distance;

        Node(int vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }
    }

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

        distToForward[s] = 0.0;
        distToBackward[t] = 0.0;

        pqForward = new PriorityQueue<>();
        pqBackward = new PriorityQueue<>();

        pqForward.add(new Node(s, distToForward[s]));
        pqBackward.add(new Node(t, distToBackward[t]));

        while (!pqForward.isEmpty() && !pqBackward.isEmpty()) {
            // Termination condition
            if (pqForward.peek().distance + pqBackward.peek().distance >= bestPathDistance) {
                break;
            }

            // Decide which direction to expand
            if (pqForward.peek().distance <= pqBackward.peek().distance) {
                Node node = pqForward.poll();
                int v = node.vertex;
                if (!visitedForward[v]) {
                    visitedForward[v] = true;
                    relaxEdgesCH(g, v, distToForward, edgeToForward, distToBackward, visitedForward, visitedBackward, pqForward, true);
                }
            } else {
                Node node = pqBackward.poll();
                int v = node.vertex;
                if (!visitedBackward[v]) {
                    visitedBackward[v] = true;
                    relaxEdgesCH(g, v, distToBackward, edgeToBackward, distToForward, visitedBackward, visitedForward, pqBackward, false);
                }
            }
        }

        return bestPathDistance == Double.POSITIVE_INFINITY ? -1 : bestPathDistance;
    }

    private void relaxEdgesCH(GraphCh g, int v, double[] distTo, EdgeCh[] edgeTo,
                              double[] oppositeDistTo, boolean[] visitedThisDirection,
                              boolean[] visitedOppositeDirection, PriorityQueue<Node> pq, boolean isForward) {
        for (EdgeCh e : g.adj(v)) {
            int w = e.other(v);
            relaxedEdgesCount++;

            // Ensure only higher rank neighbors are expanded in CH
            if (isForward && nodeRank[w] < nodeRank[v]) continue;
            if (!isForward && nodeRank[w] < nodeRank[v]) continue;

            // Relax edge
            if (distTo[w] > distTo[v] + e.weight()) {
                distTo[w] = distTo[v] + e.weight();
                edgeTo[w] = e;
                pq.add(new Node(w, distTo[w]));  // Add updated distance

                // Check for a meeting point
                if (visitedOppositeDirection[w]) {
                    double potentialBestDistance = distTo[w] + oppositeDistTo[w];
                    if (potentialBestDistance < bestPathDistance) {
                        bestPathDistance = potentialBestDistance;
                        meetingPoint = w;
                    }
                }
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

    public static void main(String[] args) {
        System.out.println("a");
        File graphFile = new File("/home/knor/route2/route-planning/app/src/main/newaug.graph");
        File randomPairsFile = new File("/home/knor/route2/route-planning/app/src/main/resources/random_pairs.txt");
        File outputFile = new File("/home/knor/route2/route-planning/app/src/main/resources/ch_bidirectional_dijkstra_results.csv");

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
