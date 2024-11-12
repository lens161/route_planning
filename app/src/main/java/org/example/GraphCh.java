package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GraphCh {
    private final int V;
    private long E; // Use long if the number of edges is large
    public Map<Integer, EdgeCh>[] adj;
    private Map<Long, Integer> vertexIdToIndexMap; // Map from vertex IDs to indices
    long[] indexToVertexId; // Map from indices to vertex IDs
    private List<Vertex2> vertices;  // List to store Vertex2 objects
    private long[] nodeRanks;        // Array to store node ranks

    @SuppressWarnings("unchecked")
    public GraphCh(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);

        // Read the first line to get V and E
        if (!sc.hasNextLine()) {
            throw new IllegalArgumentException("Graph file is empty");
        }
        String[] firstLine = sc.nextLine().trim().split("\\s+");
        if (firstLine.length < 2) {
            throw new IllegalArgumentException("First line must contain the number of vertices and edges");
        }
        int V = Integer.parseInt(firstLine[0]);
        long E_long = Long.parseLong(firstLine[1]);
        this.V = V;
        this.E = 0; // We will increment E as we add edges

        // Initialize adjacency lists
        adj = (Map<Integer, EdgeCh>[]) new ConcurrentHashMap[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ConcurrentHashMap<>();
        }

        // Initialize mappings and arrays
        vertexIdToIndexMap = new HashMap<>();
        indexToVertexId = new long[V];
        vertices = new ArrayList<>(Collections.nCopies(V, null)); // Initialize list with nulls
        nodeRanks = new long[V]; // Use long[] for node ranks

        // Read vertex data
        for (int i = 0; i < V; i++) {
            if (!sc.hasNextLine()) {
                throw new IllegalArgumentException("Insufficient vertex data in the graph file");
            }
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                i--; // Skip empty lines
                continue;
            }
            String[] vertexData = line.split("\\s+");
            if (vertexData.length < 4) {
                throw new IllegalArgumentException("Vertex data line does not contain enough information");
            }
            long vertexId = Long.parseLong(vertexData[0]);
            float longitude = Float.parseFloat(vertexData[1]);
            float latitude = Float.parseFloat(vertexData[2]);
            long vertexRank = Long.parseLong(vertexData[3]);

            // Assign index to vertex ID
            int index = i; // Indices from 0 to V-1
            vertexIdToIndexMap.put(vertexId, index);
            indexToVertexId[index] = vertexId;

            // Store rank
            nodeRanks[index] = vertexRank;

            // Create Vertex2 object
            Vertex2 vertex = new Vertex2(vertexId, index, longitude, latitude);
            vertices.set(index, vertex);
        }

        // Read edge data
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                continue; // Skip empty lines
            }
            String[] edgeData = line.split("\\s+");
            if (edgeData.length < 4) {
                throw new IllegalArgumentException("Edge data line does not contain enough information");
            }
            long vId = Long.parseLong(edgeData[0]);
            long wId = Long.parseLong(edgeData[1]);
            double weight = Double.parseDouble(edgeData[2]);
            long shortcutIndicator = Long.parseLong(edgeData[3]);

            Integer vIndex = vertexIdToIndexMap.get(vId);
            Integer wIndex = vertexIdToIndexMap.get(wId);

            if (vIndex == null || wIndex == null) {
                throw new IllegalArgumentException("Edge refers to unknown vertex ID");
            }

            EdgeCh e = new EdgeCh(vIndex, wIndex, weight, shortcutIndicator);
            addEdge(e);
        }

        sc.close();
    }

    public void addEdge(EdgeCh e) {
        int v = e.either();
        int w = e.other(v);

        adj[v].put(w, e);
        adj[w].put(v, e);
        E++;
    }

    public int V() {
        return V;
    }

    public long E() {
        return E;
    }

    public Iterable<EdgeCh> adj(int v) {
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

    public long[] getNodeRanks() {
        return nodeRanks;
    }

    public Iterable<EdgeCh> edges() {
        LinkedList<EdgeCh> list = new LinkedList<>();
        for (int v = 0; v < adj.length; v++) {
            for (EdgeCh e : adj[v].values()) {
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
