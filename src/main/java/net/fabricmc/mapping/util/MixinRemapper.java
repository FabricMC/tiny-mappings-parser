package net.fabricmc.mapping.util;

import net.fabricmc.mapping.tree.ClassDef;
import net.fabricmc.mapping.tree.FieldDef;
import net.fabricmc.mapping.tree.MethodDef;
import net.fabricmc.mapping.tree.TinyMapping;
import net.fabricmc.mappings.EntryTriple;
import org.spongepowered.asm.mixin.extensibility.IRemapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class MixinRemapper implements IRemapper {

	private final Map<EntryTriple, String> fieldNames = new HashMap<>();
	private final Map<EntryTriple, String> methodNames = new HashMap<>();
	private final ClassMapper mapper;
	private final ClassMapper unmapper;

	public MixinRemapper(TinyMapping mapping, String from, String to) {
		this(mapping.getClasses(), from, to);
	}

	public MixinRemapper(Collection<ClassDef> classes, String from, String to) {
		Map<String, String> classNames = new HashMap<>();
		Map<String, String> unmapClassNames = new HashMap<>();
		for (ClassDef clz : classes) {
			String className = clz.getName(from);
			String mappedClassName = clz.getName(to);
			classNames.put(className, mappedClassName);
			unmapClassNames.put(mappedClassName, className);
			for (FieldDef field : clz.getFields()) {
				fieldNames.put(new EntryTriple(className, field.getName(from), field.getSignature(from)), field.getName(to));
			}
			for (MethodDef method : clz.getMethods()) {
				methodNames.put(new EntryTriple(className, method.getName(from), method.getSignature(from)), method.getName(to));
			}
		}

		this.mapper = new ClassMapper(classNames);
		this.unmapper = new ClassMapper(unmapClassNames);
	}

	@Override
	public String mapMethodName(String owner, String name, String desc) {
		return methodNames.getOrDefault(new EntryTriple(owner, name, desc), name);
	}

	@Override
	public String mapFieldName(String owner, String name, String desc) {
		return fieldNames.getOrDefault(new EntryTriple(owner, name, desc), name);
	}

	@Override
	public String map(String typeName) {
		return mapper.mapClass(typeName);
	}

	@Override
	public String unmap(String typeName) {
		return unmapper.mapClass(typeName);
	}

	@Override
	public String mapDesc(String desc) {
		return mapper.mapDescriptor(desc);
	}

	@Override
	public String unmapDesc(String desc) {
		return unmapper.mapDescriptor(desc);
	}
}
