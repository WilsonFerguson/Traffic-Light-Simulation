package library.core;

public class Option<T> {
    private T value;

    public Option(T value) {
        this.value = value;
    }

    public Option() {
        this.value = null;
    }

    /**
     * Returns the value of this option if it is {@code Some(value)}. Throws an
     * exception if it is {@code None}.
     */
    public T unwrap() {
        if (this.value == null) {
            throw new RuntimeException("Called unwrap() on a None value");
        }
        return this.value;
    }

    /**
     * Returns the value of this option if it is {@code Some(value)}. Returns the
     * given input if it is {@code None}.
     */
    public T unwrapOr(T other) {
        if (this.value == null) {
            return other;
        }
        return this.value;
    }

    /**
     * Replaces the old value of this option with the given value.
     */
    public void insert(T value) {
        this.value = value;
    }

    /**
     * Returns true if the value is {@code None}, otherwise false.
     */
    public boolean isNone() {
        return this.value == null;
    }

    /**
     * Returns true if the value is {@code Some(value)}, otherwise false.
     */
    public boolean isSome() {
        return this.value != null;
    }
}
