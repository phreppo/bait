package bait.automata;

public final class Edge<S, T> {
    S from;
    S to;
    T label;

    Edge(S from, S to, T label) {
        this.from = from;
        this.to = to;
        this.label = label;
    }

    public String toString() {
        return "Edge [from=" + from + ", label=" + label + ", to=" + to + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Edge<S, T> other = (Edge) obj;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (to == null) {
            return other.to == null;
        } else
            return to.equals(other.to);
    }
}
