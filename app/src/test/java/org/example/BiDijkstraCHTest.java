package org.example;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import org.junit.Test;

public class BiDijkstraCHTest {

    public GraphCh createGraph() throws FileNotFoundException {
        File graphFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("augmented_small_test_graph_output.graph")).getFile());
        return new GraphCh(graphFile);
    }

    @Test
    public void testDistanceFrom10To16() throws FileNotFoundException {
        GraphCh graph = createGraph();
        int vertices = graph.V();
        long[] nodeRanks = graph.getNodeRanks();

        BiDirectionalDijkstraCH dijkstra = new BiDirectionalDijkstraCH(vertices, nodeRanks);

        double result = dijkstra.runBiDirectionalCHDijkstra(graph, 10, 16);

        assertEquals("Expected shortest path distance from node 10 to node 16 is 8.", 8, Math.round(result));
    }

    @Test
    public void testDistanceFrom11To19() throws FileNotFoundException {
        GraphCh graph = createGraph();
        int vertices = graph.V();
        long[] nodeRanks = graph.getNodeRanks();

        BiDirectionalDijkstraCH dijkstra = new BiDirectionalDijkstraCH(vertices, nodeRanks);

        double result = dijkstra.runBiDirectionalCHDijkstra(graph, 11, 19);

        assertEquals("Expected shortest path distance from node 11 to node 19 is 6.", 6, Math.round(result));
    }

    @Test
    public void testSelfDistance() throws FileNotFoundException {
        GraphCh graph = createGraph();
        int vertices = graph.V();
        long[] nodeRanks = graph.getNodeRanks();

        BiDirectionalDijkstraCH dijkstra = new BiDirectionalDijkstraCH(vertices, nodeRanks);

        double result = dijkstra.runBiDirectionalCHDijkstra(graph, 10, 10);
        assertEquals("Distance to self returns -1", -1, Math.round(result));
    }

    @Test
    public void testInvalidSourceVertex() throws FileNotFoundException {
        GraphCh graph = createGraph();
        int vertices = graph.V();
        long[] nodeRanks = graph.getNodeRanks();

        BiDirectionalDijkstraCH dijkstra = new BiDirectionalDijkstraCH(vertices, nodeRanks);

        double result = dijkstra.runBiDirectionalCHDijkstra(graph, 99, 16);
        assertEquals("If start vertex is not found return -1", -1, Math.round(result));

    }

    @Test
    public void testInvalidTargetVertex() throws FileNotFoundException {
        GraphCh graph = createGraph();
        int vertices = graph.V();
        long[] nodeRanks = graph.getNodeRanks();

        BiDirectionalDijkstraCH dijkstra = new BiDirectionalDijkstraCH(vertices, nodeRanks);

        double result = dijkstra.runBiDirectionalCHDijkstra(graph, 10, 99);
        assertEquals("If start vertex is not found return -1", -1, Math.round(result));
    }
}
