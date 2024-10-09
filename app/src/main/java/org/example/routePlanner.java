package org.example;

import java.io.File;
import java.util.Scanner;

public class routePlanner {

    public static void main(String[] args) {
        File input1 = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/app/src/main/resources/SmallTest.txt");

        try {
            Scanner sc = new Scanner(System.in);
            sc = new Scanner(input1);

            String line1 = sc.nextLine();
            String[] VE = line1.split(" ");
            int V = Integer.parseInt(VE[0]);
            int E = Integer.parseInt(VE[1]);
            // System.out.println(V);
            // System.out.println(E);
            Graph g = new Graph(V);

            for (int i = 0; i < V; i++) {

                String line = sc.nextLine();
                String[] vertex = line.split(" ");
                int v = Integer.parseInt(vertex[0]);
                System.out.println(v);
                float lo = Float.parseFloat(vertex[1]);
                float la = Float.parseFloat(vertex[2]);
                g.addVertex(v);
            }
            for (int i = 0; i < E; i++) {
                String line = sc.nextLine();
                String[] edge = line.split(" ");
                int v = Integer.parseInt(edge[0]);
                // System.out.println(v);
                int w = Integer.parseInt(edge[1]);
                int weight = Integer.parseInt(edge[2]);
                Edge e = new Edge(v, w, weight);
                g.addEdge(e);
            }
            sc.close();

            System.out.println(g.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    
}