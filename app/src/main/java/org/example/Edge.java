package org.example;

public class Edge implements Comparable<Edge> { 

    private final int v; 
    private final int w; 
    private final double weight;
    // the vertex the shortcut has been contracted from
    // if not a shortcut c = -1;
    public final int c;

    public Edge(int v, int w, double weight, int c) {
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.weight = weight;
        this.c = c;
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
        return vertexIndex;
        // else throw new IllegalArgumentException("Illegal endpoint: " + vertexIndex);
    }

    @Override
    public int compareTo(Edge that) {
        return Double.compare(this.weight, that.weight);
    }

    public String toString() {
        return String.format("%d-%d %.5f %s", v, w, weight, c);
    }

    public int V(){
        return v;
    }

    public int W(){
        return w;
    }
}
