import random
import os
import subprocess

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
        print(vertex_lines[0])

        for line in vertex_lines:
            tokens = line.split()
            if len(tokens) == 3:
                v = int(tokens[0])
                latitude = int(float(tokens[1]))
                longitude = int(float(tokens[2]))
                vertices.append(v)
                vertices_plus.append((v, latitude, longitude))

        edge_lines = lines[n_vertices+1:]
        print(edge_lines[0])

        for line in edge_lines:
            if line.strip():
                tokens = line.split()
                if len(tokens) == 3:
                    v = int(tokens[0])
                    w = int(tokens[1])
                    weight = int(tokens[2])
                    edges.append((v, w, weight))

    return vertices, vertices_plus, edges


vertices, vertices_plus, edges = read_graph("newdenmark.graph")

# def generate_random_pairs(vertex_list, seed, n_pairs=1000):
#     random.seed(seed)
#     random.shuffle(vertex_list)
#     pairs = []
#     for i in range(n_pairs):
#         pairs.append((vertex_list[i], vertex_list[i+1]))
#     return pairs

def select_random_pairs(vertices, seed, n_pairs):
    """
    Selects random (s, t) pairs from the list of vertices, ensuring pairs are unique.
    """
    random.seed(seed)
    selected_pairs = set()
    
    while len(selected_pairs) < n_pairs:
        v1, v2 = random.sample(vertices, 2)
        if (v1, v2) not in selected_pairs and (v2, v1) not in selected_pairs:
            selected_pairs.add((v1, v2))
    
    print("Done finding pairs   ")
    
    return list(selected_pairs)


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

def benchmark():
    input_size = 1000
    
    input_data = ' '.join(map(str, select_random_pairs(edges, seed, n_pairs=1000)))
    # arg = str(reg_count)
    
    # estimate = run_java(jar, arg, input_data)
    print(estimate)
    estimate = float(estimate)
    
    return estimate

if __name__ == "__main__":
    graph_file_path = os.path.join("newdenmark.graph")
    output_pairs_path = os.path.join("newrandom_pairs.txt")

    edges = read_graph(graph_file_path)
    if not edges:
        print("No edges found in the graph file.")
    else:
        # Generate random pairs using a fixed seed
        seed = 420
        random_pairs = select_random_pairs(vertices, seed, n_pairs=1000)
        
        # Save the pairs to a file
        save_pairs_to_file(random_pairs, output_pairs_path)
        print(f"Generated 1000 random (s, t) pairs and saved to {output_pairs_path}")