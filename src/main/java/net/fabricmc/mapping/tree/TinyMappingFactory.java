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

package net.fabricmc.mapping.tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import net.fabricmc.mapping.reader.v2.MappingGetter;
import net.fabricmc.mapping.reader.v2.MappingParseException;
import net.fabricmc.mapping.reader.v2.TinyMetadata;
import net.fabricmc.mapping.reader.v2.TinyV2Factory;
import net.fabricmc.mapping.reader.v2.TinyVisitor;

/**
 * The factory class for tree tiny mapping models.
 */
public final class TinyMappingFactory {

	public static final TinyTree EMPTY_TREE = new EmptyTinyTree();
	public static final TinyMetadata EMPTY_METADATA = new EmptyTinyTree.Metadata();

	/**
	 * Loads a tree model from a buffered reader for v2 input.
	 *
	 * @param reader the buffered reader
	 * @return the built reader model
	 * @throws IOException           if the reader throws one
	 * @throws MappingParseException if there is an issue with the v2 format
	 */
	public static TinyTree load(BufferedReader reader) throws IOException, MappingParseException {
		return load(reader, false);
	}

	/**
	 * Loads a tree model from a buffered reader for v2 input.
	 *
	 * @param reader the buffered reader
	 * @param slim   whether parameters, local variables, or comments are omitted
	 * @return the built reader model
	 * @throws IOException           if the reader throws one
	 * @throws MappingParseException if there is an issue with the v2 format
	 */
	public static TinyTree load(BufferedReader reader, boolean slim) throws IOException, MappingParseException {
		Visitor visitor = new Visitor(slim);
		TinyV2Factory.visit(reader, visitor);
		return new Tree(visitor.metadata, visitor.classNames, visitor.classes);
	}

	/**
	 * Loads a tree model from a buffered reader and automatically determine the input type.
	 *
	 * @param reader the buffered reader
	 * @return the built reader model
	 * @throws IOException           if the reader throws one
	 * @throws MappingParseException if there is an issue with the v2 format
	 */
	public static TinyTree loadWithDetection(BufferedReader reader) throws IOException, MappingParseException {
		return loadWithDetection(reader, false);
	}

	/**
	 * Loads a tree model from a buffered reader and automatically determine the input type.
	 *
	 * @param reader the buffered reader
	 * @param slim   whether parameters, local variables, or comments are omitted
	 * @return the built reader model
	 * @throws IOException           if the reader throws one
	 * @throws MappingParseException if there is an issue with the v2 format
	 */
	public static TinyTree loadWithDetection(BufferedReader reader, boolean slim) throws IOException, MappingParseException {
		reader.mark(8192);
		String firstLine = reader.readLine();
		String[] header = firstLine.split("\t");
		reader.reset();
		switch (header[0]) {
		case "tiny":
			return load(reader, slim);
		case "v1":
			return loadLegacy(reader);
		}
		throw new UnsupportedOperationException("Unsupported format with header \"" + firstLine + "\"!");
	}

	/**
	 * Loads a tree model from a buffered reader for v1 input.
	 *
	 * @param reader the buffered reader
	 * @return the built reader model
	 * @throws IOException if the reader throws one
	 */
	public static TinyTree loadLegacy(BufferedReader reader) throws IOException {
		String[] header = reader.readLine().split("\t");
		if (header.length <= 1 || !header[0].equals("v1")) {
			throw new IOException("Invalid mapping version!");
		}

		String[] namespaceList = new String[header.length - 1];

		final Map<String, Integer> namespacesToIds = new HashMap<>();
		ToIntFunction<String> namespaceMapper = namespacesToIds::get;
		final List<ClassDef> classEntries = new ArrayList<>();
		for (int i = 1; i < header.length; i++) {
			namespaceList[i - 1] = header[i];
			if (namespacesToIds.containsKey(header[i])) {
				throw new IOException("Duplicate namespace: " + header[i]);
			} else {
				namespacesToIds.put(header[i], i - 1);
			}
		}

		final Map<String, ClassImpl> firstNamespaceClassEntries = new HashMap<>();
		List<String[]> fieldLines = new ArrayList<>();
		List<String[]> methodLines = new ArrayList<>();

		String line;
		while ((line = reader.readLine()) != null) {
			String[] splitLine = line.split("\t");
			if (splitLine.length >= 2) {
				switch (splitLine[0]) {
					case "CLASS":
						ClassImpl entry = new ClassImpl(namespaceMapper, Arrays.copyOfRange(splitLine, 1, splitLine.length));
						classEntries.add(entry);
						firstNamespaceClassEntries.put(entry.getName(0), entry);
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

		DescriptorMapper mapper = new DescriptorMapper(firstNamespaceClassEntries);

		for (String[] splitLine : fieldLines) {
			// FIELD ClassName desc names ...
			String className = splitLine[1];
			ClassImpl parent = firstNamespaceClassEntries.get(className);
			if (parent == null) {
				parent = new ClassImpl(namespaceMapper, new String[]{className}); // No class for my field, sad!
				firstNamespaceClassEntries.put(className, parent);
				classEntries.add(parent);
			}

			FieldImpl field = new FieldImpl(mapper, namespaceMapper, Arrays.copyOfRange(splitLine, 3, splitLine.length), splitLine[2]);
			parent.fields.add(field);
		}

		for (String[] splitLine : methodLines) {
			// METHOD ClassName desc names ...
			String className = splitLine[1];
			ClassImpl parent = firstNamespaceClassEntries.get(className);
			if (parent == null) {
				parent = new ClassImpl(namespaceMapper, new String[] {className}); // No class for my field, sad!
				firstNamespaceClassEntries.put(className, parent);
				classEntries.add(parent);
			}

			MethodImpl method = new MethodImpl(mapper, namespaceMapper, Arrays.copyOfRange(splitLine, 3, splitLine.length), splitLine[2]);
			parent.methods.add(method);
		}

		return new Tree(new LegacyMetadata(Collections.unmodifiableList(Arrays.asList(namespaceList)), namespacesToIds), firstNamespaceClassEntries, classEntries);
	}

	private TinyMappingFactory() {
	}

	private static final class Visitor implements TinyVisitor {
		private static final MappedImpl SLIM_DUMMY = new MappedImpl(s -> 0, new String[0]) {
		};
		private final boolean slim;
		private @MonotonicNonNull TinyMetadata metadata;
		private @MonotonicNonNull ToIntFunction<String> namespaceMapper;
		private final Map<String, ClassImpl> classNames = new HashMap<>();
		private final Collection<ClassDef> classes = new ArrayList<>();
		private final DescriptorMapper descriptorMapper = new DescriptorMapper(classNames);
		private final Deque<MappedImpl> stack = new ArrayDeque<>(4);
		private boolean pushedComment = false;
		private @MonotonicNonNull ClassImpl inClass = null;
		private @MonotonicNonNull MethodImpl inMethod = null;

		Visitor(boolean slim) {
			this.slim = slim;
		}

		@Override
		public void start(TinyMetadata metadata) {
			this.metadata = metadata;
			this.namespaceMapper = metadata::index;
		}

		@Override
		public void pushClass(MappingGetter name) {
			ClassImpl clz = new ClassImpl(namespaceMapper, name.getRawNames());
			classes.add(clz);
			classNames.put(name.get(0), clz);
			inClass = clz;
			stack.addLast(clz);
		}

		@Override
		public void pushField(MappingGetter name, String descriptor) {
			if (inClass == null)
				throw new IllegalStateException();

			FieldImpl field = new FieldImpl(descriptorMapper, namespaceMapper, name.getRawNames(), descriptor);
			inClass.fields.add(field);
			stack.addLast(field);
		}

		@Override
		public void pushMethod(MappingGetter name, String descriptor) {
			if (inClass == null)
				throw new IllegalStateException();

			MethodImpl method = new MethodImpl(descriptorMapper, namespaceMapper, name.getRawNames(), descriptor);
			inClass.methods.add(method);
			inMethod = method;
			stack.addLast(method);
		}

		@Override
		public void pushParameter(MappingGetter name, int localVariableIndex) {
			if (inMethod == null) {
				throw new IllegalStateException();
			}

			if (slim) {
				stack.addLast(SLIM_DUMMY);
				return;
			}

			ParameterImpl par = new ParameterImpl(namespaceMapper, name.getRawNames(), localVariableIndex);
			inMethod.parameters.add(par);
			stack.addLast(par);
		}

		@Override
		public void pushLocalVariable(MappingGetter name, int localVariableIndex, int localVariableStartOffset, int localVariableTableIndex) {
			if (inMethod == null) {
				throw new IllegalStateException();
			}

			if (slim) {
				stack.addLast(SLIM_DUMMY);
				return;
			}

			LocalVariableImpl var = new LocalVariableImpl(namespaceMapper, name.getRawNames(), localVariableIndex, localVariableStartOffset, localVariableTableIndex);
			inMethod.localVariables.add(var);
			stack.addLast(var);
		}

		@Override
		public void pushComment(String comment) {
			if (stack.isEmpty()) {
				throw new IllegalStateException("Nothing to append comment on!");
			}


			if (pushedComment) {
				throw new IllegalStateException("Commenting on a comment!");
			}

			if (!slim) {
				stack.peekLast().setComment(comment);
			}
			pushedComment = true;
		}

		@Override
		public void pop(int count) {
			if (pushedComment) {
				pushedComment = false;
				count--;
			}
			for (int i = 0; i < count; i++) {
				stack.removeLast();
			}
		}
	}

	private static final class Tree implements TinyTree {

		private final TinyMetadata metadata;
		private final Map<String, ClassDef> map;
		private final Collection<ClassDef> classes;

		@SuppressWarnings("unchecked")
		Tree(TinyMetadata metadata, Map<String, ClassImpl> map, Collection<ClassDef> classes) {
			this.metadata = metadata;
			this.map = (Map<String, ClassDef>) (Map<?, ?>) map;
			this.classes = classes;
		}

		@Override
		public TinyMetadata getMetadata() {
			return metadata;
		}

		@Override
		public Map<String, ClassDef> getDefaultNamespaceClassMap() {
			return map;
		}

		@Override
		public Collection<ClassDef> getClasses() {
			return classes;
		}
	}

	private static final class LegacyMetadata implements TinyMetadata {
		private final List<String> namespaces;
		private final Map<String, Integer> namespacesToIds;

		LegacyMetadata(List<String> namespaces, Map<String, Integer> namespacesToIds) {
			this.namespaces = namespaces;
			this.namespacesToIds = namespacesToIds;
		}

		@Override
		public int getMajorVersion() {
			return 1;
		}

		@Override
		public int getMinorVersion() {
			return 0;
		}

		@Override
		public List<String> getNamespaces() {
			return namespaces;
		}

		@Override
		public Map<String, String> getProperties() {
			return Collections.emptyMap();
		}

		@Override
		public int index(String namespace) {
			return namespacesToIds.getOrDefault(namespace, -1);
		}
	}
}
