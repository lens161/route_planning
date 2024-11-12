package org.example;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class GraphChTest {

    private GraphCh graph;

    @Before
    public void setUp() throws FileNotFoundException {
        File testFile = new File(getClass().getClassLoader().getResource("augmented_small_test_graph_output.graph").getFile());
        graph = new GraphCh(testFile);
    }

    @Test
    public void testGraphInitialization() {
        assertNotNull("Graph should be initialized", graph);
        assertEquals("Vertex count should match the file data", 10, graph.V());
        assertEquals("Edge count should match the file data", 18, graph.E());
    }

    @Test
    public void testVertexRetrieval() {
        int vertexIndex = graph.getIndexForVertex(10);
        Vertex2 vertex = graph.getVertexByIndex(vertexIndex);

        assertNotNull("Vertex 10 should be retrievable", vertex);
        assertEquals("Vertex ID should match", 10, vertex.getId());
        assertEquals("Longitude should match", 9.12669, vertex.getLongitude(), 0.00001);
        assertEquals("Latitude should match", 55.7348, vertex.getLatitude(), 0.00001);
    }

    @Test
    public void testEdgeRetrieval() {
        int vertexIndex10 = graph.getIndexForVertex(10);
        int vertexIndex16 = graph.getIndexForVertex(16);

        EdgeCh edge = graph.adj[vertexIndex10].get(vertexIndex16);
        assertNotNull("Edge between vertices 10 and 16 should exist", edge);
        assertEquals("Edge weight should match", 8.0, edge.weight(), 0.00001);
        assertEquals("Shortcut indicator should match", false, edge.isShortcut());
    }
    
    @Test
    public void testEdgeProperties() {
        int vertexIndex11 = graph.getIndexForVertex(11);
        int vertexIndex15 = graph.getIndexForVertex(15);

        EdgeCh edge = graph.adj[vertexIndex11].get(vertexIndex15);
        assertNotNull("Edge between vertices 11 and 15 should exist", edge);
        assertEquals("Edge weight should match", 6.0, edge.weight(), 0.00001);
        assertEquals("Should be marked as a shortcut", true, edge.isShortcut());
    }

    @Test
    public void testEdgeExists() {
        int u = graph.getIndexForVertex(10);
        int w = graph.getIndexForVertex(11);

        assertTrue("Edge should exist between vertices 10 and 11", graph.edgeExists(u, w));

        int nonAdjacentVertexIndex = graph.getIndexForVertex(19);
        assertFalse("Edge should not exist between non-adjacent vertices 10 and 19", graph.edgeExists(u, nonAdjacentVertexIndex));
    }

    @Test
    public void testVertexIndexMapping() {
        int vertexIndex = graph.getIndexForVertex(12);
        long vertexId = graph.getVertexId(vertexIndex);

        assertEquals("Vertex ID should map correctly to index and back", 12, vertexId);
        assertEquals("Vertex index should map correctly from ID", vertexIndex, graph.getIndexForVertex(12));
    }

    @Test
    public void testEdgesIterable() {
        int edgeCount = 0;
        for (EdgeCh edge : graph.edges()) {
            assertNotNull("Each edge in iterable should be non-null", edge);
            edgeCount++;
        }

        assertEquals("Edge count in iterable should match expected count", graph.E(), edgeCount);
    }
}
