package bait.automata;

public final class Symbol {

    private final String stringRepresentation;

    public Symbol(String string) {
        this.stringRepresentation = string;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    @Override
    public int hashCode() {
        return stringRepresentation.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Symbol other = (Symbol) obj;
        if (stringRepresentation == null) {
            if (other.stringRepresentation != null)
                return false;
        } else if (!stringRepresentation.equals(other.stringRepresentation))
            return false;
        return true;
    }

}
