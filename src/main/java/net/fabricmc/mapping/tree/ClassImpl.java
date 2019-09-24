package net.fabricmc.mapping.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.ToIntFunction;

final class ClassImpl extends MappedImpl implements ClassDef {

	final Collection<MethodDef> methods = new ArrayList<>();
	final Collection<FieldDef> fields = new ArrayList<>();

	ClassImpl(ToIntFunction<String> namespaceMapper, String[] names) {
		super(namespaceMapper, names);
	}

	@Override
	public Collection<MethodDef> getMethods() {
		return methods;
	}

	@Override
	public Collection<FieldDef> getFields() {
		return fields;
	}
}
