package org.example;

public class EdgeCh implements Comparable<EdgeCh> {

    private final int v;
    private final int w;
    private final double weight;
    // The vertex the shortcut has been contracted from
    // If not a shortcut, c = -1
    public final long c; // Use long for shortcut indicator

    public EdgeCh(int v, int w, double weight, long c) {
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
        if (vertexIndex == v) return w;
        else if (vertexIndex == w) return v;
        else throw new IllegalArgumentException("Illegal endpoint: " + vertexIndex);
    }

    @Override
    public int compareTo(EdgeCh that) {
        return Double.compare(this.weight, that.weight);
    }

    public boolean isShortcut() {
        return c != -1;
    }
}
