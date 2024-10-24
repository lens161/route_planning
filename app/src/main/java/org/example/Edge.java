package org.example;

public class Edge implements Comparable<Edge> { 

    private final int v; 
    private final int w; 
    private final double weight;
    private final boolean shortcut;

    public Edge(int v, int w, double weight, boolean shortcut) {
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.weight = weight;
        this.shortcut = shortcut;
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
        String sc = (shortcut) ? "s" : "n";
        return String.format("%d-%d %.5f %s", v, w, weight, sc);
    }

    public int V(){
        return v;
    }

    public int W(){
        return w;
    }
}
