package net.fabricmc.tinyv2;

import java.util.Map;

/**
 * A simple class name mapper backed by a map.
 *
 * <p>The class name is intended to be in the format like {@code java/lang/String}.
 *
 * <p>Note that remapping result changes when the map changes.
 */
public final class ClassMapper {

	private final Map<String, String> map;

	/**
	 * Creates the mapper.
	 *
	 * @param map the map the mapper uses
	 */
	public ClassMapper(Map<String, String> map) {
		this.map = map;
	}

	/**
	 * Maps a class name.
	 *
	 * @param old the original name
	 * @return the mapped name
	 */
	public String mapClass(String old) {
		String got = map.get(old);
		return got == null ? old : got;
	}

	/**
	 * Maps a descriptor.
	 *
	 * <p>If the descriptor is invalid, the passed descriptor is returned.
	 *
	 * @param old the original descriptor
	 * @return the mapped descriptor
	 */
	public String mapDescriptor(String old) {
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
			builder.append('L').append(mapClass(old.substring(lastL + 1, lastSemi))).append(';');
			lastL = old.indexOf('L', lastSemi + 1);
		}

		if (lastSemi + 1 < old.length()) {
			builder.append(old, lastSemi + 1, old.length());
		}
		return builder.toString();
	}
}
