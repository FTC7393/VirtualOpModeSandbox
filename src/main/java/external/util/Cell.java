package external.util;

public class Cell<T> {
    T value;

    public Cell (T in) {
        value = in;
    }

    public T get () {
        return value;
    }

    public void set(T in) {
        value = in;
    }
}
