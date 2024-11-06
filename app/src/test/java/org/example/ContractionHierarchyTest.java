// package org.example;

// import org.junit.Test;

// import com.google.common.collect.Iterables;

// import static org.junit.Assert.*;

// import java.io.File;
// import java.io.FileNotFoundException;
// import java.io.IOException;

// public class ContractionHierarchyTest {

//     private Graph createGraph() throws FileNotFoundException {
//         File graphFile = new File(getClass().getClassLoader().getResource("SmallTest.graph").getFile());
//         return new Graph(graphFile);
//     }

//     @Test
//     public void testContractionHierarchyInitialization() throws FileNotFoundException {
//         Graph graph = createGraph();
//         ContractionHierarchy ch = new ContractionHierarchy(graph);

//         for (int rank : ch.getRank()) {
//             assertEquals("All nodes should be uncontracted initially.", -1, rank);
//         }
//     }

//     @Test
//     public void testPreprocessCreatesShortcuts() throws FileNotFoundException {
//         Graph graph = createGraph();
//         ContractionHierarchy ch = new ContractionHierarchy(graph);

//         int originalEdgeCount = graph.E();

//         ch.preprocess();

//         int augmentedEdgeCount = Iterables.size(graph.edges());
//         System.out.println(augmentedEdgeCount);

//         assertTrue("Augmented graph should have more edges than original.", augmentedEdgeCount > originalEdgeCount);
//     }

//     @Test
//     public void testShortcutProperties() throws FileNotFoundException {
//         Graph graph = createGraph();
//         ContractionHierarchy ch = new ContractionHierarchy(graph);

//         ch.preprocess();

//         for (Edge e : graph.edges()) {
//             if (e.isShortcut()) {
//                 int contractedFrom = e.c;
//                 double originalWeightSum = 0;

//                 for (Edge originalEdge : graph.adj[contractedFrom].values()) {
//                     originalWeightSum += originalEdge.weight();
//                 }

//                 assertTrue("Shortcut weight should be less than the sum of original edges it replaces.", e.weight() < originalWeightSum);
//             }
//         }
//     }

//     @Test
//     public void testSaveAugmentedGraph() throws FileNotFoundException {
//         Graph graph = createGraph();
//         ContractionHierarchy ch = new ContractionHierarchy(graph);

//         ch.preprocess();

//         String outputFilePath = "augmented_graph_output.graph";
//         try {
//             ch.saveAugmentedGraph(outputFilePath);
//             File outputFile = new File(outputFilePath);
//             assertTrue("Augmented graph output file should be created.", outputFile.exists());
//         } catch (IOException e) {
//             fail("An IOException occurred while saving the augmented graph: " + e.getMessage());
//         }
//     }
// }
