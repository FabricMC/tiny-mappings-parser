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

package net.fabricmc.mapping.util;

import java.util.Map;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A simple class name mapper backed by a unary operator.
 *
 * <p>The class name is intended to be in the format like {@code java/lang/String}.
 *
 * <p>Note that remapping result changes when the map changes.
 */
public final class ClassMapper {

	private final Function<String, @Nullable String> mapper;

	/**
	 * Creates the mapper.
	 *
	 * @param map the map the mapper uses
	 */
	public ClassMapper(Map<String, String> map) {
		this(map::get);
	}

	/**
	 * Creates the mapper.
	 * 
	 * <p>The {@code classMapping} can return {@code null}, in which
	 * this class mapper will return the original class name passed.</p>
	 *
	 * @param classMapping the simple class name mapper
	 */
	public ClassMapper(Function<String, @Nullable String> classMapping) {
		this.mapper = classMapping;
	}

	/**
	 * Maps a class name.
	 *
	 * @param old the original name
	 * @return the mapped name
	 */
	public String mapClass(String old) {
		@Nullable String got = mapper.apply(old);
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
