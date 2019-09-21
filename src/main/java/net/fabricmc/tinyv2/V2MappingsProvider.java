package net.fabricmc.tinyv2;

import net.fabricmc.mappings.ClassEntry;
import net.fabricmc.mappings.EntryTriple;
import net.fabricmc.mappings.FieldEntry;
import net.fabricmc.mappings.Mappings;
import net.fabricmc.mappings.MethodEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        private final Map<String, Integer> namespaceIndices;
        private final String[] names;

        ClassEntryImpl(Map<String, Integer> namespaceIndices, String[] names) {
            this.namespaceIndices = namespaceIndices;
            this.names = names;
        }

        @Override
        public String get(String namespace) {
            return names[namespaceIndices.get(namespace)];
        }
    }

    private static class MemberEntryImpl implements FieldEntry, MethodEntry {
        private final Map<String, Integer> namespaceIndices;
        private final EntryTriple[] names;

        MemberEntryImpl(Map<String, Integer> namespaceIndices, EntryTriple[] names) {
            this.namespaceIndices = namespaceIndices;
            this.names = names;
        }

        @Override
        public EntryTriple get(String namespace) {
            return names[namespaceIndices.get(namespace)];
        }
    }

    private static class ParameterEntryImpl implements MethodParameterEntry {
        private final Map<String, Integer> namespaceIndices;
        private final MethodParameter[] names;

        ParameterEntryImpl(Map<String, Integer> namespaceIndices, MethodParameter[] names) {
            this.namespaceIndices = namespaceIndices;
            this.names = names;
        }

        @Override
        public MethodParameter get(String namespace) {
            return names[namespaceIndices.get(namespace)];
        }
    }

    private static class LocalVariableEntryEntryImpl implements LocalVariableEntry {
        private final Map<String, Integer> namespaceIndices;
        private final LocalVariable[] names;

        LocalVariableEntryEntryImpl(Map<String, Integer> namespaceIndices, LocalVariable[] names) {
            this.namespaceIndices = namespaceIndices;
            this.names = names;
        }

        @Override
        public LocalVariable get(String namespace) {
            return names[namespaceIndices.get(namespace)];
        }
    }

    private static class Visitor implements TinyVisitor {
        private Collection<ClassEntry> classEntries;
        private Collection<MethodEntry> methodEntries;
        private Collection<FieldEntry> fieldEntries;
        private Collection<MethodParameterEntry> methodParameterEntries;
        private Collection<LocalVariableEntry> localVariableEntries;
        private Comments comments = new CommentsImpl(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
                , new ArrayList<>(), new ArrayList<>());
        private List<String> namespaces;

        private Map<String, Integer> namespaceIndices = new HashMap<>();
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
            for (int i = 0; i < namespaceAmount; i++) {
                namespaceIndices.put(namespaces.get(i), i);
            }
        }

        @Override
        public void pushClass(MappingGetter name) {
            storeClassMappings(name);
            classEntries.add(new ClassEntryImpl(namespaceIndices, currentClassNames));
            currentCommentType = CommentType.CLASS;
            currentComments = new ArrayList<>();
        }

        @Override
        public void pushField(MappingGetter name, String descriptor) {
            storeMemberMappings(name, descriptor);
            fieldEntries.add(new MemberEntryImpl(namespaceIndices, currentMemberNames));
            currentCommentType = CommentType.FIELD;
            currentComments = new ArrayList<>();
        }

        @Override
        public void pushMethod(MappingGetter name, String descriptor) {
            storeMemberMappings(name, descriptor);
            methodEntries.add(new MemberEntryImpl(namespaceIndices, currentMemberNames));
            currentCommentType = CommentType.METHOD;
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

        @Override
        public void pushParameter(MappingGetter name, int localVariableIndex) {
            currentParameterNames = new MethodParameter[namespaceAmount];
            for (int i = 0; i < namespaceAmount; i++) {
                currentParameterNames[i] = new MethodParameter(currentMemberNames[i], name.get(i), localVariableIndex);
            }
            methodParameterEntries.add(new ParameterEntryImpl(namespaceIndices, currentParameterNames));
            currentCommentType = CommentType.PARAMETER;
            currentComments = new ArrayList<>();
        }

        @Override
        public void pushLocalVariable(MappingGetter name, int localVariableIndex, int localVariableStartOffset, int localVariableTableIndex) {
            currentLocalVariableNames = new LocalVariable[namespaceAmount];
            for (int i = 0; i < namespaceAmount; i++) {
                currentLocalVariableNames[i] = new LocalVariable(currentMemberNames[i], name.get(i), localVariableIndex, localVariableStartOffset, localVariableTableIndex);
            }
            localVariableEntries.add(new LocalVariableEntryEntryImpl(namespaceIndices, currentLocalVariableNames));
            currentCommentType = CommentType.LOCAL_VARIABLE;
            currentComments = new ArrayList<>();
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
