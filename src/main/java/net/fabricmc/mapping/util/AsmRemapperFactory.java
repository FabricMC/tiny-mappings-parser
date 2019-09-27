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

package net.fabricmc.mapping.util;

import net.fabricmc.mapping.tree.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.mappings.*;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory for Java assembly remappers between namespaces in a mapping set.
 */
public final class AsmRemapperFactory {

	private static final class SimpleRemapper extends Remapper {
		private final Map<String, String> classNames = new HashMap<>();
		private final Map<EntryTriple, String> fieldNames = new HashMap<>();
		private final Map<EntryTriple, String> methodNames = new HashMap<>();

		SimpleRemapper(Collection<ClassDef> classes, String from, String to) {
			for (ClassDef clz : classes) {
				String className = clz.getName(from);
				classNames.put(className, clz.getName(to));
				for (FieldDef field : clz.getFields()) {
					fieldNames.put(new EntryTriple(className, field.getName(from), field.getDescriptor(from)), field.getName(to));
				}
				for (MethodDef method : clz.getMethods()) {
					methodNames.put(new EntryTriple(className, method.getName(from), method.getDescriptor(from)), method.getName(to));
				}
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
	private final TinyTree mapping;

	/**
	 * Create a factory backed by a set of mapping.
	 *
	 * @param mapping the mapping
	 */
	public AsmRemapperFactory(TinyTree mapping) {
		this.mapping = mapping;
	}

	/**
	 * Obtains the remapper between two namespaces.
	 *
	 * @param from the source namespace
	 * @param to the target namespace
	 * @return the remapper
	 */
	public Remapper getRemapper(String from, String to) {
		String key = from + ":" + to;
		SoftReference<SimpleRemapper> remapperRef = remapperCache.get(key);
		@Nullable SimpleRemapper ret;
		if (remapperRef != null && (ret = remapperRef.get()) != null) {
			return ret;
		} else {
			SimpleRemapper remapper = new SimpleRemapper(mapping.getClasses(), from, to);
			remapperCache.put(key, new SoftReference<>(remapper));
			return remapper;
		}
	}
}
