package bait.algorithm;

import java.util.HashSet;
import java.util.Set;

import bait.automata.State;
import bait.automata.Symbol;
import bait.comparator.PartialComparator;
import bait.collections.Pair;

public abstract class BAIncVectorEntry<T> {

    private final State indexInVector;
    private final Antichain<T> antichain;
    private final Set<T> innerFrontier;

    public abstract String toString();

    public State indexInVector() {
        return indexInVector;
    }

    public Set<T> getAntichain() {
        return antichain.elements();
    }

    public long antichainSize() {
        return antichain.size();
    }

    /**
     * Applies the Greatest Lower Bound to the entry and the new element. The
     * comparator with which the entry was created determines the result of the glb.
     *
     * @param newElement the value to compute the glb with
     * @return true iff the entry gets modified
     */
    public boolean glbWith(T newElement) {
        return antichain.glbWith(newElement);
    }

    /**
     * Applies one iteration of the function p1 or p2 to this vector entry,
     * depending on the type at runtime of the entry. Returns true iff the entry
     * gets modified.
     *
     * @param vectorAtPreviousIteration the input vector
     * @return true iff the entry gets modified
     */
    public boolean applyIteration(BAIncVector<T> vectorAtPreviousIteration) {
        boolean entryModified = false;
        for (Pair<State, Symbol> predecessor : indexInVector().predecessors()) {
            boolean entryModifiedWithThisPredecessor = updateWrtPredecessor(predecessor, vectorAtPreviousIteration);
            entryModified = entryModifiedWithThisPredecessor || entryModified;
        }
        return entryModified;
    }

    /**
     * Updates the value of the entry with respect to a certain predecessor, which
     * is specified as a pair of a state and the symbol with which the predecessor
     * reaches the state represented by this vector entry.
     *
     * @param predecessor
     * @param vectorAtPreviousIteration
     * @return true iff the entry gets modified
     */
    private boolean updateWrtPredecessor(Pair<State, Symbol> predecessor, BAIncVector<T> vectorAtPreviousIteration) {
        State predecessorState = predecessor.fst();
        Symbol predecessorSymbol = predecessor.snd();
        Set<T> predecessorValueAtLastIteration = vectorAtPreviousIteration.get(predecessorState);
        BAIncVectorEntry<T> predecessorEntry = vectorAtPreviousIteration.vector.get(predecessorState.getIntIndex());
        boolean entryModified = false;
        for (T predecessorElementInPreviousIteration : predecessorValueAtLastIteration)
            // we perform the following check to be sure that the element we're considering
            // was added during the last iteration of the algorithm, and then can actually
            // modify the content of the entry
            if (predecessorEntry.innerFrontier.contains(predecessorElementInPreviousIteration)) {
                boolean entryModifiedWithThisPredecessorElement = updateWrtPredecessorElementAtPreviousIteration(
                        predecessorElementInPreviousIteration, predecessorSymbol);
                entryModified = entryModified || entryModifiedWithThisPredecessorElement;
            }
        return entryModified;
    }

    private boolean updateWrtPredecessorElementAtPreviousIteration(T predecessorElementInPre,
            Symbol predecessorSymbol) {
        T newElement = getNewElement(predecessorElementInPre, predecessorSymbol);
        return glbWith(newElement);
    }

    /**
     * This is the only information that changes between entries in the X and Y
     * vectors: how to compute the next element applying either p1 or p2. For the X
     * vector the next element is post(y, a) [using the paper's names], and for the
     * Y vector is the new pair (y1 ∘ ctx(a), y1 ∘ ctx_F(a) ∪ y2 ∘ ctx(a)).
     *
     * @param predecessorElementInPre for X is a set of states (y), and for Y is a
     *                                pair of set of states (y1,y2)
     * @param predecessorSymbol       the symbol with which the predecessor reaches
     *                                the state
     * @return the new element after applying p1 or p2
     */
    protected abstract T getNewElement(T predecessorElementInPre, Symbol predecessorSymbol);

    /**
     * Updates the frontier of this entry. The elements of the frontier will be the
     * new ones with respect to the entry after the iteration. For example, if this
     * antichain is {1,2} and the antichain in the entry after the iteration is
     * {1,2,3}, the frontier will be {1}.
     *
     * @param entryInVectorAfterIteration the entry of the vector after computing
     *                                    one Kleene's iterate.
     */
    public void addNewElementsToTheFrontier(BAIncVectorEntry<T> entryInVectorAfterIteration) {
        for (T newValue : entryInVectorAfterIteration.antichain.elements())
            if (!antichain.elements().contains(newValue))
                innerFrontier.add(newValue);
    }

    public void clearInnerFrontier() {
        innerFrontier.clear();
    }

    public void setCurrentAntichainAsInnerFrontier() {
        clearInnerFrontier();
        innerFrontier.addAll(antichain.elements());
    }

    public int innerFrontierSize() {
        return innerFrontier.size();
    }

    /**
     * @param indexInVector state that is index of the new entry
     * @param comparator    comparator that is used to compute the minor when adding
     *                      new elements to the entry
     */
    protected BAIncVectorEntry(State indexInVector, PartialComparator<T> comparator) {
        this.indexInVector = indexInVector;
        this.antichain = new Antichain<>(comparator);
        this.innerFrontier = new HashSet<>();
    }

}
