import random
import os
import subprocess

TIMEOUT = 150
SEED = 42

argument = input("contract or benchmark - c(contract), b(benchmark all), di(bench. djik.), bi(bench. BiDijk), ch(bench. CH): ")

def read_graph(graph_path):
    vertices = []
    vertices_plus = []  # vertices with longitude and latitude
    edges = []
    with open(graph_path, "r") as file:
        lines = file.readlines()
        counts = lines[0].split()
        n_vertices = int(counts[0]) 
        n_edges = int(counts[1])

        vertex_lines = lines[1:n_vertices]
        edge_lines = lines[n_vertices+1:]

        for line in vertex_lines:
            tokens = line.split()
            if len(tokens) == 3:
                v = int(tokens[0])
                latitude = int(float(tokens[1]))
                longitude = int(float(tokens[2]))
                vertices.append(v)
                vertices_plus.append((v, latitude, longitude))


        for line in edge_lines:
            tokens = line.split()
            if len(tokens) == 3:
                v = int(tokens[0])
                w = int(tokens[1])
                weight = int(tokens[2])
                edges.append((v, w, weight))

    return vertices, vertices_plus, edges


vertices, vertices_plus, edges = read_graph("denmark.graph")

def generate_random_pairs(vertex_list, seed, n_pairs=1000):
    random.seed(seed)
    random.shuffle(vertex_list)
    pairs = []
    for i in range(n_pairs):
        pairs.append((vertex_list[i], vertex_list[i+1]))
    return pairs

def save_pairs_to_file(pairs, output_path):
    with open(output_path, "w") as file:
        for v, w in pairs:
            file.write(f"{v} {w}\n")

def run_java(jar: str, arg: str, input: str) -> str:
    p = subprocess.Popen(['java', '-jar', jar, arg], 
                         stdin=subprocess.PIPE, 
                         stdout=subprocess.PIPE)

    (output, _) = p.communicate(input.encode('utf-8'), timeout=TIMEOUT)
    return output.decode('utf-8')

def benchmark(version, random_pairs, graph):
    # input_size = 1000
    jar = "" # TO-DO: compile java project and add jar
    graph = '\n'.join(f"{u} {v}" for u, v in random_pairs)
    input_data = '\n'.join(f"{u} {v}" for u, v in random_pairs)
    arg = version
    # print(input_data)
    
    output = run_java(jar, arg, input_data)
    print(output)
    
    return output

if __name__ == "__main__":
    graph_file_path = os.path.join("denmark_new.graph")
    output_pairs_path = os.path.join("random_pairs.txt")
    random_pairs = generate_random_pairs(vertices, SEED, n_pairs=1000)
    n_v = len(vertices_plus) 
    n_e = len(edges) 
    g_vertices = '\n'.join(f"{v} {la} {lo}" for v, la, lo in vertices_plus)
    g_edges = '\n'.join(f"{u} {v} {w}" for u, v, w in edges)

    graph = '\n'.join(f"{n_v} {n_e} {g_vertices} {g_edges}")
    
    # if c selected contract the graph
    if argument == "c":
        run_java("", "buildgraph", graph)
    
    # if b selected run benchmarks on all SP algorithms
    if argument == "b":
        dijkstra = benchmark("dijkstra", random_pairs)
        bi_dijkstra = benchmark("BiDijkstra", random_pairs)
        contraction_hirarchies = benchmark("CH", random_pairs)
    # select single SP algs
    if argument == "di":
        dijkstra = benchmark("dijkstra", random_pairs)
    if argument == "bi":
        dijkstra = benchmark("BiDijkstra", random_pairs)
    if argument == "ch":
        dijkstra = benchmark("CH", random_pairs)

    save_pairs_to_file(random_pairs, output_pairs_path)
    print(f"Generated 1000 random pairs and saved to {output_pairs_path}")