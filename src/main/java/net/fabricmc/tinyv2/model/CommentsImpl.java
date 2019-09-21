package net.fabricmc.tinyv2.model;

import java.util.Collection;
import java.util.List;

public class CommentsImpl implements Comments {
    private List<CommentEntry.Class> classComments;
    private List<CommentEntry.Field> fieldComments;
    private List<CommentEntry.Method> methodComments;
    private List<CommentEntry.Parameter> methodParameterComments;
    private List<CommentEntry.LocalVariableComment> localVariableComments;

    public CommentsImpl(List<CommentEntry.Class> classComments, List<CommentEntry.Field> fieldComments, List<CommentEntry.Method> methodComments, List<CommentEntry.Parameter> methodParameterComments, List<CommentEntry.LocalVariableComment> localVariableComments) {
        this.classComments = classComments;
        this.fieldComments = fieldComments;
        this.methodComments = methodComments;
        this.methodParameterComments = methodParameterComments;
        this.localVariableComments = localVariableComments;
    }

    @Override
    public Collection<CommentEntry.Class> getClassComments() {
        return classComments;
    }

    @Override
    public Collection<CommentEntry.Field> getFieldComments() {
        return fieldComments;
    }

    @Override
    public Collection<CommentEntry.Method> getMethodComments() {
        return methodComments;
    }

    @Override
    public Collection<CommentEntry.Parameter> getMethodParameterComments() {
        return methodParameterComments;
    }

    @Override
    public Collection<CommentEntry.LocalVariableComment> getLocalVariableComments() {
        return localVariableComments;
    }
}
