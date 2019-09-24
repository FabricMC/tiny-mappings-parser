package net.fabricmc.mappings.model;

import net.fabricmc.mappings.EntryTriple;

import java.util.Objects;

public class LocalVariable {
    private EntryTriple method;
    private String name;
    private int localVariableIndex;
    private int localVariableStartOffset;
    private int localVariableTableIndex;

    public LocalVariable(EntryTriple method, String name, int localVariableIndex, int localVariableStartOffset, int localVariableTableIndex) {
        this.method = method;
        this.name = name;
        this.localVariableIndex = localVariableIndex;
        this.localVariableStartOffset = localVariableStartOffset;
        this.localVariableTableIndex = localVariableTableIndex;
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

    public int getLocalVariableStartOffset() {
        return localVariableStartOffset;
    }

    public int getLocalVariableTableIndex() {
        return localVariableTableIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalVariable that = (LocalVariable) o;
        return localVariableIndex == that.localVariableIndex &&
                localVariableStartOffset == that.localVariableStartOffset &&
                localVariableTableIndex == that.localVariableTableIndex &&
                method.equals(that.method) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, name, localVariableIndex, localVariableStartOffset, localVariableTableIndex);
    }

    @Override
    public LocalVariable clone(){return new LocalVariable(method,name,localVariableIndex,localVariableStartOffset,localVariableTableIndex);}
}
