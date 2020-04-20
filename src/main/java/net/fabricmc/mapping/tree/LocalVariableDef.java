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

import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Represents a local variable element in a method.
 */
public interface LocalVariableDef extends Mapped {

	/**
	 * Gets the local variable index of this local variable.
	 *
	 * @return the local variable index
	 */
	@NonNegative int getLocalVariableIndex();

	/**
	 * Gets the local variable start offset of this local variable.
	 *
	 * @return the local variable start offset
	 */
	@NonNegative int getLocalVariableStartOffset();

	/**
	 * Gets the index of this local variable in the local variable table.
	 *
	 * <p>This may be absent; when absent, {@code -1} is returned.
	 *
	 * @return the index in the local variable table or {@code -1}
	 */
	int getLocalVariableTableIndex();
}
