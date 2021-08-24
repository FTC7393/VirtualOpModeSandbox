package external.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void limit() {

        // manual tests
        Assertions.assertEquals(Utility.limit(5, 0, 10), 5);
        assertEquals(Utility.limit(-2, 5, 10), 5);
        assertEquals(Utility.limit(100, 40, 20), 40);

    }

    @Test
    void circularArrayList() {

        CircularArrayList<Boolean> c = new CircularArrayList<>();

        c.add(true);
        c.add(false);
        c.add(true);

        assert(c.current());
        assert(!c.next());
        assert(c.next());
        assert(c.next());
        assert(!c.next());
        assert(c.prev());
        assert(c.current());

    }

}