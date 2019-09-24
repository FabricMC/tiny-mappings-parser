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
		int t = namespaceMapper.applyAsInt(namespace);
		return t >= names.length ? null : names[t];
	}

	@Override
	public String getComment() {
		return comment;
	}

	String getName(int namespace) {
		return namespace >= names.length ? names[names.length - 1] : names[namespace];
	}

	void setComment(String comment) {
		this.comment = comment;
	}
}
