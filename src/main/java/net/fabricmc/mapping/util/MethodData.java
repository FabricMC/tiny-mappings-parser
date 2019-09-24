package net.fabricmc.mapping.util;

import java.util.HashMap;
import java.util.Map;

public final class MethodData {

	private final String name;
	private final Map<Integer, String> parameterNames = new HashMap<>();

	public MethodData(String name) {
		this.name = name;
	}

	public Map<Integer, String> getParameterNames() {
		return parameterNames;
	}

	public String getName() {
		return name;
	}
}
