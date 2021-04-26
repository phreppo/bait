package bait.comparator;

import java.util.Map;
import java.util.Set;

import bait.automata.State;
import bait.collections.Pair;
import bait.utility.Algorithms;

public final class YInclusionComparator implements PartialComparator<Pair<Map<State, Set<State>>, Map<State, Set<State>>>> {

    @Override
    public boolean lesserOrEqual(Pair<Map<State, Set<State>>, Map<State, Set<State>>> t1,
            Pair<Map<State, Set<State>>, Map<State, Set<State>>> t2) {
        return Algorithms.isSubset(t1.fst(), t2.fst()) && Algorithms.isSubset(t1.snd(), t2.snd());
    }

}
