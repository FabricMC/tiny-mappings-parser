package net.fabricmc.mapping.tree;

import java.util.function.ToIntFunction;

final class LocalVariableImpl extends MappedImpl implements LocalVariableDef {

	final int index;
	final int startOffset;
	final int tableIndex;

	LocalVariableImpl(ToIntFunction<String> namespaceMapper, String[] names, int index, int startOffset, int tableIndex) {
		super(namespaceMapper, names);
		this.index = index;
		this.startOffset = startOffset;
		this.tableIndex = tableIndex;
	}

	@Override
	public int getLocalVariableIndex() {
		return index;
	}

	@Override
	public int getLocalVariableStartOffset() {
		return startOffset;
	}

	@Override
	public int getLocalVariableTableIndex() {
		return tableIndex;
	}
}
