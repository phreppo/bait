package bait.algorithm;

import java.util.Map;
import java.util.Set;

import bait.automata.BuchiAutomaton;
import bait.automata.State;
import bait.automata.Symbol;
import bait.comparator.PartialComparator;
import bait.comparator.XInclusionComparator;
import bait.comparator.YInclusionComparator;
import bait.utility.Algorithms;
import bait.utility.Args;
import bait.collections.Pair;
import bait.utility.Timer;

public final class BAInc {

    private final Timer timer;
    private final Args args;
    private int xIterations = 0;
    private int yIterations = 0;
    private int totalXAntichainsSize = 0;
    private int totalYAntichainsSize = 0;
    private int numberOfComputedYs = 0;
    private int iterationsLastKleene = 0;

    /**
     * @return the time to run the algorithm in milliseconds
     */
    public long getRuntime() {
        return timer.getMeasuredTime();
    }

    public double getXIterations() {
        return xIterations;
    }

    public double getYAverageIterations() {
        if (numberOfComputedYs > 0)
            return (double) yIterations / (double) numberOfComputedYs;
        else
            return 0.0;
    }

    public double getYTotalIterations() {
        return yIterations;
    }

    public double getXSize() {
        return totalXAntichainsSize;
    }

    public double getYAverageSize() {
        if (numberOfComputedYs > 0)
            return (double) totalYAntichainsSize / ((double) numberOfComputedYs);
        else
            return 0.0;
    }

    public BAInc(Args args) {
        timer = new Timer();
        this.args = args;
    }

    /**
     * @param a first automaton
     * @param b second automaton
     * @return true iff the language of a is a subset of the language of b
     */
    public boolean run(BuchiAutomaton a, BuchiAutomaton b) {
        timer.start();
        XVector x = computeX(a, b);
        updateXStatistics(x);
        for (State finalState : a.finalStates()) {
            YVector y = computeY(a, b, finalState);
            updateYStatistics(y);
            for (Set<State> xElement : x.get(finalState))
                for (Pair<Map<State, Set<State>>, Map<State, Set<State>>> yElement : y.get(finalState))
                    if (!C(xElement, yElement)) {
                        timer.stop();
                        return false;
                    }
        }
        timer.stop();
        return true;
    }

    /**
     * @param a the first automaton
     * @param b the second automaton
     * @return the X vector described in the paper applying the least fixpoint
     *         algorithm
     */
    private XVector computeX(BuchiAutomaton a, BuchiAutomaton b) {
        PartialComparator<Set<State>> comparator = new XInclusionComparator();
        XVector vectorBefore = XVector.initialX(a, b, comparator);
        // Initialize the vectorAfter with the same value
        XVector vectorAfter = XVector.initialX(a, b, comparator);
        if (args.minimalDebug())
            System.out.println("\nComputing X\n");
        return (XVector) kleene(vectorBefore, vectorAfter, comparator);
    }

    /**
     * @param a the first automaton
     * @param b the second automaton
     * @return the Y vector described in the paper applying the least fixpoint
     *         algorithm
     */
    private YVector computeY(BuchiAutomaton a, BuchiAutomaton b, State finalState) {
        PartialComparator<Pair<Map<State, Set<State>>, Map<State, Set<State>>>> comparator = new YInclusionComparator();
        Map<Symbol, Map<State, Set<State>>> contextB = b.context();
        Map<Symbol, Map<State, Set<State>>> finalContextB = b.finalContext();
        YVector vectorBefore = YVector.initialY(a, b, finalState, comparator, contextB, finalContextB);
        // Initialize the vectorAfter with the same value
        YVector vectorAfter = YVector.initialY(a, b, finalState, comparator, contextB, finalContextB);
        if (args.minimalDebug())
            System.out.println("Computing Y relative to final state " + finalState + "\n");
        return (YVector) kleene(vectorBefore, vectorAfter, comparator);
    }

    /**
     * @param x entry of the X vector
     * @param y entry of the Y vector
     * @return the result of the C function described in the paper
     */
    private boolean C(Set<State> x, Pair<Map<State, Set<State>>, Map<State, Set<State>>> y) {
        Map<State, Set<State>> y1 = y.fst();
        Map<State, Set<State>> y2 = y.snd();
        Map<State, Set<State>> transitiveClosureOfY1 = Algorithms.transitiveClosure(y1);
        Map<State, Set<State>> composition = Algorithms.compose(Algorithms.compose(transitiveClosureOfY1, y2),
                transitiveClosureOfY1);
        for (State p : x)
            if (transitiveClosureOfY1.containsKey(p))
                for (State q : transitiveClosureOfY1.get(p))
                    if (composition.containsKey(q) && composition.get(q).contains(q))
                        return true; // there's the pair (q,q) in y₁* ∘ y₂ ∘ y₁*
        return false;
    }

    /**
     * @param vectorBefore vector that is the initial vector described in the BAIncS
     *                     algorithm
     * @param vectorAfter  copy of vectorBefore
     * @return the fixpoint of the function p1 or p2 described in the paper,
     *         depending on the concrete type of the BAIncVector
     */
    private <T> BAIncVector<T> kleene(BAIncVector<T> vectorBefore, BAIncVector<T> vectorAfter,
            PartialComparator<T> comparator) {
        Set<State> changedEntries = vectorBefore.initiallyChangedEntries();
        // the outer frontier is the set of states which are successors of states that
        // changed during the last iteration. It is used to update the entries of the
        // vector more efficiently
        Set<State> outerFrontier;
        boolean reachedFixpoint = false;
        iterationsLastKleene = 0;
        printInitialMessage(vectorBefore);
        while (!reachedFixpoint) {
            outerFrontier = State.post(changedEntries);
            printFirstMessage(vectorBefore, outerFrontier);
            changedEntries = vectorAfter.iterate(vectorBefore, outerFrontier);
            vectorBefore.updateInnerFrontiers(vectorAfter, changedEntries);
            reachedFixpoint = vectorAfter.entriesAreComparable(vectorBefore, changedEntries, comparator);
            printSecondMessage(vectorBefore, vectorAfter, changedEntries);
            vectorBefore.copyEntries(vectorAfter, changedEntries);
            iterationsLastKleene++;
        }
        printFinalMessage(vectorBefore);
        return vectorBefore;
    }

    /**
     * Updates the counters for the statistics relative to the algorithm
     *
     * @param ySize total number of elements in the entries of the Y vector
     */
    private void updateYStatistics(YVector y) {
        totalYAntichainsSize += y.totNumberOfElementsInEntries();
        numberOfComputedYs++;
        yIterations += iterationsLastKleene;
    }

    /**
     * Updates the counters for the statistics relative to the algorithm
     *
     * @param x the X vector
     */
    private void updateXStatistics(XVector x) {
        totalXAntichainsSize += x.totNumberOfElementsInEntries();
        xIterations = iterationsLastKleene;
    }

    private <T> void printInitialMessage(BAIncVector<T> vectorBefore) {
        if (args.veryVerboseDebug()) {
            System.out.println("Initial vector");
            System.out.println(vectorBefore);
            System.out.println();
        }
    }

    private <T> void printFirstMessage(BAIncVector<T> vectorBefore, Set<State> frontier) {
        if (args.verboseDebug()) {
            System.out.println("Iteration " + iterationsLastKleene);
            if (args.veryVerboseDebug()) {
                System.out.println("Frontier");
                System.out.println(frontier);
                System.out.println("Vector before computation:");
                System.out.println(vectorBefore);
            }
        }
    }

    private <T> void printSecondMessage(BAIncVector<T> vectorBefore, BAIncVector<T> vectorAfter,
            Set<State> changedEntries) {
        if (args.verboseDebug()) {
            System.out.println();
            System.out.println(changedEntries.size() + " entries in the vector changed during this iteration");
            System.out.println(
                    vectorBefore.innerFrontiersTotalSize() + " elements have been added to the entries of the vector");
            if (args.veryVerboseDebug()) {
                System.out.println("Vector after computation:");
                System.out.println(vectorAfter);
                System.out.println();
            }
        }
    }

    private <T> void printFinalMessage(BAIncVector<T> vectorBefore) {
        if (args.veryVerboseDebug()) {
            String name = vectorBefore.getClass().getSimpleName();
            System.out.println("Fixpoint for " + name);
            System.out.println(vectorBefore);
        }
    }

}
