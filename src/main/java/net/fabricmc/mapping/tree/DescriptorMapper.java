/*
 * Copyright (c) 2019-2020 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.mapping.tree;

import java.util.Map;

/**
 * A simple class name mapper backed by a map.
 *
 * <p>The class name is intended to be in the format like {@code java/lang/String}.
 *
 * <p>Note that remapping result changes when the map changes.
 */
final class DescriptorMapper {

	private final Map<String, ? extends MappedImpl> map;

	DescriptorMapper(Map<String, ? extends MappedImpl> map) {
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
