package org.example;

// for use in contraction hierarchie
public class PqItem implements Comparable<PqItem>{

        private int index;
        private int importance;

        public PqItem(int index, int importance) {
            this.index = index;
            this.importance = importance;
        }

        public int getIndex() {
            return index;
        }

        public int getImportance() {
            return importance;
        }

        @Override
        public int compareTo(PqItem other) {
            if (this.importance != other.importance) {
                return Integer.compare(this.importance, other.importance);
            } else {
                return Integer.compare(this.index, other.index);
            }
        }
}
