package bait.automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bait.collections.Pair;

public final class BuchiAutomaton {

    protected Set<State> states;
    protected State initialState;
    protected Set<State> finalStates;
    protected Alphabet alphabet;

    protected BuchiAutomaton() {
    }

    public Set<State> states() {
        return new HashSet<>(states);
    }

    public State initialState() {
        return initialState;
    }

    public Set<State> finalStates() {
        return finalStates;
    }

    public int numberOfStates() {
        return states().size();
    }

    public void setAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    public Alphabet alphabet() {
        return alphabet;
    }

    /**
     * @return the map that associates each symbol in the alphabet with the set of
     *         pairs of states in the automaton such that from the first state it is
     *         possible to to reach the second state following the symbol. If the
     *         context of a symbol is empty, it won't be in the result. For example,
     *         if in the automaton there's the transition q1-a->q2, then (q1,q2)
     *         will be in the context associated with the symbol a. The set of pairs
     *         is represented as a map for efficiency reasons.
     */
    public Map<Symbol, Map<State, Set<State>>> context() {
        Map<Symbol, Map<State, Set<State>>> ctx = new HashMap<>();
        for (Symbol symbol : alphabet)
            ctx.put(symbol, context(symbol));
        return ctx;
    }

    private Map<State, Set<State>> context(Symbol symbol) {
        Map<State, Set<State>> ctx = new HashMap<>();
        for (State from : states()) {
            Set<State> to = from.post(symbol);
            if (!to.isEmpty())
                ctx.put(from, to);
        }
        return ctx;
    }

    /**
     * @return the map that associates each symbol in the alphabet with the set of
     *         pairs of states in the automaton such that from the first state it is
     *         possible to reach the second state following the symbol, and at least
     *         one of the two states is final. If the final context of a symbol is
     *         empty, it won't be in the result. For example, if in the automaton
     *         there's the transition q1-a->q2, then (q1,q2) will be in the context
     *         associated with the symbol a if and only if at least one of the two
     *         states is final. The set of pairs is represented as a map for
     *         efficiency reasons.
     */
    public Map<Symbol, Map<State, Set<State>>> finalContext() {
        Map<Symbol, Map<State, Set<State>>> ctxF = new HashMap<>();
        for (Symbol symbol : alphabet)
            ctxF.put(symbol, finalContext(symbol));
        return ctxF;
    }

    private Map<State, Set<State>> finalContext(Symbol symbol) {
        Map<State, Set<State>> ctxF = new HashMap<>();
        for (State from : states()) {
            Set<State> to = from.postF(symbol);
            if (!to.isEmpty())
                ctxF.put(from, to);
        }
        return ctxF;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Buchi Automaton\n");
        builder.append("Initial state: " + initialState() + "\n");
        builder.append("Final states: " + finalStates() + "\n");
        builder.append("States:\n");
        for (State s : states())
            addStateToBuilder(builder, s);
        return builder.toString();
    }

    private void addStateToBuilder(StringBuilder builder, State s) {
        builder.append("\tState: " + s + "\n");
        builder.append("\tSuccessors of " + s + ":\n");
        addSuccessorsToBuilder(builder, s);
        builder.append("\tPredecessors of " + s + ":\n");
        addPredecessorsToBuilder(builder, s);
    }

    private void addSuccessorsToBuilder(StringBuilder builder, State s) {
        for (Map.Entry<Symbol, Set<State>> succ : s.successors().entrySet())
            for (State successor : succ.getValue())
                builder.append("\t\t- " + succ.getKey() + " -> " + successor + "\n");
    }

    private void addPredecessorsToBuilder(StringBuilder builder, State s) {
        for (Pair<State, Symbol> pred : s.predecessors())
            builder.append("\t\t<- " + pred.snd() + " - " + pred.fst() + "\n");
    }
}
