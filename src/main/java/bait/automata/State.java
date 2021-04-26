package bait.automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Objects;

import bait.collections.Pair;

public final class State {

    private final String identifier;
    private final Set<Pair<State, Symbol>> predecessors;
    private final Map<Symbol, Set<State>> successors;
    private int index;
    private boolean isFinal;

    public State(String identifier) {
        this.identifier = identifier;
        this.predecessors = new HashSet<>();
        this.successors = new HashMap<>();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIntIndex() {
        return index;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public Set<Pair<State, Symbol>> predecessors() {
        return predecessors;
    }

    public Map<Symbol, Set<State>> successors() {
        return successors;
    }

    public void addPredecessor(State predecessor, Symbol symbol) {
        predecessors.add(Pair.of(predecessor, symbol));
    }

    public void addSuccessor(State successor, Symbol symbol) {
        if (successors.containsKey(symbol)) {
            successors.get(symbol).add(successor);
        } else {
            Set<State> singleton = new HashSet<>();
            singleton.add(successor);
            successors.put(symbol, singleton);
        }
    }

    /**
     * @param symbol is the symbol to follow from this state
     * @return the set of states that this state can reach following the symbol
     */
    public Set<State> post(Symbol symbol) {
        Set<State> post = successors.get(symbol);
        return Objects.requireNonNullElseGet(post, HashSet::new);
    }

    /**
     * @return the set of successors of this state, following any symbol
     */
    public Set<State> post() {
        Set<State> allSuccessors = new HashSet<>();
        for (Map.Entry<Symbol, Set<State>> entry : this.successors.entrySet())
            allSuccessors.addAll(entry.getValue());
        return allSuccessors;
    }

    /**
     * @param symbol is the symbol to follow from this state
     * @return the set of states that this state can reach following the symbol. If
     *         this state is final this function equals the post function. If this
     *         state is not final, then the returned set of states will contain only
     *         final states.
     */
    public Set<State> postF(Symbol symbol) {
        if (this.isFinal())
            return post(symbol);
        else {
            Set<State> postF = new HashSet<>();
            for (State successor : post(symbol))
                if (successor.isFinal())
                    postF.add(successor);
            return postF;
        }
    }

    /**
     * @param states input set of states
     * @param symbol the symbol to follow from the set of states
     * @return the set of states that can be reached from one state in the input set
     *         following the symbol
     */
    public static Set<State> post(Set<State> states, Symbol symbol) {
        Set<State> successors = new HashSet<>();
        for (State state : states)
            successors.addAll(state.post(symbol));
        return successors;
    }

    /**
     * @param states input set of states
     * @return the set of states that can be reached from one state in the input set
     *         following any symbol
     */
    public static Set<State> post(Set<State> states) {
        Set<State> successors = new HashSet<>();
        for (State state : states)
            successors.addAll(state.post());
        return successors;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
        State other = (State) obj;
        if (identifier == null) {
            return other.identifier == null;
        } else
            return identifier.equals(other.identifier);
    }

}
