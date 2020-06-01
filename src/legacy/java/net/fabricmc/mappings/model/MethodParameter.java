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

package net.fabricmc.mappings.model;

import net.fabricmc.mappings.EntryTriple;

import java.util.Objects;

@Deprecated
public class MethodParameter {
    private EntryTriple method;
    private String name;
    private int localVariableIndex;

    public MethodParameter(EntryTriple method, String name, int localVariableIndex) {
        this.method = method;
        this.name = name;
        this.localVariableIndex = localVariableIndex;
    }

    public EntryTriple getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public int getLocalVariableIndex() {
        return localVariableIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodParameter that = (MethodParameter) o;
        return localVariableIndex == that.localVariableIndex &&
                method.equals(that.method) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, name, localVariableIndex);
    }

    @Override
    public String toString() {
        return "MethodParameter{" +
                "method=" + method +
                ", name='" + name + '\'' +
                ", localVariableIndex=" + localVariableIndex +
                '}';
    }

    @Override
    public MethodParameter clone(){ return new MethodParameter(method,name,localVariableIndex);}
}
