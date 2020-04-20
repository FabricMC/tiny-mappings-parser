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

import java.util.Collection;
import java.util.Collections;

final class DummyMappings implements Mappings {
	static final DummyMappings INSTANCE = new DummyMappings();

	private DummyMappings() {

	}

	@Override
	public Collection<String> getNamespaces() {
		return Collections.emptySet();
	}

	@Override
	public Collection<ClassEntry> getClassEntries() {
		return Collections.emptyList();
	}

	@Override
	public Collection<FieldEntry> getFieldEntries() {
		return Collections.emptyList();
	}

	@Override
	public Collection<MethodEntry> getMethodEntries() {
		return Collections.emptyList();
	}
}
