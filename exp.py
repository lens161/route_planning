import random
import os
import subprocess

TIMEOUT = 150
SEED = 42

print("if contract has never been run before, run contract before running 'b' or 'ch' pls :)")
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


vertices, vertices_plus, edges = read_graph("newdenmark.graph")

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

def benchmark(version):
    jar = "app/build/libs/app.jar" 
    arg = version
    
    output = run_java(jar, arg, "")
    print(output)
    
    return output

if __name__ == "__main__":
    graph_file_path = os.path.join("newdenmark.graph")
    output_pairs_path = os.path.join("random_pairs.txt")
    random_pairs = generate_random_pairs(vertices, SEED, n_pairs=1000)

    save_pairs_to_file(random_pairs, output_pairs_path)
    print(f"Generated 1000 random pairs and saved to {output_pairs_path}")
    n_v = len(vertices_plus) 
    n_e = len(edges) 
    g_vertices = '\n'.join(f"{v} {la} {lo}" for v, la, lo in vertices_plus)
    g_edges = '\n'.join(f"{u} {v} {w}" for u, v, w in edges)

    graph = f"{n_v} {n_e}\n{g_vertices}\n{g_edges}"
    
    # if c selected contract the graph
    if argument == "c":
        contract = benchmark("contract")    
    # if b selected run benchmarks on all SP algorithms
    if argument == "b":
        dijkstra = benchmark("dijkstra")
        bi_dijkstra = benchmark("BiDijkstra")
        contraction_hierarchies = benchmark("CH")
    # select single SP algs
    if argument == "di":
        dijkstra = benchmark("dijkstra")
    if argument == "bi":
        bi_dijkstra = benchmark("BiDijkstra")
    if argument == "ch":
        contraction_hierarchies = benchmark("CH")