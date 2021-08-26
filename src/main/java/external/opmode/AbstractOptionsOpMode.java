package external.opmode;

import external.webinterface.WebInterface;
import external.util.*;

import java.io.File;
import java.io.IOException;

/**
 * A TeleOp for making persistent K-V settings.
 * <p>
 * When the TeleOp is run, a menu shows up, similar to the one shown here:
 * <p>
 * <img src="doc/example_optionsop_menu.png"/>
 * </p>
 * Use dpad up and down to navigate the menu. Changing values is implementation-dependent, but for
 * the pre-made types, use left and right bumper to modify values.
 * <p>
 * TODO: Include explanation of OptionEntries and type-value associations
 * </p>
 * All you need to implement for this class is a constructor. However, there are some exposed protected
 * fields that you can edit for increased functionality.
 * @see OptionEntries
 */
public abstract class AbstractOptionsOpMode extends AbstractTeleOp {

    // This probably won't work with the ftc app, since telemetry is not in mono (settings maybe?)
    /**
     * The amount of lines the output terminal has. For now, keep it odd (I think it still works otherwise).
     */
    protected final int LINES = 11; // Precondition: Amount of lines must always be odd
    /**
     * The width of a line on the output terminal
     */
    protected final int LINE_WIDTH = 80;
    private final int START_OFFSET = (LINES - 1) / 2;
    private final int leftSpacing;

    private final File physicalFile;
    private final OptionsFile file;

    private final Enum<?>[] optionsList;
//    private final Map<Enum<?>, Object> optionsMap;

    /**
     * The index of the option which is currently selected
     */
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

    /**
     * Load the selected option's associated value as the type specified in the OptionEntry's TypeData
     * @param option The option to load associated value with
     * @return The object from the internal map
     */
    private Object load(Enum<?> option) {
        Converter<?> c = asTypeData(option).converter;
        if (c != null) {
            Object out = c.fromString(file.getValues().get(option.name()));
            if (out == null) return asTypeData(option).fallback;
            return out;
        } else {
            return file.get(option.name(), asTypeData(option).fallback);
        }
    }

    /**
     * Load the selected option's associated value as a {@code String}
     * @param option The option to load associated value with
     * @return The {@code String} representation of the object from the internal map
     */
    private String loadRaw(Enum<?> option) {
        return file.getValues().get(option.name());
    }

    /**
     * Store the given object as the option's associated value
     * @param option The option to store with
     * @param value The value to store
     */
    private void store(Enum<?> option, Object value) {
        Converter c = asTypeData(option).converter;
//        Object storing = optionsMap.get(option);
        if (c != null) {
            file.getValues().put(option.name(), c.toString(value));
        } else {
            Class<?> cl = asTypeData(option).type;
            file.set(option.name(), cl.cast(value));
        }
    }

    /**
     * Helper function to make it easier to work with OptionEntry enums
     */
    private static OptionEntries.TypeData asTypeData(Enum<?> o) {
        return ((OptionEntries) o).getData();
    }

    /**
     * Helper function to display an entire page to the terminal
     */
    protected void display() {
        int base = selected - START_OFFSET;
        for (int i = 0; i < LINES; i++) {
            displayLine(base + i);
        }
    }

    /**
     * Display a certain line
     */
    private void displayLine(int index) {

        if (index < 0 || index >= optionsList.length) {
            WebInterface.out.println();
            return;
        }

        Enum<?> option = optionsList[index];
        String value = loadRaw(option);

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

    /**
     * Describes the options to the backend
     * <p>
     *     TODO: Explain better, maybe even don't use "backend"
     * </p>
     * @param optionsFilePath
     * @param options
     */
    protected AbstractOptionsOpMode(String optionsFilePath, Class<? extends Enum<?>> options) {
        this.optionsList = options.getEnumConstants();

        // determine the necessary amount of spacing to display comfortably
        int longest = 0;
        for (Enum<?> e : optionsList) {
            longest = Math.max(longest, e.name().length());
        }
        leftSpacing = longest - (longest % 4) + 4 + 4; // add an extra 4 on there to make sure there's definitely space between name and option

        physicalFile = new File(optionsFilePath);
        try {
            if (physicalFile.createNewFile()) {
                System.out.println("Past options were not found. Generating..");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new OptionsFile(BasicConverters.getInstance(), physicalFile);
    }

    @Override
    protected Logger createLogger() {
        return null;
    }

    @Override
    protected void setup() {
        // ignore
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

            // mutating each entry
            Enum<?> mutating = optionsList[selected];
            Object next = asTypeData(mutating).mutator.mutate(driver1, load(mutating));
            if (next != null) {
                store(mutating, next);
            }

            display();

        }

    }

    @Override
    protected void end() {
        file.writeToFile(physicalFile);
    }
}
