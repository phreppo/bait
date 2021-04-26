package bait.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import bait.automata.BuchiAutomaton;
import bait.automata.State;
import bait.comparator.PartialComparator;

public abstract class BAIncVector<T> {

    protected BuchiAutomaton a;
    protected BuchiAutomaton b;
    protected ArrayList<BAIncVectorEntry<T>> vector;

    /**
     * Returns the set of states that are added to the initialization vector. For
     * example, in the X vector this set will be a singleton containing the initial
     * a's state.
     *
     * @return the set of states that are added to the initialization vector
     */
    public abstract Set<State> initiallyChangedEntries();

    /**
     * Returns the value in the initialization vector at the entry index. Returns
     * null if the index was not in the initial vector.
     *
     * @param index index in the initial vector
     * @return the value in the initialization vector at the entry index
     */
    public abstract Set<T> getInitialValue(State index);

    /**
     * Returns the value in the vector indexed by the index state. Complexity: O(1),
     * not amortized.
     *
     * @param indexState index in the form of a State
     * @return corresponding value
     */
    public Set<T> get(State indexState) {
        return vector.get(indexState.getIntIndex()).getAntichain();
    }

    /**
     * Copies a selected set of entries from another vector.
     *
     * @param toCopy        vector to copy
     * @param entriesToCopy set of entries to copy
     */
    public void copyEntries(BAIncVector<T> toCopy, Set<State> entriesToCopy) {
        removeMissingElements(toCopy, entriesToCopy);
        addNewElements(toCopy, entriesToCopy);
    }

    private void removeMissingElements(BAIncVector<T> toCopy, Set<State> entriesToCopy) {
        entriesToCopy.stream()
                .forEach(index -> get(index).removeIf(setOfStates -> !toCopy.get(index).contains(setOfStates)));
    }

    public void addNewElements(BAIncVector<T> toCopy, Set<State> entriesToCopy) {
        entriesToCopy.stream().forEach(index -> get(index).addAll(toCopy.get(index)));
    }

    /**
     * Computes one iteration of the function P_{1,A} or P_{2,A} (depending on the
     * type of the elements) when applied to the vector at the previous iteration.
     * Assumes that this object and the vector at the previous iteration are the
     * same vector.
     *
     * @param vectorAtPreviousIteration the input vector
     * @param frontier                  the set of states that have a predecessor
     *                                  tha changed during the last iteration
     * @return the set of entries of the vector that changed during this iteration
     */
    public Set<State> iterate(BAIncVector<T> vectorAtPreviousIteration, Set<State> frontier) {
        Set<State> changedStates = new HashSet<>();
        for (State stateInFrontier : frontier) {
            BAIncVectorEntry<T> entryToUpdate = vector.get(stateInFrontier.getIntIndex());
            boolean entryModified = entryToUpdate.applyIteration(vectorAtPreviousIteration);
            if (entryModified)
                changedStates.add(entryToUpdate.indexInVector());
        }
        return changedStates;
    }

    /**
     * Updates the inner frontiers of the entries of the vector.
     *
     * @param vectorAfterIteration is the vector after applying the iterate method
     * @param changedEntries       the set of states that changed during the last
     *                             iteration
     */
    public void updateInnerFrontiers(BAIncVector<T> vectorAfterIteration, Set<State> changedEntries) {
        for (BAIncVectorEntry<T> entryToUpdate : vector)
            if (changedEntries.contains(entryToUpdate.indexInVector()))
                updateInnerFrontierOfEntryThatChanged(entryToUpdate, vectorAfterIteration);
            else
                updateInnerFrontierOfEntryThatDidNotChange(entryToUpdate);
    }

    /**
     * Updates the inner frontier of entryToUpdate, which is an entry that changed
     * during last iteration. The new inner frontier for the entry is the set of
     * elements that were added during the last iteration.
     *
     * @param entryToUpdate        entry in the vector that has to be updated
     * @param vectorAfterIteration the vector after applying the iterate method
     */
    private void updateInnerFrontierOfEntryThatChanged(BAIncVectorEntry<T> entryToUpdate,
            BAIncVector<T> vectorAfterIteration) {
        int entryIndex = entryToUpdate.indexInVector().getIntIndex();
        BAIncVectorEntry<T> entryAfterIteration = vectorAfterIteration.vector.get(entryIndex);
        entryToUpdate.clearInnerFrontier();
        entryToUpdate.addNewElementsToTheFrontier(entryAfterIteration);
    }

    private void updateInnerFrontierOfEntryThatDidNotChange(BAIncVectorEntry<T> entryToUpdate) {
        entryToUpdate.clearInnerFrontier();
    }

    /**
     * @return the total number of elements in the inner frontiers over all entries
     *         in the vector
     */
    public int innerFrontiersTotalSize() {
        int total = 0;
        for (var vectorEntry : vector)
            total += vectorEntry.innerFrontierSize();
        return total;
    }

    /**
     * @return the total number of elements in the entries of the vector
     */
    public long totNumberOfElementsInEntries() {
        long sum = 0;
        for (var vectorEntry : vector)
            sum += vectorEntry.antichainSize();
        return sum;
    }

    /**
     * @param other            the other vector to compare with
     * @param entriesToCompare the set of indices to compare between the two vectors
     * @param comparator       the comparator to decide when one element is lesser
     *                         or equal than another
     * @return true iff the entries in entriesToCompare of the two vectors are
     *         comparable according to the comparator
     */
    public boolean entriesAreComparable(BAIncVector<T> other, Set<State> entriesToCompare,
            PartialComparator<T> comparator) {
        return entriesToCompare.stream()
                .allMatch(indexToCompare -> valuesInEntryAreSubsumed(indexToCompare, other, comparator));
    }

    /**
     * @param indexToCompare index for which we have to compare the entries
     * @param other          the other vector to compare with
     * @param comparator     the comparator to decide when one element is lesser or
     *                       equal than another
     * @return true iff the values in the index of this vector are subsumed by the
     *         values in the index of the other vector according to the comparator
     */
    private boolean valuesInEntryAreSubsumed(State indexToCompare, BAIncVector<T> other,
            PartialComparator<T> comparator) {
        Set<T> myEntryValues = get(indexToCompare);
        Set<T> itsEntryValues = other.get(indexToCompare);
        return myEntryValues.stream()
                .allMatch(myValue -> thereExistsOneGreaterElement(myValue, itsEntryValues, comparator));
    }

    /**
     * @param element    element to compare to elements
     * @param elements   set of elements to compare
     * @param comparator comparator that decides when one element is lesser or equal
     *                   than another
     * @return true iff exists one element e in elements such that element is lesser
     *         or equal than e
     */
    private boolean thereExistsOneGreaterElement(T element, Set<T> elements, PartialComparator<T> comparator) {
        return elements.stream().anyMatch(elementInSet -> comparator.lesserOrEqual(element, elementInSet));
    }

    protected BAIncVector(BuchiAutomaton a, BuchiAutomaton b) {
        this.a = a;
        this.b = b;
        vector = new ArrayList<>(a.numberOfStates());
    }

    @Override
    public String toString() {
        // We are using single letters separator (;:→) so it is easy to use with tr
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        Iterator<BAIncVectorEntry<T>> it = vector.iterator();
        while (it.hasNext()) {
            BAIncVectorEntry<T> vecEntry = it.next();
            // We printing values for final states only
            if (vecEntry.indexInVector().isFinal()) {
                builder.append(vecEntry.indexInVector());
                builder.append("→#");
                builder.append(vecEntry.antichainSize());
                // Not interested in content but size ATM;
                builder.append("=");
                builder.append(vecEntry.toString());
                if (it.hasNext())
                    builder.append("; ");
            }
        }
        builder.append(')');
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
        result = prime * result + ((vector == null) ? 0 : vector.hashCode());
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
        try {
            BAIncVector<T> other = (BAIncVector<T>) obj;
            if (vector == null) {
                return other.vector == null;
            } else
                return vector.equals(other.vector);
        } catch (ClassCastException e) {
            return false;
        }
    }
}
