package net.fabricmc.mapping.tree;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.ToIntFunction;

abstract class MappedImpl implements Mapped {

	final ToIntFunction<String> namespaceMapper;
	final String[] names;
	@Nullable String comment;

	MappedImpl(ToIntFunction<String> namespaceMapper, String[] names) {
		this.namespaceMapper = namespaceMapper;
		this.names = names;
		this.comment = null;
	}

	@Override
	public String getName(String namespace) {
		int t = namespaceMapper.applyAsInt(namespace);
		return getName(t);
	}

	@Override
	public String getMappedName(String namespace) {
		return names[namespaceMapper.applyAsInt(namespace)];
	}

	@Override
	public @Nullable String getComment() {
		return comment;
	}

	String getName(int namespace) {
		while (names[namespace].isEmpty())
			namespace--;
		return names[namespace];
	}

	void setComment(@Nullable String comment) {
		this.comment = comment;
	}
}
