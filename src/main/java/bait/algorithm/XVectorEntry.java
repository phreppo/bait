package bait.algorithm;

import bait.automata.State;
import bait.automata.Symbol;
import bait.comparator.PartialComparator;

import java.util.Set;
import java.util.stream.Collectors;

public final class XVectorEntry extends BAIncVectorEntry<Set<State>> {

    public XVectorEntry(State index, PartialComparator<Set<State>> comparator) {
        super(index, comparator);
    }

    @Override
    public String toString() {
        // Calling valueOf with either element.size() or element just works
        String joinedString = super.getAntichain().stream()
                .map(element -> String.valueOf(element.size()))
                .collect(Collectors.joining(":", "{", "}"));
        return joinedString;
    }

    @Override
    protected Set<State> getNewElement(Set<State> predecessorElementInPre, Symbol predecessorSymbol) {
        return State.post(predecessorElementInPre, predecessorSymbol);
    }
}
