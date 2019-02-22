/*
 * Copyright 2019 FabricMC
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

package net.fabricmc.mappings;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

interface MappedStringDeduplicator {
    enum Category {
        NAME,
        CLASS_NAME,
        FIELD_DESCRIPTOR,
        METHOD_DESCRIPTOR
    }

    String deduplicate(Category category, String string);

    MappedStringDeduplicator EMPTY = (category, string) -> string;

    class MapBased implements MappedStringDeduplicator {
        private final Map<Category, Map<String, String>> data = new EnumMap<>(Category.class);

        MapBased() {
            for (Category category : Category.values()) {
                data.put(category, new HashMap<>());
            }
        }

        @Override
        public String deduplicate(Category category, String string) {
            Map<String, String> map = data.get(category);
            return map.putIfAbsent(string, string);
        }
    }
}
