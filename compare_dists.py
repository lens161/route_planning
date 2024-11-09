import csv

def compare_csv_files(file1, file2):
    # Open both CSV files for reading
    with open(file1, 'r') as f1, open(file2, 'r') as f2:
        reader1 = csv.reader(f1)
        reader2 = csv.reader(f2)

        # Skip the header row in both files
        next(reader1)
        next(reader2)

        # Initialize counter for mismatches
        mismatch_count = 0

        minus1_count=0

        # Create dictionaries to store source-target as key and distance as value
        file1_data = {}
        file2_data = {}

        mismatch_lens = {}

        # Read data from the first file
        for row in reader1:
            # Skip rows that are empty or don't have enough columns
            if len(row) < 3:
                print(f"Skipping malformed row in file1: {row}")
                continue
            source = row[0]
            target = row[1]
            try:
                distance = float(row[2])
            except ValueError:
                print(f"Skipping row with invalid distance in file1: {row}")
                continue
            file1_data[(source, target)] = distance

        # Read data from the second file
        for row in reader2:
            # Skip rows that are empty or don't have enough columns
            if len(row) < 3:
                print(f"Skipping malformed row in file2: {row}")
                continue
            source = row[0]
            target = row[1]
            try:
                distance = float(row[2])
            except ValueError:
                print(f"Skipping row with invalid distance in file2: {row}")
                continue
            file2_data[(source, target)] = distance

        # Compare the distances for the same source-target pairs
        for key in file1_data:
            if key in file2_data:
                if file1_data[key] == -1 or file2_data[key] == -1:
                    minus1_count += 1
                    print(f"Minus one found: Source={key[0]}, Target={key[1]}, "
                        f"Distance CH-Bi-Dijkstra={file1_data[key]}, Distance Dijkstra={file2_data[key]}")
                if file1_data[key] != file2_data[key]:
                    difference = file1_data[key] - file2_data[key]
                    if difference not in mismatch_lens:
                        mismatch_lens[difference] = 0
                    mismatch_lens[difference] += 1
                    print(f"Mismatch found: Source={key[0]}, Target={key[1]}, "
                        f"Distance CH-Bi-Dijkstra={file1_data[key]}, Distance Dijkstra={file2_data[key]}, Difference = {difference}")
                    mismatch_count += 1

        # Output the result
        print(f"\nTotal mismatches: {mismatch_count}")
        print(f"\nTotal instances of -1: {minus1_count}\n")
        for diff in sorted(mismatch_lens):
            print(f'{mismatch_lens.get(diff)} instances of difference {diff}')


# Example usage
#file1 = '/home/najj/applied_algo/route-planning/app/src/main/resources/naja_debug2_bidirectional_dijkstra_results.csv'
file1 = '/home/najj/applied_algo/route-planning/app/src/main/resources/naja_debug2_CHbidijkstra_results.csv'
file2 = '/home/najj/applied_algo/route-planning/app/src/main/resources/naja_debug_dijkstra_results_slow.csv'
compare_csv_files(file1, file2)
