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

import org.objectweb.asm.commons.Remapper;

import java.io.*;
import java.util.*;

class TinyMappings implements Mappings {
	private static class ClassEntryImpl implements ClassEntry {
		private final Map<String, Integer> namespacesToIds;
		private final String[] names;

		ClassEntryImpl(Map<String, Integer> namespacesToIds, MappedStringDeduplicator deduplicator, String[] data, String[] namespaceList) {
			this.namespacesToIds = namespacesToIds;
			names = new String[namespaceList.length];
			for (int i = 0; i < namespaceList.length; i++) {
				names[i] = deduplicator.deduplicate(MappedStringDeduplicator.Category.CLASS_NAME, data[i + 1]);
			}
		}

		@Override
		public String get(String namespace) {
			return names[namespacesToIds.get(namespace)];
		}
	}

	private static class EntryImpl implements FieldEntry, MethodEntry {
		private final Map<String, Integer> namespacesToIds;
		private final EntryTriple[] names;

		EntryImpl(Map<String, Integer> namespacesToIds, MappedStringDeduplicator deduplicator, String[] data, String[] namespaceList, Map<String, ClassRemapper> targetRemappers, boolean isMethod) {
			MappedStringDeduplicator.Category descCategory = isMethod
					? MappedStringDeduplicator.Category.METHOD_DESCRIPTOR
					: MappedStringDeduplicator.Category.FIELD_DESCRIPTOR;

			this.namespacesToIds = namespacesToIds;
			names = new EntryTriple[namespaceList.length];
			// add namespaceList[0]
			names[0] = new EntryTriple(
					deduplicator.deduplicate(MappedStringDeduplicator.Category.CLASS_NAME, data[1]),
					deduplicator.deduplicate(MappedStringDeduplicator.Category.NAME, data[3]),
					deduplicator.deduplicate(descCategory, data[2])
			);
			// add namespaceList[1+]
			for (int i = 1; i < namespaceList.length; i++) {
				String target = namespaceList[i];
				String mappedOwner = targetRemappers.get(target).map(data[1]);
				String mappedDesc = isMethod ? targetRemappers.get(target).mapMethodDesc(data[2]) : targetRemappers.get(target).mapDesc(data[2]);
				names[i] = new EntryTriple(
						mappedOwner, /* already deduplicated */
						deduplicator.deduplicate(MappedStringDeduplicator.Category.NAME, data[3 + i]),
						deduplicator.deduplicate(descCategory, mappedDesc)
				);
			}
		}

		@Override
		public EntryTriple get(String namespace) {
			return names[namespacesToIds.get(namespace)];
		}
	}

	private static class ClassRemapper extends Remapper {
		private final Map<String, ClassEntryImpl> firstNamespaceClassEntries;
		private final String destinationNamespace;

		ClassRemapper(Map<String, ClassEntryImpl> firstNamespaceClassEntries, String destinationNamespace) {
			this.firstNamespaceClassEntries = firstNamespaceClassEntries;
			this.destinationNamespace = destinationNamespace;
		}

		@Override
		public String map(String typeName) {
			ClassEntryImpl entry = firstNamespaceClassEntries.get(typeName);
			if (entry != null) {
				String out = entry.get(destinationNamespace);
				if (out != null) {
					return out;
				}
			}

			return typeName;
		}
	}

	private final Map<String, Integer> namespacesToIds;
	private final List<ClassEntryImpl> classEntries;
	private final List<EntryImpl> fieldEntries, methodEntries;

	@Deprecated
	protected TinyMappings(InputStream stream) throws IOException {
		this(stream, new MappedStringDeduplicator.MapBased());
	}

	protected TinyMappings(InputStream stream, MappedStringDeduplicator deduplicator) throws IOException {
		try (
			InputStreamReader streamReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(streamReader);
		) {

			String[] header = reader.readLine().split("\t");
			if (header.length <= 1 || !header[0].equals("v1")) {
				throw new IOException("Invalid mapping version!");
			}

			String[] namespaceList = new String[header.length - 1];
			namespacesToIds = new HashMap<>();
			for (int i = 1; i < header.length; i++) {
				namespaceList[i - 1] = header[i];
				if (namespacesToIds.containsKey(header[i])) {
					throw new IOException("Duplicate namespace: " + header[i]);
				} else {
					namespacesToIds.put(header[i], i - 1);
				}
			}

			classEntries = new ArrayList<>();

			String firstNamespace = header[1];
			Map<String, ClassEntryImpl> firstNamespaceClassEntries = new HashMap<>();
			List<String[]> fieldLines = new ArrayList<>();
			List<String[]> methodLines = new ArrayList<>();

			String line;
			while ((line = reader.readLine()) != null) {
				String[] splitLine = line.split("\t");
				if (splitLine.length >= 2) {
					switch (splitLine[0]) {
						case "CLASS":
							ClassEntryImpl entry = new ClassEntryImpl(namespacesToIds, deduplicator, splitLine, namespaceList);
							classEntries.add(entry);
							firstNamespaceClassEntries.put(entry.get(firstNamespace), entry);
							break;
						case "FIELD":
							fieldLines.add(splitLine);
							break;
						case "METHOD":
							methodLines.add(splitLine);
							break;
					}
				}
			}

			fieldEntries = new ArrayList<>(fieldLines.size());
			methodEntries = new ArrayList<>(methodLines.size());

			Map<String, ClassRemapper> targetRemappers = new HashMap<>();
			for (int i = 1; i < namespaceList.length; i++) {
				targetRemappers.put(namespaceList[i], new ClassRemapper(firstNamespaceClassEntries, namespaceList[i]));
			}

			for (String[] splitLine : fieldLines) {
				fieldEntries.add(new EntryImpl(namespacesToIds, deduplicator, splitLine, namespaceList, targetRemappers, false));
			}

			for (String[] splitLine : methodLines) {
				methodEntries.add(new EntryImpl(namespacesToIds, deduplicator, splitLine, namespaceList, targetRemappers, false));
			}

			((ArrayList<ClassEntryImpl>) classEntries).trimToSize();
			// fieldEntries/methodEntries are already the right size
		}
	}

	@Override
	public Collection<String> getNamespaces() {
		return namespacesToIds.keySet();
	}

	@Override
	public Collection<ClassEntry> getClassEntries() {
		//noinspection unchecked
		return (Collection<ClassEntry>) (Collection<?>) classEntries;
	}

	@Override
	public Collection<FieldEntry> getFieldEntries() {
		//noinspection unchecked
		return (Collection<FieldEntry>) (Collection<?>) fieldEntries;
	}

	@Override
	public Collection<MethodEntry> getMethodEntries() {
		//noinspection unchecked
		return (Collection<MethodEntry>) (Collection<?>) methodEntries;
	}
}
