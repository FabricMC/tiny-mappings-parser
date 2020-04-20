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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.ToIntFunction;

abstract class MappedImpl implements Mapped {

	final ToIntFunction<String> namespaceMapper;
	final String[] names;
	@Nullable String comment;

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
	public String getRawName(String namespace) {
		return names[namespaceMapper.applyAsInt(namespace)];
	}

	@Override
	public @Nullable String getComment() {
		return comment;
	}

	String getName(int namespace) {
		if (namespace >= names.length)
			namespace = names.length - 1;
		while (names[namespace].isEmpty()) {
			if (namespace == 0)
				return "";
			namespace--;
		}
		return names[namespace];
	}

	void setComment(@Nullable String comment) {
		this.comment = comment;
	}
}
