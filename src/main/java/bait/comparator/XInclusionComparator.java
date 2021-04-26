package bait.comparator;

import java.util.Set;

import bait.automata.State;

public final class XInclusionComparator implements PartialComparator<Set<State>> {

    @Override
    public boolean lesserOrEqual(Set<State> t1, Set<State> t2) {
        return t2.containsAll(t1);
    }

}
