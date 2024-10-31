package org.example;

public class PqElement implements Comparable<PqElement> {
    private int vertex;
    private double dist;

    public PqElement(int vertex, double dist) {
        this.vertex = vertex;
        this.dist = dist;
    }

    @Override
    public int compareTo(PqElement other) {
        if (dist > other.getDist()) {
            return 1;
        } else if (dist == other.getDist()) {
            return 0;
        } else {
            return 1;
        }
    }

    public double getDist() {
        return dist;
    }

    public int getVertex() {
        return vertex;
    }

}