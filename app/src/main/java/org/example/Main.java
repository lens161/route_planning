package org.example;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        // File input1 = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/denmark_new.graph");
        // Graph g = new Graph(input1);

        Scanner sc = new Scanner(System.in);
        String version = args[0];

        switch (version) {
            case "buildgraph":
                Graph g = buildGraph(sc);
                Contract c = new Contract(g);
                c.saveGraph();
                break;

            case "dijkstra":
                Dijkstra d = new Dijkstra();
                
                
                break;
            
            case "biDijkstra":
                
            default:
                break;
        }

    }

    private static Graph buildGraph(Scanner sc) {
        int vCount = Integer.parseInt(sc.nextLine());
        int eCount = Integer.parseInt(sc.nextLine());
        long[] vertices = new long[vCount];
        Edge[] edges = new Edge[eCount];
        for (int i = 0; i < vCount; i++) {
            String[] vertex = sc.nextLine().split(" ");
            vertices[i] = Integer.parseInt(vertex[0]);
        }
        System.out.println(vertices[0]);
        System.out.println(vertices[vCount-1]);
        for (int i = 0; i < eCount; i++) {
            String[] edge = sc.nextLine().split(" ");
            int v = Integer.parseInt(edge[0]);
            int w = Integer.parseInt(edge[1]);
            int weight = Integer.parseInt(edge[2]);
            edges[i] = new Edge(v, w, weight, -1);
        }
        System.out.println(edges[0]);
        System.out.println(edges[eCount-1]);

        return new Graph(vertices, edges);
    }
}
