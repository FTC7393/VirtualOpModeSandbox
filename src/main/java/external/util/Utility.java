package external.util;

import java.util.Collection;

/**
 * This file was made by the electronVolts, FTC team 7393 Date Created: 2/17/16
 */
public class Utility {

    public static String center(String in, int width) {
        StringBuilder out = new StringBuilder();
        int padLength = width - in.length();

        if (padLength < 0) {
            return in;
        } else if (padLength % 2 == 0) {
            out.append(" ".repeat(padLength / 2));
            out.append(in);
            out.append(" ".repeat(padLength / 2));
        } else {
            out.append(" ".repeat(padLength / 2 + 1));
            out.append(in);
            out.append(" ".repeat(padLength / 2));
        }

        return out.toString();
    }

    /**
     * limits the value to be between a and b
     *
     * @param input the value to be limited
     * @param a one end of the range
     * @param b the other end of the range
     * @return the input limited to the range between a and b
     */
    public static <T extends Comparable<T>> T limit(T input, T a, T b) {
        if (a.equals(b)) return a; // if the ends of the range are equal

        // set min and max to a and b, making sure that min < max
        T min = a, max = b;
        if (a.compareTo(b) > 0) {
            min = b;
            max = a;
        }

        // limit the input to be min < input < max
        if (input.compareTo(max) > 0) return max;
        if (input.compareTo(min) < 0) return min;
        return input;
    }

    /**
     * a limit function where min = -max
     *
     * @param input the value to be limited
     * @param max the max absolute value
     * @return the input limited to be between -max to max
     */
    public static double mirrorLimit(double input, double max) {
        return limit(input, -max, max);
    }

    /**
     * Limiting function for motor power
     *
     * @param input the value to be limited
     * @return the input limited to the range from -1 to 1
     */
    public static double motorLimit(double input) {
        return mirrorLimit(input, 1);
    }

    /**
     * Limiting function for servo positions
     *
     * @param input the value to be limited
     * @return the input limited to the range from 0 to 1
     */
    public static double servoLimit(double input) {
        return limit(input, 0D, 1D);
    }

    /**
     * Join a list of objects with a separator
     * 
     * @param list the list of values
     * @param separator the String to put between the values
     * @return a String of the joined items
     */
    public static <T> String join(Collection<T> list, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (T item : list) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * Join an array of booleans with a separator
     * 
     * @param array the array of values
     * @param separator the String to put between the values
     * @return a String of the joined items
     */
    public static String join(boolean[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (boolean item : array) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * Join an array of bytes with a separator
     * 
     * @param array the array of values
     * @param separator the String to put between the values
     * @return a String of the joined items
     */
    public static String join(byte[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (byte item : array) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * Join an array of chars with a separator
     * 
     * @param array the array of values
     * @param separator the String to put between the values
     * @return a String of the joined items
     */
    public static String join(char[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (char item : array) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * Join an array of shorts with a separator
     * 
     * @param array the array of values
     * @param separator the String to put between the values
     * @return a String of the joined items
     */
    public static String join(short[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (short item : array) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * Join an array of ints with a separator
     * 
     * @param array the array of values
     * @param separator the String to put between the values
     * @return a String of the joined items
     */
    public static String join(int[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int item : array) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * Join an array of longs with a separator
     * 
     * @param array the array of values
     * @param separator the String to put between the values
     * @return a String of the joined items
     */
    public static String join(long[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (long item : array) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * Join an array of floats with a separator
     * 
     * @param array the array of values
     * @param separator the String to put between the values
     * @return a String of the joined items
     */
    public static String join(float[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (float item : array) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * Join an array of doubles with a separator
     * 
     * @param array the array of values
     * @param separator the String to put between the values
     * @return a String of the joined items
     */
    public static String join(double[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (double item : array) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

}
