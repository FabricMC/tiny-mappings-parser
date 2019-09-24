package net.fabricmc.mapping.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.ToIntFunction;

final class MethodImpl extends SignaturedImpl implements MethodDef {
	final Collection<ParameterDef> parameters = new ArrayList<>();
	final Collection<LocalVariableDef> localVariables = new ArrayList<>();

	MethodImpl(SignatureMapper mapper, ToIntFunction<String> namespaceMapper, String[] names, String signature) {
		super(mapper, namespaceMapper, names, signature);
	}

	@Override
	public Collection<ParameterDef> getParameters() {
		return parameters;
	}

	@Override
	public Collection<LocalVariableDef> getLocalVariables() {
		return localVariables;
	}
}
