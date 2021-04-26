package bait.collections;

public final class Pair<A, B> {
    private final A fst;
    private final B snd;

    public A fst() {
        return this.fst;
    }

    public B snd() {
        return this.snd;
    }

    public static <A, B> Pair<A, B> of(A fst, B snd) {
        return new Pair<>(fst, snd);
    }

    private Pair(A a, B b) {
        this.fst = a;
        this.snd = b;
    }

    @Override
    public String toString() {
        return "(" + fst() + ',' + snd() + ')';
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (!fst.equals(pair.fst))
            return false;
        return snd.equals(pair.snd);
    }

    @Override
    public int hashCode() {
        return 31 * fst.hashCode() + snd.hashCode();
    }

}
