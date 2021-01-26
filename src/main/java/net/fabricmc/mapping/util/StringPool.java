/*
 * Copyright 2021 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.mapping.util;

import java.util.HashMap;
import java.util.function.Function;

/**
 * A pool of strings used for de-duplicating string references.
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
        return this.map.computeIfAbsent(str, Function.identity() /* computed value is the key */);
    }

    /**
     * De-duplicates an array of strings in-place.
     * @param strings The array of strings to de-duplicate
     * @return The value passed to the {@param strings}
     */
    public String[] pool(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            strings[i] = this.pool(strings[i]);
        }

        return strings;
    }
}
