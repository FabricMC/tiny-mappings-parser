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

import net.fabricmc.mapping.util.ClassMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapperTest {

	@Test
	public void testMapper() {
		Map<String, String> map = new HashMap<>();

		map.put("java/lang/Object", "net/minecraft/client/Minecraft");
		map.put("abc", "net/fabricmc/tinyv2/TinyVisitor");

		ClassMapper mapper = new ClassMapper(map);

		assertEquals("(IIZLjava/lang/Integer;Lnet/fabricmc/tinyv2/TinyVisitor;J)V", mapper.mapDescriptor("(IIZLjava/lang/Integer;Labc;J)V"));
		assertEquals("(IIZLnet/minecraft/client/Minecraft;Lnet/fabricmc/tinyv2/TinyVisitor;J)Lnet/fabricmc/tinyv2/TinyVisitor;", mapper.mapDescriptor("(IIZLjava/lang/Object;Labc;J)Labc;"));
		assertEquals("(IIZLjava/lang/Object;Labc;J)Labc", mapper.mapDescriptor("(IIZLjava/lang/Object;Labc;J)Labc")); // wrong one
	}
}
