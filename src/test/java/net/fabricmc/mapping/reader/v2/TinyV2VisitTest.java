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
			indent();
			System.out.println(Arrays.toString(name.getRaw()));
		}

		@Override
		public void pushField(MappingGetter name, String descriptor) {
			level++;
			indent();
			System.out.println(name.get(0));
			indent();
			System.out.println(Arrays.toString(name.getRaw()));
			indent();
			System.out.println(descriptor);
		}

		@Override
		public void pushMethod(MappingGetter name, String descriptor) {
			level++;
			indent();
			System.out.println(name.get(0));
			indent();
			System.out.println(Arrays.toString(name.getRaw()));
			indent();
			System.out.println(Arrays.toString(name.getRaw()));
			indent();
			System.out.println(descriptor);
		}

		@Override
		public void pushParameter(MappingGetter name, int localVariableIndex) {
			level++;
			indent();

		}

		@Override
		public void pushLocalVariable(MappingGetter name, int localVariableIndex, int localVariableStartOffset, int localVariableTableIndex) {
			level++;
			indent();

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
	}
}
