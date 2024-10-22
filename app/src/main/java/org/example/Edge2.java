package org.example;

public class Edge2 implements Comparable<Edge2> { 

    private final int v; 
    private final int w; 
    private final double weight;

    public Edge2(int v, int w, double weight) {
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    public double weight() {
        return weight;
    }

    public int either() {
        return v;
    }

    public int other(int vertexIndex) {
        if      (vertexIndex == v) return w;
        else if (vertexIndex == w) return v;
        else throw new IllegalArgumentException("Illegal endpoint: " + vertexIndex);
    }

    @Override
    public int compareTo(Edge2 that) {
        return Double.compare(this.weight, that.weight);
    }

    public String toString() {
        return String.format("%d-%d %.5f", v, w, weight);
    }
}
