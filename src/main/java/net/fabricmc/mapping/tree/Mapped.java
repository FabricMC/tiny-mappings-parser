/*
 * Copyright 2019 FabricMC
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

/**
 * Represents an element that can be mapped and have comments.
 */
public interface Mapped {

	/**
	 * Gets the mapped name of the element in the target namespace.
	 *
	 * @param namespace the target namespace
	 * @return the mapped name
	 */
	String getName(String namespace);

	/**
	 * Gets an explicitly mapped name of the element in the target namespace.
	 *
	 * <p>May be empty if the target namespace inherits the name from another
	 * namespace.
	 *
	 * @param namespace the target namespace
	 * @return the explicitly mapped name
	 */
	String getRawName(String namespace);

	/**
	 * Gets the comment (JavaDoc, etc.) on an element.
	 *
	 * <p>The comment may be {@code null} if there is none.
	 *
	 * @return the comment or {@code null}
	 */
	@Nullable String getComment();
}
