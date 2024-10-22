package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph2 {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;
    private int E;
    private List<Edge2>[] adj;
    private Map<Long, Integer> vertexIdToIndexMap;
    private long[] indexToVertexId;

    public Graph2(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);

        String[] VE = sc.nextLine().split(" ");
        int V = Integer.parseInt(VE[0]);
        int E = Integer.parseInt(VE[1]);
        this.V = V;
        this.E = 0; 

        adj = (List<Edge2>[]) new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }

        vertexIdToIndexMap = new HashMap<>();
        indexToVertexId = new long[V];


        for (int i = 0; i < V; i++) {
            String[] vertex = sc.nextLine().split(" ");
            long vertexId = Long.parseLong(vertex[0]);
            vertexIdToIndexMap.put(vertexId, i);
            indexToVertexId[i] = vertexId;

        }

        // Read edges
        for (int i = 0; i < E; i++) {
            String line = sc.nextLine();
            String[] edge = line.split(" ");
            long vId = Long.parseLong(edge[0]);
            long wId = Long.parseLong(edge[1]);
            double weight = Double.parseDouble(edge[2]);

            int v = vertexIdToIndexMap.get(vId);
            int w = vertexIdToIndexMap.get(wId);

            Edge2 e = new Edge2(v, w, weight);
            addEdge(e);
        }
        sc.close();
    }

    public void addEdge(Edge2 e) {
        int v = e.either();
        int w = e.other(v);

        adj[v].add(e);
        adj[w].add(e);
        E++;
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public Iterable<Edge2> adj(int v) {
        return adj[v];
    }

    public int getIndexForVertex(long vertexId) {
        return vertexIdToIndexMap.getOrDefault(vertexId, -1);
    }

    public long getVertexId(int index) {
        return indexToVertexId[index];
    }

    public Iterable<Long> getVertexIds() {
        return vertexIdToIndexMap.keySet();
    }
}
