package net.fabricmc.mapping.tree;

import net.fabricmc.mapping.reader.v2.TinyV2VisitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

final class DefaultTinyTreeTest {

	private TinyTree tree;

	@BeforeEach
	void setupTree() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(TinyV2VisitTest.class.getResourceAsStream("/bigboi_mappings.tinyv2"), StandardCharsets.UTF_8))) {
			tree = TinyMappingFactory.load(reader);
		}
	}

	@Test
	void testMusicTrackerGetterDescRemapping() {
		ClassDef classDef = findClass("net/minecraft/class_310", "intermediary");
		MethodDef methodDef = findMethod(classDef, "method_1538", "intermediary");
		Assertions.assertEquals("()Lnet/minecraft/client/sound/MusicTracker;", methodDef.getDescriptor("named"));
		Assertions.assertEquals("()Lnet/minecraft/class_1142;", methodDef.getDescriptor("intermediary"));
		Assertions.assertEquals("()Leak;", methodDef.getDescriptor("official"));
	}

	@Test
	void testEntityDispatcherRenderDescRemapping() {
		ClassDef classDef = findClass("net/minecraft/class_898", "intermediary");
		MethodDef methodDef = findMethod(classDef, "method_3954", "intermediary");
		Assertions.assertEquals("(Lnet/minecraft/entity/Entity;DDDFFZ)V", methodDef.getDescriptor("named"));
		Assertions.assertEquals("(Lnet/minecraft/class_1297;DDDFFZ)V", methodDef.getDescriptor("intermediary"));
		Assertions.assertEquals("(Laio;DDDFFZ)V", methodDef.getDescriptor("official"));
	}

	@Test
	void testConfigureEntityRenderDispatcherDescRemapping() {
		ClassDef classDef = findClass("net/minecraft/class_898", "intermediary");
		MethodDef methodDef = findMethod(classDef, "method_3941", "intermediary");
		Assertions.assertEquals("(Lnet/minecraft/world/World;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/render/Camera;Lnet/minecraft/entity/Entity;Lnet/minecraft/client/options/GameOptions;)V", methodDef.getDescriptor("named"));
		Assertions.assertEquals("(Lnet/minecraft/class_1937;Lnet/minecraft/class_327;Lnet/minecraft/class_4184;Lnet/minecraft/class_1297;Lnet/minecraft/class_315;)V", methodDef.getDescriptor("intermediary"));
		Assertions.assertEquals("(Lbhr;Lcyu;Lcxq;Laio;Lcyg;)V", methodDef.getDescriptor("official"));
	}

	private ClassDef findClass(String name, String namespace) {
		for (ClassDef each : tree.getClasses()) {
			if (each.getName(namespace).equals(name)) {
				return each;
			}
		}
		throw new NoSuchElementException("No class " + name + " in " + namespace);
	}

	private MethodDef findMethod(ClassDef classDef, String name, String namespace) {
		for (MethodDef each : classDef.getMethods()) {
			if (each.getName(namespace).equals(name)) {
				return each;
			}
		}
		throw new NoSuchElementException("No method " + name + " in " + namespace);
	}
}
