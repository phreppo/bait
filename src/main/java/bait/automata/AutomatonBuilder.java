package bait.automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AutomatonBuilder {

    public static class BuildError extends Error {
        private static final long serialVersionUID = 1L;

        public BuildError(String message) {
            super(message);
        }
    }

    private String initialStateString;
    private Set<String> finalStatesString;
    private Set<Edge<String, String>> edgesString;
    private final Map<String, State> statesMap;

    private State initialState;
    private final Set<State> finalStates;
    private final Set<State> states;
    private final Set<Edge<State, Symbol>> edges;
    private Alphabet alphabet;

    public AutomatonBuilder() {
        states = new HashSet<>();
        finalStates = new HashSet<>();
        edges = new HashSet<>();
        statesMap = new HashMap<>();
    }

    /**
     * Uses the given string as initial state to build the automaton.
     *
     * @param initialStateString the identifier of the initial state
     */
    public void withInitialState(String initialStateString) {
        this.initialStateString = initialStateString;
    }

    /**
     * Uses the given set of states as final states to build the automaton.
     *
     * @param finalStatesString the identifiers of the final states
     */
    public void withFinalStates(Set<String> finalStatesString) {
        this.finalStatesString = finalStatesString;
    }

    /**
     * Uses the given edges to build the automaton.
     *
     * @param edgesString the string representation of the edges.
     */
    public void withEdges(Set<Edge<String, String>> edgesString) {
        this.edgesString = edgesString;
    }

    /**
     * Uses this alphabet to build the automaton. The alphabet must contain at least
     * all symbols in the edges of the automaton.
     *
     * @param alphabet the alphabet used to build the automaton
     */
    public void withAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    /**
     * Builds the automaton with the required initial state, edges and final states.
     */
    public BuchiAutomaton build() throws BuildError {
        checkIfSpecifiedAllComponents();
        buildStatesAndEdgesSetsFromStringRepresentation();
        BuchiAutomaton ba = buildAutomatonWithStatesAndEdges();
        enumerateStates(ba);
        setAlphabet(ba);
        return ba;
    }

    private void checkIfSpecifiedAllComponents() throws BuildError {
        if (initialStateString == null)
            throw new BuildError("Initial state not specified");
        if (edgesString == null)
            throw new BuildError("Edges not specified");
        if (finalStatesString == null)
            throw new BuildError("Final states not specified");
        if (alphabet == null)
            throw new BuildError("Alphabet not specified");
    }

    private void buildStatesAndEdgesSetsFromStringRepresentation() {
        insertStates();
        buildEdgesFromStringRepresentation();
        buildStatesFromStringRepresentation();
        markFinalStates();
    }

    private void insertStates() {
        insertInitialState();
        insertFinalStates();
        insertStatesInEdges();
    }

    private void insertInitialState() {
        statesMap.putIfAbsent(initialStateString, new State(initialStateString));
    }

    private void insertFinalStates() {
        for (String s : finalStatesString)
            statesMap.putIfAbsent(s, new State(s));
    }

    private void insertStatesInEdges() {
        for (Edge<String, String> e : edgesString) {
            statesMap.putIfAbsent(e.from, new State(e.from));
            statesMap.putIfAbsent(e.to, new State(e.to));
        }
    }

    private void buildEdgesFromStringRepresentation() {
        for (Edge<String, String> e : edgesString) {
            State from = statesMap.get(e.from);
            State to = statesMap.get(e.to);
            Symbol symbol = new Symbol(e.label);
            Edge<State, Symbol> newEdge = new Edge<>(from, to, symbol);
            edges.add(newEdge);
        }
    }

    private void buildStatesFromStringRepresentation() {
        initialState = statesMap.get(initialStateString);
        for (String finalStateString : finalStatesString)
            finalStates.add(statesMap.get(finalStateString));
        states.addAll(statesMap.values());
    }

    private void markFinalStates() {
        for (State s : this.states) {
            if (this.finalStates.contains(s))
                s.setIsFinal(true);
        }
    }

    private BuchiAutomaton buildAutomatonWithStatesAndEdges() {
        BuchiAutomaton ba = new BuchiAutomaton();
        setAutomatonStates(ba);
        setAutomatonEdges(ba);
        return ba;
    }

    private void setAutomatonStates(BuchiAutomaton ba) {
        ba.states = this.states;
        ba.initialState = this.initialState;
        ba.finalStates = this.finalStates;
    }

    private void setAutomatonEdges(BuchiAutomaton ba) {
        for (State s : ba.states()) {
            for (Edge<State, Symbol> edge : this.edges) {
                if (s.equals(edge.from))
                    s.addSuccessor(edge.to, edge.label);
                if (s.equals(edge.to))
                    s.addPredecessor(edge.from, edge.label);
            }
        }
    }

    private void enumerateStates(BuchiAutomaton ba) {
        int index = 0;
        for (State state : ba.states())
            state.setIndex(index++);
    }

    private void setAlphabet(BuchiAutomaton ba) {
        ba.setAlphabet(this.alphabet);
    }

}
