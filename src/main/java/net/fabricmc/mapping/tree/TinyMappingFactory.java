package net.fabricmc.mapping.tree;

import com.google.common.collect.ImmutableList;
import net.fabricmc.mapping.reader.v2.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.function.ToIntFunction;

/**
 * The factory class for tree tiny mapping models.
 */
public final class TinyMappingFactory {

	/**
	 * Loads a tree model from a buffered reader for v2 input.
	 *
	 * @param reader the buffered reader
	 * @return the built reader model
	 * @throws IOException           if the reader throws one
	 * @throws MappingParseException if there is an issue with the v2 format
	 */
	public static TinyMapping load(BufferedReader reader) throws IOException, MappingParseException {
		Visitor visitor = new Visitor();
		TinyV2Factory.visit(reader, visitor);
		return new Mapping(visitor.metadata, visitor.classNames, visitor.classes);
	}

	/**
	 * Loads a tree model from a buffered reader for v1 input.
	 *
	 * @param reader the buffered reader
	 * @return the built reader model
	 * @throws IOException if the reader throws one
	 */
	public static TinyMapping loadLegacy(BufferedReader reader) throws IOException {
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
						ClassImpl entry = new ClassImpl(namespaceMapper, splitLine);
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

		SignatureMapper mapper = new SignatureMapper(firstNamespaceClassEntries);

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
				parent = new ClassImpl(namespaceMapper, new String[]{className}); // No class for my field, sad!
				firstNamespaceClassEntries.put(className, parent);
				classEntries.add(parent);
			}

			MethodImpl method = new MethodImpl(mapper, namespaceMapper, Arrays.copyOfRange(splitLine, 3, splitLine.length), splitLine[2]);
			parent.methods.add(method);
		}

		return new Mapping(new LegacyMetadata(ImmutableList.copyOf(namespaceList), namespacesToIds), firstNamespaceClassEntries, classEntries);
	}

	private TinyMappingFactory() {
	}

	private static final class Visitor implements TinyVisitor {
		TinyMetadata metadata;
		ToIntFunction<String> namespaceMapper;
		Map<String, ClassImpl> classNames = new HashMap<>();
		Collection<ClassDef> classes = new ArrayList<>();
		SignatureMapper signatureMapper = new SignatureMapper(classNames);
		MappedImpl last;
		ClassImpl inClass;
		MethodImpl inMethod;

		@Override
		public void start(TinyMetadata metadata) {
			this.metadata = metadata;
			this.namespaceMapper = metadata::index;
		}

		@Override
		public void pushClass(MappingGetter name) {
			ClassImpl clz = new ClassImpl(namespaceMapper, name.getRaw());
			classes.add(clz);
			classNames.put(name.get(0), clz);
			inClass = clz;
			last = clz;
		}

		@Override
		public void pushField(MappingGetter name, String descriptor) {
			if (inClass == null)
				throw new IllegalStateException();

			FieldImpl field = new FieldImpl(signatureMapper, namespaceMapper, name.getRaw(), descriptor);
			inClass.fields.add(field);
			last = field;
		}

		@Override
		public void pushMethod(MappingGetter name, String descriptor) {
			if (inClass == null)
				throw new IllegalStateException();

			MethodImpl method = new MethodImpl(signatureMapper, namespaceMapper, name.getRaw(), descriptor);
			inClass.methods.add(method);
			inMethod = method;
			last = method;
		}

		@Override
		public void pushParameter(MappingGetter name, int localVariableIndex) {
			if (inMethod == null)
				throw new IllegalStateException();

			ParameterImpl par = new ParameterImpl(namespaceMapper, name.getRaw(), localVariableIndex);
			inMethod.parameters.add(par);
			last = par;
		}

		@Override
		public void pushLocalVariable(MappingGetter name, int localVariableIndex, int localVariableStartOffset, int localVariableTableIndex) {
			if (inMethod == null)
				throw new IllegalStateException();

			LocalVariableImpl var = new LocalVariableImpl(namespaceMapper, name.getRaw(), localVariableIndex, localVariableStartOffset, localVariableTableIndex);
			inMethod.localVariables.add(var);
			last = var;
		}

		@Override
		public void pushComment(String comment) {
			if (last == null)
				throw new IllegalStateException();
			last.setComment(comment);
		}

		@Override
		public void pop(int count) {
		}
	}

	private static final class Mapping implements TinyMapping {

		private final TinyMetadata metadata;
		private final Map<String, ClassDef> map;
		private final Collection<ClassDef> classes;

		@SuppressWarnings("unchecked")
		Mapping(TinyMetadata metadata, Map<String, ClassImpl> map, Collection<ClassDef> classes) {
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
		public int index(String namespace) throws IllegalArgumentException {
			Integer t = namespacesToIds.get(namespace);
			if (t == null)
				throw new IllegalArgumentException("Invalid namespace \"" + namespace + "\"!");
			return t;
		}
	}
}
