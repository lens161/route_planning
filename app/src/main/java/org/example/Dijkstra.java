/*
@@%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@@@@@@@@@@@@@@@@@@@@@@@@@@
%%%%%%##########%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@@@@@@@@@@@@@@@@@@@@@@@@@@
%%%##################%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@@@@@@@@@@@@@@@@@@@@@@
#########################%%%%%%%%%%%####%%%%%%%%%%%%%%%%%@@@@@@@@@@@@@@@@@@@@
#################################****#######*+*%%%%%%%%%%%@@@@@@@@@@@@@@@@@@@
#############################*****##%%%%####***###%%%%%%%%%%@@@@@@@@@@@@@@@@@
#########################%#%%####*#%#**+**###%@@@@%%%%%%%%%%%%@@@@@@@@@@@@@@@
########################%#%%###%*#%###*%%@@@@@@@@@@@%%%%%%%%%%%%@@@@@@@@@@@@@
######################*#####%*###%%%%%%%%@@@@@@@@@@%%%%%%%%%%%%%%%@@@@@@@@@@@
********############*#*######%%%###%@%###%###%%%%#***#%#%%%%%%%%%%@@@@@@@@@@@
**********###***###**#####%%%%%###*%######**#*****++==*#%%%%%%%%%%%%%@@@@@@@@
*******************#@%#@@@@@@%%%##%###*******++***++==+*%%%%%%%%%%%%%%@@@@@@@
*****************#%%@%@@@@@@@%%######**#*********+*+++++*%%%%%%%%%%%%%%@@@@@@
****************%%%@@@@@@@@@%%%#**#####***********+++++++#%%%%%%%%%%%%%%%@@@@
****************%@@%@@@@@@%%%##*+************++++==+=====+%%%%%%%%%%%%%%%%@@@
****************@@@%@%%@#%%###********#*****++++++++**++++*#%%%%%%%%%%%%%%%@@
***************#@@@%@%%%%%#####************+++****###*####**#%%%%%%%%%%%%%%%@
****************@@@@%@%@%%%###%#####*****+++*%#%@@@%%##%@@%##%%%%%%%%%%%%%%%%
****************#@@@@%%%%###%%%%%%###*****##%%##%@@@@#%@@@%##%%%%%%%%%%%%%%%%
*****************%@@@@@%%%%%%@@%%%###**##%@*%@@@@@@@@%%@@@@%##%%%%%%%%%%%%%%%
*****************#*+++*@@@@@@@@@%%%%%%%###%@@@@@@%#@@@#**%####%%%%%%%%%%%%%%%
*****+************%@@@@%%%@%%#%%%#****#%%%#%##%#%%%@@@%*++**###%%%%%%%%%%%%%%
+++++++***+++**+++#@@@@@@%%%%%%######**++*###*####%@%%#+===+*##%%%%%%%%%%%%%%
++++++++++++++++*+*%%%@@@@%%%@@%%%###%#++++####***%%%%#+++=+#####%%%%%%%%%%%%
++++++++++++++++++**#*%@@%*#%@@%%%###%%%%%##*++###@%#############%%%%%%%%%%%%
+++++++++++++++++*+=+%***#%%%@@@%%%%%%%%%@%%%***#%%@@@@@@%#######%%%%%%%%%%%%
+++++++++++++++++=#%%#%%%%@@@@@%%%%%%%%%%@%%#*##%#%%%@%@@%#*#####%%%%%%%%%%%%
+++++++++++++++++=--=*#%%%%%@@@@%%%%%%%%%##%###%######%%###*#####%%%%%%%%%%%%
++++++++++++++***+=-=***#%@@@@@%%%##%%%%#%%%%%@%#%%%%%@%%%%######%%%%%%%%%%%%
++++++++******##%#==-+*#*#@@@@@%%%%#%%%%%%%%%@@@@@@@@@@@@%%######%%%%%%%%%%%%
+++++**############==-+*#@@@@@%@@%%%%%%%%%%%%@@@@@@@%#%#####%#%##%%%%%%%%%%%%
++++**##############*===#@@@@@@@@@@@@%%%%%%%%@@@%@@%%%%%##**#%%##%%%%%%%%%%%%
++*#################%#+==*@@@@@@@@@@@@@@@%@@@@@@%%%%%%%###***%%%+=%%%%%%%%%%%
**################%##%%*+==#@@@@%@@@@@@@@@@@@@@@@@%%%%%%%%###%@%*-=*%%%%%%%%%
#######%######%%###%%%%%#*===#@@@@@@@@@@@@@@@@@@@@@@@%%@@%%%%@@@*--=*%%%%%%%%
%@@%%%#%%%%%%%%%%%#%%%%%%**+=-=%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*--==#%%%%%%%
#=--=+%%%%%%%%%%%@@%%%%%%*+#*#@%#@@@@@%%%@@@@@@@@@@@@@@@@@@@@@@@#=====#%%%%%%
=-------=#%%%%%@%%@@@%@@%+==%%#%**%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*====+=#%%%%%
=---------=%%%%%%@%@@@@@@%+-=++%%##@@@@@@@@@@@%@@@@@@@@@@@@@@@@@*======+#%%%%
=-----------+%%%%%@@@@@@@@%#@@@@@%%#@@@@@@@@@@%@@@@@@@@@@@@@@@@@*+==+=+=*%%%%
===-====---=-=*@%%%%@@@@@@@@@@@@@@%%#@@@@@@@%%@@@@@@@@@@@@@@@@@%*+==+=+==#%%%
==--=--=++==-=-=%@@%@@@@@@@@@@@@@%%@@%%@@@@@%@@@@@@@@@@@@@@@@@@*+++++=++==#%%
+==-=----=++=====%@@@@@@@@@@@@@@@@%##%@@@@%%@@@@@@@@@@@@@@@@@@#*+++++=++=-*%%
==-------=-=++=-==%@@@@@@@@@@@@@@@@@###%%%%@@@@@@@@@@@@@@@@@@%**+++++=+++-=#%
===--=-=----==++===%@@@@@@@@@@@@@@@@%#**#%#@@@@@@@@@@@@@@@@@@***+++++=+++=-+#
====--------=-=+*+=+@@@@@@@@@@@@@@@@@@#*##*@@@@@@@@@@@@@@@@@%##**++++=+++==-*
====------=======++=#@@@@@@@@@@@@@@@@@@%#**@@@@@@@@@@@@@@@@@%##***+++=+++=---
====--------=-====+==#@@@@@@@@@@@@@@@@@@@%*@@@@@@@@@@@@@@@@@%%#****++=+*+===-
===-===-=-========++++#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%##***+++++*===-
======+===---=====++++=@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%%#**++++**+===-
=========+==--======++++@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%##*+*+++***====
====-=-=-====--=====++++%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@##****+***++===
======-=-======-====++++*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@##***+****+=+==
+===-=-=----========+++*+#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%###*****++++==
**==--------=-=-=====++*+*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%%##****++++*+++
##*+=-------===+++===++**@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%%##*******#*#
 */
package org.example;

// import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import edu.princeton.cs.algs4.IndexMinPQ;
import java.io.File;
import java.io.FileNotFoundException;

public class Dijkstra{
    // private double [] distTo;         // distTo[v] = distance  of shortest s->v path
    private int CUT_OFF = 50;
    private HashMap<Integer, Double> distTo;
    private Edge[] edgeTo;           // edgeTo[v] = last Edg on shortest s->v path
    private HashMap<Integer, Integer> hopTo;
    private IndexMinPQ<Double> pq;    // priority queue of vertices
    // private HashMap<Long, Integer> index;
    // private Graph g;
    private int relaxedEdgesCount;

    public Dijkstra(){
        // index = g.getVertexINdex();
    }

    public double runDijkstra(Graph g, long s, long t) {
        int start = g.getIndexForVertex(s);
        int target = g.getIndexForVertex(t);
        // for (Edge e : g.edges()) {
        //     if (e.weight() < 0)
        //         throw new IllegalArgumentException("Edg " + e + " has negative weight");
        // }
        int VertexCount = g.V();

        // distTo = new double[VertexCount];
        edgeTo = new Edge[VertexCount];
        

        distTo = new HashMap<>();
        // for (int i = 0; i < g.V(); i++)
        //     distTo[i] = Double.POSITIVE_INFINITY;
        // distTo[start] = 0.0;
        distTo.put(start, 0.0);

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(VertexCount);
        pq.insert(start, distTo.get(start));

        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : g.adj(v))
                relax(e, v, g);
            if(v==target)
                return distTo(v);
        }
        // check optimality conditions
        return distTo.get(target);
    }

    public double runDijkstraLim(Graph g, long s, long t, double distLimit, int hopLimit) {
        int start = g.getIndexForVertex(s);
        int target = g.getIndexForVertex(t);
        // for (Edge e : g.edges()) {
        //     if (e.weight() < 0)
        //         throw new IllegalArgumentException("Edg " + e + " has negative weight");
        // }
        int VertexCount = g.V();
        
        edgeTo = new Edge[VertexCount];
        hopTo = new HashMap<>();
        distTo = new HashMap<>();
        distTo.put(start, 0.0);

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(VertexCount);
        pq.insert(start, distTo.get(start));

        while (!pq.isEmpty() && relaxedEdgesCount < CUT_OFF) {
            int v = pq.delMin();
            // System.out.println(v);

            if(!hopTo.containsKey(v))
                hopTo.put(v,0);
            if(!distTo.containsKey(v))
                distTo.put(v, Double.POSITIVE_INFINITY);

            if(distTo.get(v) > distLimit || hopTo.get(v) > hopLimit)
                return distTo.get(target);
                
            for (Edge e : g.adj(v))
                relaxLim(e, v, g, distLimit, hopLimit);

            if(v==target)
                return distTo.get(v);
        }
        // check optimality conditions
        // assert check(g, s);
        if(distTo.containsKey(target))
            return distTo.get(target);
        else 
            return -1;
    }

    // relax Edge e and update pq if changed
    // capital V is the the ID of the vertex, not the index used in the dijkstra implementation
    private void relaxLim(Edge e, int v, Graph g, double distLimit, int hopLimit) {
        int w = e.other(v);
        // Vertex W = g.getVertices().get(w);
        if(!distTo.containsKey(w))
            distTo.put(w, Double.POSITIVE_INFINITY);

        hopTo.put(w, hopTo.get(v) + 1);
        hopTo.put(w, 1);

        double newDist = distTo.get(v) + e.weight();

        if(newDist >= distLimit || hopTo.get(w) >= hopLimit){
            // System.out.println("cutoff");
            return;
        }

        if (distTo.get(w) > newDist) {
            distTo.put(w, newDist);
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo.get(w));
            else                pq.insert(w, distTo.get(w));
        }
        relaxedEdgesCount ++;
    }
    // relax Edge e and update pq if changed
    // capital V is the the ID of the vertex, not the index used in the dijkstra implementation
    private void relax(Edge e, int v, Graph g) {
        int w = e.other(v);
        // Vertex W = g.getVertices().get(w);
        
        if (distTo.containsKey(w) && distTo.get(w) > distTo.get(v) + e.weight()) {
            // distTo.get(w) = distTo[v] + e.weight();
            distTo.put(w, distTo.get(v) + e.weight());
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo.get(w));
            else                pq.insert(w, distTo.get(w));
        }
        relaxedEdgesCount ++;
    }

    public double distTo(int v) {
        // validateVertex(v);
        return distTo.get(v);
    }

    public boolean hasPathTo(Graph g, int V) {
        int v = g.getIndexForVertex(V);
        // validateVertex(v);
        return distTo.get(v) < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(Graph g, int v) {
        //TO DO: make new path to
        // validateVertex(v);
        if (!hasPathTo(g, v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        // int x = v;
        // for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
        //     path.push(e);
        //     x = e.other(x);
        // }
        return path;
    }


    // check optimality conditions:
    // (i) for all Edgs e = v-w:            distTo[w] <= distTo[v] + e.weight()
    // (ii) for all Edg e = v-w on the SPT: distTo[w] == distTo[v] + e.weight()
    private boolean check(Graph g, Long S) {
        int s = g.getIndexForVertex(S);

        // check that Edg weights are non-negative
        for (Edge e : g.edges()) {
            if (e.weight() < 0) {
                System.err.println("negative Edg weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo.get(s) != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < g.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo.get(v) != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all Edgs e = v-w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < g.V(); v++) {
            for (Edge e : g.adj[v]) {
                int w = e.other(v);
                if (distTo.get(v) + e.weight() < distTo.get(w)) {
                    System.err.println("Edg " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all Edgs e = v-w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < g.V(); w++) {
            if (edgeTo[w] == null) continue;
            Edge e = edgeTo[w];
            if (w != e.either() && w != e.other(e.either())) return false;
            int v = (int)e.other(w);
            if (distTo.get(v) + e.weight() != distTo.get(w)) {
                System.err.println("Edg " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    /**
     * Unit tests the {@code DijkstraUndirectedSP} data type.
     *
     * @param args the command-line arguments
     */

    public int getRelaxedEdgesCount(){
        return relaxedEdgesCount;
    }

    public void clear(){
        this.edgeTo = null;
        this.distTo = null;
        this.pq = null;
        this.relaxedEdgesCount=0;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File input1 = new File("/Users/lennart/Documents/00_ITU/03_Sem03/02_Applied_Algorithms/Assignment3/route-planning/denmark_new.graph");
        Graph g = new Graph(input1);
        Dijkstra dijkstra = new Dijkstra();

        long start = System.nanoTime();
        System.out.println(dijkstra.runDijkstra(g, 47021985,2064649066));
        long end = System.nanoTime();
        System.out.println((end - start));
    }

}