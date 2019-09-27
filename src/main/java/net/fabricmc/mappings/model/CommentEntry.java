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

import java.util.List;

public abstract class CommentEntry {
    private List<String> comments;

    public static class Class extends CommentEntry{
        public Class(List<String> comments, String className) {
            super(comments);
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        private String className;
    }
    public static class Method extends CommentEntry{
        public Method(List<String> comments, EntryTriple method) {
            super(comments);
            this.method = method;
        }

        public EntryTriple getMethod() {
            return method;
        }

        private EntryTriple method;
    }
    public static class Field extends CommentEntry{
        public Field(List<String> comments, EntryTriple field) {
            super(comments);
            this.field = field;
        }

        private EntryTriple field;

        public EntryTriple getField() {
            return field;
        }
    }

    public static class Parameter extends CommentEntry{
        public Parameter(List<String> comments, MethodParameter parameter) {
            super(comments);
            this.parameter = parameter;
        }

        public MethodParameter getParameter() {
            return parameter;
        }

        private MethodParameter parameter;
    }
    public static class LocalVariableComment extends CommentEntry{
        public LocalVariableComment(List<String> comments, LocalVariable localVariable) {
            super(comments);
            this.localVariable = localVariable;
        }

        public LocalVariable getLocalVariable() {
            return localVariable;
        }

        private LocalVariable localVariable;
    }

    private CommentEntry(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getComments() {
        return comments;
    }
}
