package external.util;

import java.util.ArrayList;
import java.util.List;

public class CircularArrayList<T> extends ArrayList<T> implements List<T> {

    private int index;

    public T current() {
        return get(index);
    }

    public T next() {
        if (++index >= size()) {
            index = 0;
        }

        return get(index);
    }

    public T prev() {
        if (--index < 0) {
            index = size() - 1;
        }

        return get(index);
    }

}
