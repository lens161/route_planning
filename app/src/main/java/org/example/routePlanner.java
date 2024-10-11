package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class routePlanner {

    public static void main(String[] args) throws FileNotFoundException {
        File input1 = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/app/src/main/resources/SmallTest.txt");
        
        Graph g = new Graph(input1);

        Dijkstra d1 = new Dijkstra();
        System.out.println(d1.runDijkstra(g, 10, 20));

        System.out.println(g.toString());
}
}