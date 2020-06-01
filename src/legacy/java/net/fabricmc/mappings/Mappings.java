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

package net.fabricmc.mappings;

import net.fabricmc.mappings.model.Comments;
import net.fabricmc.mappings.model.CommentsImpl;
import net.fabricmc.mappings.model.LocalVariableEntry;

import net.fabricmc.mappings.model.MethodParameterEntry;

import java.util.ArrayList;
import java.util.Collection;

@Deprecated
public interface Mappings {
	Collection<String> getNamespaces();

	Collection<ClassEntry> getClassEntries();
	Collection<FieldEntry> getFieldEntries();
	Collection<MethodEntry> getMethodEntries();
	default Collection<MethodParameterEntry> getMethodParameterEntries() { return new ArrayList<>(); }
	default Collection<LocalVariableEntry> getLocalVariableEntries() { return new ArrayList<>(); }
	default Comments getComments(){
		return new CommentsImpl(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
	}

}
