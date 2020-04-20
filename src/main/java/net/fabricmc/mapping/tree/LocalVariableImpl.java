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

import java.util.function.ToIntFunction;

final class LocalVariableImpl extends MappedImpl implements LocalVariableDef {

	final int index;
	final int startOffset;
	final int tableIndex;

	LocalVariableImpl(ToIntFunction<String> namespaceMapper, String[] names, int index, int startOffset, int tableIndex) {
		super(namespaceMapper, names);
		this.index = index;
		this.startOffset = startOffset;
		this.tableIndex = tableIndex;
	}

	@Override
	public int getLocalVariableIndex() {
		return index;
	}

	@Override
	public int getLocalVariableStartOffset() {
		return startOffset;
	}

	@Override
	public int getLocalVariableTableIndex() {
		return tableIndex;
	}
}
