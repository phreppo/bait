package bait.automata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class Alphabet implements Iterable<Symbol> {

    private final Set<Symbol> symbolsSet;

    public Alphabet(Set<Symbol> symbols) {
        this.symbolsSet = new HashSet<>(symbols);
    }

    /**
     * Builds one alphabet using the set of symbols.
     *
     * @param symbols set of input symbols
     * @return the corresponding alphabet
     */
    public static Alphabet of(Set<String> symbols) {
        Set<Symbol> symbolsSet = new HashSet<>();
        for (String s : symbols)
            symbolsSet.add(new Symbol(s));
        return new Alphabet(symbolsSet);
    }

    /**
     * Merges two alphabets. Duplicates are eliminated.
     *
     * @param a1 first alphabet
     * @param a2 second alphabet
     * @return the merge of the two
     */
    public static Alphabet merge(Alphabet a1, Alphabet a2) {
        Set<Symbol> symbols = new HashSet<>();
        symbols.addAll(a1.symbolsSet);
        symbols.addAll(a2.symbolsSet);
        return new Alphabet(symbols);
    }

    @Override
    public Iterator<Symbol> iterator() {
        return symbolsSet.iterator();
    }

}
