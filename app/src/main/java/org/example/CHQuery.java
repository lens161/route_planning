package org.example;

import java.util.Comparator;
import java.util.PriorityQueue;

public class CHQuery {
    private final Graph graph;
    private final int[] rank;

    public CHQuery(Graph graph, int[] rank) {
        this.graph = graph;
        this.rank = rank;
    }

    public double query(int source, int target) {
        PriorityQueue<Node> forwardPQ = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
        PriorityQueue<Node> backwardPQ = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

        forwardPQ.add(new Node(source, 0));
        backwardPQ.add(new Node(target, 0));

        double bestDistance = Double.POSITIVE_INFINITY;

        while (!forwardPQ.isEmpty() && !backwardPQ.isEmpty()) {
            if (!forwardPQ.isEmpty()) bestDistance = processNext(forwardPQ, bestDistance);
            if (!backwardPQ.isEmpty()) bestDistance = processNext(backwardPQ, bestDistance);
        }

        return bestDistance == Double.POSITIVE_INFINITY ? -1 : bestDistance;
    }

    private double processNext(PriorityQueue<Node> pq, double bestDistance) {
        Node current = pq.poll();
        int v = current.vertex;
        double dist = current.distance;

        for (Edge edge : graph.adj(v)) {
            int w = edge.other(v);
            if (rank[w] < rank[v]) continue; // Only move up in rank order

            double newDist = dist + edge.weight();
            if (newDist < bestDistance) {
                bestDistance = newDist;
                pq.add(new Node(w, newDist));
            }
        }
        return bestDistance;
    }

    private static class Node {
        int vertex;
        double distance;

        public Node(int vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }
}
