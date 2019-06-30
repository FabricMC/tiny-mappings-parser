package net.fabricmc.tinyv2;

import net.fabricmc.tinyv2.ClassMapper;

import java.util.HashMap;
import java.util.Map;

public class MapperTest {

	public static void main(String... args) {
		Map<String, String> map = new HashMap<>();

		map.put("java/lang/Object", "net/minecraft/client/Minecraft");
		map.put("abc", "net/fabricmc/tinyv2/TinyVisitor");

		ClassMapper mapper = new ClassMapper(map);

		System.out.println(mapper.mapDescriptor("(IIZLjava/lang/Integer;Labc;J)V"));
		System.out.println(mapper.mapDescriptor("(IIZLjava/lang/Object;Labc;J)Labc;"));
		System.out.println(mapper.mapDescriptor("(IIZLjava/lang/Object;Labc;J)Labc")); // wrong one
	}
}
