package org.example;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import org.junit.Test;

public class BiDijkstraTest {

    public Graph createGraph() throws FileNotFoundException {
        File graphFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("SmallTest.graph")).getFile());
        return new Graph(graphFile);
    }

    @Test
    public void testDistanceFrom10To16() throws FileNotFoundException {
        Graph graph = createGraph();
        BiDirectionalDijkstra dijkstra = new BiDirectionalDijkstra();

        double result = dijkstra.runBiDirectionalDijkstra(graph, 10, 16);

        assertEquals("Expected shortest path distance from node 10 to node 16 is 8.", 8, Math.round(result));
    }

    @Test
    public void testDistanceFrom11To19() throws FileNotFoundException {
        Graph graph = createGraph();
        BiDirectionalDijkstra dijkstra = new BiDirectionalDijkstra();

        double result = dijkstra.runBiDirectionalDijkstra(graph, 11, 19);

        assertEquals("Expected shortest path distance from node 11 to node 19 is 6.", 6, Math.round(result));
    }

    @Test
    public void testDistanceFrom12To15() throws FileNotFoundException {
        Graph graph = createGraph();
        BiDirectionalDijkstra dijkstra = new BiDirectionalDijkstra();

        double result = dijkstra.runBiDirectionalDijkstra(graph, 12, 15);

        assertEquals("Expected shortest path distance from node 12 to node 15 is 4.", 4, Math.round(result));
    }

    @Test
    public void testSelfDistance() throws FileNotFoundException {
        Graph graph = createGraph();
        BiDirectionalDijkstra dijkstra = new BiDirectionalDijkstra();

        double result = dijkstra.runBiDirectionalDijkstra(graph, 10, 10);
        assertEquals("Distance to self in a single node graph should be zero.", 0, Math.round(result));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInvalidSourceVertex() throws FileNotFoundException {
        Graph graph = createGraph();
        BiDirectionalDijkstra dijkstra = new BiDirectionalDijkstra();

        dijkstra.runBiDirectionalDijkstra(graph, 99, 16);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInvalidTargetVertex() throws FileNotFoundException {
        Graph graph = createGraph();
        BiDirectionalDijkstra dijkstra = new BiDirectionalDijkstra();

        dijkstra.runBiDirectionalDijkstra(graph, 10, 99);
    }
}
