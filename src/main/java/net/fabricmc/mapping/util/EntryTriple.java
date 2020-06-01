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

package net.fabricmc.mapping.util;

/**
 * A reference to methods or fields.
 */
public final class EntryTriple {
	private final String owner;
	private final String name;
	private final String descriptor;

	public EntryTriple(String owner, String name, String descriptor) {
		this.owner = owner;
		this.name = name;
		this.descriptor = descriptor;
	}

	/**
	 * Returns the name of the owner class of the entry in internal form.
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Returns the name of the entry.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the descriptor of the entry.
	 */
	public String getDescriptor() {
		return descriptor;
	}

	/**
	 * Returns a new entry as a result of remapping this entry.
	 *
	 * @param mapper  the mapper that remap class names
	 * @param newName the new name for the entry
	 * @return a new entry
	 */
	public EntryTriple map(ClassMapper mapper, String newName) {
		return new EntryTriple(mapper.mapClass(this.owner), newName, mapper.mapDescriptor(this.descriptor));
	}

	@Override
	public String toString() {
		return "EntryTriple{owner=" + owner + ",name=" + name + ",desc=" + descriptor + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EntryTriple)) {
			return false;
		} else if (o == this) {
			return true;
		} else {
			EntryTriple other = (EntryTriple) o;

			return other.owner.equals(owner) && other.name.equals(name) && other.descriptor.equals(descriptor);
		}
	}

	@Override
	public int hashCode() {
		return owner.hashCode() * 37 + name.hashCode() * 19 + descriptor.hashCode();
	}
}
