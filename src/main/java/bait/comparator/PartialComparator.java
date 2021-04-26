package bait.comparator;

public interface PartialComparator<T> {

    /**
     * Compares two elements using a *partial* order, meaning that it could happen
     * that t1 is not lesser or equal to t2 and t2 is not lesser or equal to t1.
     *
     * @param t1 the first element to compare
     * @param t2 the second element to compare
     * @return t1 <= t2
     */
    boolean lesserOrEqual(T t1, T t2);

}
