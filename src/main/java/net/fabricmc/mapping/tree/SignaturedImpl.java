package net.fabricmc.mapping.tree;

import java.util.function.ToIntFunction;

abstract class SignaturedImpl extends MappedImpl implements Signatured {

	final SignatureMapper mapper;
	final String signature;

	SignaturedImpl(SignatureMapper mapper, ToIntFunction<String> namespaceMapper, String[] names, String signature) {
		super(namespaceMapper, names);
		this.mapper = mapper;
		this.signature = signature;
	}

	@Override
	public String getSignature(String namespace) {
		int t = namespaceMapper.applyAsInt(namespace);
		return t == 0 ? signature : mapper.mapDescriptor(t, signature);
	}
}
