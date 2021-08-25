package external.opmode;

import external.webinterface.WebInterface;
import external.util.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractOptionsOpMode extends AbstractTeleOp {

    // This probably won't work with the ftc app, since telemetry is not in mono (settings maybe?)
    protected final int LINES = 11; // Precondition: Amount of lines must always be odd
    protected final int LINE_WIDTH = 80;
    private final int START_OFFSET = (LINES - 1) / 2;
    private final int leftSpacing;

    private final File physicalFile;
    private final OptionsFile file;

    private final Enum<?>[] optionsList;
    private final Map<Enum<?>, Object> optionsMap;

    private int selected;

    // I don't like this system. However, it makes writing an OptionEntries enum pain-free.

    // It's difficult to create a generalized (human-readable) enum serializer/deserializer by strings,
    // since, as far as I know, to deserialize you would have to iterate through all possible
    // enumerated values, then compare their string values to see if they are the same. However,
    // to be able to iterate through all possible enumerated values, you need to create a custom
    // converter for that specific enum class. This is impractical to do with generics within the
    // BasicConverters class, since you'd have to have knowledge about the enum class before it was
    // created.

    // The current solution moves the serialize/deserialize implementation into the OptionEntries.
    // When an enum is registered as an OptionEntry, a converter is automatically generated for it
    // (see OptionEntries::enumType(Class<T extends Enum<?>) )
    // Then, when an OptionEntry provides a custom converter, it is used in place of the default converter.
    // This code requires null checking, so if it is kept this way it will probably degrade into
    // legacy hell.

    // POSSIBLE SOLUTIONS:

    // Have a global enum registry
    // I really don't like this solution, because it makes it difficult to deal with overlap
    // of names between two different enums, which shouldn't be a problem anyway. It also makes a
    // bunch of redundant checks necessary.

    // Overload an AbstractOptionsOpMode.converter variable
    // This is what I mean by the solution below being "pain-free". Here, you only have to register
    // the enum once (in the OptionEntries implementation), and not worry about it again. With this
    // solution, you'd have to register the enum twice, once to use it as an option, and once as a
    // serialize/deserialize target when you extend the Converters class.

    // Use a custom converter designed specifically for OptionsOp
    // This makes the converter itself less flexible (maybe will pose problems extending it), but I
    // think this is the best option. It would, however, introduce even more unfamiliar api when making
    // a custom converter.
    private Object load(Enum<?> option) {
        Converter c = asTypeData(option).converter;
        if (c != null) {
            Object out = c.fromString(file.getValues().get(option.name()));
            if (out == null) return asTypeData(option).fallback;
            return out;
        } else {
            return file.get(option.name(), asTypeData(option).fallback);
        }
    }

    private void store(Enum<?> option) {
        Converter c = asTypeData(option).converter;
        Object storing = optionsMap.get(option);
        if (c != null) {
            file.getValues().put(option.name(), c.toString(storing));
        } else {
            Class<?> cl = asTypeData(option).type;
            file.set(option.name(), cl.cast(storing));
        }
    }

    private void loadAll() {
        optionsMap.clear();
        for (Enum<?> option : optionsList) {
            optionsMap.put(option, load(option));
        }
    }

    private void storeAll() {
        WebInterface.out.println(optionsMap);
        for (Enum<?> option : optionsList) {
            store(option);
            WebInterface.out.println(optionsMap.get(option));
        }
        file.writeToFile(physicalFile);
    }

    private static OptionEntries.TypeData asTypeData(Enum<?> o) {
        return ((OptionEntries) o).getData();
    }

    protected void display() {
        int base = selected - START_OFFSET;
        for (int i = 0; i < LINES; i++) {
            displayLine(base + i);
        }
    }

    private void displayLine(int index) {

        if (index < 0 || index >= optionsList.length) {
            WebInterface.out.println();
            return;
        }

        Enum<?> option = optionsList[index];
        String value = optionsMap.get(option).toString(); // TODO: Use converters here instead of Object::toString

        if (index == selected) {

            WebInterface.out.printf(
                    ">%-" + leftSpacing + "s%s%n",
                    option.name(),
                    Utility.center(String.format("< %s >", value), LINE_WIDTH - leftSpacing)
            );

        } else {

            WebInterface.out.printf(
                    " %-" + leftSpacing + "s%s%n",
                    option.name(),
                    Utility.center(value, LINE_WIDTH - leftSpacing)
            );

        }
    }

    protected AbstractOptionsOpMode(String filename, Class<? extends Enum<?>> options) {
        this.optionsList = options.getEnumConstants();

        // determine the necessary amount of spacing to display comfortably
        int longest = 0;
        for (Enum<?> e : optionsList) {
            longest = Math.max(longest, e.name().length());
        }
        leftSpacing = longest - (longest % 4) + 4 + 4; // add an extra 4 on there to make sure there's definitely space between name and option

        physicalFile = new File(filename);
        try {
            if (physicalFile.createNewFile()) {
                System.out.println("Past options were not found. Generating..");
            }
        } catch (IOException e) {
            // ignore for now
            e.printStackTrace();
        }
        file = new OptionsFile(BasicConverters.getInstance(), physicalFile);

        optionsMap = new HashMap<>();
    }

    @Override
    protected Logger createLogger() {
        return null;
    }

    @Override
    protected void setup() {
        loadAll();
    }

    @Override
    protected void setup_act() {
        // ignore
    }

    @Override
    protected void go() {
        display();
    }

    @Override
    protected void act() {

        if (driver1.justTriggered()) {

            // selecting
            if (driver1.dpad_up.justPressed()) {
                selected = Utility.limit(--selected, 0, optionsList.length - 1);
            }
            if (driver1.dpad_down.justPressed()) {
                selected = Utility.limit(++selected, 0, optionsList.length - 1);
            }

//            WebPrint.out.printf("Currently selecting: %s on index %d\n", optionsList[selected], selected);

            // mutating each entry
            Enum<?> mutating = optionsList[selected];
            // debugging entry
//            WebPrint.out.printf("old %s: %s\n", mutating.name(), optionsMap.get(mutating));

            Object next = asTypeData(mutating).mutator.mutate(driver1, optionsMap.get(mutating));
            if (next != null) {
                optionsMap.put(mutating, next);
//                WebPrint.out.printf("new %s: %s\n", mutating.name(), optionsMap.get(mutating));
            }

            display();

        }

    }

    @Override
    protected void end() {
        storeAll();
    }
}
