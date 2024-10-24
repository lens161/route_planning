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

            Edge e = new Edge(v, w, weight, false );
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

    public Iterable<Long> getVertexIds() {
        return vertexIdToIndexMap.keySet();
    }

    public Iterable<Edge> edges() {
        LinkedList<Edge> list = new LinkedList<Edge>();
        for (int v = 0 ; v < adj.length; v ++) {
            int selfLoops = 0;
            for (Edge e : adj[v]) {
                if (e.other(v) > v) {
                    list.add(e);
                }
                // only add one copy of each self loop (self loops will be consecutive)
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }
}
