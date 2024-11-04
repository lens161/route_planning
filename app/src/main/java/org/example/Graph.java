package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Graph {
    private final int V;
    private int E;
    public Map<Integer, Edge>[] adj;
    private Map<Long, Integer> vertexIdToIndexMap;
    private long[] indexToVertexId;
    private List<Vertex2> vertices;  // List to store Vertex2 objects

    // read Graph independently from a File
    @SuppressWarnings("unchecked")
    public Graph(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);

        String[] VE = sc.nextLine().split(" ");
        int V = Integer.parseInt(VE[0]);
        int E = Integer.parseInt(VE[1]);
        this.V = V;
        this.E = 0;

        adj = (Map<Integer, Edge>[]) new ConcurrentHashMap[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ConcurrentHashMap<>();
        }

        vertexIdToIndexMap = new HashMap<>();
        indexToVertexId = new long[V];
        vertices = new ArrayList<>(Collections.nCopies(V, null)); // Initialize list with nulls

        // Read vertices and create Vertex2 instances
        for (int i = 0; i < V; i++) {
            String[] vertexData = sc.nextLine().split(" ");
            long vertexId = Long.parseLong(vertexData[0]);
            float longitude = Float.parseFloat(vertexData[1]);
            float latitude = Float.parseFloat(vertexData[2]);

            vertexIdToIndexMap.put(vertexId, i);
            indexToVertexId[i] = vertexId;

            // Create a new Vertex2 and store it in the list
            Vertex2 vertex = new Vertex2(vertexId, i, longitude, latitude);
            vertices.set(i, vertex);  // Add Vertex2 object to the list
        }

        // Read edges
        for (int i = 0; i < E; i++) {
            String line = sc.nextLine();
            // System.out.println(line);
            String[] edgeData = line.split(" ");
            long vId = Long.parseLong(edgeData[0]);
            long wId = Long.parseLong(edgeData[1]);
            double weight = Double.parseDouble(edgeData[2]);

            int v = vertexIdToIndexMap.get(vId);
            int w = vertexIdToIndexMap.get(wId);

            Edge e = new Edge(v, w, weight, -1);
            addEdge(e);
        }
        sc.close();
    }

    public void addEdge(Edge e) {
        int v = e.either();
        int w = e.other(v);

        adj[v].put(w, e);
        adj[w].put(v, e);
        E++;
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public Iterable<Edge> adj(int v) {
        return adj[v].values();
    }

    public int getIndexForVertex(long vertexId) {
        return vertexIdToIndexMap.getOrDefault(vertexId, -1);
    }

    public long getVertexId(int index) {
        return indexToVertexId[index];
    }

    public Vertex2 getVertexByIndex(int index) {
        return vertices.get(index);
    }

    public Iterable<Long> getVertexIds() {
        return vertexIdToIndexMap.keySet();
    }

    public Iterable<Edge> edges() {
        LinkedList<Edge> list = new LinkedList<>();
        for (int v = 0; v < adj.length; v++) {
            for (Edge e : adj[v].values()) {
                int w = e.other(v);
                if (v < w) { // Ensure each edge is only added once
                    list.add(e);
                }
            }
        }
        return list;
    }

    public boolean edgeExists(int u, int w) {
        return adj[u].containsKey(w);
    }
}
