/*
 * Copyright (c) 2019-2020 FabricMC
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

import java.io.IOException;
import java.io.InputStream;

@Deprecated
public final class MappingsProvider {
    private MappingsProvider() {

    }

    public static Mappings createEmptyMappings() {
        return DummyMappings.INSTANCE;
    }

    public static Mappings readTinyMappings(InputStream stream) throws IOException {
        return readTinyMappings(stream, true);
    }

    public static Mappings readTinyMappings(InputStream stream, boolean saveMemoryUsage) throws IOException {
        return new TinyMappings(stream,
                saveMemoryUsage ? new MappedStringDeduplicator.MapBased() : MappedStringDeduplicator.EMPTY
        );
    }
}
