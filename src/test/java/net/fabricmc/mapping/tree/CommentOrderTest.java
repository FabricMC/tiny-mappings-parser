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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collections;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentOrderTest {

	private TinyTree tree;

	@BeforeAll
	public void readOneFifteenTwoBuildTwoMappings() {
		try (FileSystem fs = FileSystems.newFileSystem(new URI("jar:" + CommentOrderTest.class.getResource("/yarn-1.15.2+build.2-v2.jar").toString()), Collections.emptyMap())) {
			try (BufferedReader reader = Files.newBufferedReader(fs.getPath("mappings", "mappings.tiny"))) {
				tree = TinyMappingFactory.load(reader);
			}
		} catch (IOException | URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Test
	public void testContainerMenuSizeCheckComment() {
		ClassDef cl = tree.getDefaultNamespaceClassMap().get("net/minecraft/class_1703"); // Container/Menu etc

		MethodDef m17359 = null;

		for (MethodDef each : cl.getMethods()) {
			if (each.getName("intermediary").equals("method_17359")) {
				m17359 = each;
				break;
			}
		}

		if (m17359 == null) {
			throw new RuntimeException("Cannot find \"checkContainerSize\" method!");
		}

		Assertions.assertEquals("Checks that the size of the provided inventory is at least as large as the {@code expectedSize}.\n\n@throws IllegalArgumentException if the inventory size is smaller than {@code exceptedSize}", m17359.getComment());
		for (ParameterDef par : m17359.getParameters()) {
			Assertions.assertNull(par.getComment());
		}
	}

	@Test
	public void testPropertyDelegateComment() {
		ClassDef cl = tree.getDefaultNamespaceClassMap().get("net/minecraft/class_3913"); // Container/Menu etc

		Assertions.assertEquals("A property delegate represents an indexed list of integer properties.\n\n<p>Property delegates are used for displaying integer values in screens,\nsuch as the progress bars in furnaces.", cl.getComment());

		for (MethodDef each : cl.getMethods()) {
			Assertions.assertNull(each.getComment());
			for (ParameterDef par : each.getParameters()) {
				Assertions.assertNull(par.getComment());
			}
			for (LocalVariableDef par : each.getLocalVariables()) {
				Assertions.assertNull(par.getComment());
			}
		}

		for (FieldDef each : cl.getFields()) {
			Assertions.assertNull(each.getComment());
		}
	}
}
