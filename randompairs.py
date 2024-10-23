import random
import os
import subprocess

TIMEOUT = 30

def read_edges_from_graph(graph_path):
    edges = []
    with open(graph_path, "r") as file:
        lines = file.readlines()
        # Skip the vertex lines (first 569586 lines)
        edge_lines = lines[569587:]
        for line in edge_lines:
            if line.strip():
                tokens = line.split()
                if len(tokens) == 3:  # Assuming edge lines contain 3 tokens: s, t, weight
                    s = int(tokens[0])
                    t = int(tokens[1])
                    edges.append((s, t))
    return edges


def generate_random_pairs(edges, seed, n_pairs=1000):
    random.seed(seed)
    pairs = random.sample(edges, n_pairs)
    return pairs

def save_pairs_to_file(pairs, output_path):
    with open(output_path, "w") as file:
        for s, t in pairs:
            file.write(f"{s} {t}\n")

def run_java(jar: str, arg: str, input: str) -> str:
    p = subprocess.Popen(['java', '-jar', jar, arg], 
                         stdin=subprocess.PIPE, 
                         stdout=subprocess.PIPE)

    (output, _) = p.communicate(input.encode('utf-8'), timeout=TIMEOUT)
    return output.decode('utf-8')

def benchmark():
    input_size = 1000
    
    input_data = ' '.join(map(str, generate_random_pairs(edges, seed, n_pairs=1000)))
    # arg = str(reg_count)
    
    # estimate = run_java(jar, arg, input_data)
    print(estimate)
    estimate = float(estimate)
    
    return estimate

if __name__ == "__main__":
    graph_file_path = os.path.join("denmark.graph")
    output_pairs_path = os.path.join("random_pairs.txt")

    edges = read_edges_from_graph(graph_file_path)
    if not edges:
        print("No edges found in the graph file.")
    else:
        # Generate random pairs using a fixed seed
        seed = 42
        random_pairs = generate_random_pairs(edges, seed, n_pairs=1000)
        
        # Save the pairs to a file
        save_pairs_to_file(random_pairs, output_pairs_path)
        print(f"Generated 1000 random (s, t) pairs and saved to {output_pairs_path}")