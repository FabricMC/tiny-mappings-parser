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

package net.fabricmc.mappings.helpers.mixin;

import net.fabricmc.mappings.ClassEntry;
import net.fabricmc.mappings.EntryTriple;
import net.fabricmc.mappings.FieldEntry;
import net.fabricmc.mappings.Mappings;
import net.fabricmc.mappings.MethodEntry;
import org.objectweb.asm.commons.Remapper;
import org.spongepowered.asm.mixin.extensibility.IRemapper;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class MixinMappingsRemapper implements IRemapper {
	private final Map<String, String> classMap = new HashMap<>();
	private final Map<String, String> classMapInverse = new HashMap<>();
	private final Map<EntryTriple, String> fieldMap = new HashMap<>();
	private final Map<EntryTriple, String> methodMap = new HashMap<>();

	private final SimpleClassMapper classMapper = new SimpleClassMapper(classMap);
	private final SimpleClassMapper classUnmapper = new SimpleClassMapper(classMapInverse);

	private static class SimpleClassMapper extends Remapper {
		final Map<String, String> classMap;

		public SimpleClassMapper(Map<String, String> map) {
			this.classMap = map;
		}

		@Override
		public String map(String typeName) {
			return this.classMap.getOrDefault(typeName, typeName);
		}
	}

	public MixinMappingsRemapper(Mappings mappings, String from, String to) {
		for (ClassEntry entry : mappings.getClassEntries()) {
			String fromC = entry.get(from);
			String toC = entry.get(to);

			classMap.put(fromC, toC);
			classMapInverse.put(toC, fromC);
		}

		for (FieldEntry entry : mappings.getFieldEntries()) {
			fieldMap.put(entry.get(from), entry.get(to).getName());
		}

		for (MethodEntry entry : mappings.getMethodEntries()) {
			methodMap.put(entry.get(from), entry.get(to).getName());
		}
	}

	@Override
	public String mapMethodName(String owner, String name, String desc) {
		return methodMap.getOrDefault(new EntryTriple(owner, name, desc), name);
	}

	@Override
	public String mapFieldName(String owner, String name, String desc) {
		return fieldMap.getOrDefault(new EntryTriple(owner, name, desc), name);
	}

	@Override
	public String map(String typeName) {
		return classMap.getOrDefault(typeName, typeName);
	}

	@Override
	public String unmap(String typeName) {
		return classMapInverse.getOrDefault(typeName, typeName);
	}

	@Override
	public String mapDesc(String desc) {
		return classMapper.mapDesc(desc);
	}

	@Override
	public String unmapDesc(String desc) {
		return classUnmapper.mapDesc(desc);
	}
}
