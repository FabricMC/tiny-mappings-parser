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

package net.fabricmc.mapping.reader.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A factory for the Tiny V2 mapping parser.
 */
public final class TinyV2Factory {

	private static final String HEADER_MARKER = "tiny";
	private static final char INDENT = '\t';
	private static final String SPACE_STRING = "\t";
	private static final String ESCAPED_NAMES_PROPERTY = "escaped-names";
	private static final String TO_ESCAPE = "\\\n\r\0\t";
	private static final String ESCAPED = "\\nr0t";

	/**
	 * Explores a Tiny V2 mapping with a visitor.
	 *
	 * <p>This method will not close the {@code reader}!
	 *
	 * @param reader  the reader that provides the mapping content
	 * @param visitor the visitor
	 * @throws IOException           if the reader throws one
	 * @throws MappingParseException if a mapping parsing error is encountered
	 */
	public static void visit(BufferedReader reader, TinyVisitor visitor) throws IOException, MappingParseException {
		String line;
		final int namespaceCount;
		final boolean escapedNames;
		try {
			final TinyMetadata meta = readMetadata(reader);
			namespaceCount = meta.getNamespaces().size();
			escapedNames = meta.getProperties().containsKey(ESCAPED_NAMES_PROPERTY);
			visitor.start(meta);
			line = reader.readLine();
		} catch (RuntimeException ex) {
			throw new MappingParseException("Error in the header!", ex);
		}

		int lastIndent = -1;
		final TinyState[] stack = new TinyState[4]; // max depth 4
		for (; line != null; line = reader.readLine()) {
			try {
				int currentIndent = countIndent(line);
				if (currentIndent > lastIndent + 1)
					throw new IllegalArgumentException("Broken indent! Maximum " + (lastIndent + 1) + ", actual " + currentIndent);
				if (currentIndent <= lastIndent) {
					visitor.pop(lastIndent - currentIndent + 1);
				}
				lastIndent = currentIndent;

				final String[] parts = line.split(SPACE_STRING, -1);
				final TinyState currentState = TinyState.get(currentIndent, parts[currentIndent]);

				if (!currentState.checkPartCount(currentIndent, parts.length, namespaceCount)) {
					throw new IllegalArgumentException("Wrong number of parts for definition of a " + currentState + "!");
				}

				if (!currentState.checkStack(stack, currentIndent)) {
					throw new IllegalStateException("Invalid stack " + Arrays.toString(stack) + " for a " + currentState + " at position" + currentIndent + "!");
				}

				stack[currentIndent] = currentState;

				currentState.visit(visitor, parts, currentIndent, escapedNames);
			} catch (RuntimeException ex) {
				throw new MappingParseException("Error on line \"" + line + "\"!", ex);
			}
		}

		if (lastIndent > -1) {
			visitor.pop(lastIndent + 1);
		}
	}

	/**
	 * Peeks the metadata of a Tiny V2 mapping.
	 *
	 * <p>This method will not close the {@code reader}!
	 *
	 * <p>It is recommended to call {@link #visit(BufferedReader, TinyVisitor)} if the mapping content is to be
	 * visited.
	 *
	 * @param reader the reader that provides the mapping content
	 * @return the metadata of the mapping
	 * @throws IOException              if the reader throws one
	 * @throws IllegalArgumentException if a mapping format error is encountered
	 */
	public static TinyMetadata readMetadata(final BufferedReader reader) throws IOException, IllegalArgumentException {
		final String firstLine = reader.readLine();
		if (firstLine == null)
			throw new IllegalArgumentException("Empty reader!");
		final String[] parts = firstLine.split(SPACE_STRING, -1);
		if (parts.length < 5 || !parts[0].equals(HEADER_MARKER)) {
			throw new IllegalArgumentException("Unsupported format!");
		}

		final int majorVersion;
		try {
			majorVersion = Integer.parseInt(parts[1]);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid major version!", ex);
		}
		final int minorVersion;
		try {
			minorVersion = Integer.parseInt(parts[2]);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid minor version!", ex);
		}

		final Map<String, String> properties = new LinkedHashMap<>();
		String line;
		reader.mark(8192);
		while ((line = reader.readLine()) != null) {
			switch (countIndent(line)) {
				case 0: {
					reader.reset();
					return makeHeader(majorVersion, minorVersion, parts, properties);
				}
				case 1: {
					String[] elements = line.split(SPACE_STRING, -1); // Care about "" values
					properties.put(elements[1], elements.length == 2 ? null : elements[2]);
					break;
				}
				default: {
					throw new IllegalArgumentException("Invalid indent in header! Encountered \"" + line + "\"!");
				}
			}
			reader.mark(8192);
		}

		return makeHeader(majorVersion, minorVersion, parts, properties);
	}

	private static int countIndent(String st) {
		final int len = st.length();
		int ret = 0;
		while (ret < len && st.charAt(ret) == INDENT) {
			ret++;
		}
		return ret;
	}

	private static TinyHeader makeHeader(int major, int minor, String[] parts, Map<String, String> props) {
		List<String> list = new ArrayList<>();
		Map<String, Integer> map = new HashMap<>();
		for (int i = 3; i < parts.length; i++) {
			list.add(parts[i]);
			map.put(parts[i], i - 3);
		}
		return new TinyHeader(major, minor, Collections.unmodifiableList(list), Collections.unmodifiableMap(map), Collections.unmodifiableMap(new HashMap<>(props)));
	}

	private static String unescapeOpt(String raw, boolean escapedStrings) {
		return escapedStrings ? unescape(raw) : raw;
	}

	private static String unescape(String str) {
		// copied from matcher, lazy!
		int pos = str.indexOf('\\');
		if (pos < 0) return str;

		StringBuilder ret = new StringBuilder(str.length() - 1);
		int start = 0;

		do {
			ret.append(str, start, pos);
			pos++;
			int type;

			if (pos >= str.length()) {
				throw new RuntimeException("incomplete escape sequence at the end");
			} else if ((type = ESCAPED.indexOf(str.charAt(pos))) < 0) {
				throw new RuntimeException("invalid escape character: \\" + str.charAt(pos));
			} else {
				ret.append(TO_ESCAPE.charAt(type));
			}

			start = pos + 1;
		} while ((pos = str.indexOf('\\', start)) >= 0);

		ret.append(str, start, str.length());

		return ret.toString();
	}

	private TinyV2Factory() {
	}

	private enum TinyState {
		// c names...
		CLASS(1) {
			@Override
			boolean checkStack(TinyState[] stack, int currentIndent) {
				return currentIndent == 0;
			}

			@Override
			void visit(TinyVisitor visitor, String[] parts, int indent, boolean escapedStrings) {
				visitor.pushClass(makeGetter(parts, indent, escapedStrings));
			}
		},
		// f desc names...
		FIELD(2) {
			@Override
			boolean checkStack(TinyState[] stack, int currentIndent) {
				return currentIndent == 1 && stack[currentIndent - 1] == CLASS;
			}

			@Override
			void visit(TinyVisitor visitor, String[] parts, int indent, boolean escapedStrings) {
				visitor.pushField(makeGetter(parts, indent, escapedStrings), unescapeOpt(parts[indent + 1], escapedStrings));
			}
		},
		// m desc names...
		METHOD(2) {
			@Override
			boolean checkStack(TinyState[] stack, int currentIndent) {
				return currentIndent == 1 && stack[currentIndent - 1] == CLASS;
			}

			@Override
			void visit(TinyVisitor visitor, String[] parts, int indent, boolean escapedStrings) {
				visitor.pushMethod(makeGetter(parts, indent, escapedStrings), unescapeOpt(parts[indent + 1], escapedStrings));
			}
		},
		// p lvIndex names...
		PARAMETER(2) {
			@Override
			boolean checkStack(TinyState[] stack, int currentIndent) {
				return currentIndent == 2 && stack[currentIndent - 1] == METHOD;
			}

			@Override
			void visit(TinyVisitor visitor, String[] parts, int indent, boolean escapedStrings) {
				visitor.pushParameter(makeGetter(parts, indent, escapedStrings), Integer.parseInt(parts[indent + 1]));
			}
		},
		// v lvIndex lvStartOffset lvtIndex names...
		LOCAL_VARIABLE(4) {
			@Override
			boolean checkStack(TinyState[] stack, int currentIndent) {
				return currentIndent == 2 && stack[currentIndent - 1] == METHOD;
			}

			@Override
			void visit(TinyVisitor visitor, String[] parts, int indent, boolean escapedStrings) {
				visitor.pushLocalVariable(makeGetter(parts, indent, escapedStrings), Integer.parseInt(parts[indent + 1]), Integer.parseInt(parts[indent + 2]), Integer.parseInt(parts[indent + 3]));
			}
		},
		// c comment
		COMMENT(2, false) {
			@Override
			boolean checkStack(TinyState[] stack, int currentIndent) {
				if (currentIndent == 0)
					return false;
				switch (stack[currentIndent - 1]) {
					case CLASS:
					case METHOD:
					case FIELD:
					case PARAMETER:
					case LOCAL_VARIABLE:
						// Use a whitelist
						return true;
					default:
						return false;
				}
			}

			@Override
			void visit(TinyVisitor visitor, String[] parts, int indent, boolean escapedStrings) {
				visitor.pushComment(unescape(parts[indent + 1]));
			}
		};

		final int actualParts;
		final boolean namespaced;

		TinyState(int actualParts) {
			this(actualParts, true);
		}

		TinyState(int actualParts, boolean namespaced) {
			this.actualParts = actualParts;
			this.namespaced = namespaced;
		}

		static TinyState get(int indent, String identifier) {
			switch (identifier) {
				case "c":
					return indent == 0 ? CLASS : COMMENT;
				case "m":
					return METHOD;
				case "f":
					return FIELD;
				case "p":
					return PARAMETER;
				case "v":
					return LOCAL_VARIABLE;
				default:
					throw new IllegalArgumentException("Invalid identifier \"" + identifier + "\"!");
			}
		}

		boolean checkPartCount(int indent, int partCount, int namespaceCount) {
			return partCount - indent == (namespaced ? namespaceCount + actualParts : actualParts);
		}

		abstract boolean checkStack(TinyState[] stack, int currentIndent);

		abstract void visit(TinyVisitor visitor, String[] parts, int indent, boolean escapedStrings);

		MappingGetter makeGetter(String[] parts, int indent, boolean escapedStrings) {
			return new PartGetter(indent + actualParts, parts, escapedStrings);
		}
	}

	private static final class TinyHeader implements TinyMetadata {

		private final int majorVersion;
		private final int minorVersion;
		private final List<String> namespaces;
		private final Map<String, Integer> mapper;
		private final Map<String, String> properties;

		TinyHeader(int majorVersion, int minorVersion, List<String> namespaces, Map<String, Integer> mapper, Map<String, String> properties) {
			this.majorVersion = majorVersion;
			this.minorVersion = minorVersion;
			this.namespaces = namespaces;
			this.mapper = mapper;
			this.properties = properties;
		}

		@Override
		public int getMajorVersion() {
			return majorVersion;
		}

		@Override
		public int getMinorVersion() {
			return minorVersion;
		}

		@Override
		public List<String> getNamespaces() {
			return namespaces;
		}

		@Override
		public Map<String, String> getProperties() {
			return properties;
		}

		@Override
		public int index(String namespace) {
			return mapper.getOrDefault(namespace, -1);
		}
	}

	private static final class PartGetter implements MappingGetter {
		private final int offset;
		private final String[] parts;
		private final boolean escapedStrings;

		PartGetter(int offset, String[] parts, boolean escapedStrings) {
			this.offset = offset;
			this.parts = parts;
			this.escapedStrings = escapedStrings;
		}

		@Override
		public String get(int namespace) {
			int index = offset + namespace;
			while (parts[index].isEmpty())
				index--;
			return unescapeOpt(parts[index], escapedStrings);
		}

		@Override
		public String getRaw(int namespace) {
			return unescapeOpt(parts[offset + namespace], escapedStrings);
		}

		@Override
		public String[] getRawNames() {
			if (!escapedStrings) {
				return Arrays.copyOfRange(parts, offset, parts.length);
			}

			final String[] ret = new String[parts.length - offset];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = unescape(parts[i + offset]);
			}
			return ret;
		}

		@Override
		public String[] getAllNames() {
			final String[] ret = getRawNames();
			for (int i = 1; i < ret.length; i++) {
				if (ret[i].isEmpty()) {
					ret[i] = ret[i - 1];
				}
			}
			return ret;
		}
	}
}
