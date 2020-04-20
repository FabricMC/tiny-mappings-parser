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

import net.fabricmc.mapping.reader.v2.TinyMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a tiny mapping tree.
 */
public interface TinyTree {

	/**
	 * Gets the metadata of this mapping tree.
	 *
	 * @return the metadata
	 */
	TinyMetadata getMetadata();

	/**
	 * Gets the map from the default namespace class names to the class mappings.
	 *
	 * @return the name to class map
	 */
	Map<String, ClassDef> getDefaultNamespaceClassMap();

	/**
	 * Gets all the classes in the mappings.
	 *
	 * @return all the classes
	 */
	Collection<ClassDef> getClasses();
}
