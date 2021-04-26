package bait.algorithm;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import bait.automata.BuchiAutomaton;
import bait.automata.State;
import bait.comparator.PartialComparator;

public final class XVector extends BAIncVector<Set<State>> {

    /**
     * Returns the vector of sets of sets of states which in the index of the
     * initial state of automaton a has a singleton containing the initial state of
     * automaton b.
     *
     * @param a          first automaton
     * @param b          second automaton
     * @param comparator the comparator used to compute the minor for the elements
     * @return initial X vector of the algorithm BAInc based on automata a and b
     */
    public static XVector initialX(BuchiAutomaton a, BuchiAutomaton b, PartialComparator<Set<State>> comparator) {
        XVector newVector = new XVector(a, b, comparator);
        Set<Set<State>> initialAStateEntry = newVector.get(a.initialState());
        Set<State> singletonInitialBState = new HashSet<>(Collections.singletonList(b.initialState()));
        initialAStateEntry.add(singletonInitialBState);
        for (var initiallyChangedState : newVector.initiallyChangedEntries()) {
            var entry = newVector.vector.get(initiallyChangedState.getIntIndex());
            entry.setCurrentAntichainAsInnerFrontier();
        }
        return newVector;
    }

    private XVector(BuchiAutomaton a, BuchiAutomaton b, PartialComparator<Set<State>> comparator) {
        super(a, b);
        for (State state : a.states())
            vector.add(new XVectorEntry(state, comparator));
    }

    /**
     * Returns the set of states that are added to the initialization vector. This
     * set will be a singleton containing the initial a's state.
     *
     * @return the set of states that are added to the initialization vector
     */
    @Override
    public Set<State> initiallyChangedEntries() {
        return new HashSet<>(Collections.singletonList(a.initialState()));
    }

    @Override
    public Set<Set<State>> getInitialValue(State index) {
        if (index.equals(a.initialState())) {
            Set<State> singletonInitialBState = new HashSet<>(Collections.singletonList(b.initialState()));
            return new HashSet<>(Collections.singletonList(singletonInitialBState));
        } else
            return null;
    }

}
