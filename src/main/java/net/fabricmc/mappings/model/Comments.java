package net.fabricmc.mappings.model;

import java.util.Collection;

public interface Comments {
    Collection<CommentEntry.Class> getClassComments();
	Collection<CommentEntry.Field> getFieldComments();
	Collection<CommentEntry.Method> getMethodComments();
	Collection<CommentEntry.Parameter> getMethodParameterComments();
	Collection<CommentEntry.LocalVariableComment> getLocalVariableComments();
}
