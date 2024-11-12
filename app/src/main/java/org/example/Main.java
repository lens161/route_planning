package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String[] arg = args;

        String alg = args[0];
        System.out.println(alg);

        switch (alg) {
            case "contract":
                Graph graph = new Graph(new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/newdenmark.graph"));

                ContractionHierarchy ch = new ContractionHierarchy(graph);
        
                ch.preprocess();
                ch.saveAugmentedGraph("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/augmented_graph_output_fast.graph");
                break;
            case "dijkstra":
                runDijkstra();
                break;
            
            case "BiDijkstra":
                runBiDirectionalDijkstra();
                break;
            case "CH":
                runCHBiDisjkstra();
                break;
            default:
                break;
        }

    }
    public static void runDijkstra(){
        try {
            File input1 = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/newdenmark.graph");
            Graph g = new Graph(input1);
            Dijkstra dijkstra = new Dijkstra();
            File outputFile = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/dijkstra_results.csv");
    
            File randomPairsFile = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/random_pairs.txt");
    
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
    }
    public static void runBiDirectionalDijkstra() throws FileNotFoundException{
        File graphFile = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/newdenmark.graph");
        Graph g = new Graph(graphFile);

        File randomPairsFile = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/random_pairs.txt");
        if (!randomPairsFile.exists()) {
            System.err.println("The random pairs file does not exist.");
            return;
        }

        BiDirectionalDijkstra biDijkstra = new BiDirectionalDijkstra();
        File outputFile = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/bi_dijkstra_results.csv");

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
    public static void runCHBiDisjkstra(){
        File graphFile = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/augmented_graph_output_fast.graph");
        File randomPairsFile = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/random_pairs.txt");
        File outputFile = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/ch_results.csv");

        try {
            // Load the graph
            GraphCh graph = new GraphCh(graphFile);
            long[] nodeRanks = graph.getNodeRanks();

            // Initialize BiDirectionalDijkstraCH instance
            BiDirectionalDijkstraCH biDijkstra = new BiDirectionalDijkstraCH(graph.V(), nodeRanks);

            // Read pairs from the file
            List<long[]> pairs = readPairs(randomPairsFile);

            long totalExecutionTime = 0;
            int totalRelaxedEdges = 0;
            int numberOfPairs = pairs.size();

            // Prepare to write the output
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.println("Source,Target,Distance,ExecutionTime");

                // Perform bidirectional search for each pair
                for (long[] pair : pairs) {
                    long source = pair[0];
                    long target = pair[1];

                    long startTime = System.nanoTime();
                    double distance = biDijkstra.runBiDirectionalCHDijkstra(graph, source, target);
                    long endTime = System.nanoTime();

                    long duration = endTime - startTime;
                    totalExecutionTime += duration;
                    totalRelaxedEdges += biDijkstra.getRelaxedEdgesCount();

                    writer.printf("%d,%d,%.5f,%d%n", source, target, distance, duration);
                    writer.flush();

                    // Clear the BiDirectionalDijkstraCH instance for the next pair
                    biDijkstra.clear();
                }

                // Calculate and print averages
                double averageExecutionTime = totalExecutionTime / (numberOfPairs * 1_000_000_000.0);
                double averageRelaxedEdges = (double) totalRelaxedEdges / numberOfPairs;

                // Write averages to the console and file
                System.out.println("Average Execution Time: " + averageExecutionTime + " seconds");
                System.out.println("Average Relaxed Edges: " + averageRelaxedEdges);
                writer.println("Average Execution Time," + averageExecutionTime + " seconds");
                writer.println("Average Relaxed Edges," + averageRelaxedEdges);
            }

            System.out.println("Bidirectional CH Dijkstra results saved to " + outputFile.getPath());

        } catch (IOException e) {
            System.err.println("Error loading files: " + e.getMessage());
        }
    }

    // helper for runCHBiDijkstra
    private static List<long[]> readPairs(File file) {
        List<long[]> pairs = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().trim().split("\\s+");
                if (line.length < 2) {
                    continue;
                }
                long source = Long.parseLong(line[0]);
                long target = Long.parseLong(line[1]);
                pairs.add(new long[]{source, target});
            }
        } catch (FileNotFoundException e) {
            System.err.println("Random pairs file not found: " + e.getMessage());
        }
        return pairs;
    }
}


