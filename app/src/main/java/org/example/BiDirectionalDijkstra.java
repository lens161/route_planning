package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.PriorityQueue;

public class BiDirectionalDijkstra {
    private double[] distToForward;
    private double[] distToBackward;
    private Edge[] edgeToForward;
    private Edge[] edgeToBackward;
    private boolean[] visitedForward;
    private boolean[] visitedBackward;
    private int[] hopTo;
    private PriorityQueue<Node> pqForward;
    private PriorityQueue<Node> pqBackward;
    private int relaxedEdgesCount;

    private double bestPathDistance;
    private int meetingPoint; // index of the meeting point

    public BiDirectionalDijkstra() {
        this.relaxedEdgesCount = 0;
    }

    public double runBiDirectionalDijkstra(Graph g, long sVertexId, long tVertexId) {
        this.relaxedEdgesCount = 0;
        this.bestPathDistance = Double.POSITIVE_INFINITY;
        this.meetingPoint = -1;

        int V = g.V();
        distToForward = new double[V];
        distToBackward = new double[V];
        edgeToForward = new Edge[V];
        edgeToBackward = new Edge[V];
        visitedForward = new boolean[V];
        visitedBackward = new boolean[V];
        hopTo = new int[V];

        Arrays.fill(distToForward, Double.POSITIVE_INFINITY);
        Arrays.fill(distToBackward, Double.POSITIVE_INFINITY);

        int s = g.getIndexForVertex(sVertexId);
        int t = g.getIndexForVertex(tVertexId);
        if (s==t) {return 0;}

        distToForward[s] = 0.0;
        distToBackward[t] = 0.0;

        pqForward = new PriorityQueue<>();
        pqBackward = new PriorityQueue<>();

        Node start = new Node(s, distToForward[s]);
        Node target = new Node(t, distToBackward[t]);
        pqForward.add(start);
        pqBackward.add(target);

        while (!pqForward.isEmpty() && !pqBackward.isEmpty()) {
            // Termination condition
            int minForward = pqForward.peek().id;
            int minBackward = pqBackward.peek().id;
            double minDistForward = distToForward[minForward];
            double minDistBackward = distToBackward[minBackward];
            if (minDistForward + minDistBackward >= bestPathDistance) {
                break;
            }

            // Decide which direction to expand
            if (minDistForward <= minDistBackward) {
                int v = pqForward.poll().id;
                if (!visitedForward[v]) {
                    visitedForward[v] = true;
                    relaxEdges(g, v, distToForward, edgeToForward, distToBackward, visitedForward, visitedBackward, pqForward);
                }
            } else {
                int v = pqBackward.poll().id;
                if (!visitedBackward[v]) {
                    visitedBackward[v] = true;
                    relaxEdges(g, v, distToBackward, edgeToBackward, distToForward, visitedBackward, visitedForward, pqBackward);
                }
            }
        }

        return bestPathDistance == Double.POSITIVE_INFINITY ? -1 : bestPathDistance;
    }
 
    private void relaxEdges(Graph g, int v, double[] distTo, Edge[] edgeTo,
                            double[] oppositeDistTo, boolean[] visitedThisDirection,
                            boolean[] visitedOppositeDirection, PriorityQueue<Node> pq) {
        for (Edge e : g.adj(v)) {
            int w = e.other(v);
            if (visitedThisDirection[w]) continue;  // Skip if already visited at all
            relaxedEdgesCount++;

            // if (bestPathDistance != Double.POSITIVE_INFINITY) {
            //     return;  // Return early if the best path has been found
            // }

            // System.out.printf("Relaxing edge %d-%d with weight %.2f. Current distance to %d: %.2f\n",
            //     v, w, e.weight(), w, distTo[w]);

            // Relax the edge only if it leads to a shorter path
            if (distTo[w] > distTo[v] + e.weight()) {
                distTo[w] = distTo[v] + e.weight();
                edgeTo[w] = e;
                
                // if (pq.contains(w)) {
                    // pq.decreaseKey(w, distTo[w]);
                // } else {
                    pq.add(new Node(w, distTo[w]));
                // }
                // System.out.printf("Updated distance to %d: %.2f\n", w, distTo[w]);
            }

            if (visitedOppositeDirection[w]) {
                double potentialBestDistance = distTo[w] + oppositeDistTo[w];
                if (potentialBestDistance < bestPathDistance) {
                    bestPathDistance = potentialBestDistance;
                    meetingPoint = w;
                    // System.out.printf("Updated best path distance to %.2f via meeting point %d\n",
                    //         bestPathDistance, meetingPoint);
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

    //for testing purposes
    public void verifySymmetry(Graph g) {
        for (int v = 0; v < g.V(); v++) {
            for (Edge e : g.adj(v)) {
                int w = e.other(v);
                if (g.edgeExists(w, v)) {
                    double forwardWeight = e.weight();
                    double backwardWeight = g.getEdgeWeight(w, v);
                    if (forwardWeight != backwardWeight) {
                        System.out.printf("Asymmetry detected: Edge %d-%d has weight %.2f, but edge %d-%d has weight %.2f\n",
                                v, w, forwardWeight, w, v, backwardWeight);
                    }
                } else {
                    System.out.printf("Missing reverse edge: Edge %d-%d exists, but edge %d-%d does not.\n", v, w, w, v);
                }
            }
        }
    }
    

    // public static void main(String[] args) throws FileNotFoundException {
    //     File input1 = new File("/home/knor/AA/route4/route-planning/app/src/main/newdenmark.graph");
    //     Graph g = new Graph(input1);
    //     BiDirectionalDijkstra dijkstra = new BiDirectionalDijkstra();

    //     long start = System.nanoTime();
    //     System.out.println(dijkstra.runBiDirectionalDijkstra(g, 47021985,2064649066));
    //     long end = System.nanoTime();
    //     System.out.println((end - start));
    // }
}
