package external.util;

import external.gamepad.GamepadManager;
import external.webinterface.WebInterface;

import java.util.Arrays;
import java.util.Collections;

public interface OptionEntries {

    interface Mutator<T> {
        T mutate(GamepadManager gamepad, T value);
    }

    class TypeData<T> {

        public Class<T> type;
        public T fallback;
        public Mutator<T> mutator;
        public Converter<T> converter;

        // boolean (none for now)

        // integer
        int step;
        int min;
        int max;

        // enum
        CircularArrayList<Enum<?>> variants;

        public TypeData<T> withFallback(T fallback) {
            this.fallback = fallback;
            return this;
        }

        public TypeData<T> withMutator(Mutator<T> mutator) {
            this.mutator = mutator;
            return this;
        }

        public TypeData<T> withConverter(Converter<T> converter) {
            this.converter = converter;
            return this;
        }

        public static TypeData<Object> blankType() {
            TypeData<Object> t = new TypeData<>();
            t.type = Object.class;

            return t.withMutator((g, value) -> null);
        }

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

    TypeData<?> getData();

}
