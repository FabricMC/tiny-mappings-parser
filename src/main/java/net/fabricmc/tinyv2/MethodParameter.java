package net.fabricmc.tinyv2;

import net.fabricmc.mappings.EntryTriple;

import java.util.Objects;

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
