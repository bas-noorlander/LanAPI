package scripts.LanAPI.Core.Dynamic;

import java.util.HashMap;

/**
 * A dynamic data bag which can be added/retrieved/deleted upon at runtime.
 *
 * @author Laniax
 */
public class Bag {

    private final HashMap<String, Object> _values = new HashMap<>();

    /**
     * Returns the amount of items in the bag
     * @return
     */
    public int getCount() {
        return _values.size();
    }

    /**
     * Returns if the bag is empty or not
     * @return
     */
    public boolean isEmpty() {
        return getCount() == 0;
    }

    /**
     * Adds a item to the bag
     * @param name
     * @param value
     * @return true is successfully added, false if the key was already present.
     */
    public boolean add(String name, Object value) {

        if (_values.containsKey(name))
            return false;

        _values.put(name, value);
        return true;
    }

    public void addOrUpdate(String name, Object value) {
        _values.put(name, value);
    }

    /**
     * Gets an item from the bag
     * @param name
     * @return The object, or null if it didnt exist.
     */
    public <T> T get(String name) {
        if (!_values.containsKey(name))
            return null;

        return (T)_values.get(name);
    }
    /**
     * Gets an item from the bag, if it doesn't exist this will return the defaultValue.
     * @param name
     * @param defaultValue
     * @return The object in the bag, or the defaultValue if it didnt exist in the bag.
     */
    public <T> T get(String name, T defaultValue) {

        Object obj;
        return (obj = get(name)) != null ? (T)obj : defaultValue;

    }

    /**
     * Removes an item from the bag
     * @param name
     * @return true if removed, false if not found.
     */
    public boolean remove(String name) {
        if (!_values.containsKey(name))
            return false;

        _values.remove(name);
        return true;
    }

}