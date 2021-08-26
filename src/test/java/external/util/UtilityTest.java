package external.util;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilityTest {

    @Test
    void limit() {

        // manual tests
        assertEquals(Utility.limit(5, 0, 10), 5);
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

    @Test
    void optionsFile() throws IOException, ParseException {

        String optionsPath = "options-test-OptionFileClass";
        String[] checkArray = new String[]{"1", "2", "3", "4"};

        OptionsFile f = new OptionsFile(BasicConverters.getInstance(), new File(optionsPath));

        f.setArray("bruh", checkArray);
        f.set("bruh2", "bruh3");
        assert(f.writeToFile());

        OptionsFile g = new OptionsFile(BasicConverters.getInstance(), new File(optionsPath));
        String[] arrayElems = g.getArray("bruh", String.class);
        assertArrayEquals(checkArray, arrayElems);
        assertEquals(g.get("bruh2", String.class), "bruh3");

    }

}