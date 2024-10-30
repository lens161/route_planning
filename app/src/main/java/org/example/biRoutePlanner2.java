package org.example;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.Files;
import java.io.IOException;
import java.io.PrintWriter;

public class biRoutePlanner2 {

    public static void main(String[] args) throws IOException {
        File graphFile = new File("/home/knor/route/route-planning/app/src/main/resources/denmark.graph");
        Graph g = new Graph(graphFile);

        File randomPairsFile = new File("/home/knor/route/route-planning/app/src/main/resources/random_pairs.txt");
        if (!randomPairsFile.exists()) {
            System.err.println("The random pairs file does not exist.");
            return;
        }

        BiDirectionalDijkstra biDijkstra = new BiDirectionalDijkstra();
        File outputFile = new File("/home/knor/route/route-planning/app/src/main/resources/bidirectional_dijkstra_results.csv");

        try (PrintWriter writer = new PrintWriter(outputFile)) {
            writer.println("Source,Target,Distance,ExecutionTime");

            List<String> pairs = Files.readAllLines(Paths.get(randomPairsFile.toURI()));
            if (pairs.isEmpty()) {
                System.err.println("No pairs found in the random pairs file.");
                return;
            }

            long totalExecutionTime = 0;
            int totalRelaxedEdges = 0;
            int numberOfPairs = 0;

            for (String pair : pairs) {
                String[] vertices = pair.split(" ");
                if (vertices.length != 2) {
                    System.err.println("Invalid pair: " + pair);
                    continue;
                }
                long s = Long.parseLong(vertices[0]);
                long t = Long.parseLong(vertices[1]);

                long startTime = System.nanoTime();
                double distance = biDijkstra.runBiDirectionalDijkstra(g, s, t);
                long endTime = System.nanoTime();

                long duration = endTime - startTime;
                totalExecutionTime += duration;
                numberOfPairs++;

                int relaxedEdges = biDijkstra.getRelaxedEdgesCount();
                totalRelaxedEdges += relaxedEdges;

                // Write the result to CSV
                writer.println(s + "," + t + "," + distance + "," + duration);
                writer.flush();

                biDijkstra.clear();
            }

            double averageExecutionTime = totalExecutionTime / (numberOfPairs * 1_000_000_000.0); 
            double averageRelaxedEdges = (double) totalRelaxedEdges / numberOfPairs;
            System.out.println("Average Execution Time: " + averageExecutionTime + " seconds");
            System.out.println("Average Relaxed Edges: " + averageRelaxedEdges);

            writer.println("Average Execution Time," + averageExecutionTime + " seconds");
            writer.println("Average Relaxed Edges," + averageRelaxedEdges);

        } catch (IOException | NumberFormatException e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
