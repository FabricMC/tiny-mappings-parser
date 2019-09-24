package net.fabricmc.mapping.tree;

import java.util.function.ToIntFunction;

final class ParameterImpl extends MappedImpl implements ParameterDef {

	final int index;

	ParameterImpl(ToIntFunction<String> namespaceMapper, String[] names, int index) {
		super(namespaceMapper, names);
		this.index = index;
	}

	@Override
	public int getLocalVariableIndex() {
		return index;
	}
}
