package net.fabricmc.mapping.tree;

import java.util.Map;

/**
 * A simple class name mapper backed by a map.
 *
 * <p>The class name is intended to be in the format like {@code java/lang/String}.
 *
 * <p>Note that remapping result changes when the map changes.
 */
final class SignatureMapper {

	private final Map<String, ? extends MappedImpl> map;

	SignatureMapper(Map<String, ? extends MappedImpl> map) {
		this.map = map;
	}

	String mapClass(int namespace, String old) {
		MappedImpl got = map.get(old);
		return got == null ? old : got.getName(namespace);
	}

	String mapDescriptor(int namespace, String old) {
		int lastL = old.indexOf('L');
		int lastSemi = -1;
		if (lastL < 0) {
			return old;
		}
		StringBuilder builder = new StringBuilder((int) (old.length() * 1.2)); // approximate
		while (lastL >= 0) {
			if (lastSemi + 1 < lastL) {
				builder.append(old, lastSemi + 1, lastL);
			}
			lastSemi = old.indexOf(';', lastL + 1);
			if (lastSemi == -1)
				return old; // Invalid desc, nah!
			builder.append('L').append(mapClass(namespace, old.substring(lastL + 1, lastSemi))).append(';');
			lastL = old.indexOf('L', lastSemi + 1);
		}

		if (lastSemi + 1 < old.length()) {
			builder.append(old, lastSemi + 1, old.length());
		}
		return builder.toString();
	}
}
