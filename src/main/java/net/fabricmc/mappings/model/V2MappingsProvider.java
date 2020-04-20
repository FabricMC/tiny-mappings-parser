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

import net.fabricmc.mappings.ClassEntry;
import net.fabricmc.mappings.EntryTriple;
import net.fabricmc.mappings.FieldEntry;
import net.fabricmc.mappings.Mappings;
import net.fabricmc.mappings.MethodEntry;
import net.fabricmc.mapping.reader.v2.MappingGetter;
import net.fabricmc.mapping.reader.v2.TinyMetadata;
import net.fabricmc.mapping.reader.v2.TinyV2Factory;
import net.fabricmc.mapping.reader.v2.TinyVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.ToIntFunction;

public class V2MappingsProvider {
    public static Mappings readTinyMappings(BufferedReader reader) throws IOException {
        Visitor visitor = new Visitor();
        TinyV2Factory.visit(reader, visitor);
        return visitor.getMappings();
    }

    private enum CommentType {
        CLASS,
        FIELD,
        METHOD,
        PARAMETER,
        LOCAL_VARIABLE
    }

    private static class ClassEntryImpl implements ClassEntry {
        private final ToIntFunction<String> namespaceIndices;
        private final String[] names;

        ClassEntryImpl(ToIntFunction<String> namespaceIndices, String[] names) {
            this.namespaceIndices = namespaceIndices;
            this.names = names;
        }

        @Override
        public String get(String namespace) {
            return names[namespaceIndices.applyAsInt(namespace)];
        }
    }

    private static class MemberEntryImpl implements FieldEntry, MethodEntry {
        private final ToIntFunction<String> namespaceIndices;
        private final EntryTriple[] names;

        MemberEntryImpl(ToIntFunction<String> namespaceIndices, EntryTriple[] names) {
            this.namespaceIndices = namespaceIndices;
            this.names = names;
        }

        @Override
        public EntryTriple get(String namespace) {
            return names[namespaceIndices.applyAsInt(namespace)];
        }
    }

    private static class ParameterEntryImpl implements MethodParameterEntry {
        private final ToIntFunction<String> namespaceIndices;
        private final MethodParameter[] names;

        ParameterEntryImpl(ToIntFunction<String> namespaceIndices, MethodParameter[] names) {
            this.namespaceIndices = namespaceIndices;
            this.names = names;
        }

        @Override
        public MethodParameter get(String namespace) {
            return names[namespaceIndices.applyAsInt(namespace)];
        }
    }

    private static class LocalVariableEntryEntryImpl implements LocalVariableEntry {
        private final ToIntFunction<String> namespaceIndices;
        private final LocalVariable[] names;

        LocalVariableEntryEntryImpl(ToIntFunction<String> namespaceIndices, LocalVariable[] names) {
            this.namespaceIndices = namespaceIndices;
            this.names = names;
        }

        @Override
        public LocalVariable get(String namespace) {
            return names[namespaceIndices.applyAsInt(namespace)];
        }
    }

    private static class Visitor implements TinyVisitor {
        private Collection<ClassEntry> classEntries = new ArrayList<>();
        private Collection<MethodEntry> methodEntries = new ArrayList<>();
        private Collection<FieldEntry> fieldEntries = new ArrayList<>();
        private Collection<MethodParameterEntry> methodParameterEntries = new ArrayList<>();
        private Collection<LocalVariableEntry> localVariableEntries = new ArrayList<>();
        private Comments comments = new CommentsImpl(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
                , new ArrayList<>(), new ArrayList<>());
        private List<String> namespaces;

        private ToIntFunction<String> namespaceIndices;
        private int namespaceAmount;

        private String[] currentClassNames;
        private EntryTriple[] currentMemberNames;
        private MethodParameter[] currentParameterNames;
        private LocalVariable[] currentLocalVariableNames;
        private List<String> currentComments;
        private CommentType currentCommentType;

        @Override
        public void start(TinyMetadata metadata) {
            namespaces = metadata.getNamespaces();
            namespaceAmount = namespaces.size();
            namespaceIndices = metadata::index;
        }

        @Override
        public void pushClass(MappingGetter name) {
            storeClassMappings(name);
            classEntries.add(new ClassEntryImpl(namespaceIndices, currentClassNames));
            setNewCommentType(CommentType.CLASS);
        }

        @Override
        public void pushField(MappingGetter name, String descriptor) {
            storeMemberMappings(name, descriptor);
            fieldEntries.add(new MemberEntryImpl(namespaceIndices, currentMemberNames));
            setNewCommentType(CommentType.FIELD);
        }

        @Override
        public void pushMethod(MappingGetter name, String descriptor) {
            storeMemberMappings(name, descriptor);
            methodEntries.add(new MemberEntryImpl(namespaceIndices, currentMemberNames));
            setNewCommentType(CommentType.METHOD);
        }

        @Override
        public void pushParameter(MappingGetter name, int localVariableIndex) {
            currentParameterNames = new MethodParameter[namespaceAmount];
            for (int i = 0; i < namespaceAmount; i++) {
                currentParameterNames[i] = new MethodParameter(currentMemberNames[i], name.get(i), localVariableIndex);
            }
            methodParameterEntries.add(new ParameterEntryImpl(namespaceIndices, currentParameterNames));
            setNewCommentType(CommentType.PARAMETER);
        }

        @Override
        public void pushLocalVariable(MappingGetter name, int localVariableIndex, int localVariableStartOffset, int localVariableTableIndex) {
            currentLocalVariableNames = new LocalVariable[namespaceAmount];
            for (int i = 0; i < namespaceAmount; i++) {
                currentLocalVariableNames[i] = new LocalVariable(currentMemberNames[i], name.get(i), localVariableIndex, localVariableStartOffset, localVariableTableIndex);
            }
            localVariableEntries.add(new LocalVariableEntryEntryImpl(namespaceIndices, currentLocalVariableNames));
            setNewCommentType(CommentType.LOCAL_VARIABLE);
        }

        @Override
        public void pushComment(String comment) {
            switch (currentCommentType) {
                case CLASS:
                    if (currentComments.isEmpty()) {
                        comments.getClassComments().add(new CommentEntry.Class(currentComments, currentClassNames[0]));
                    }
                    break;
                case FIELD:
                    if (currentComments.isEmpty()) {
                        comments.getFieldComments().add(new CommentEntry.Field(currentComments, currentMemberNames[0]));
                    }
                    break;
                case METHOD:
                    if (currentComments.isEmpty()) {
                        comments.getMethodComments().add(new CommentEntry.Method(currentComments, currentMemberNames[0]));
                    }
                    break;
                case PARAMETER:
                    if (currentComments.isEmpty()) {
                        comments.getMethodParameterComments().add(new CommentEntry.Parameter(currentComments, currentParameterNames[0]));
                    }
                    break;
                case LOCAL_VARIABLE:
                    if (currentComments.isEmpty()) {
                        comments.getLocalVariableComments().add(new CommentEntry.LocalVariableComment(currentComments, currentLocalVariableNames[0]));
                    }
                    break;
                default:
                    throw new RuntimeException("unexpected comment without parent");
            }

            currentComments.add(comment);
        }

        private void setNewCommentType(CommentType type) {
            currentCommentType = type;
            currentComments = new ArrayList<>();
        }

        private void storeClassMappings(MappingGetter namesProvider) {
            currentClassNames = new String[namespaceAmount];
            for (int i = 0; i < namespaceAmount; i++) {
                currentClassNames[i] = namesProvider.get(i);
            }
        }

        private void storeMemberMappings(MappingGetter namesProvider, String descriptor) {
            currentMemberNames = new EntryTriple[namespaceAmount];
            for (int i = 0; i < namespaceAmount; i++) {
                currentMemberNames[i] = new EntryTriple(currentClassNames[i], namesProvider.get(i), descriptor);
            }
        }

        private class MappingsImpl implements Mappings {
            private Collection<ClassEntry> classEntries;
            private Collection<MethodEntry> methodEntries;
            private Collection<FieldEntry> fieldEntries;
            private Collection<MethodParameterEntry> methodParameterEntries;
            private Collection<LocalVariableEntry> localVariableEntries;
            private Collection<String> namespaces;
            private Comments comments;

            public MappingsImpl(Collection<ClassEntry> classEntries, Collection<MethodEntry> methodEntries, Collection<FieldEntry> fieldEntries, Collection<MethodParameterEntry> methodParameterEntries, Collection<LocalVariableEntry> localVariableEntries, Collection<String> namespaces, Comments comments) {
                this.classEntries = classEntries;
                this.methodEntries = methodEntries;
                this.fieldEntries = fieldEntries;
                this.methodParameterEntries = methodParameterEntries;
                this.localVariableEntries = localVariableEntries;
                this.namespaces = namespaces;
                this.comments = comments;
            }

            @Override
            public Collection<ClassEntry> getClassEntries() {
                return classEntries;
            }

            @Override
            public Collection<MethodEntry> getMethodEntries() {
                return methodEntries;
            }

            @Override
            public Collection<FieldEntry> getFieldEntries() {
                return fieldEntries;
            }

            @Override
            public Collection<MethodParameterEntry> getMethodParameterEntries() {
                return methodParameterEntries;
            }

            @Override
            public Collection<LocalVariableEntry> getLocalVariableEntries() {
                return localVariableEntries;
            }

            @Override
            public Collection<String> getNamespaces() {
                return namespaces;
            }

            @Override
            public Comments getComments() {
                return comments;
            }
        }

        public Mappings getMappings() {
            return new MappingsImpl(classEntries, methodEntries, fieldEntries, methodParameterEntries,
                    localVariableEntries, namespaces, comments);
        }
    }


}
