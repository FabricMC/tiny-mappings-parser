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

package net.fabricmc.mapping.reader.v2;

/**
 * A getter for a mapping.
 *
 * Don't keep many instances of mapping getters! It will be memory-expensive!
 */
public interface MappingGetter {

	/**
	 * Gets the corresponding mapped name under a namespace with the namespace's index.
	 *
	 * <p>To retrieve a namespace's index, call {@link TinyMetadata#index(String)}.
	 *
	 * @param namespace the namespace's index
	 * @return the retrieved name
	 */
	String get(int namespace);

	/**
	 * Gets the raw mapped name under a namespace with the namespace's index. May be empty.
	 *
	 * @param namespace the namespace's index
	 * @return the retrieved raw name
	 */
	String getRaw(int namespace);

	/**
	 * Creates an array of all mapped names, ordered by their namespace's appearance.
	 *
	 * <p>This method returns all the mapped names in the declaration. It does not fill the
	 * array to fill the empty namespaces with names from the last declared namespace. Use
	 * {@link #getAllNames()} for a version that fills empty namespaces.
	 *
	 * <p>This method call would be expensive as each call may create a new array.
	 *
	 * <p>This method may return empty elements in the array.
	 *
	 * @return all mapped names
	 */
	String[] getRawNames();

	/**
	 * Creates an array of all mapped names, ordered by their namespace's appearance.
	 *
	 * <p>This method returns all the mapped names. It fills the empty namespaces with
	 * names from the last declared namespace. Use {@link #getRawNames()} for a version that
	 * does not fill empty namespaces.
	 *
	 * <p>This method call would be expensive as each call may create a new array.
	 *
	 * @return all mapped names
	 */
	String[] getAllNames();
}
