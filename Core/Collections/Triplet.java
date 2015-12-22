package scripts.LanAPI.Core.Collections;

/**
 * @author Laniax
 */
public class Triplet<T, U, V> {

    final T a;
    final U b;
    final V c;

    public Triplet(T a, U b, V c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public T getA() {
        return a;
    }

    public U getB() {
        return b;
    }

    public V getC() {
        return c;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Triplet))
            return false;

        Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) o;

        return this.a.equals(other.a) && this.b.equals(other.b) && this.c.equals(other.c);
    }
}
