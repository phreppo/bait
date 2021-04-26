package bait.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class provides useful algorithm, especially to deal with the
 * representation as maps of the set of pairs. For efficiency reasons, we
 * represent the set of pairs as maps.
 */
public final class Algorithms {

    /**
     * @param <T>       the type of the elements
     * @param firstSet
     * @param secondSet
     * @return true iff the first set is a subset of the second
     */
    public static <T> boolean isSubset(Map<T, Set<T>> firstSet, Map<T, Set<T>> secondSet) {
        for (Map.Entry<T, Set<T>> entry : firstSet.entrySet()) {
            if (!secondSet.containsKey(entry.getKey()))
                return false;
            if (!secondSet.get(entry.getKey()).containsAll(entry.getValue()))
                return false;
        }
        return true;
    }

    /**
     * @param <T>       the type of the elements in the sets
     * @param firstSet
     * @param secondSet
     * @return the union of the two sets
     */
    public static <T> Map<T, Set<T>> union(Map<T, Set<T>> firstSet, Map<T, Set<T>> secondSet) {
        Map<T, Set<T>> union = new HashMap<>(firstSet);
        for (Map.Entry<T, Set<T>> entry : secondSet.entrySet())
            union.computeIfAbsent(entry.getKey(), k -> new HashSet<>()).addAll(entry.getValue());
        return union;
    }

    /**
     * @param <T>       the type of the elements in the sets
     * @param firstSet
     * @param secondSet
     * @return the composition of the two sets
     */
    public static <T> Map<T, Set<T>> compose(Map<T, Set<T>> firstSet, Map<T, Set<T>> secondSet) {
        Map<T, Set<T>> composition = new HashMap<>();
        for (Map.Entry<T, Set<T>> entry : firstSet.entrySet()) {
            Set<T> newElementsRelatedToKey = new HashSet<>();
            for (T elementRelatedToKey : entry.getValue())
                if (secondSet.containsKey(elementRelatedToKey))
                    newElementsRelatedToKey.addAll(secondSet.get(elementRelatedToKey));
            if (!newElementsRelatedToKey.isEmpty())
                composition.put(entry.getKey(), newElementsRelatedToKey);
        }
        return composition;
    }

    /**
     * @param <T>      the type of the elements in the set
     * @param relation
     * @return the transitive closure of the relation
     */
    public static <T> Map<T, Set<T>> transitiveClosure(Map<T, Set<T>> relation) {
        Map<T, Set<T>> transitiveClosure = new HashMap<>(relation);
        Map<T, Set<T>> transitiveClosurePost = new HashMap<>(); // new iteration of the transitive closure algorithm
        boolean reachedFixpoint = false;
        while (!reachedFixpoint) {
            boolean addedElementsDuringIteration = transitiveClosureIteration(transitiveClosure, transitiveClosurePost);
            if (!addedElementsDuringIteration)
                reachedFixpoint = true;
        }
        return transitiveClosure;
    }

    private static <T> boolean transitiveClosureIteration(Map<T, Set<T>> transitiveClosure,
            Map<T, Set<T>> transitiveClosurePost) {
        boolean addedElements = false;
        for (Map.Entry<T, Set<T>> entry : transitiveClosure.entrySet()) {
            Set<T> newElementsRelatedToKey = new HashSet<>();
            Set<T> elementsAlreadyInTransitiveClosure = entry.getValue();
            for (T elementRelatedToKey : entry.getValue()) {
                newElementsRelatedToKey.add(elementRelatedToKey);
                if (transitiveClosure.containsKey(elementRelatedToKey)) {
                    for (T elementRelatedToElementRelatedToKey : transitiveClosure.get(elementRelatedToKey)) {
                        // and then we add each element related to `elementRelatedToKey`
                        newElementsRelatedToKey.add(elementRelatedToElementRelatedToKey);
                        if (!elementsAlreadyInTransitiveClosure.contains(elementRelatedToElementRelatedToKey))
                            // we added something new
                            addedElements = true;
                    }
                }
            }
            transitiveClosurePost.put(entry.getKey(), newElementsRelatedToKey);
        }
        transitiveClosure.putAll(transitiveClosurePost);
        return addedElements;
    }

    private Algorithms() {
    }

}
