package org.example;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.Files;
import java.io.IOException;
import java.io.PrintWriter;

public class routePlanner {


    public static void main(String[] args) throws IOException {
        // File input1 = new File("/home/knor/AA/route4/route-planning/app/src/main/newdenmark.graph");
        // Graph g = new Graph(input1);
        // Dijkstra d1 = new Dijkstra();
        // // System.out.println(g.toString());
        // d1.runDijkstra(g, 73206082, 1926705245);
        // d1.getPath(g, 1926705245);

        try {

            File input1 = new File("/home/knor/AA/route4/route-planning/app/src/main/newdenmark.graph");
            Graph g = new Graph(input1);
        Dijkstra dijkstra = new Dijkstra();
        File outputFile = new File("/home/knor/AA/route4/route-planning/app/src/main/resources/kris_debug2_dijkstra_results.csv");

        File randomPairsFile = new File("/home/knor/AA/route4/route-planning/random_pairs.txt");

        try (PrintWriter writer = new PrintWriter(outputFile)) {
            writer.println("Source,Target,Distance,ExecutionTime");


        List<String> pairs = Files.readAllLines(Paths.get(randomPairsFile.toURI()));
        long totalExecutionTime = 0;
        int totalRelaxedEdges = 0;

        for (String pair : pairs) {
            String[] vertices = pair.split(" ");
            if (vertices.length != 2) {
                System.err.println("Invalid pair: " + pair);
                continue;
            }
            long s = Long.parseLong(vertices[0]);
            long t = Long.parseLong(vertices[1]);

            long startTime = System.nanoTime();
            double distance = dijkstra.runDijkstra(g, s, t);
            long endTime = System.nanoTime();

            long duration = endTime - startTime;
            totalExecutionTime += duration;

            int relaxedEdges = dijkstra.getRelaxedEdgesCount();
            totalRelaxedEdges += relaxedEdges;

            
            writer.println(s + "," + t + "," + distance + "," + duration);
            writer.flush();

            // dijkstra.clear();
            // System.out.println("Distance from " + s + " to " + t + ": " + distance);
            
            // System.out.println("\nAverage running time: " + averageExecutionTime + " nanoseconds");
        }
        double averageExecutionTime = totalExecutionTime / (1000 * 1_000_000_000.0); 
        double averageRelaxedEdges = (double) totalRelaxedEdges / 1000;
        System.out.println("Average Execution Time: " + averageExecutionTime + " seconds");
        System.out.println("Average Relaxed Edges: " + averageRelaxedEdges);

        writer.println("Average Execution Time," + averageExecutionTime + " seconds");
        writer.println("Average Relaxed Edges," + averageRelaxedEdges);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    } catch (IOException e) {
        System.err.println("An unexpected error occurred: " + e.getMessage());
        e.printStackTrace();
    }
        // System.out.println(d1.runDijkstra(g, 26672401, 26672400));

}
}