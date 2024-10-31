package org.example;

public class Node implements Comparable<Node> {
    public int id;
    public double dist;

    public Node(int id, double dist) {
        this.id = id;
        this.dist = dist;
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.dist, other.dist);
    }
}
