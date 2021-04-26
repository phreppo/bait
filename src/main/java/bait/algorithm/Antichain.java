package bait.algorithm;

import java.util.HashSet;
import java.util.Set;

import bait.comparator.PartialComparator;

/**
 * Antichain represents a set of elements for which holds: for each x in the
 * set, for each y in the set, x is not lesser or equal to y and y is not lesser
 * or equal to x. To compare two elements a partial comparator is used.
 *
 * @param <T> type of the elements
 */
public final class Antichain<T> {

    private final Set<T> elements;
    private final PartialComparator<T> comparator;

    public Antichain(PartialComparator<T> comparator) {
        elements = new HashSet<>();
        this.comparator = comparator;
    }

    public Set<T> elements() {
        return elements;
    }

    public int size() {
        return elements.size();
    }

    /**
     * Greatest lower bound. Returns true iff the antichain gets modified.
     *
     * @param newElement the element to add to the antichain
     * @return true iff the antichain was modified.
     */
    public boolean glbWith(T newElement) {
        boolean removedElements = removeStrictlyGreaterElements(newElement);
        boolean addedElement = addIfDoesNotExistsSmallerElement(newElement);
        return removedElements || addedElement;
    }

    /**
     * Least upper bound. Returns true iff the antichain gets modified.
     *
     * @param newElement the element to add to the antichain
     * @return true iff the antichain was modified.
     */
    public boolean lubWith(T newElement) {
        boolean removedElements = removeStrictlySmallerElements(newElement);
        boolean addedElement = addIfDoesNotExistsGreaterElement(newElement);
        return removedElements || addedElement;
    }

    /**
     * @param newElement the element to be compared
     * @return true iff some elements were removed
     */
    private boolean removeStrictlyGreaterElements(T newElement) {
        int sizeBefore = elements.size();
        elements.removeIf(
                setOfStates -> !setOfStates.equals(newElement) && comparator.lesserOrEqual(newElement, setOfStates));
        return elements.size() < sizeBefore;
    }

    /**
     * @param newElement the element to be compared
     * @return true iff some elements were removed
     */
    private boolean removeStrictlySmallerElements(T newElement) {
        int sizeBefore = elements.size();
        elements.removeIf(
                setOfStates -> !setOfStates.equals(newElement) && comparator.lesserOrEqual(setOfStates, newElement));
        return elements.size() < sizeBefore;
    }

    /**
     * @param newElement the element to add
     * @return true iff the new element is added
     */
    private boolean addIfDoesNotExistsSmallerElement(T newElement) {
        boolean existsSmallerElement = elements.stream()
                .anyMatch(setOfStates -> comparator.lesserOrEqual(setOfStates, newElement));
        if (!existsSmallerElement) {
            elements.add(newElement);
            return true;
        } else
            return false;
    }

    /**
     * @param newElement the element to add
     * @return true iff the new element is added
     */
    private boolean addIfDoesNotExistsGreaterElement(T newElement) {
        boolean thereIsSomethingGreater = elements.stream()
                .anyMatch(setOfStates -> comparator.lesserOrEqual(newElement, setOfStates));
        if (!thereIsSomethingGreater) {
            elements.add(newElement);
            return true;
        } else
            return false;
    }
}
