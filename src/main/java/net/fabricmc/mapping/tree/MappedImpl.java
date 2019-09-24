package net.fabricmc.mapping.tree;

import java.util.function.ToIntFunction;

abstract class MappedImpl implements Mapped {

	final ToIntFunction<String> namespaceMapper;
	final String[] names;
	String comment;

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
	public String getComment() {
		return comment;
	}

	String getName(int namespace) {
		while (names[namespace].isEmpty())
			namespace--;
		return names[namespace];
	}

	void setComment(String comment) {
		this.comment = comment;
	}
}
