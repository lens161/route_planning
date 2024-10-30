package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph {
    @SuppressWarnings("unused")
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;
    private int E;
    public List<Edge>[] adj;
    private Map<Long, Integer> vertexIdToIndexMap;
    private long[] indexToVertexId;
    private List<Vertex2> vertices;  // List to store Vertex2 objects

    @SuppressWarnings("unchecked")
    public Graph(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);

        String[] VE = sc.nextLine().split(" ");
        int V = Integer.parseInt(VE[0]);
        int E = Integer.parseInt(VE[1]);
        this.V = V;
        this.E = 0;

        adj = (List<Edge>[]) new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
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

    public Iterable<Edge> adj(int v) {
        return adj[v];
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
            int selfLoops = 0;
            for (Edge e : adj[v]) {
                if (e.other(v) > v) {
                    list.add(e);
                } else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }
}
