package github.meloweh.wolfcompanion.util;

public class Pair<F, S> {
    public final F first;
    public final S second;

    protected Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Creates a pair object
     * @param first must be non-null
     * @param second must be non-null
     * @return The pair object.
     */
    public static <F, S> Pair<F, S> of(F first, S second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Pair.of requires non null values.");
        }
        return new Pair<F, S>(first, second);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Pair)) {
            return false;
        }
        Pair<?, ?> rhs = (Pair<?, ?>) other;
        return first.equals(rhs.first) && second.equals(rhs.second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() * 37 + second.hashCode();
    }
}
