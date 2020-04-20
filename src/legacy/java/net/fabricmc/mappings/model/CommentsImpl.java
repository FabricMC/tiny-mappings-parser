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

package net.fabricmc.mappings.model;

import java.util.Collection;
import java.util.List;

@Deprecated
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
