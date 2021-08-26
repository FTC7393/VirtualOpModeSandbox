package external.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * This file was made by the electronVolts, FTC team 7393
 * <p>
 * This class stores and retrieves values from a file. It should probably be
 * replaced by an XML or JSON interpreter.
 */
public class OptionsFile {

    private final Converters converters;

    private JSONObject values;
    private final File physicalFile;

    /**
     * @param converters the utilities that convert strings to and from objects
     * @param values     the map of values to be loaded
     * @param file       The file which will be written to (keep in mind that it will be overwritten, and this method will
     *                   not read it. It will be gone, reduced to atoms);
     * @throws IOException When the file couldn't be created
     */
    @SuppressWarnings("unused")
    public OptionsFile(Converters converters, Map<String, String> values, File file) throws IOException {
        this.physicalFile = file;
        this.converters = converters;

        physicalFile.delete();
        if (!physicalFile.createNewFile()) {
            throw new IOException("An options storage file was not found, but could not create a new one");
        }

        this.values = new JSONObject(values);
        writeToFile();

    }

    /**
     * retrieve an OptionsFile from a file
     *
     * @param converters the utilities that convert strings to and from objects
     * @param file       the file to read from
     * @throws IOException    When the file couldn't be created
     * @throws ParseException When the given file doesn't have a valid json format
     */
    @SuppressWarnings("unused")
    public OptionsFile(Converters converters, File file) throws IOException, ParseException {
        this.converters = converters;
        this.physicalFile = file;
        try {
            values = (JSONObject) new JSONParser().parse(new BufferedReader(new FileReader(file)));
        } catch (FileNotFoundException | ParseException e) {
            file.delete();
            if (!file.createNewFile()) {
                throw new IOException("An options storage file was not found, but could not create a new one");
            }
            values = new JSONObject();
        }
    }

    /**
     * store the values to a file
     *
     * @return whether or not it worked
     */
    public boolean writeToFile() {
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(physicalFile));
            values.writeJSONString(w);
            w.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Request a drop of all stored data, refer to data stored in file
     * @return Operation success
     */
    public boolean drop() {
        try {
            values = (JSONObject) new JSONParser().parse(new BufferedReader(new FileReader(physicalFile)));
        } catch (IOException | ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * @return A map of all the values from the file
     */
    public Map<String, String> getValues() {
        return values;
    }

    /**
     * set a value in the map
     *
     * @param tag    the name of the value
     * @param object the Object to put into the map
     */
    public <T> void set(String tag, T object) {

        if (object == null) {
            values.put(tag, null);
            return;
        }

        //get the converter for the specified class
        Class<T> clazz = (Class<T>) object.getClass();
        Converter<T> converter = converters.getConverter(clazz);

        if (converter == null) {
            throw new MissingResourceException("No converter given.", converters.getClass().getName(), clazz.getName());
        }

        //convert the value to a string
        String string = converter.toString(object);

        //if the result is null, throw an exception
        if (string == null) throw new IllegalFormatConversionException((char) 0, clazz);

        //add the key-value pair to the values map
        values.put(tag, string);
    }

    /**
     * set an array of values in a map
     *
     * @param tag     the name of the value
     * @param objects the array of objects to put in the map
     */
    public <T> void setArray(String tag, T[] objects) {
        //if the object is null, add a null value to the map
        if (objects == null) {
            values.put(tag, null);
            return;
        }

        //get the class to convert to
        Class<T> clazz = (Class<T>) objects.getClass().getComponentType();

        //get the converter for the specified class
        Converter<T> converter = converters.getConverter(clazz);

        //throw an error if there is no converter for the class
        if (converter == null) {
            throw new MissingResourceException("No converter given for \"" + clazz.getName() + "\".", converters.getClass().getName(), clazz.getName());
        }

        List<String> out = new ArrayList<>();

        for (T object : objects) {
            out.add(converter.toString(object));
        }
        //add the key-value pair to the values map
        values.put(tag, out);
    }

    /**
     * @param tag   the name of the value
     * @param clazz the class to convert to
     * @return an array of the specified type
     * @throws IllegalArgumentException if there is no converter for the given
     *                                  type
     */
    public <T> T[] getArray(String tag, Class<T> clazz) {
        //get the converter for the specified class
        Converter<T> converter = converters.getConverter(clazz);

        //throw an error if there is no converter for the class
        if (converter == null) {
            throw new MissingResourceException("No converter given.", converters.getClass().getName(), clazz.getName());
        }

        if (!values.containsKey(tag)) {
            throw new IllegalArgumentException();
        }

        //get the value from the map
        JSONArray valueArray = (JSONArray) values.get(tag);
        T[] results = (T[]) Array.newInstance(clazz, valueArray.size()); // like rust unsafe code but worse

        int it = 0;
        for (Object v : valueArray) {
            results[it] = (T) v;
            it++;
        }

        return results;
    }

    /**
     * @param tag   the name of the value
     * @param clazz the class to convert to
     * @return the value converted to the specified type
     * @throws MissingResourceException         if there is no converter for the given
     *                                          type
     * @throws IllegalArgumentException         if there is no value with the given tag
     * @throws IllegalFormatConversionException if the string could not be
     *                                          converted to the specified object
     */
    public <T> T get(String tag, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        //get the converter for the specified class
        Converter<T> converter = converters.getConverter(clazz);

        //throw an error if there is no converter for the class
        if (converter == null) {
            throw new MissingResourceException("No converter given.", converters.getClass().getName(), clazz.getName());
        }

        String string = getRaw(tag);

        //if the input is null, return null
        if (string == null) return null;

        //convert the string to the object
        T result = converter.fromString(string);

        //if the result is null, throw an exception
        if (result == null) throw new IllegalFormatConversionException((char) 0, clazz);

        return result;
    }

    /**
     * @param tag      the name of the value
     * @param fallback the value to use if none is found
     * @return the value converted to the specified type
     * @throws IllegalArgumentException if there is no converter for the given
     *                                  type
     */
    public <T> T get(String tag, T fallback) {
        Class<T> clazz = (Class<T>) fallback.getClass();
        try {
            return get(tag, clazz);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    /**
     * Return a string representation of the given tag
     * @param tag the name of the value
     * @return the string representation of the value
     */
    public String getRaw(String tag) {
        if (!values.containsKey(tag)) {
            throw new IllegalArgumentException();
        }

        return (String) values.get(tag);
    }

}
