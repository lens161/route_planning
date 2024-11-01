package org.example;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.Test;
import java.util.*;

import com.google.common.collect.Iterables;

public class GraphTest {

    @Test
    public void testGraphVerticesSize() throws FileNotFoundException{
        File graphFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("SmallTest.graph")).getFile());
        Graph g = new Graph(graphFile);

        Iterable<Long> vertices = g.getVertexIds();

        assertEquals("Expected number of vertices should match actual number", 10, Iterables.size(vertices));
        assertEquals("Expected number of vertices should match V()", 10, g.V());
    }

    @Test
    public void testGraphEdgesSize() throws FileNotFoundException{
        File graphFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("SmallTest.graph")).getFile());
        Graph g = new Graph(graphFile);

        Iterable<Edge> edges = g.edges();

        assertEquals("Expected number of vertices should match actual number", 16, Iterables.size(edges));
        assertEquals("Expected number of vertices should match V()", 16, g.E());
    }

    @Test
    public void testEdgeConnections() throws FileNotFoundException {
        File graphFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("SmallTest.graph")).getFile());
        Graph g = new Graph(graphFile);
    
        Scanner sc = new Scanner(graphFile);
        sc.nextLine(); 
        
        int vertexCount = g.V();
        for (int i = 0; i < vertexCount; i++) {
            sc.nextLine();
        }
    
        Map<Integer, List<String>> expectedAdjacencyMap = new HashMap<>();
        
        while (sc.hasNextLine()) {
            String[] edgeLine = sc.nextLine().split(" ");
            long vId = Long.parseLong(edgeLine[0]);
            long wId = Long.parseLong(edgeLine[1]);
            double weight = Double.parseDouble(edgeLine[2]);
    
            int v = g.getIndexForVertex(vId);
            int w = g.getIndexForVertex(wId);
    
            Edge edge = new Edge(v, w, weight, -1);
            String edgeString = edge.toString();
    
            expectedAdjacencyMap.computeIfAbsent(v, k -> new ArrayList<>()).add(edgeString);
            expectedAdjacencyMap.computeIfAbsent(w, k -> new ArrayList<>()).add(edgeString);
        }
        sc.close();
    
        for (Map.Entry<Integer, List<String>> entry : expectedAdjacencyMap.entrySet()) {
            int vertexIndex = entry.getKey();
            List<String> expectedEdges = entry.getValue();
    
            List<String> actualEdges = new ArrayList<>();
            for (Edge e : g.adj(vertexIndex)) {
                actualEdges.add(e.toString());
            }
    
            Collections.sort(expectedEdges);
            Collections.sort(actualEdges);
    
            // if there's a mismatch
            if (!actualEdges.equals(expectedEdges)) {
                System.out.println("Mismatch at vertex " + vertexIndex);
                System.out.println("Expected edges: " + expectedEdges);
                System.out.println("Actual edges: " + actualEdges);
            }
    
            assertEquals("Edges for vertex " + vertexIndex + " should match expected edges.", 
                         expectedEdges, actualEdges);
        }
    }
    


    @Test
    public void testSelfLoopAndDuplicateEdges() throws FileNotFoundException {
        File graphFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("SmallTest.graph")).getFile());
        Graph g = new Graph(graphFile);

        for (Edge edge : g.edges()) {
            int v = edge.either();
            int w = edge.other(v);

            if (v == w) {
                assertEquals("Self-loop edges should appear only once.", 1, Iterables.size(g.adj(v)));
            } else { 
                long countV = ((Collection<Edge>) g.adj(v)).stream().filter(e -> e.equals(edge)).count();
                long countW = ((Collection<Edge>) g.adj(w)).stream().filter(e -> e.equals(edge)).count();

                assertEquals("Each edge should appear only once in each adjacency list.", 1, countV);
                assertEquals("Each edge should appear only once in each adjacency list.", 1, countW);
            }
        }
    }
    
}
