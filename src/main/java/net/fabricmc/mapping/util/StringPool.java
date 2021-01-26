package net.fabricmc.mapping.util;

import java.util.HashMap;

/**
 * A pool of strings used for de-duplication.
 */
public class StringPool {
    // The HashMap uses value-equality to de-duplicate keys. Because we need a value type, we simply stuff the key
    // reference back in to make the code below pretty.
    private final HashMap<String, String> map = new HashMap<>();

    /**
     * Adds a string to the pool, returning a de-duplicated reference to the string.
     * @param str The string to de-duplicate
     * @return A de-duplicated reference to a string with the same contents as {@param str}
     */
    public String pool(String str) {
        return this.map.computeIfAbsent(str, StringPool::identity);
    }

    /**
     * @param strings The array of strings to de-duplicate
     * @return A new array of de-duplicated strings (see {@link StringPool#pool(String)})
     */
    public String[] pool(String[] strings) {
        String[] dedupStrings = new String[strings.length];

        for (int i = 0; i < strings.length; i++) {
            dedupStrings[i] = this.pool(strings[i]);
        }

        return dedupStrings;
    }

    private static <T> T identity(T obj) {
        return obj;
    }
}
