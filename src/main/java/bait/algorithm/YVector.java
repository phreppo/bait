package bait.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bait.automata.BuchiAutomaton;
import bait.automata.State;
import bait.automata.Symbol;
import bait.comparator.PartialComparator;
import bait.collections.Pair;

public final class YVector extends BAIncVector<Pair<Map<State, Set<State>>, Map<State, Set<State>>>> {

    private Set<State> initiallyChangedEntries;
    private final Map<State, Set<Pair<Map<State, Set<State>>, Map<State, Set<State>>>>> initialEntries;

    public static YVector initialY(BuchiAutomaton a, BuchiAutomaton b, State finalState,
            PartialComparator<Pair<Map<State, Set<State>>, Map<State, Set<State>>>> comparator,
            Map<Symbol, Map<State, Set<State>>> ctxB, Map<Symbol, Map<State, Set<State>>> ctxFB) {
        YVector newVector = new YVector(a, b, comparator);
        newVector.initiallyChangedEntries = finalState.post();
        for (Map.Entry<Symbol, Set<State>> successorsEntry : finalState.successors().entrySet()) {
            Symbol symbol = successorsEntry.getKey();
            Set<State> successorsFollowingSymbol = successorsEntry.getValue();
            for (State successor : successorsFollowingSymbol) {
                YVectorEntry entry = (YVectorEntry) newVector.vector.get(successor.getIntIndex());
                Map<State, Set<State>> fst = ctxB.get(symbol);
                Map<State, Set<State>> snd = ctxFB.get(symbol);
                Pair<Map<State, Set<State>>, Map<State, Set<State>>> newValue = Pair.of(fst, snd);
                entry.glbWith(newValue);
                newVector.initialEntries.put(successor, entry.getAntichain());
            }
        }

        for (var initiallyChangedState : newVector.initiallyChangedEntries()) {
            var entry = newVector.vector.get(initiallyChangedState.getIntIndex());
            entry.setCurrentAntichainAsInnerFrontier();
        }
        return newVector;
    }

    private YVector(BuchiAutomaton a, BuchiAutomaton b,
            PartialComparator<Pair<Map<State, Set<State>>, Map<State, Set<State>>>> comparator) {
        super(a, b);
        this.initiallyChangedEntries = new HashSet<>();
        this.initialEntries = new HashMap<>();
        for (State state : a.states())
            vector.add(new YVectorEntry(state, comparator));
    }

    @Override
    public Set<Pair<Map<State, Set<State>>, Map<State, Set<State>>>> getInitialValue(State index) {
        return initialEntries.get(index);
    }

    @Override
    public Set<State> initiallyChangedEntries() {
        return initiallyChangedEntries;
    }

}
