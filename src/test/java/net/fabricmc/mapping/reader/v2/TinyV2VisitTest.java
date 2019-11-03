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

package net.fabricmc.mapping.reader.v2;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TinyV2VisitTest {
	@Test
	public void testVisitInheritanceTree() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(TinyV2VisitTest.class.getResourceAsStream("/inhtree.tiny"), StandardCharsets.UTF_8))) {
			TinyV2Factory.visit(reader, new Visitor());
		}
	}

	private static final class Visitor implements TinyVisitor {

		int level = -1;

		@Override
		public void start(TinyMetadata metadata) {
			System.out.println(metadata.getNamespaces());
			System.out.println(metadata.getProperties());
		}

		@Override
		public void pushClass(MappingGetter name) {
			level++;
			indent();
			System.out.println(name.get(0));
			printNames(name);
		}

		@Override
		public void pushField(MappingGetter name, String descriptor) {
			level++;
			indent();
			System.out.println(name.get(0));
			printNames(name);
			indent();
			System.out.println(descriptor);
		}

		@Override
		public void pushMethod(MappingGetter name, String descriptor) {
			level++;
			indent();
			System.out.println(name.get(0));
			printNames(name);
			indent();
			System.out.println(descriptor);
		}

		@Override
		public void pushParameter(MappingGetter name, int localVariableIndex) {
			level++;
			indent();
			printNames(name);
		}

		@Override
		public void pushLocalVariable(MappingGetter name, int localVariableIndex, int localVariableStartOffset, int localVariableTableIndex) {
			level++;
			indent();
			printNames(name);
		}

		@Override
		public void pushComment(String comment) {
			level++;
			indent();
			System.out.println(comment);
		}

		@Override
		public void pop(int count) {
			level -= count;
			System.out.println("level -= " + count);
		}

		private void indent() {
			System.out.print(Strings.repeat("\t", level));
		}

		private void printNames(MappingGetter name) {
			indent();
			System.out.println(Arrays.toString(name.getRawNames()));
			indent();
			System.out.println(Arrays.toString(name.getAllNames()));
		}
	}
}
