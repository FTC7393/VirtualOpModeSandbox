package external.util;

import external.gamepad.GamepadManager;
import external.webinterface.WebInterface;

import java.util.Arrays;
import java.util.Collections;

/**
 * This is a helper interface for the AbstractOptionsOpMode. By implementing this interface
 * on an enum, you can create and run an implementation of an AbstractOptionsOpMode.<br><br>
 * <p>
 * Here is an example of an implementation of {@code OptionEntries}:
 * <pre>{@code
 * enum EElricOptions extends OptionEntries {
 *     HEIGHT(TypeData.integerType(1, 139, 149).withFallback(149)),
 *     EXTENDED(TypeData.booleanType().withFallback(false));
 *
 *     TypeData<?> data;
 *
 *     Options(TypeData<?> data) {
 *         this.data = data;
 *     }
 *
 *     public TypeData<?> getData() {
 *         return this.data;
 *     }
 * }
 * }</pre>
 * <p>
 * This implementation contains two settable options, {@code HEIGHT} and {@code EXTENDED}, an
 * {@code Integer} and {@code Boolean} respectively. To learn more about how to create TypeData
 * instances, look at the TypeData documentation.
 * To run this inside an opmode, you can do this:
 *
 * <pre>{@code
 * class EElricOptionsOpMode extends AbstractOptionsOpMode {
 *
 *     public OptionsOpMode() {
 *         super("filename", EElricOptions.class);
 *     }
 *
 * }
 * }</pre>
 * <p>
 * Or use your preferred method of setting the file which the OpMode will write to.
 * See the {@link external.opmode.AbstractOptionsOpMode#AbstractOptionsOpMode AbstractOptionsOpMode constructor}
 * for more details on how to do that.
 *
 * @see external.opmode.AbstractOptionsOpMode
 * @see TypeData
 */
public interface OptionEntries {

    interface Mutator<T> {
        T mutate(GamepadManager gamepad, T value);
    }

    /**
     * Representation of a type for use in {@link external.opmode.AbstractOptionsOpMode}
     * <br><br>
     * This class presents to {@code AbstractOptionsOpMode} instructions for how to manipulate a type
     * which is registered in an implementation of {@link OptionEntries}. When you register the TypeData,
     * it will be associated to a option. This option is set to a certain value, which will be called the
     * associated value. For example, in order to set a value as a fallback, you can do the following:
     * <pre>{@code
     * enum Kaeri {
     *     GOHAN,
     *     FURO,
     *     WATASHI,
     * }
     *
     * TypeData.enumType(Kaeri.class).withFallback(Kaeri.GOHAN);
     * }</pre>
     * In other words, it's a thing which tells you how to do the thing on the
     * thing that you put in the thing. An associated thing, or metathing, if you will.
     * <br><br>
     * For ease of use, factory methods have been included in this class to generate TypeData for commonly
     * used types. You can see them in the method list below. It is structurally allowed, but not recommended, to change
     * the fields by direct access. Instead, you should build it incrementally using the with* functions provided.
     *
     * @param <T> The type which TypeData represents
     */
    class TypeData<T> {

        /**
         * An instance of {@code Class<T>} for easy access in {@code AbstractOptionsOpMode}
         */
        public Class<T> type;
        /**
         * The value to which the option will revert if it couldn't find a value in a file.
         */
        public T fallback;
        /**
         * The function which determines how the option reacts to input.
         * See {@link #withMutator(Mutator)} for more details.
         */
        public Mutator<T> mutator;
        /**
         * The functions which determine how the option is serialized/deserialized.
         * See {@link #withConverter(Converter)} for more details.
         */
        public Converter<T> converter;

//        // boolean (none for now)
//
//        // integer
//        int step;
//        int min;
//        int max;
//
//        // enum
//        CircularArrayList<Enum<?>> variants;

        /**
         * Assigns a fallback value to use if there was no associated value before
         * <br><br>
         * WARNING: This method doesn't do any checks on the value you pass it, except what's
         * enforced by the java typing system. Any weird values you end up with are your own responsibility.
         *
         * @param fallback The value to use on fallback
         * @return The caller
         */
        public TypeData<T> withFallback(T fallback) { //TODO: Make this method unnecessary for factory types
            this.fallback = fallback;
            return this;
        }

        /**
         * Assigns a mutator on the associated value
         * <br><br>
         * TLDR, a mutator is just a function which changes the associated value. A mutator takes
         * the GamepadManager as the first argument, and the current associated value as the second argument.
         * It should return the mutated associated value. However, it also is allowed to return {@code null},
         * which means that there should be no change to the associated value.
         * <br><br>
         * If you used a factory method to make a predefined TypeData, you won't need to use this method,
         * since it had already been called internally. However, this method is still accessible if you need it.
         * <br><br>
         * WARNING: This method doesn't do any checks on the value you pass it, except what's
         * enforced by the java typing system. Any weird values you end up with are your own responsibility.
         *
         *
         * @param mutator The intended mutator
         * @return The caller
         */
        public TypeData<T> withMutator(Mutator<T> mutator) { //TODO: Decide if this should be done with Cells
            this.mutator = mutator;
            return this;
        }

        /**
         * Assigns a converter on the associated value
         * <br><br>
         * The converter is a serializer/deserializer which specializes in a certain type. If you want your
         * value to be displayed in a custom way, then you will want this function. If there is no converter
         * in the {@code Converters} impl that you passed to the AbstractOptionsOpMode, you will need this function.
         * <br><br>
         * If you used a factory method to make a predefined TypeData, you won't need to use this method,
         * since, if it was necessary, it had already been called internally. However, this method is still
         * accessible if you need it.
         * <br><br>
         * WARNING: This method doesn't do any checks on the value you pass it, except what's
         * enforced by the java typing system. Any weird values you end up with are your own responsibility.
         * @param converter The intended converter
         * @return The caller
         * @see Converters
         * @see Converter
         */
        public TypeData<T> withConverter(Converter<T> converter) {
            this.converter = converter;
            return this;
        }

        /** Generates a blank type
         *
         * used mostly for testing
         *
         * @deprecated
         * @return A {@code TypeData<Object>}
         */
        public static TypeData<Object> blankType() {
            TypeData<Object> t = new TypeData<>();
            t.type = Object.class;

            return t.withMutator((g, value) -> null);
        }

        /** Generates an integer type
         *
         * @param step The amount to step every trigger
         * @param min The least the option can be
         * @param max The most the option can be
         * @return A {@code TypeData<Integer>}
         */
        public static TypeData<Integer> integerType(int step, int min, int max) {
            TypeData<Integer> t = new TypeData<>();
            t.type = Integer.class;

//            t.step = step;
//            t.min = min;
//            t.max = max;

            return t
                    .withMutator((gamepad, value) -> {
                        if (gamepad.right_bumper.justPressed()) {
                            return Utility.limit(value + step, min, max);
                        } else if (gamepad.left_bumper.justPressed()) {
                            return Utility.limit(value - step, min, max);
                        } else {
                            return null;
                        }
                    });
        }

        /** Generates a boolean type
         *
         * @return A {@code TypeData<Boolean>}
         */
        public static TypeData<Boolean> booleanType() {
            TypeData<Boolean> t = new TypeData<>();
            t.type = Boolean.class;

            return t
                    .withMutator((gamepad, value) -> {
                        if (gamepad.right_bumper.justPressed() || gamepad.left_bumper.justPressed()) {
                            return !value;
                        }
                        return null;
                    });
        }

        /** Generates an enum type
         *
         * @param <T> The type of the enum
         * @param e The {@link Class} of {@code T}
         * @return A {@code TypeData<Enum<?>>}
         */
        public static <T extends Enum<?>> TypeData<Enum<?>> enumType(Class<T> e) {
            TypeData<Enum<?>> t = new TypeData<>();

            CircularArrayList<Enum<?>> variants = new CircularArrayList<>();
            Collections.addAll(variants, e.getEnumConstants());

            return t
                    .withMutator((gamepad, value) -> {
                        if (gamepad.right_bumper.justPressed()) {
                            return variants.next();
                        } else if (gamepad.left_bumper.justPressed()) {
                            return variants.prev();
                        } else {
                            return null;
                        }

                    })
                    .withConverter(new Converter<>() {
                        @Override
                        public String toString(Enum<?> object) {
                            return object.name();
                        }

                        @Override
                        public Enum<?> fromString(String string) {
                            for (Enum<?> _enum : e.getEnumConstants()) {
                                if (_enum.name().equals(string)) return _enum;
                            }
                            return null;
                        }
                    });
        }

    }

    /** Access function for data
     *
     * See the class documentation for an example for how to implement this.
     *
     * @return the associated {@code TypeData}
     */
    TypeData<?> getData();

}
