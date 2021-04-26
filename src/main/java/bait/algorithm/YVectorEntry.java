package bait.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bait.automata.State;
import bait.automata.Symbol;
import bait.comparator.PartialComparator;
import bait.collections.Pair;

public final class YVectorEntry extends BAIncVectorEntry<Pair<Map<State, Set<State>>, Map<State, Set<State>>>> {

    public YVectorEntry(State index,
            PartialComparator<Pair<Map<State, Set<State>>, Map<State, Set<State>>>> comparator) {
        super(index, comparator);
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    protected Pair<Map<State, Set<State>>, Map<State, Set<State>>> getNewElement(
            Pair<Map<State, Set<State>>, Map<State, Set<State>>> predecessorElementInPre, Symbol predecessorSymbol) {
        Map<State, Set<State>> newFst = getNewFst(predecessorElementInPre, predecessorSymbol);
        Map<State, Set<State>> newSnd = getNewSnd(predecessorElementInPre, predecessorSymbol);
        return Pair.of(newFst, newSnd);
    }

    private Map<State, Set<State>> getNewFst(
            Pair<Map<State, Set<State>>, Map<State, Set<State>>> predecessorElementInPre, Symbol predecessorSymbol) {
        Map<State, Set<State>> newFst = new HashMap<>();
        for (State keyInY1 : predecessorElementInPre.fst().keySet()) {
            Set<State> elementsRelatedToKey = predecessorElementInPre.fst().get(keyInY1);
            Set<State> newElementsRelatedToKey = new HashSet<>();
            for (var element : elementsRelatedToKey)
                newElementsRelatedToKey.addAll(element.post(predecessorSymbol));
            if (!newElementsRelatedToKey.isEmpty())
                newFst.put(keyInY1, newElementsRelatedToKey);
        }
        return newFst;
    }

    private Map<State, Set<State>> getNewSnd(
            Pair<Map<State, Set<State>>, Map<State, Set<State>>> predecessorElementInPre, Symbol predecessorSymbol) {
        Map<State, Set<State>> newSnd = new HashMap<>();
        for (State keyInY1 : predecessorElementInPre.fst().keySet()) {
            Set<State> elementsRelatedToKeyInY1 = predecessorElementInPre.fst().get(keyInY1);
            Set<State> newElementsRelatedToKeyInY1 = new HashSet<>();
            for (var element : elementsRelatedToKeyInY1)
                newElementsRelatedToKeyInY1.addAll(element.postF(predecessorSymbol));
            if (!newElementsRelatedToKeyInY1.isEmpty())
                newSnd.put(keyInY1, newElementsRelatedToKeyInY1);
        }
        for (State keyInY2 : predecessorElementInPre.snd().keySet()) {
            Set<State> elementsRelatedToKeyInY2 = predecessorElementInPre.snd().get(keyInY2);
            Set<State> newElementsRelatedToKeyInY2 = new HashSet<>();
            for (var element : elementsRelatedToKeyInY2)
                newElementsRelatedToKeyInY2.addAll(element.post(predecessorSymbol));
            if (newSnd.containsKey(keyInY2)) {
                // one element in y1 had this key
                if (!newElementsRelatedToKeyInY2.isEmpty()) {
                    newSnd.get(keyInY2).addAll(newElementsRelatedToKeyInY2);
                }
            } else {
                if (!newElementsRelatedToKeyInY2.isEmpty())
                    newSnd.put(keyInY2, newElementsRelatedToKeyInY2);
            }
        }
        return newSnd;
    }

}
