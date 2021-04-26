package bait.automata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;

public final class AutomatonParser {

    public static class ParseError extends Error {
        private static final long serialVersionUID = 1L;

        public ParseError(String message) {
            super(message);
        }
    }

    public static Alphabet parseAlphabet(String automaton1, String automaton2) {
        Alphabet alphabet1 = parseAlphabet(automaton1);
        Alphabet alphabet2 = parseAlphabet(automaton2);
        return Alphabet.merge(alphabet1, alphabet2);
    }

    public static Alphabet parseAlphabet(String source) {
        Set<String> symbols = extractAlphabet(source);
        return Alphabet.of(symbols);
    }

    private static Set<String> extractAlphabet(String source) {
        Set<String> alphabet = new HashSet<>();
        String[] lines = source.split("\n");
        for (String line : lines) {
            Edge<String, String> parsedEdge = parseEdgeLine(line);
            if (parsedEdge != null)
                alphabet.add(parsedEdge.label);
        }
        return alphabet;
    }

    /*
     * The source must be in BA format:
     * http://languageinclusion.org/doku.php?id=tools#the_ba_format
     */
    public static BuchiAutomaton parse(String source, Alphabet alphabet)
            throws ParseError, AutomatonBuilder.BuildError {
        LinkedList<String> lines = new LinkedList<>(Arrays.asList(source.split("\n")));

        String initialState = parseInitialState(lines);
        Set<Edge<String, String>> edges = parseLines(lines, AutomatonParser::parseEdgeLine);
        Set<String> finalStates;
        if (lines.isEmpty())
            finalStates = allStates(initialState, edges);
        else
            finalStates = parseLines(lines, AutomatonParser::parseStateOrSymbol);

        checkInputEnded(lines);

        return buildAutomaton(initialState, edges, finalStates, alphabet);
    }

    private static BuchiAutomaton buildAutomaton(String initialState, Set<Edge<String, String>> edges,
            Set<String> finalStates, Alphabet alphabet) throws AutomatonBuilder.BuildError {

        AutomatonBuilder builder = new AutomatonBuilder();
        builder.withInitialState(initialState);
        builder.withEdges(edges);
        builder.withFinalStates(finalStates);
        builder.withAlphabet(alphabet);
        return builder.build();
    }

    private static String parseInitialState(LinkedList<String> lines) throws ParseError {
        if (lines.isEmpty())
            return null;
        return parseInitialStateFromLine(lines.peek(), lines);
    }

    private static String parseInitialStateFromLine(String line, LinkedList<String> lines) {
        String initialStateFromEdge = parseInitialStateFromEdge(line);
        if (initialStateFromEdge != null)
            // we don't pop lines because the first edges is needed to parse edges
            return initialStateFromEdge;
        String initialState = parseStateOrSymbol(line);
        if (initialState != null)
            lines.pop();
        return initialState;
    }

    private static String parseInitialStateFromEdge(String line) {
        Edge<String, String> firstEdge = parseEdgeLine(line);
        return firstEdge != null ? firstEdge.from : null;
    }

    private static <S> Set<S> parseLines(LinkedList<String> lines, Function<String, S> parser) {
        Set<S> elements = new HashSet<>();
        boolean parsed = false;
        while (!parsed && !lines.isEmpty()) {
            S parsedElement = parser.apply(lines.peek());
            if (parsedElement != null) {
                elements.add(parsedElement);
                lines.pop();
            } else
                parsed = true;
        }
        return elements;
    }

    private static Set<String> allStates(String initialState, Set<Edge<String, String>> edges) {
        Set<String> states = extractStates(edges);
        states.add(initialState);
        return states;
    }

    private static Set<String> extractStates(Set<Edge<String, String>> edges) {
        Set<String> states = new HashSet<>();
        edges.stream().forEach(edge -> states.add(edge.from));
        edges.stream().forEach(edge -> states.add(edge.to));
        return states;
    }

    private static String parseStateOrSymbol(String line) {
        if (line.isEmpty() || !lineContainsForbiddenSubtring(line))
            return line;
        else
            return null;
    }

    private static boolean lineContainsForbiddenSubtring(String line) {
        return lineContainsArrow(line) || lineContainsComma(line);
    }

    private static boolean lineContainsArrow(String line) {
        return line.contains("->");
    }

    private static boolean lineContainsComma(String line) {
        return line.contains(",");
    }

    private static Edge<String, String> parseEdgeLine(String line) {
        String[] stringsSeparatedByCommas = line.split(",");
        if (stringsSeparatedByCommas.length != 2)
            return null;
        String label = parseStateOrSymbol(stringsSeparatedByCommas[0]);
        if (label == null)
            return null;
        String fromTo = stringsSeparatedByCommas[1];
        String[] stringsSeparatedByArrows = fromTo.split("->");
        if (stringsSeparatedByArrows.length != 2)
            return null;
        String from = parseStateOrSymbol(stringsSeparatedByArrows[0]);
        String to = parseStateOrSymbol(stringsSeparatedByArrows[1]);
        if (from == null || to == null)
            return null;
        return new Edge<>(from, to, label);
    }

    private static void checkInputEnded(LinkedList<String> lines) throws ParseError {
        if (!lines.isEmpty())
            throw new ParseError("Parse error at line: \"" + lines.peek()
                    + "\". Please check the specification of the '.ba' format.");
    }

    private AutomatonParser() {
    }

}
