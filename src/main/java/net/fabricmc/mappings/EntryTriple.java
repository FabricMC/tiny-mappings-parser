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

package net.fabricmc.mappings;

public class EntryTriple {
	private final String owner;
	private final String name;
	private final String desc;

	public EntryTriple(String owner, String name, String desc) {
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	public String getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return "EntryTriple{owner=" + owner + ",name=" + name + ",desc=" + desc + "}";
	}

	@Override
	protected Object clone() {
		return new EntryTriple(owner, name, desc);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EntryTriple)) {
			return false;
		} else if (o == this) {
			return true;
		} else {
			EntryTriple other = (EntryTriple) o;

			return other.owner.equals(owner) && other.name.equals(name) && other.desc.equals(desc);
		}
	}

	@Override
	public int hashCode() {
		return owner.hashCode() * 37 + name.hashCode() * 19 + desc.hashCode();
	}
}
