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
