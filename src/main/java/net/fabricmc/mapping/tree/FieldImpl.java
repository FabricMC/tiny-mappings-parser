package net.fabricmc.mapping.tree;

import java.util.function.ToIntFunction;

final class FieldImpl extends SignaturedImpl implements FieldDef {
	FieldImpl(SignatureMapper mapper, ToIntFunction<String> namespaceMapper, String[] names, String signature) {
		super(mapper, namespaceMapper, names, signature);
	}
}
