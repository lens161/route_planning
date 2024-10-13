package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class routePlanner {

    public static void main(String[] args) throws FileNotFoundException {
        File input1 = new File("/home/knor/route/route-planning/app/src/main/resources/SmallTest.txt");
        
        Graph g = new Graph(input1);

        Dijkstra d1 = new Dijkstra();
        System.out.println(d1.runDijkstra(g, 16, 18));

        System.out.println(g.toString());
}
}