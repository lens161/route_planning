import random
import os

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

if __name__ == "__main__":
    graph_file_path = os.path.join("/home/knor/route/route-planning/app/src/main/resources/denmark.graph")  # Adjust the path as necessary
    output_pairs_path = os.path.join("/home/knor/route/route-planning/app/src/main/resources/random_pairs.txt")

    # Read edges from the graph file
    edges = read_edges_from_graph(graph_file_path)
    if not edges:
        print("No edges found in the graph file.")
    else:
        # Generate random pairs using a fixed seed
        seed = 42  # Set your desired seed value here
        random_pairs = generate_random_pairs(edges, seed, n_pairs=1000)
        
        # Save the pairs to a file
        save_pairs_to_file(random_pairs, output_pairs_path)
        print(f"Generated 1000 random (s, t) pairs and saved to {output_pairs_path}")