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

package net.fabricmc.mappings.helpers.asm;

import org.objectweb.asm.commons.Remapper;

import net.fabricmc.mappings.*;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class AsmRemapperCache {
	private static class SimpleRemapper extends Remapper {
		private final Map<String, String> classNames;
		private final Map<EntryTriple, String> fieldNames;
		private final Map<EntryTriple, String> methodNames;

		public SimpleRemapper(Mappings mappings, String from, String to) {
			classNames = new HashMap<>();
			fieldNames = new HashMap<>();
			methodNames = new HashMap<>();

			for (ClassEntry entry : mappings.getClassEntries()) {
				classNames.put(entry.get(from), entry.get(to));
			}

			for (FieldEntry entry : mappings.getFieldEntries()) {
				fieldNames.put(entry.get(from), entry.get(to).getName());
			}

			for (MethodEntry entry : mappings.getMethodEntries()) {
				methodNames.put(entry.get(from), entry.get(to).getName());
			}
		}

		@Override
		public String map(String typeName) {
			return classNames.getOrDefault(typeName, typeName);
		}

		@Override
		public String mapFieldName(final String owner, final String name, final String descriptor) {
			return fieldNames.getOrDefault(new EntryTriple(owner, name, descriptor), name);
		}

		@Override
		public String mapMethodName(final String owner, final String name, final String descriptor) {
			return methodNames.getOrDefault(new EntryTriple(owner, name, descriptor), name);
		}
	}

	private final Map<String, SoftReference<SimpleRemapper>> remapperCache = new HashMap<>();

	public Remapper getRemapper(Mappings mappings, String from, String to) {
		String key = from + ":" + to;
		SoftReference<SimpleRemapper> remapperRef = remapperCache.get(key);
		if (remapperRef != null && remapperRef.get() != null) {
			return remapperRef.get();
		} else {
			SimpleRemapper remapper = new SimpleRemapper(mappings, from, to);
			remapperCache.put(key, new SoftReference<>(remapper));
			return remapper;
		}
	}
}
